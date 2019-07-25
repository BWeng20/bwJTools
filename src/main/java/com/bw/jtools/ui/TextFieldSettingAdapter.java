/*
 * (c) copyright 2015-2019 Bernd Wengenroth
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. 
 */
package com.bw.jtools.ui;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.bw.jtools.persistence.Store;

/**
 * Adapter to write changed from a text-field to some user-setting.
 */
public class TextFieldSettingAdapter implements DocumentListener
{

    boolean value_set_ = false;
    final String pref_;
    String last_value_;
    JTextField text_;

    public TextFieldSettingAdapter(JTextField f, String pref, String defaultValue )
    {
        text_ = f;
        pref_ = pref;
        last_value_ = Store.getString(pref_, (null != defaultValue) ? defaultValue : "");
        text_.setText(last_value_);
        value_set_ = true;
        text_.getDocument().addDocumentListener(this);
    }

    
    public TextFieldSettingAdapter(JTextField f, String pref)
    {
    	this(f,pref, null );
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        textChanged_();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        textChanged_();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        textChanged_();
    }

    protected void textChanged_()
    {
        if (value_set_)
        {
            final String nv = text_.getText();
            if (nv != null && false == nv.equals(last_value_))
            {
                last_value_ = nv;
                Store.setString(pref_, nv);
            }
        }
    }
}
