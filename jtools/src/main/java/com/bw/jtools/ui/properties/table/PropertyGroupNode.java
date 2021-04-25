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

import com.bw.jtools.collections.TransformedIterator;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyValue;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Iterator;

/**
 * A Property-Group.<br>
 * User can collapse groups, hiding all child-properties.
 */
public class PropertyGroupNode extends DefaultMutableTreeNode implements Iterable<PropertyNode>
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 1582128396793964999L;

	protected PropertyGroup group_;

	/**
	 * Creates a node from the groups.
	 *
	 * @param group The group.
	 */
	public PropertyGroupNode(PropertyGroup group)
	{
		super();
		this.group_ = group;
		if (group != null)
			for (PropertyValue value : group)
				addProperty(value);
	}

	/**
	 * Adds a child property.
	 *
	 * @param value The new value to add.
	 */
	public void addProperty(PropertyValue value)
	{
		add(new PropertyNode(value));
	}

	/**
	 * Gets a property by display name.
	 * Please avoid usage as it is possibly expensive.
	 *
	 * @param name Display name of the property to search.
	 * @return The property or null.
	 */
	public PropertyNode getProperty(String name)
	{
		for (TreeNode node : children)
		{
			if (((PropertyNode) node).property_.displayName_.equals(name))
			{
				return (PropertyNode) node;
			}
		}
		return null;
	}

	/**
	 * Convenience getter.<br>
	 * Same as (PropertyValue)getChildAt(index).
	 *
	 * @param index Index of the child-property.
	 * @return The property value.
	 */
	public PropertyNode getPropertyAt(int index)
	{
		return (PropertyNode) getChildAt(index);
	}

	@Override
	public Iterator<PropertyNode> iterator()
	{
		return new TransformedIterator<TreeNode, PropertyNode>(children.iterator(), item -> (PropertyNode) item);
	}

	public Iterator<PropertyValue> values()
	{
		return new TransformedIterator<TreeNode, PropertyValue>(children.iterator(), item -> ((PropertyNode) item).property_);
	}
}
