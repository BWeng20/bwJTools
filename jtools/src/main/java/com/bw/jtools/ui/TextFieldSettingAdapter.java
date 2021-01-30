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

import com.bw.jtools.persistence.Store;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Adapter to write changes from a text-field to some preference-setting.<br>
 * Can be used in custom setting panels to ease handling of update/store.<br>
 * The values are retrieved and stored via class {@link Store Store}.<br>
 * The value is updated each time the value of the text-field is changed by user.
 * Store is not flushed, so the caller has to ensure that the Store is written to
 * storage if needed. Typically this is done on application exit.<br>
 * <i>Example</i>
 * <pre><code>
 *  JTextField tfAge = new JTextField(3);
 *  new TextFieldSettingAdapter( tfAge, "Age" );
 *  panel.add( tfAge );
 *  JTextField tfZIP = new JTextField(10);
 *  new TextFieldSettingAdapter( tfZIP, "ZIP" );
 *  panel.add( tfZIP );
 * </code></pre>
 * The code above will initialize the text-fields
 * from the current values for preference-keys "Age" and "ZIP".<br>
 * If the user change one of these values, the value in preferences
 * will automatically be updated. 
 */
public class TextFieldSettingAdapter implements DocumentListener
{
    protected boolean value_set_ = false;
    protected final String pref_;
    protected String last_value_;
    protected JTextField text_;

    /**
     * Creates an adapter to load and store preference "pref" into/from the given text-field.
     * @param f The text field to connect to.
     * @param pref The Preference key to load and store the value.
     * @param defaultValue The default to use if Store doesn't have a value yet.
     */
    public TextFieldSettingAdapter(JTextField f, String pref, String defaultValue )
    {
        text_ = f;
        pref_ = pref;
        last_value_ = Store.getString(pref_, (null != defaultValue) ? defaultValue : "");
        text_.setText(last_value_);
        value_set_ = true;
        text_.getDocument().addDocumentListener(this);
    }

    /**
     * Creates an adapter to load and store preference "pref" into/from the given text-field.
     * @param f The text field to connect to.
     * @param pref The Preference key to load and store the value.
     */
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
