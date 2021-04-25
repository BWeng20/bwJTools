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
package com.bw.jtools.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Combination of input field and list.
 *
 * @param <T> The element type to select.
 */
public class JInputList<T> extends JPanel
{
	protected JTextField textfield;
	protected JList<T> list;
	private List<ListSelectionListener> listener_ = new ArrayList<>();
	protected boolean inUpdate = false;

	private InputListAdapter<T> defaultAdapter_ = (item) -> item == null ? "" : item.toString();
	protected InputListAdapter<T> adapter_;

	/**
	 * Creates a input list for items with a usable toString function.
	 */
	public JInputList(Collection<T> values, int numCols, int numRows)
	{
		this(values, numCols, numRows, null);
	}

	/**
	 * Creates an input list with a custom adapter that handles the string representation.
	 */
	public JInputList(Collection<T> values, int numCols, int numRows, InputListAdapter<T> adapter)
	{
		setLayout(new BorderLayout());

		if (adapter == null)
			adapter_ = defaultAdapter_;
		else
			adapter_ = adapter;

		textfield = new JTextField(numCols);
		textfield.addActionListener(e -> handleTextInput());
		textfield.getDocument()
				 .addDocumentListener(new DocumentListener()
				 {
					 @Override
					 public void insertUpdate(DocumentEvent e)
					 {
						 handleTextInput();
					 }

					 @Override
					 public void removeUpdate(DocumentEvent e)
					 {
						 handleTextInput();
					 }

					 @Override
					 public void changedUpdate(DocumentEvent e)
					 {
						 handleTextInput();
					 }
				 });
		add(textfield, BorderLayout.NORTH);

		DefaultListModel<T> model = new DefaultListModel<>();
		for (T v : values)
			model.addElement(v);
		list = new JList<T>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(numRows);
		list.addListSelectionListener(e -> handleSelectionChange());

		add(new JScrollPane(list), BorderLayout.CENTER);
	}

	/**
	 * Sets font for inputfield and list.
	 *
	 * @param f The font.
	 */
	public void setFont(Font f)
	{
		super.setFont(f);
		if (textfield != null)
		{
			textfield.setFont(f);
			list.setFont(f);
		}
	}

	protected void handleSelectionChange()
	{
		if (!inUpdate)
		{
			inUpdate = true;
			try
			{
				String l = adapter_.text(list.getSelectedValue());
				if (l != null)
				{
					textfield.setText(l);
				}
				fireChange(list.getSelectedIndex());
			}
			finally
			{
				inUpdate = false;
			}
		}
	}

	protected void handleTextInput()
	{
		if (!inUpdate)
		{
			inUpdate = true;
			try
			{
				ListModel<T> model = list.getModel();
				int oldIdx = list.getSelectedIndex();
				int idx = -1;
				String key = textfield.getText()
									  .toLowerCase();
				for (int k = 0; k < model.getSize(); k++)
				{
					T data = model.getElementAt(k);
					String dataText = adapter_.text(data);
					if (dataText.toLowerCase()
								.startsWith(key))
					{
						list.setSelectedValue(data, true);
						idx = k;
						break;
					}
				}
				if (idx != oldIdx)
					fireChange(idx);
			}
			finally
			{
				inUpdate = false;
			}
		}
	}

	public void setSelected(T sel)
	{
		ListModel<T> model = list.getModel();
		int N = model.getSize();
		final String selectedText = adapter_.text(sel);
		for (int i = 0; i < N; ++i)
		{
			T item = model.getElementAt(i);
			if (Objects.equals(selectedText, adapter_.text(item)))
			{
				list.setSelectedIndex(i);
				break;
			}
		}
		textfield.setText(selectedText);
	}

	public String getEditedValue()
	{
		return textfield.getText();
	}

	public T getSelectedItem()
	{
		return list.getSelectedValue();
	}

	public void addSelectionListener(ListSelectionListener l)
	{
		if (l != null)
		{
			removeSelectionListener(l);
			listener_.add(l);
		}
	}

	public void removeSelectionListener(ListSelectionListener l)
	{
		if (l != null)
		{
			listener_.remove(l);
		}
	}

	protected void fireChange(int index)
	{
		List<ListSelectionListener> ls = new ArrayList<>(listener_);
		for (ListSelectionListener ll : ls)
		{
			if (ll != null) ll.valueChanged(new ListSelectionEvent(list.getSelectionModel(), index, index, false));
		}
	}

	/**
	 * Sets the list cell renderer used in the list-part.
	 *
	 * @param cellRenderer
	 */
	public void setListCellRenderer(ListCellRenderer cellRenderer)
	{
		list.setCellRenderer(cellRenderer);
	}
}