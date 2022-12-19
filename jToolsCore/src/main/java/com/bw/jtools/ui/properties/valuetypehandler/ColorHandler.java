package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.JColorChooserButton;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import java.awt.Color;
import java.awt.Component;

public class ColorHandler extends ValueTypeHandler<Color>
{

	JColorChooserButton color_;

	@Override
	public void initEditor(PropertyValue<Color> value, PropertyEditorComponents pec)
	{
		value_ = value;
		Color c = value.getValue();
		if (c == null) c = Color.BLACK;

		if (color_ == null)
		{
			color_ = new JColorChooserButton();
			color_.setFont(pec.getFont());
			color_.addItemListener((ie) -> updatePropertyFromEditor());
		}
		color_.setValue(c);
	}

	public Color getCurrentValueFromEditor()
	{
		return color_.getValue();

	}

	public void updateEditorFromProperty()
	{
		Color c = value_.getValue();
		if (c == null) c = Color.BLACK;
		color_.setValue(c);
	}

	@Override
	public Component getComponent()
	{
		return color_;
	}
}