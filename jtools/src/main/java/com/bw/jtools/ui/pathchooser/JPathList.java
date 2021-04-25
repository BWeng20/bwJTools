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
import com.bw.jtools.ui.pathchooser.impl.PathInfoListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Shows the list of files and folders inside a folder.
 */
public class JPathList extends JComponent
{
	protected DefaultListModel<PathInfo> listModel_ = new DefaultListModel<>();
	protected JList<PathInfo> list_ = new JList<>(listModel_);
	protected PathInfo currentDirectory_;

	protected PathInfoProvider pathInfoProvider_;

	protected PathInfoListCellRenderer cellRenderer_;

	@Override
	public void updateUI()
	{
		super.updateUI();
		cellRenderer_.installBorders();
	}

	public JPathList(PathInfoProvider pathInfoProvider_)
	{
		super();
		this.pathInfoProvider_ = pathInfoProvider_;
		this.cellRenderer_ = new PathInfoListCellRenderer(pathInfoProvider_);
		setLayout(new BorderLayout());

		list_.putClientProperty("List.isFileList", Boolean.TRUE);
		list_.setCellRenderer(cellRenderer_);
		list_.setFixedCellHeight(-1);
		add(new JScrollPane(list_), BorderLayout.CENTER);
		setPreferredSize(new Dimension(800, 500));

		UIToolSwing.addAction(list_, new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				fireSelectionChanged(true);
			}
		}, "ENTER");

		list_.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					fireSelectionChanged(true);
				}
			}
		});

		list_.addListSelectionListener(e ->
		{
			fireSelectionChanged(false);
		});

	}

	public void setFont(Font f)
	{
		super.setFont(f);
		list_.setFont(f); //< To trigger UI to update row heights.
		cellRenderer_.setFont(f);
	}

	protected void fireSelectionChanged(boolean finalSelection)
	{
		PathInfo selectedValue = list_.getSelectedValue();

		Object[] listeners = listenerList.getListenerList();
		PathSelectionEvent ev = new PathSelectionEvent(selectedValue, finalSelection);
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

	/**
	 * Sets the folder to show content for.
	 */
	public void setDirectory(PathInfo p)
	{
		if (!Objects.equals(currentDirectory_, p))
		{
			currentDirectory_ = p;
			listModel_.clear();
			if (currentDirectory_ != null)
			{
				try
				{
					DirectoryStream<Path> dir = pathInfoProvider_.getDirectoryStream(currentDirectory_);
					List<PathInfo> l = new ArrayList<>();
					for (Path file : dir)
					{
						PathInfo pi = pathInfoProvider_.createPathInfo(currentDirectory_, file);
						if (pi.isReadable())
							l.add(pi);
					}
					listModel_.addAll(l);
				}
				catch (Exception e)
				{
					Log.error(getClass().getName() + ".setDirectory", e);
				}
			}
		}
	}

	public ListSelectionModel getSelectionModel()
	{
		return list_.getSelectionModel();
	}

	public ListModel<PathInfo> getModel()
	{
		return listModel_;
	}

	public PathInfo getSelectedPath()
	{
		return list_.getSelectedValue();
	}

	public void clearSelection()
	{
		list_.clearSelection();
	}

}
