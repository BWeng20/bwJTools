package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.JFontButton;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import java.awt.Component;
import java.awt.Font;

public class FontHandler extends ValueTypeHandler<Font>
{

	JFontButton fontb_;

	@Override
	public void initEditor(PropertyValue<Font> value, PropertyEditorComponents pec)
	{
		value_ = value;

		if (fontb_ == null)
		{
			fontb_ = new JFontButton();
			fontb_.addItemListener(e -> updatePropertyFromEditor());
		}
		fontb_.setValue((Font) value.getValue());
	}

	public Font getCurrentValueFromEditor()
	{
		return fontb_.getValue();

	}

	public void updateEditorFromProperty()
	{
		fontb_.setValue(value_.getValue());
	}

	@Override
	public Component getComponent()
	{
		return fontb_;
	}

}