package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.paintchooser.JPaintChooserButton;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;

public class PaintHandler extends ValueTypeHandler<Paint>
{
	JPaintChooserButton paint_;

	@Override
	public void initEditor(PropertyValue<Paint> value, PropertyEditorComponents pec)
	{
		value_ = value;

		Paint p = value.getValue();
		if (p == null) p = Color.BLACK;

		if (paint_ == null)
		{
			paint_ = new JPaintChooserButton();
			paint_.setFont(pec.getFont());
			paint_.addItemListener((ie) ->
					{
						Paint newPaint = (Paint) ie.getItem();
						if (value_ != null)
						{
							value_.setValue(newPaint);
						}
					}
			);
		}
		paint_.setValue(p);
	}

	public Paint getCurrentValueFromEditor()
	{
		return paint_.getValue();
	}

	public void updateEditorFromProperty()
	{
		Paint p = value_.getValue();
		if (p == null) p = Color.BLACK;
		paint_.setValue(p);
	}

	@Override
	public Component getComponent()
	{
		return paint_;
	}

}