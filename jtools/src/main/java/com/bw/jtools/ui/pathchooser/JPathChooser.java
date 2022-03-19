/*
 * Copyright Bernd Wengenroth.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bw.jtools.ui.pathchooser;

import com.bw.jtools.Log;
import com.bw.jtools.ui.pathchooser.impl.ShellIconProvider;
import com.bw.jtools.ui.pathchooser.impl.SystemIconProvider;
import com.bw.jtools.ui.pathchooser.impl.ZipAwarePathProvider;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Locale;

/**
 * A File-Browser with support for browsing into java.nio file systems like zip-archives.<br>
 * <b>Why a separate implementation, not simply using FileSystemView with JFileChooser.</b>
 * I tried hard and failed. All available "examples" that try to add support for zip/jar-archives may work for
 * specific Look-And-Feel-version, but they will not work (at least) for windows lafs.<br>
 * The LAFs for JFileChooser work only partial via FileSystemView API and fail for paths
 * outside the os-file-systems.<br>
 * The nio-path-API is designed to work on other file-systems and java has already build-in
 * file-system-provider for zip/jar-archives. There are also many other file-system-provider out there.<br>
 *
 * <br>
 */
public class JPathChooser extends JComponent
{
	protected JButton okButton_;
	protected JButton cancelButton_;
	protected JDialog dialog_;
	protected JPanel buttons_;

	protected PathIconProvider icons_;
	protected PathInfoProvider handler_;

	protected JPathFolderTree systemTree_;
	protected JPathList fileList_;
	protected JPathInput input_;
	protected JPanel accessories_;

	protected PathInfo selectedPath_;

	protected Color forcedBackground_;

	private PathChooserMode fileSelectionMode_ = PathChooserMode.FILES_ONLY;


	/**
	 * Creates the main sub-components of the file chooser:<br>
	 *     <ul>
	 *         <li>PathIconProvider  {@link #icons_}</li>
	 *         <li>PathInfoProvider  {@link #handler_}</li>
	 *         <li>JFileSystemTree  {@link #systemTree_}</li>
	 *         <li>JFileList  {@link #fileList_}</li>
	 *     </ul>
	 * Override this method if other implementations of these components are needed.
	 */
	protected void createComponents()
	{
		// Check if API for platform dependent icons is accessible.
		icons_ = ShellIconProvider.isSupported() ? new ShellIconProvider() : new SystemIconProvider();
		handler_ = new ZipAwarePathProvider(icons_);

		systemTree_ = new JPathFolderTree(handler_);
		fileList_ = new JPathList(handler_);
		input_ = new JPathInput(handler_);
	}


	public JPathChooser()
	{
		super();
		setLayout(new BorderLayout());

		createComponents();

		FileSystemProvider p;

		okButton_ = new JButton("");
		okButton_.addActionListener(e ->
		{
			if (dialog_ != null) dialog_.setVisible(false);
		});


		cancelButton_ = new JButton("");
		cancelButton_.addActionListener(e ->
		{
			fileList_.clearSelection();
			if (dialog_ != null) dialog_.setVisible(false);
		});

		buttons_ = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons_.add(okButton_);
		buttons_.add(cancelButton_);

		forcedBackground_ = new Color(UIManager.getColor("List.background").getRGB());
		buttons_.setBackground(forcedBackground_);

		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		JPanel left = new JPanel(new BorderLayout());
		left.add(fileList_, BorderLayout.CENTER );
		left.add(input_,BorderLayout.SOUTH);

		splitter.setRightComponent(left);
		splitter.setLeftComponent(systemTree_);
		add(splitter, BorderLayout.CENTER);
		add(buttons_, BorderLayout.SOUTH);

		addPropertyChangeListener("font", evt ->
		{
			fileList_.setFont(getFont());
			systemTree_.setFont(getFont());
		});
		setFont(new Font("Dialog", Font.PLAIN, icons_.isUseLargeIcons() ? 16 : 12));

		systemTree_.addPathSelectionListener(ev ->
		{
			PathInfo newPath = ev.getSelectedPath();
			fireSelectionChanged(newPath, ev.isFinalSelection());
			if (newPath != null)
			{
				if ( ! ( ev.isFinalSelection() && doSelection(newPath)) )
				{
					fileList_.setDirectory(newPath);
					input_.setPath(newPath);
				}
			}
		});

		fileList_.addPathSelectionListener(ev ->
		{
			PathInfo newPath = ev.getSelectedPath();
			fireSelectionChanged(newPath, ev.isFinalSelection());
			if (newPath != null)
			{
				if ( ev.isFinalSelection() )
				{
					if ( !doSelection(newPath))
					{
						if (newPath.isTraversable())
						{
							systemTree_.setDirectory(newPath);
							fileList_.setDirectory(newPath);
						}
					}
				}
				input_.setPath(newPath);
			}
		});

		input_.addPathSelectionListener(ev ->
		{
			PathInfo newPath = ev.getSelectedPath();
			fireSelectionChanged(newPath, ev.isFinalSelection());
			if (newPath != null)
			{
				if (!( ev.isFinalSelection() && doSelection(newPath)) )
				{
					if (newPath.isTraversable())
					{
						systemTree_.setDirectory(newPath);
						fileList_.setDirectory(newPath);
					}
					else
						fileList_.setPath( newPath );
				}
			}
		});
	}

	protected boolean doSelection(PathInfo p)
	{
		selectedPath_ = p;
		if (p != null)
		{
			switch (fileSelectionMode_)
			{
				case FILES_ONLY:
					if ((!p.isTraversable()) || Files.isRegularFile(selectedPath_.getPath()))
						closeDialog();
					else
						return false;
					break;
				case FOLDERS:
					if (p.isTraversable())
						closeDialog();
					else
						return false;
					break;
				case FILES_AND_FOLDERS:
					closeDialog();
					break;
			}
		}
		return true;
	}

	protected void fireSelectionChanged(PathInfo selectedPath, boolean finalSelection)
	{
		Object[] listeners = listenerList.getListenerList();
		PathSelectionEvent ev = new PathSelectionEvent(selectedPath, finalSelection);
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == PathSelectionListener.class)
			{
				try
				{
					((PathSelectionListener) listeners[i + 1]).selectedPathChanged(ev);
				}
				catch (Exception e)
				{
					Log.error("PathSelectionListener " + listeners[i + 1].getClass()
																		 .getSimpleName(), e);
				}

			}
		}
	}

	/**
	 * Adds a listener for changes in the current path selection.
	 */
	public void addPathSelectionListener(PathSelectionListener listener)
	{
		listenerList.add(PathSelectionListener.class, listener);
	}

	/**
	 * Removed a path listener.
	 */
	public void removePathSelectionListener(PathSelectionListener listener)
	{
		listenerList.remove(PathSelectionListener.class, listener);
	}


	/**
	 * Adds a component to the button panel.
	 */
	public void addButton(JComponent button)
	{
		buttons_.add(button);
	}

	/**
	 * If icon provider supports large icons,
	 * large icons are used.
	 */
	public void setUseLargeIcons(boolean large)
	{
		if (large != icons_.isUseLargeIcons())
		{
			icons_.setUseLargeIcons(large);

			if (large == icons_.isUseLargeIcons())
			{
				setFont(getFont().deriveFont(large ? 16f : 12f));
				repaint();
			}
		}
	}

	/**
	 * Returns if icon provider is using large icons.
	 */
	public boolean isUseLargeIcons()
	{
		return icons_.isUseLargeIcons();
	}


	@Override
	public void updateUI()
	{
		super.updateUI();
		icons_.updateUIIcons();

		forcedBackground_ = new Color(UIManager.getColor("List.background").getRGB());

		buttons_.setBackground(forcedBackground_);
		systemTree_.setBackground(forcedBackground_);
		if (accessories_ != null)
		{
			accessories_.setBackground(forcedBackground_);
			int N = accessories_.getComponentCount();
			for ( int ci = 0 ; ci < N; ++ci )
				accessories_.getComponent(ci).setBackground(forcedBackground_);

		}
	}

	/**
	 * Sets the initial directory to show.
	 *
	 * @param p The path
	 */
	public void setDirectory(Path p)
	{
		PathInfo pi = handler_.createPathInfo(p);
		fileList_.setDirectory(pi);
		systemTree_.setDirectory(pi);
	}

	/**
	 * Get the current selection mode.
	 */
	public PathChooserMode getFileSelectionMode()
	{
		return fileSelectionMode_;
	}

	/**
	 * Set the selection mode.
	 *
	 * @param fileSelectionMode The mode.
	 */
	public void setFileSelectionMode(PathChooserMode fileSelectionMode)
	{
		this.fileSelectionMode_ = fileSelectionMode;
	}

	/**
	 * Shows a modal file selector in open mode.<br>
	 * The default jdk filechooser titles and button labels are used.
	 *
	 * @param parent The parent component that is used to detect the parent windows.
	 * @return Returns true if the user selected a path and false if selection was aborted.
	 */
	public boolean showOpenDialog(Component parent)
	{
		final Locale locale = parent != null ? parent.getLocale() : Locale.getDefault();
		return showDialog(parent,
				UIManager.getString("FileChooser.openDialogTitleText", locale),
				UIManager.getString("FileChooser.saveButtonText", locale));
	}

	/**
	 * Shows a modal file selector in save mode.<br>
	 * The default jdk filechooser titles and button labels are used.
	 *
	 * @param parent The parent component that is used to detect the parent windows.
	 * @return Returns true if the user selected a path and false if selection was aborted.
	 */
	public boolean showSaveDialog(Component parent)
	{
		final Locale locale = parent != null ? parent.getLocale() : Locale.getDefault();
		return showDialog(parent,
				UIManager.getString("FileChooser.saveDialogTitleText", locale),
				UIManager.getString("FileChooser.saveButtonText", locale));
	}

	/**
	 * Shows a custom modal file selector.<br>
	 *
	 * @param parent      The parent component that is used to detect the parent windows.
	 * @param dialogTitle The dialog title.
	 * @param okButton    The text of the "ok" button.
	 * @return Returns true if the user selected a path and false if selection was aborted.
	 */
	public boolean showDialog(Component parent, String dialogTitle, String okButton)
	{
		selectedPath_ = null;
		final Locale locale = parent != null ? parent.getLocale() : Locale.getDefault();
		okButton_.setText(okButton);
		cancelButton_.setText(UIManager.getString("FileChooser.cancelButtonText", locale));

		Window window = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
		if (window instanceof Frame)
		{
			dialog_ = new JDialog((Frame) window, dialogTitle, true);
		}
		else
		{
			dialog_ = new JDialog((Dialog) window, dialogTitle, true);
		}
		dialog_.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog_.setContentPane(this);

		dialog_.pack();
		dialog_.setLocationRelativeTo(parent);
		dialog_.setVisible(true);

		return !fileList_.getSelectionModel()
						 .isSelectionEmpty();
	}

	/**
	 * Close the dialog if existing.
	 */
	protected void closeDialog()
	{
		if (dialog_ != null)
		{
			dialog_.setVisible(false);
			dialog_.setContentPane(new JPanel());
			dialog_ = null;
		}
	}

	public Path getSelectedPath()
	{
		return selectedPath_ == null ? null : selectedPath_.path_;
	}

	/**
	 * Adds an accessory.
	 *
	 * @param accessory the accessory
	 */
	public void addAccessory(JComponent accessory)
	{
		if (accessories_ == null)
		{
			accessories_ = new JPanel(new GridLayout(0,1));
			accessories_.setBackground(forcedBackground_);
			add(accessories_, BorderLayout.EAST);
		}
		accessory.setBackground(forcedBackground_);
		accessories_.add(accessory);
	}

	/**
	 * Remove an accessory.
	 *
	 * @param accessory the accessory
	 */
	public void removeAccessory(JComponent accessory)
	{
		if (accessories_ != null)
		{
			accessories_.remove(accessory);
			if (accessories_.getComponentCount() == 0)
			{
				remove(accessories_);
				accessories_ = null;
			}
		}
	}

	/**
	 * Adds a file system to the directory tree.
	 *
	 * @param fs   the file-system to show. All roots are added as children.
	 * @param name The name to show on the root node
	 * @param icon The icon to show or null
	 */
	public void addFileSystem(FileSystem fs, String name, Icon icon)
	{
		systemTree_.addFileSystem(fs, name, icon);
	}

}
