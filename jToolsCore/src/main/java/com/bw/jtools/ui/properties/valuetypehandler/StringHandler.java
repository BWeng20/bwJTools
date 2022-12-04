package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.JTextField;
import java.awt.Component;

public class StringHandler extends ValueTypeHandler
{

	PropertyValue value_;
	JTextField text_;

	@Override
	public void initEditor(PropertyValue value, PropertyEditorComponents pec)
	{

		value_ = value;
		if (text_ == null)
		{
			text_ = new JTextField();
			text_.setFont(pec.getFont());
			text_.addActionListener((actionEvent) -> updatePropertyFromEditor()
			);
		}
		if (value.hasContent())
			text_.setText(String.valueOf(value.getValue()));
		else
			text_.setText("");
	}

	public Object getCurrentValueFromEditor()
	{
		String text = text_.getText();
		if (text.isEmpty() && value_.nullable_)
			return null;
		else
			return text;

	}

	public void updateEditorFromProperty()
	{
		if (value_.hasContent())
			text_.setText(String.valueOf(value_.getValue()));
		else
			text_.setText("");
	}

	@Override
	public Component getComponent()
	{
		return text_;
	}

}