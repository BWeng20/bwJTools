package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyListValue;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ListHandler extends ValueTypeHandler<List<?>>
{
    private JButton edit_;
    private List<?> editedValue_;

    @Override
    public void initEditor(PropertyValue<List<?>> value, PropertyEditorComponents pec)
    {
        value_ = value;

        editedValue_ = value.getValue();
        if (editedValue_ == null) editedValue_ = new ArrayList<>();

        if (edit_ == null)
        {
            edit_ = new JButton();
            edit_.addActionListener(ie ->
                    {
                        PropertyListValue<?> mvalue = (PropertyListValue) value_;
                        List<?> newValue = ListEditor.showDialog(edit_, mvalue.key_, (List) mvalue.getValue(),
                                mvalue.getListValueClass());
                        if (newValue != null)
                        {
                            editedValue_ = newValue;
                            mvalue.setValue((List) editedValue_);
                            edit_.setText(editedValue_.toString());
                        }
                    }
            );
        }
        edit_.setText(editedValue_.toString());
    }

    @Override
    public List<?> getCurrentValueFromEditor()
    {
        return editedValue_;
    }

    @Override
    public void updateEditorFromProperty()
    {
        List<?> list = value_.getValue();
        if (list == null) list = new ArrayList<>();
        edit_.setText(list.toString());
    }

    @Override
    public Component getComponent()
    {
        return edit_;
    }

}