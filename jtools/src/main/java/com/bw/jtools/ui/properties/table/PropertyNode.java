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
package com.bw.jtools.ui.properties.table;

import com.bw.jtools.properties.PropertyChangeListener;
import com.bw.jtools.properties.PropertyValue;

import javax.swing.tree.DefaultMutableTreeNode;

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
 *  property.setUserObject( 10 );
 *}
 * </pre>
 */
public class PropertyNode extends DefaultMutableTreeNode
{
    /**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -7565043274211746142L;

	public PropertyValue property_;

    /**
     * Constructs a new node for a property.
     * @param property  The property.
     */
    public PropertyNode(PropertyValue property )
    {
        this.property_ = property;
    }

    /**
     * Checks if the property has some content.
     * Convenience replacement for "getUserObject() != null"
     * @return True if some value exists.
     */
    public boolean hasContent()
    {
        return property_ != null && property_.hasContent();
    }
}
