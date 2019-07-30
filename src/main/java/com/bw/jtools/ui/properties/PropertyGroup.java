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
import javax.swing.tree.TreeNode;

/**
 * A Property-Group.<br>
 * User can collapse groups, hiding all child-properties.
 */
public class PropertyGroup extends DefaultMutableTreeNode
{
    /**
     * The name of the property to show.
     */
    public String displayName_;

    /**
     * Creates a groups with a name.
     * @param name The name to show.
     */
    public PropertyGroup(String name)
    {
        super( );
        displayName_ = name;
    }

    /**
     * Adds a child property by value.
     * @param name   The name to show.
     * @param value  The value to show. Has to be not null.
     * @return The created node.
     */
    public PropertyValue addProperty( String name, Object value )
    {
        PropertyValue node = new PropertyValue(name, value.getClass() );
        node.setUserObject(value);
        add(node);
        return node;
    }

    /**
     * Adds a child property by class.
     * @param name        The name to show.
     * @param valueClazz  The class of the value to show.
     *                    The value will initial be null.
     * @return The created node.
     */
    public PropertyValue addProperty( String name, Class valueClazz )
    {
        PropertyValue node = new PropertyValue(name, valueClazz );
        add(node);
        return node;
    }

    /**
     * Adds a child property.
     * @param value The new value to add.
     */
    public void addProperty( PropertyValue value )
    {
        add(value);
    }

    /**
     * Gets a property by display name.
     * Please avoid usage as it is possibly expensive.
     * @param name  Display name of the property to search.
     * @return The property or null.
     */
    public PropertyValue getProperty( String name )
    {
        for ( TreeNode node : children)
        {
            if ( ((PropertyValue)node).displayName_.equals(name) )
            {
                return (PropertyValue)node;
            }
        }
        return null;
    }

    /**
     * Convenience getter.<br>
     * Same as (PropertyValue)getChildAt(index).
     * @param index Index of the child-property.
     * @return The property value.
     */
    public PropertyValue getPropertyAt(int index)
    {
        return (PropertyValue)getChildAt(index);
    }

}
