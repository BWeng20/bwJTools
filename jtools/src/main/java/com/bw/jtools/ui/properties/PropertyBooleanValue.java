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
package com.bw.jtools.ui.properties;

/**
 * Convenience wrapper for a property with Boolean content.
 * Boolean values are shown as combo-box or as checkbox, depending
 * on "nullability".
 *
 */
public class PropertyBooleanValue extends PropertyValue
{
    /**
     * Creates a new Boolean value.
     * @param name Name to display.
     * @param value The initial value. Can be null.
     */
    public PropertyBooleanValue( String name, Boolean value )
    {
        super( name, Boolean.class );
        setUserObject(value);
    }

    /**
     * Convenience setter. Same as setUserObject(v).
     * @param v The new value.
     */
    public void setValue( Boolean v )
    {
        setUserObject(v);
    }

    /**
     * Convenience getter.
     * Same as "(Boolean)getUserObject".
     * If node is not nullable, Boolean.FALSE is returned in case the
     * internal value is still null.
     * @return the current value as Boolean.
     */
    public Boolean getValue()
    {
        Boolean bv = (Boolean)getUserObject();
        if ( nullable_ || bv != null)
            return bv;
        else
        {
            return Boolean.FALSE;
        }
    }

}
