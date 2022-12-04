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

import javax.swing.Icon;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base of all type specific properties.<br>
 * This class can be used directly without the custom "PropertyXXXValue" classes,
 * but in this case the caller has to ensure that the value-class is compatible with
 * the content of the user-object.<br>
 * <br>
 * <i>Example:</i><br>
 * <pre>
 * {@code
 *  PropertyValue property = new PropertyValue( "My Number", Integer.class );
 *  property.setUserObject( 10 );
 * }
 * </pre>
 */
public class PropertyValue<T>
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -7565043274211746142L;

	/**
	 * The payload.
	 */
	private T payload_;

	/**
	 * The key of the property.
	 */
	public final String key_;

	/**
	 * The display name of the property.
	 */
	public String displayName_;

	/**
	 * The optional Icon of the property.
	 */
	public Icon diplayIcon_;

	/**
	 * Values restrictions.
	 */
	public Map<String, T> possibleValues_;


	/**
	 * The value-class of this property.
	 */
	public Class<? extends T> valueClazz_;

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
	 * List of change listeners that listen for this property.<br>
	 */
	private List<WeakReference<PropertyChangeListener>> propertyChangeListener_;


	/**
	 * Constructs a new property by name and value-clazz.
	 * The value will initially be null.
	 *
	 * @param key        The key (and initial display name) of the property to show.
	 * @param valueClazz The value-class of the property.
	 */
	public PropertyValue(String key, Class<? extends T> valueClazz)
	{
		this.displayName_ = key;
		this.key_ = key;
		this.valueClazz_ = valueClazz;
	}

	public void setDisplayName(String name)
	{
		this.displayName_ = name;
	}


	public void setIcon(Icon icon)
	{
		this.diplayIcon_ = icon;
	}

	/**
	 * Gets the payload.
	 */
	public T getValue()
	{
		return payload_;
	}

	/**
	 * Sets the payload.
	 */
	public void setValue(T payload)
	{
		if (!Objects.equals(payload, payload_))
		{
			payload_ = payload;
			firePropertyChange();
		}
	}

	/**
	 * Checks if the property has some content.
	 * Convenience replacement for "getPayload() != null"
	 *
	 * @return True if some value exists.
	 */
	public boolean hasContent()
	{
		return payload_ != null;
	}

	/**
	 * Adds a change listener.
	 * The listeners are stored vias weak reference.
	 * If lambdas are used, the caller has to hold a strong reference.
	 *
	 * @param l The listener.
	 */
	public void addPropertyChangeListener(PropertyChangeListener<T> l)
	{
		removePropertyChangeListener(l);
		if (propertyChangeListener_ == null)
			propertyChangeListener_ = new ArrayList<>();
		propertyChangeListener_.add(new WeakReference<PropertyChangeListener>(l));
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
			Iterator<WeakReference<PropertyChangeListener>> it = propertyChangeListener_.iterator();
			while (it.hasNext())
			{
				WeakReference<PropertyChangeListener> wl = it.next();
				PropertyChangeListener pl = wl.get();
				if (pl == null || pl == l)
					it.remove();
			}
		}
	}

	/**
	 * Calls all change listener.
	 */
	public void firePropertyChange()
	{
		if (propertyChangeListener_ != null)
		{
			List<WeakReference<PropertyChangeListener>> l = new ArrayList<>(propertyChangeListener_);
			for (WeakReference<PropertyChangeListener> wp : l)
			{
				PropertyChangeListener pl = wp.get();
				if (pl != null)
					pl.propertyChanged(this);
			}
		}
	}

	/**
	 * Returns the string representation of the value.
	 */
	@Override
	public String toString()
	{
		if (hasContent())
			return String.valueOf(getValue());
		else
			return "";
	}

}
