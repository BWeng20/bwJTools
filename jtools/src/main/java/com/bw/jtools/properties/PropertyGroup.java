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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A Property-Group.<br>
 */
public class PropertyGroup implements Iterable<PropertyValue>
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 1582128396793964999L;

	/**
	 * The name of the property group to show.
	 */
	public final String displayName_;

	/**
	 * The properties of this group.
	 */
	protected List<PropertyValue> values_ = new ArrayList<>();

	/**
	 * List of change listeners that are handled via group.<br>
	 */
	private List<PropertyChangeListener> propertyChangeListener_;


	/**
	 * Creates a groups with a name.
	 *
	 * @param name The name to show.
	 */
	public PropertyGroup(String name)
	{
		super();
		displayName_ = name;
	}

	/**
	 * Adds a property by value.
	 *
	 * @param name  The name to show.
	 * @param value The value to show. Has to be not null.
	 * @return The created node.
	 */
	public PropertyValue addProperty(String name, Object value)
	{
		PropertyValue prop = new PropertyValue(name, value.getClass());
		prop.setPayload(value);
		addProperty(prop);
		return prop;
	}

	/**
	 * Adds a property by class.
	 *
	 * @param name       The name to show.
	 * @param valueClazz The class of the value to show.
	 *                   The value will initial be null.
	 * @return The created node.
	 */
	public PropertyValue addProperty(String name, Class<?> valueClazz)
	{
		PropertyValue value = new PropertyValue(name, valueClazz);
		addProperty(value);
		return value;
	}

	/**
	 * Adds a property from the group.
	 *
	 * @param value The new value to add.
	 */
	public void addProperty(PropertyValue value)
	{
		if (propertyChangeListener_ != null)
			for (PropertyChangeListener l : propertyChangeListener_)
				value.addPropertyChangeListener(l);
		values_.add(value);
	}

	/**
	 * Removes a property to the group.
	 *
	 * @param value The value to remove.
	 */
	public void removeProperty(PropertyValue value)
	{
		if (values_.remove(value) && (propertyChangeListener_ != null))
			for (PropertyChangeListener l : propertyChangeListener_)
				value.removePropertyChangeListener(l);
	}

	/**
	 * Gets a property by display name.
	 * Please avoid usage as it is possibly expensive.
	 *
	 * @param name Display name of the property to search.
	 * @return The property or null.
	 */
	public PropertyValue getProperty(String name)
	{
		for (PropertyValue value : values_)
		{
			if (value.displayName_.equals(name))
			{
				return value;
			}
		}
		return null;
	}

	/**
	 * Gets size of this group, equal the number of properties in this group.
	 *
	 * @return The number of properties.
	 */
	public int size()
	{
		return values_.size();
	}

	/**
	 * Adds a listener to all properties of this group.
	 * Also properties that are later added are considered.
	 *
	 * @param l The listener.
	 */
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		removePropertyChangeListener(l);
		if (propertyChangeListener_ == null)
			propertyChangeListener_ = new ArrayList<>();
		propertyChangeListener_.add(l);
		for (PropertyValue v : values_)
			v.addPropertyChangeListener(l);
	}

	/**
	 * Removes a change listener.
	 *
	 * @param l The listener.
	 */
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		if (propertyChangeListener_ != null)
		{
			if (propertyChangeListener_.remove(l))
			{
				for (PropertyValue v : values_)
					v.removePropertyChangeListener(l);
			}
		}
	}

	/**
	 * Gets a property by index.
	 *
	 * @param index Index of the property.
	 * @return The property value.
	 */
	public PropertyValue getPropertyAt(int index)
	{
		return values_.get(index);
	}

	@Override
	public Iterator<PropertyValue> iterator()
	{
		return values_.iterator();
	}

	@Override
	public Spliterator<PropertyValue> spliterator()
	{
		return values_.spliterator();
	}

	@Override
	public void forEach(Consumer<? super PropertyValue> action)
	{
		values_.forEach(action);
	}
}
