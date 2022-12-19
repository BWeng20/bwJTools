package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.Component;

public class BooleanHandler extends ValueTypeHandler<Boolean>
{
	private JComboBox<Boolean> booleanNullable_;
	private JCheckBox booleanCheckbox_;

	@Override
	public void initEditor(PropertyValue<Boolean> value, PropertyEditorComponents pec)
	{
		Boolean val =  value.getValue();
		value_ = value;
		if (value.nullable_)
		{
			if (booleanNullable_ == null)
			{
				booleanNullable_ = new JComboBox<>();
				booleanNullable_.addItem(null);
				booleanNullable_.addItem(Boolean.TRUE);
				booleanNullable_.addItem(Boolean.FALSE);
				booleanNullable_.setFont(pec.getFont());
				booleanNullable_.addItemListener(e -> updatePropertyFromEditor());
			}
			booleanNullable_.setSelectedItem(val);
		}
		else
		{
			if (booleanCheckbox_ == null)
			{
				booleanCheckbox_ = new JCheckBox();
				booleanCheckbox_.setFont(pec.getFont());
				booleanCheckbox_.setOpaque(false);
				booleanCheckbox_.addItemListener(e -> updatePropertyFromEditor());
			}

			booleanCheckbox_.setSelected(val != null && val);
		}

	}

	public Boolean getCurrentValueFromEditor()
	{
		Boolean newBool;
		if (value_.nullable_)
		{
			newBool = (Boolean) booleanNullable_.getSelectedItem();
		}
		else
		{
			newBool = booleanCheckbox_.isSelected();
		}
		return newBool;
	}

	public void updateEditorFromProperty()
	{
		Boolean val = value_.getValue();
		if (value_.nullable_)
		{
			booleanNullable_.setSelectedItem(val);
		}
		else
		{
			booleanCheckbox_.setSelected(val != null && val);
		}
	}

	@Override
	public Component getComponent()
	{
		if (value_.nullable_)
		{
			return booleanNullable_;
		}
		else
		{
			return booleanCheckbox_;
		}
	}
}
