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
 * Convenience wrapper for a property with numeric content.<br>
 * <br>
 * <i>Examples:</i><br>
 * <pre>
 *{@code
 *  PropertyNumberValue int_node   = new PropertyNumberValue( "My Number", 10 );
 *  PropertyNumberValue float_node = new PropertyNumberValue( "My Number", 1.11f );
 *}
 * </pre>
 */
public class PropertyNumberValue extends PropertyValue
{
    /**
     * Creates a numeric property with name and value.
     * @param name Name of the property.
     * @param nb   The number-value. Must not be null.
     */
    public PropertyNumberValue( String name, Number nb )
    {
        super( name, nb.getClass() );
        setUserObject(nb);
    }

    /**
     * Creates a numeric property with name and class.
     * @param name  Name of the property.
     * @param clazz The number-class.
     */
    public PropertyNumberValue( String name, Class<? extends Number> clazz  )
    {
        super( name, clazz );
    }


    /**
     * Convenience setter.Same as setUserObject().
     * @param v The value to set. Can be null.
     */
    public void setValue( Number v )
    {
        setUserObject(v);
    }

    /**
     * Convenience getter.Same as (Number)getUserObject.
     * @return The numeric instance. Can be null.
     */
    public Number getValue()
    {
        return (Number)getUserObject();
    }
}
