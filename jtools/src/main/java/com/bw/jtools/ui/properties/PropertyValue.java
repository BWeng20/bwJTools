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

import javax.swing.tree.DefaultMutableTreeNode;
import java.text.NumberFormat;

/**
 * Node inside the tree that represents a property.<br>
 * This class can be used directly without the custom "PropertyXXXValue" classes,
 * but in this case the caller has to ensure that the value-class is compatible with
 * the content of the user-object.<br>
 * <br>
 * <i>Example:</i><br>
 * <pre>
 *{@code
 *  PropertyValue property = new PropertyValue( "My Number", Integer.class );
 *  node.setUserObject( 10 );
 *}
 * </pre>
 */
public class PropertyValue extends DefaultMutableTreeNode
{
    /**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -7565043274211746142L;

	/**
     * The name of the property to show.
     */
    public String displayName_;

    /**
     * The value-class of this property.
     */
    public Class<?>  valueClazz_;

    /**
     * The format to display numbers.
     * Can be set to support other formats, eg. more fractions digits.
     */
    public NumberFormat nf_ = null;

    /**
     * True if value is nullable.
     */
    public boolean nullable_ = true;

    /**
     * Constructs a new property by name and value-clazz.
     * The value will initially be null.
     * @param name        The name of the property to show.
     * @param valueClazz  The value-class of the property.
     */
    public PropertyValue( String name, Class<?> valueClazz )
    {
        this.displayName_=name;
        this.valueClazz_ = valueClazz;
    }

    /**
     * Checks if the property has some content.
     * Convenience replacement for "getUserObject() != null"
     * @return True if some value exists.
     */
    public boolean hasContent()
    {
        return getUserObject() != null;
    }

}
