package com.bw.jtools.ui.properties.sheet;

import com.bw.jtools.properties.PropertyValue;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.text.ParseException;

public class JPropertyTextField extends JTextField
{
	public JPropertyTextField(PropertyValue value)
	{
		this.value_ = value;
		nfEdit_ = NumberFormat.getInstance();
		nfEdit_.setGroupingUsed(false);
		nfDisplay_ = NumberFormat.getInstance();
		setText(getDisplayValue());
	}

	protected PropertyValue value_;
	protected NumberFormat nfDisplay_;
	protected NumberFormat nfEdit_;


	@Override
	protected void processFocusEvent(FocusEvent e)
	{
		super.processFocusEvent(e);

		if (e.isTemporary())
		{
			return;
		}

		if (e.getID() == FocusEvent.FOCUS_LOST)
		{
			updateCurrentValue();
			setText(getDisplayValue());
		}
		else if (e.getID() == FocusEvent.FOCUS_GAINED)
		{
			setText(getEditValue());
		}
	}

	protected void updateCurrentValue()
	{
		Object newUserObject = value_.getPayload();

		if (value_.valueClazz_ == String.class)
		{
			String text = getText();
			if (text.isEmpty() && value_.nullable_)
				newUserObject = null;
			else
				newUserObject = text;
		}
		else if (Number.class.isAssignableFrom(value_.valueClazz_))
		{
			Number nb;
			String txt = getText().trim();
			try
			{
				NumberFormat nf = nfEdit_;
				if (value_ != null && value_.nf_ != null)
					nf = value_.nf_;
				nb = nf.parse(getText());

			}
			catch (ParseException e)
			{
				nb = null;
			}

			if (nb != null)
			{
				newUserObject = value_.scaleNumber(nb);
			}
			else if (value_.nullable_)
			{
				newUserObject = null;
			}
		}
		value_.setPayload(newUserObject);
	}

	protected String getDisplayValue()
	{

		if (Number.class.isAssignableFrom(value_.valueClazz_))
		{
			Number i = (Number) value_.getPayload();
			if (i != null)
				return (value_.nf_ == null ? nfDisplay_ : value_.nf_).format(i);
		}
		else
		{
			if (value_.hasContent())
				return String.valueOf(value_.getPayload());
		}
		return "";
	}

	protected String getEditValue()
	{

		if (Number.class.isAssignableFrom(value_.valueClazz_))
		{
			Number i = (Number) value_.getPayload();
			if (i != null)
			{
				NumberFormat nf = (value_.nf_ == null ? nfEdit_ : value_.nf_);
				boolean gu = nf.isGroupingUsed();
				if (gu) nf.setGroupingUsed(false);
				String v = nf.format(i);
				if (gu) nf.setGroupingUsed(true);
				return v;
			}
		}
		else
		{
			if (value_.hasContent())
				return String.valueOf(value_.getPayload());
		}
		return "";
	}
}
