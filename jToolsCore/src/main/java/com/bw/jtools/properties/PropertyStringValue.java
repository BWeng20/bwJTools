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
 * Convenience wrapper for a property with string content.<br>
 * <br>
 * <i>Examples:</i><br>
 * <pre>
 * {@code
 *  PropertyStringValue propValue = new PropertyStringValue( "My Name", "Alf" );
 * }
 * </pre>
 */
public class PropertyStringValue extends PropertyValue<String>
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 5599878965239413140L;

	/**
	 * Creates a string property with name and value.
	 *
	 * @param key   Key of the property.
	 * @param value The number-value. Can be null.
	 */
	public PropertyStringValue(String key, String value)
	{
		super(key, String.class);
		setValue(value);
	}
}
