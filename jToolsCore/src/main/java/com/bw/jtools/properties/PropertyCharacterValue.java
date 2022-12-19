/*
 * (c) copyright Bernd Wengenroth
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
package com.bw.jtools.properties;

/**
 * Convenience wrapper for a property with a character content.<br>
 * <br>
 * <i>Examples:</i><br>
 * <pre>
 * {@code
 *  PropertyCharacterValue myCharProperty
 *           = new PropertyCharacterValue( "My Character", 'X' );
 * }
 * </pre>
 */
public class PropertyCharacterValue extends PropertyValue<Character>
{

	/**
	 * Constructs a property by value.
	 * Value has to be not null.
	 *
	 * @param key   Key name.
	 * @param value Initial value.
	 */
	public PropertyCharacterValue(String key, Character value)
	{
		super(key, Character.class );
		setValue(value);
	}

}
