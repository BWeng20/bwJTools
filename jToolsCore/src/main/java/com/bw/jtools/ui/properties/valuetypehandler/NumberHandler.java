package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.JTextField;
import java.awt.Component;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberHandler extends ValueTypeHandler
{

	NumberFormat nf_;
	JTextField text_;

	@Override
	public void initEditor(PropertyValue value, PropertyEditorComponents pec)
	{
		value_ = value;

		if (text_ == null)
		{
			text_ = new JTextField();
			text_.setFont(pec.getFont());
			text_.addActionListener((actionEvent) -> updatePropertyFromEditor());
		}
		nf_ = (value.nf_ == null ? pec.getNumberFormat() : value.nf_);
		Number i = (Number) value.getValue();
		if (i != null)
		{
			boolean gu = nf_.isGroupingUsed();
			if (gu) nf_.setGroupingUsed(false);
			text_.setText(nf_.format(i));
			if (gu) nf_.setGroupingUsed(true);
		}
		else
			text_.setText("");
	}

	public Object getCurrentValueFromEditor()
	{
		try
		{
			NumberFormat nf = nf_;
			if (value_ != null && value_.nf_ != null)
				nf = value_.nf_;
			Number nb = nf.parse(text_.getText());
			if (value_ != null)
			{
				if (value_.valueClazz_ == Integer.class)
					return nb.intValue();
				else if (value_.valueClazz_ == Double.class)
					return nb.doubleValue();
				else if (value_.valueClazz_ == Long.class)
					return nb.longValue();
				else if (value_.valueClazz_ == Short.class)
					return nb.shortValue();
				else if (value_.valueClazz_ == Byte.class)
					return nb.byteValue();
				else if (value_.valueClazz_ == Float.class)
					return nb.floatValue();
			}
			return nb;
		}
		catch (ParseException e)
		{
			if (value_.nullable_)
				return null;
			else
				return value_.getValue();
		}
	}

	public void updateEditorFromProperty()
	{
		Number i = (Number) value_.getValue();
		if (i != null)
		{
			NumberFormat nf = (value_.nf_ == null ? nf_ : value_.nf_);
			boolean gu = nf.isGroupingUsed();
			if (gu) nf.setGroupingUsed(false);
			text_.setText(nf.format(i));
			if (gu) nf.setGroupingUsed(true);
		}
		else
			text_.setText("");
	}

	@Override
	public Component getComponent()
	{
		return text_;
	}

}
