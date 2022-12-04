package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.JComboBox;
import java.awt.Component;
import java.util.Map;
import java.util.Objects;

public class ChoiceHandler extends ValueTypeHandler
{
	JComboBox<String> choice_;

	@Override
	public void initEditor(PropertyValue value, PropertyEditorComponents pec)
	{
		value_ = value;

		if (value.possibleValues_ != null)
		{
			if (choice_ == null)
			{
				choice_ = new JComboBox<>();
				choice_.setFont(pec.getFont());
				// Force UI to act as Cell editor (mainly to use a different selection/focus
				// handling)
				choice_.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
				choice_.addItemListener(e -> updatePropertyFromEditor());
			}


			choice_.removeAllItems();

			Object v = value.getValue();
			if (value.nullable_)
			{
				choice_.addItem(null);
				if (v == null)
					choice_.setSelectedIndex(0);
			}

			for (Map.Entry<String, Object> entry : ((Map<String, Object>) value_.possibleValues_).entrySet())
			{
				choice_.addItem(entry.getKey());
				if (Objects.equals(entry.getValue(), v))
				{
					choice_.setSelectedIndex(choice_.getItemCount() - 1);
				}
			}
		}
		else
			throw new IllegalArgumentException("Property with list of possible values needed");
	}

	public Object getCurrentValueFromEditor()
	{
		String v = (String) choice_.getSelectedItem();
		return v == null ? null : value_.possibleValues_.get(v);
	}

	public void updateEditorFromProperty()
	{
		if (value_.possibleValues_ != null)
		{
			Object v = value_.getValue();
			if (value_.nullable_ && v == null)
			{
				choice_.setSelectedIndex(0);
			}
			else
			{
				Object val = value_.getValue();
				for (Map.Entry<String, Object> entry : ((Map<String, Object>) value_.possibleValues_).entrySet())
				{
					if (Objects.equals(entry.getValue(), val))
					{
						choice_.setSelectedItem(entry.getKey());
						break;
					}
				}
			}
		}
	}

	@Override
	public Component getComponent()
	{
		return choice_;
	}

}