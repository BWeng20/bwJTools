package com.bw.jtools.ui;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for "chooser" buttons that opens dialogs to edit some option.
 *
 * @see JFontButton
 * @see JColorChooserButton
 */
public abstract class JChooserButtonBase<E> extends JButton
{
	private String dialogTitle_;
	private List<ItemListener> listeners_ = new ArrayList<>();
	private E currentValue_;

	protected JChooserButtonBase(String dialogTitle)
	{
		dialogTitle_ = dialogTitle;

		addActionListener((ae) ->
				{
					E f = showChooserDialog();
					if (f != null && !f.equals(currentValue_))
					{
						currentValue_ = f;
						setValue(currentValue_);
						fireSelectionChanged(currentValue_);
					}
				}
		);


	}

	protected abstract E showChooserDialog();

	/**
	 * Gets the dialog title of the chooser dialog.
	 *
	 * @return The dialog title
	 */
	public String getDialogTitle()
	{
		return dialogTitle_;
	}

	/**
	 * Sets the dialog title of the chooser dialog.
	 *
	 * @param title
	 */
	public void setDialogTitle(String title)
	{
		dialogTitle_ = title;
	}

	/**
	 * Gets the current value;
	 */
	public E getValue()
	{
		return currentValue_;
	}

	/**
	 * Sets the current value.
	 */
	public void setValue(E v)
	{
		currentValue_ = v;
	}

	/**
	 * Adds an ItemListener. The listener is called if the font is changed.
	 *
	 * @param l The listener
	 */
	public void addItemListener(ItemListener l)
	{
		if (l != null)
		{
			removeSelectionListener(l);
			listeners_.add(l);
		}
	}

	/**
	 * Removed an ItemListener.
	 *
	 * @param l The listener
	 */
	public void removeSelectionListener(ItemListener l)
	{
		if (l != null)
		{
			listeners_.remove(l);
		}
	}

	protected void fireSelectionChanged(E value)
	{
		List<ItemListener> ll = new ArrayList<>(listeners_);
		for (ItemListener l : ll)
			if (l != null) l.itemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
					value, ItemEvent.SELECTED));
	}

}
