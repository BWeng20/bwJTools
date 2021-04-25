/*
 * (c) copyright Bernd Wengenroth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools.ui.pathchooser;

import com.bw.jtools.Log;
import com.bw.jtools.ui.UIToolSwing;
import com.bw.jtools.ui.pathchooser.impl.*;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

/**
 * Panel to show a file system folder tree.
 */
public class JPathFolderTree extends JPanel
{
	/**
	 * The root node. Parent of the root elements from the file systems.
	 */
	private DefaultMutableTreeNode root_ = new DefaultMutableTreeNode(null);

	/**
	 * Used tree model.
	 */
	private DefaultTreeModel model_ = new DefaultTreeModel(root_);
	private PathInfoTreeCellRenderer cellRenderer_;

	/**
	 * Tree component to show the file system folder tree
	 */
	private JTree tree_ = new JTree(model_);

	/**
	 * Handler used to get icons and path information.
	 */
	private PathInfoProvider handler_;

	/**
	 * The currently selected path.
	 */
	private PathInfo selectedPath_;

	private List<FileSystemInfo> fileSystems_ = new ArrayList<>();

	private boolean debug = Log.isDebugEnabled();
	private int updatesRunning_ = 0;

	/**
	 * Creates a File System Tree.
	 */
	public JPathFolderTree(PathInfoProvider handler)
	{
		super();
		handler_ = handler;
		setLayout(new BorderLayout());
		cellRenderer_ = new PathInfoTreeCellRenderer(handler);
		tree_.setCellRenderer(cellRenderer_);
		tree_.setRootVisible(false);
		tree_.setShowsRootHandles(true);
		tree_.setExpandsSelectedPaths(true);
		add(new JScrollPane(tree_), BorderLayout.CENTER);

		setMinimumSize(new Dimension(100, 100));
		installDefaults();

		addPropertyChangeListener("font", evt ->
		{
			Font f = getFont();
			cellRenderer_.setFont(f);
			tree_.setFont(f);
		});

		tree_.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			@Override
			public void treeWillExpand(TreeExpansionEvent event)
					throws ExpandVetoException
			{
				final Object lp = event.getPath()
									   .getLastPathComponent();
				if (lp instanceof SystemTreeNode)
				{
					SystemTreeNode node = (SystemTreeNode) lp;
					if (!node.expanded) update(node);
				}
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event)
			{
			}
		});

		tree_.addTreeSelectionListener(e ->
				{
					Object node = tree_.getLastSelectedPathComponent();
					if (updatesRunning_ == 0 || node != null)
					{
						PathInfo pi = null;
						if (node instanceof PathInfoFolderNode)
						{
							pi = ((PathInfoFolderNode) node).getPathInfo();
						}
						if (!Objects.equals(selectedPath_, pi))
						{
							selectedPath_ = pi;
							fireSelectionChanged(false);
						}
					}
				}
		);
		UIToolSwing.addAction(tree_, new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (selectedPath_ != null)
				{
					fireSelectionChanged(true);
				}
			}
		}, "ENTER");
	}

	protected void fireSelectionChanged(boolean finalSelection)
	{
		Object[] listeners = listenerList.getListenerList();
		PathSelectionEvent ev = new PathSelectionEvent(selectedPath_, finalSelection);
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == PathSelectionListener.class)
			{
				((PathSelectionListener) listeners[i + 1]).selectedPathChanged(ev);
			}
		}
	}

	public void addPathSelectionListener(PathSelectionListener listener)
	{
		listenerList.add(PathSelectionListener.class, listener);
	}

	public void removePathSelectionListener(PathSelectionListener listener)
	{
		listenerList.remove(PathSelectionListener.class, listener);
	}

	protected void installDefaults()
	{
		if (cellRenderer_ != null)
		{
			cellRenderer_.setFont(getFont());
			tree_.setRowHeight(0);
		}
		setPreferredSize(new Dimension(300, 800));
	}

	@Override
	public void updateUI()
	{
		super.updateUI();
		installDefaults();
	}

	/**
	 * Sets the file system to show.
	 */
	public void checkFileSystem(FileSystem fsys)
	{
		if (getFileSystemInfo(fsys) == null)
		{
			Log.debug("New file system: " + fsys);
			FileSystemInfo fsysInfo = new FileSystemInfo(fsys, null, null, null);
			fileSystems_.add(fsysInfo);

			update(root_);
		}
	}

	/**
	 * Selects a directory.<br>
	 * Sets also the file-system if needed.
	 *
	 * @param newPath the new path.
	 */
	public void setDirectory(PathInfo newPath)
	{
		selectedPath_ = null;
		if (newPath != null)
		{
			PathInfo root = newPath;
			while (root.getParent() != null)
				root = root.getParent();

			checkFileSystem(root.getPath()
								.getFileSystem());
			DefaultMutableTreeNode snode = getOrCreatePath(newPath);
			if (snode != null)
			{
				selectedPath_ = (PathInfo) snode.getUserObject();
				TreePath pp = new TreePath(snode.getPath());
				try
				{
					tree_.setSelectionPath(pp);
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	/**
	 * Gets a existing node for a path.<br>
	 * If the path is currently not exists, the nodes are created as placeholders and marked for update.
	 *
	 * @param info Path to search
	 * @return The node.
	 */
	protected DefaultMutableTreeNode getOrCreatePath(PathInfo info)
	{
		DefaultMutableTreeNode fn = null;

		if (info != null)
		{
			final Path path = info.getPath();
			final String name = (path.getNameCount() > 0 ? path.getFileName() : path.getRoot()).toString();
			PathInfo parentInfo = info.getParent();
			DefaultMutableTreeNode parentNode;
			if (parentInfo != null && !parentInfo.isSkipped())
			{
				parentNode = getOrCreatePath(parentInfo);
			}
			else
			{
				parentNode = getFileSystemRoot(path);
			}

			for (int cidx = 0; cidx < parentNode.getChildCount(); ++cidx)
			{
				TreeNode node = parentNode.getChildAt(cidx);
				if (node instanceof PathInfoFolderNode)
				{
					PathInfoFolderNode candiate = (PathInfoFolderNode) node;
					if (name.equals(candiate.getPathInfo()
											.getFileName()))
					{
						fn = candiate;
						break;
					}
				}
			}
			if (fn == null)
			{
				fn = new PathInfoFolderNode(handler_.createPathInfo(info.getParent(), path), false);
				model_.insertNodeInto(fn, parentNode, parentNode.getChildCount());
			}
		}
		else
		{
			fn = root_;
		}
		return fn;
	}

	/**
	 * Gets info about an additional file-system.<br>
	 * If the filesystem is not "additional" null is returned.
	 */
	protected FileSystemInfo getFileSystemInfo(FileSystem fsys)
	{
		for (FileSystemInfo fsysi : fileSystems_)
		{
			if (fsysi.fsys_ == fsys)
			{
				return fsysi;
			}
		}
		return null;
	}

	/**
	 * Gets a root node for a file-system.<br>
	 * If the filesystem is not "additional" file-system, root is returned
	 */
	protected DefaultMutableTreeNode getFileSystemRoot(Path path)
	{
		FileSystemInfo fsysi = getFileSystemInfo(path.getFileSystem());
		if (fsysi == null)
			return root_;
		else if (fsysi.baseNode_ != null)
		{
			return fsysi.baseNode_;
		}
		else
		{
			for (PathInfoFolderNode node : fsysi.roots_)
			{
				if (node.getPathInfo()
						.getPath()
						.equals(path))
					return node;
			}
			return root_;
		}
	}

	/**
	 * Updates content of a node.
	 */
	protected void update(final DefaultMutableTreeNode node)
	{
		++updatesRunning_;
		if (debug)
		{
			Log.debug("Update " + node.getUserObject());
		}
		SwingWorker<List<PathInfo>, Void> worker = new SwingWorker<List<PathInfo>, Void>()
		{
			@Override
			protected List<PathInfo> doInBackground()
			{
				List<PathInfo> result = new ArrayList<>();

				if (node == root_)
				{
					for (FileSystemInfo fsysi : fileSystems_)
					{
						// Add root-directories for all implicit file-systems.
						if (fsysi.baseNode_ == null && fsysi.roots_.isEmpty())
						{
							scanRootDirectories(result, fsysi.fsys_, null);
						}
					}
				}
				else if (node instanceof PathInfoFolderNode)
				{
					PathInfoFolderNode fn = (PathInfoFolderNode) node;
					scanPath(result, fn.getPathInfo(), fn);
				}
				else if (node instanceof FileSystemNode)
				{
					FileSystemNode fn = (FileSystemNode) node;
					FileSystem fsys = fn.getFileSystemInfo().fsys_;
					int oldSize = result.size();
					for (Path p : fsys.getRootDirectories())
					{
						PathInfo pi = handler_.createPathInfo(null, p);
						// Skip the "/" root of virtual filesystems.
						if ("/".equals(pi.fileName_))
						{
							scanPath(result, pi, fn);
						}
						else
							result.add(pi);
					}
				}
				return result;
			}

			@Override
			protected void done()
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				List<PathInfo> files;
				try
				{
					files = get();
				}
				catch (Exception e)
				{
					files = Collections.EMPTY_LIST;
				}

				if (node == root_)
				{
					int index = 0;
					for (FileSystemInfo fsysi : fileSystems_)
					{
						// Add base-nodes of all explicitly named file-systems at the top.
						if (fsysi.baseNode_ != null && !node.isNodeChild(fsysi.baseNode_))
						{
							node.insert(fsysi.baseNode_, index++);
						}
					}
				}

				HashMap<String, PathInfoFolderNode> nodes = new HashMap<>();
				for (int ci = node.getChildCount() - 1; ci >= 0; --ci)
				{
					DefaultMutableTreeNode cn = (DefaultMutableTreeNode) node.getChildAt(ci);
					if (cn instanceof PathInfoFolderNode)
					{
						PathInfoFolderNode n = (PathInfoFolderNode) cn;
						nodes.put(n.getPathInfo()
								   .getFileName(), n);
					}
					else if (!(cn instanceof SystemTreeNode))
					{
						// temporary "loading" node .
						node.remove(ci);
					}
				}
				for (PathInfo pi : files)
				{
					PathInfoFolderNode n = nodes.remove(pi.getFileName());
					if (n == null)
					{
						n = new PathInfoFolderNode(pi, true);
						node.add(n);
					}
				}
				for (PathInfoFolderNode n : nodes.values())
				{
					node.remove(n);
				}

				if (node instanceof SystemTreeNode)
				{
					((SystemTreeNode) node).expanded = true;
				}

				node.setAllowsChildren(node.getChildCount() > 0);
				try
				{
					model_.nodeStructureChanged(node);
				}
				catch (Exception e)
				{
					Log.error("Failed to update node", e);
					model_.reload();
				}
				--updatesRunning_;

				if (selectedPath_ != null && 0 == updatesRunning_ &&
						tree_.getLastSelectedPathComponent() == null)
				{
					SwingUtilities.invokeLater(() ->
					{
						final TreePath pp = new TreePath(getOrCreatePath(selectedPath_).getPath());
						tree_.setSelectionPath(pp);
					});
				}

				if (debug)
				{
					Log.debug("Update done " + node + " -> " + node.getChildCount());
				}

			}
		};
		worker.execute();
	}

	/**
	 * Collects all entries in a directory.
	 *
	 * @param result The list the node shall be added to.
	 * @param node   The parent node. Can be null.
	 */
	protected void scanPath(List<PathInfo> result, PathInfo pi, SystemTreeNode node)
	{
		try
		{
			if (debug)
				Log.debug("Scanning " + pi);
			try (DirectoryStream<Path> dir = handler_.getDirectoryStream(pi))
			{
				for (Path d : dir)
				{
					PathInfo pf = handler_.createPathInfo(pi, d);
					if (pf.isTraversable() && pf.isReadable())
					{
						result.add(pf);
					}
				}
			}
			if (debug)
				Log.debug("Done scanning " + pi);
		}
		catch (Exception e)
		{
			if (node != null)
				node.setError(e);
		}
	}

	/**
	 * Collects all root directories of a file-system.
	 *
	 * @param result The list the node shall be added to.
	 * @param fsys   The file system.
	 * @param node   The parent node. Can be null.
	 */
	protected void scanRootDirectories(List<PathInfo> result, FileSystem fsys, SystemTreeNode node)
	{
		for (Path p : fsys.getRootDirectories())
		{
			PathInfo pi = handler_.createPathInfo(null, p);
			// Skip the "/" root of virtual filesystems.
			if ("/".equals(pi.fileName_))
			{
				scanPath(result, pi, node);
			}
			else
				result.add(pi);
		}
	}

	/**
	 * Adds a file system to the directory tree.<br>
	 * The root of the file-systems can be shown below a explicit base-node
	 * or implicit together with other roots on top level.<br>
	 * To show the roots below an explicit base-node set some name <> null.
	 * If name is set, also an icon can be given. If name is null, the icon is ignored.
	 *
	 * @param fs   the file-system to show. All roots are added as children.
	 * @param name The name to show on the base node of the file-system or null.
	 */
	public void addFileSystem(FileSystem fs, String name, Icon icon)
	{
		for (FileSystemInfo fi : fileSystems_)
		{
			if (fi.fsys_ == fs)
			{
				return;
			}
		}
		fileSystems_.add(new FileSystemInfo(fs, name, icon, name == null ? null : new FileSystemNode()));
		if (isShowing())
			update(root_);

	}
}
