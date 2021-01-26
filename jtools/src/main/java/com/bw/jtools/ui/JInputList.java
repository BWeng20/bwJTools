package com.bw.jtools.ui;

import javax.print.attribute.standard.JobKOctets;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

class JInputList extends JPanel
{
	protected JTextField textfield;
	protected JList list;

	public JInputList(Collection<String> values, int numCols) {
		setLayout(new BorderLayout());
		textfield = new JTextField(numCols);
		textfield.addActionListener(e ->
		{
			ListModel model = list.getModel();
			String key = textfield.getText().toLowerCase();
			for (int k = 0; k < model.getSize(); k++) {
				String data = (String) model.getElementAt(k);
				if (data.toLowerCase().startsWith(key)) {
					list.setSelectedValue(data, true);
					break;
				}
			}
		});
		textfield.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				fireChange();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				fireChange();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				fireChange();
			}
		});
		add(textfield,BorderLayout.NORTH);

		DefaultListModel model = new DefaultListModel();
		model.addAll(values);
		list = new JList(model);
		list.setVisibleRowCount(4);
		list.addListSelectionListener(e -> {
			String l = (String)list.getSelectedValue();
			if ( l != null )
			{
				textfield.setText(l);
			}
		});

		add(new JScrollPane(list),BorderLayout.CENTER);
	}

	public void setSelected(String sel) {
		list.setSelectedValue(sel, true);
		textfield.setText(sel);
	}

	public String getSelected() {
		return textfield.getText();
	}

	private void fireChange()
	{

	}


}