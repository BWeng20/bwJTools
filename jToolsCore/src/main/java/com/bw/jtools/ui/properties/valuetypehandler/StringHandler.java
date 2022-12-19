package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.JLimitedText;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import java.awt.*;

public class StringHandler extends ValueTypeHandler<String>
{
    private JLimitedText text_;
    private int maxCharacters_ = -1;

    @Override
    public void initEditor(PropertyValue<String> value, PropertyEditorComponents pec)
    {

        value_ = value;
        if (text_ == null)
        {
            text_ = new JLimitedText();
            text_.setMaxCharacters(maxCharacters_);
            text_.setFont(pec.getFont());
            text_.addActionListener((actionEvent) -> updatePropertyFromEditor()
            );
        }
        if (value.hasContent())
            text_.setText(String.valueOf(value.getValue()));
        else
            text_.setText("");
    }

    public String getCurrentValueFromEditor()
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

    public void setMaxCharacters(int limit)
    {
        if (maxCharacters_ != limit)
        {
            maxCharacters_ = limit;
            if (text_ != null)
            {
                text_.setMaxCharacters(maxCharacters_);
            }
        }
    }

}