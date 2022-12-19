package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

public class CharacterHandler extends StringHandler
{
    @Override
    public void initEditor(PropertyValue<String> value, PropertyEditorComponents pec)
    {
        super.initEditor(value, pec);
        setMaxCharacters(1);
    }

}