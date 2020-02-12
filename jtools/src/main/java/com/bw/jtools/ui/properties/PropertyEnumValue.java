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
 * Convenience wrapper for a property with an enumeration content.<br>
 * <br>
 * <i>Examples:</i><br>
 * <pre>
 *{@code
 *  enum MyEnum { ONE, TWO, THREE, FOUR }
 *
 *  PropertyEnumValue<MyEnum> myEnumByValue
 *           = new PropertyEnumValue<>( "My Enum", MyEnum.ONE );
 *
 *  PropertyEnumValue<MyEnum> myEnumByClass
 *           = new PropertyEnumValue<>( "My Enum", MyEnum.class );
 *  myEnumByClass.setValue( MyEnum.ONE );
 *}
 * </pre>
 */
public class PropertyEnumValue<E extends Enum> extends PropertyValue
{
    /**
     * Constructs a property by declaring class. Value must not be null.
     * @param name Name of the property that is displayed.
     * @param clazz Value class, in this case an Enum.
     */
    public PropertyEnumValue( String name, Class<E> clazz )
    {
        super( name, clazz );
    }

    /**
     * Constructs a property by value.
     * Value has to be not null.
     * @param name Display name.
     * @param value Initial value.
     */
    public PropertyEnumValue( String name, E value )
    {
        super( name, value.getDeclaringClass() );
        setUserObject(value);
    }

    /**
     * Convenience setter. Same as setUserObject(v).
     * @param v The new value.
     */
    public void setValue( E v )
    {
        setUserObject(v);
    }

    /**
     * Convenience getter.
     * Same as "(E)getUserObject".
     * @return the current value as E.
     */
    public E getValue()
    {
        return (E)getUserObject();
    }

}
