package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import java.awt.Component;
import java.util.Objects;

public abstract class ValueTypeHandler
{
	protected PropertyValue value_;

	public abstract void initEditor(PropertyValue value, PropertyEditorComponents pec);

	public abstract Object getCurrentValueFromEditor();

	public abstract void updateEditorFromProperty();

	public abstract Component getComponent();

	public void updatePropertyFromEditor()
	{
		if (value_ != null)
		{
			Object newUserObject = getCurrentValueFromEditor();

			if (value_.nullable_ || newUserObject != null)
			{
				boolean changed = !Objects.equals(newUserObject, value_.getValue());
				if (changed)
				{
					value_.setValue(newUserObject);
				}
			}
		}
	}

}
