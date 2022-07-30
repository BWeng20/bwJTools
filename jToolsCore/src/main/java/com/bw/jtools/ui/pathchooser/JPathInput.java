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

import com.bw.jtools.io.IOTool;
import com.bw.jtools.ui.JAutoCompleteTextField;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Input field for manual path selection.
 * Supports autocompletion. Up/Down Arrow key scroll through the available matches.
 */
public class JPathInput extends JPanel
{
	protected PathInfoProvider pathInfoProvider_;
	protected JAutoCompleteTextField input_;
	protected PathAutoCompletionModel pathAutoComAutocompletionModel_;

	public JPathInput(PathInfoProvider pathInfoProvider)
	{
		super(new BorderLayout());
		this.pathInfoProvider_ = pathInfoProvider;
		this.pathAutoComAutocompletionModel_ = new PathAutoCompletionModel(pathInfoProvider);
		this.input_ = new JAutoCompleteTextField(this.pathAutoComAutocompletionModel_);
		this.input_.setColumns(30);

		this.input_.addActionListener(e ->
		{
			fireSelectionChanged(true);
		});

		add(this.input_, BorderLayout.CENTER);
	}

	public void setPath(PathInfo pi)
	{
		final Path p = pi.getPath();

		if (p.getFileSystem() == FileSystems.getDefault())
		{
			input_.setText(p.toString());
		}
		else
		{
			input_.setText(p.toUri()
							.toString());
		}
	}

	public Path getSelectedPath()
	{
		String i = input_.getText()
						 .trim();
		return IOTool.getPath(i);
	}

	public PathInfo getSelectedPathInfo()
	{
		Path p = getSelectedPath();
		return p == null ? null : pathInfoProvider_.createPathInfo(p);
	}


	protected void fireSelectionChanged(boolean finalSelection)
	{
		PathInfo selectedValue = pathInfoProvider_.createPathInfo(getSelectedPath());

		if (selectedValue != null)
		{
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
	}


	public void addPathSelectionListener(PathSelectionListener listener)
	{
		listenerList.add(PathSelectionListener.class, listener);
	}

	public void removePathSelectionListener(PathSelectionListener listener)
	{
		listenerList.remove(PathSelectionListener.class, listener);
	}

}
