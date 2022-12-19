package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyMapValue;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.JButton;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class MapHandler extends ValueTypeHandler
{
	private JButton edit_;
	private Map editedValue_;

	@Override
	public void initEditor(PropertyValue value, PropertyEditorComponents pec)
	{
		value_ = value;

		editedValue_ = (Map) value.getValue();
		if (editedValue_ == null) editedValue_ = new HashMap();

		if (edit_ == null)
		{
			edit_ = new JButton();
			edit_.addActionListener(ie ->
					{
						PropertyMapValue mvalue = (PropertyMapValue) value_;
						Map newValue = MapEditor.showDialog(edit_, mvalue.key_, (Map) mvalue.getValue());
						if (newValue != null)
						{
							editedValue_ = newValue;
							mvalue.setValue(editedValue_);
							edit_.setText(editedValue_.toString());
						}
					}
			);
		}
		edit_.setText(editedValue_.toString());
	}

	@Override
	public Object getCurrentValueFromEditor()
	{
		return editedValue_;
	}

	@Override
	public void updateEditorFromProperty()
	{
		Map map = (Map) value_.getValue();
		if (map == null) map = new HashMap<>();
		edit_.setText(map.toString());
	}

	@Override
	public Component getComponent()
	{
		return edit_;
	}

}