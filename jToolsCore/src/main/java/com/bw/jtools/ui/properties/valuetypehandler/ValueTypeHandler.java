package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import java.awt.Component;
import java.util.Objects;

public abstract  class ValueTypeHandler<T>
{
	protected PropertyValue<T> value_;
	protected boolean isInitializing = false;

	public abstract void initEditor(PropertyValue<T> value, PropertyEditorComponents pec);

	public abstract T getCurrentValueFromEditor();

	public abstract void updateEditorFromProperty();

	public abstract Component getComponent();

	public void updatePropertyFromEditor()
	{
		if (value_ != null && !isInitializing)
		{
			T newUserObject = getCurrentValueFromEditor();

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
