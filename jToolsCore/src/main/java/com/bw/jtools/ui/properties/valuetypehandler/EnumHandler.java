package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.JComboBox;
import java.awt.Component;

public class EnumHandler extends ValueTypeHandler<Enum<?>>
{
	JComboBox<Enum> enums_;

	@Override
	public void initEditor(PropertyValue<Enum<?>> value, PropertyEditorComponents pec)
	{
		value_ = value;

		if (enums_ == null)
		{
			enums_ = new JComboBox<>();
			enums_.setFont(pec.getFont());
			// Force UI to act as Cell editor (mainly to use a different selection/focus
			// handling)
			enums_.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
			enums_.addItemListener(e -> updatePropertyFromEditor());
		}
		boolean oldIsInitializing = isInitializing;
		isInitializing = true;
		enums_.removeAllItems();
		if (value.nullable_)
			enums_.addItem(null);

		Enum[] vals = value.valueClazz_.getEnumConstants();
		for (Enum v : vals)
			enums_.addItem(v);
		enums_.setSelectedItem(value.getValue());
		isInitializing = oldIsInitializing;
	}

	public Enum getCurrentValueFromEditor()
	{
		return (Enum)enums_.getSelectedItem();

	}

	public void updateEditorFromProperty()
	{
		enums_.setSelectedItem(value_.getValue());
	}

	@Override
	public Component getComponent()
	{
		return enums_;
	}

}