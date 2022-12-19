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
package com.bw.jtools.ui.properties;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.valuetypehandler.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to create and configure editor-components for property-values.<br>
 * Holds one instance for each property type.<br>
 * <ul>
 * <li>Enum-types: A combo-box with all possible values is shown.
 * <li>Number-types: A text-field with local dependent number format is shown.
 * <li>Color: A colorized icon and the RGB-value is shown. A click opens the jdk
 * color-chooser.
 * <li>Boolean: A combo-box with "true", "false" and an empty entry is shown.
 * </ul>
 */
public class PropertyEditorComponents
{
	private static NumberFormat nf_;
	private static Font font_ = new Font("SansSerif", Font.PLAIN, 11);

	private final Map<Class<?>,Class<? extends ValueTypeHandler>>
			typeEditorHandlersClasses_ = new HashMap<>();

	private final Map<String, ValueTypeHandler>
			typeEditorHandlersInstances_ = new HashMap<>();

	public PropertyEditorComponents()
	{
		typeEditorHandlersClasses_.put( String.class, StringHandler.class );
		typeEditorHandlersClasses_.put( Color.class, ColorHandler.class );
		typeEditorHandlersClasses_.put( Paint.class, PaintHandler.class );
		typeEditorHandlersClasses_.put( Boolean.class, BooleanHandler.class );
		typeEditorHandlersClasses_.put( Font.class, FontHandler.class );
		typeEditorHandlersClasses_.put( Number.class, NumberHandler.class );
		typeEditorHandlersClasses_.put( List.class, ChoiceHandler.class );
		typeEditorHandlersClasses_.put(Enum.class, EnumHandler.class);
		typeEditorHandlersClasses_.put(Map.class, MapHandler.class);
	}

	protected ValueTypeHandler createHandlerForType(Class<?> clazz, boolean choice)
	{
		Class<? extends ValueTypeHandler> teClazz ;
		if ( choice )
		{
			teClazz = typeEditorHandlersClasses_.get(List.class);
		}
		else if ( clazz.isEnum() )
		{
			teClazz = typeEditorHandlersClasses_.get(Enum.class);
		}
		else
		{
			teClazz = typeEditorHandlersClasses_.get(clazz);

			if (teClazz == null)
			{
				// Classloader-mess or no direct match.
				// Search for best match.
				Class<?> bestTeValueClazz = null;
				// Get the closed value class (e.g. Integer instead of Number)
				for (Map.Entry<Class<?>, Class<? extends ValueTypeHandler>> teC : typeEditorHandlersClasses_.entrySet())
				{
					Class<?> teValueClazz = teC.getKey();
					if (teValueClazz.isAssignableFrom(clazz) && (
							bestTeValueClazz == null || bestTeValueClazz.isAssignableFrom(teValueClazz)))
					{
						bestTeValueClazz = teValueClazz;
						teClazz = teC.getValue();
					}
				}

			}
		}
		if (teClazz != null)
		{
			try
			{
				return teClazz.getDeclaredConstructor().newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}


	protected ValueTypeHandler getHandlerForType(Class<?> clazz,  boolean choice)
	{
		String valueClassName = choice ? List.class.getName() : clazz.getName();

		ValueTypeHandler th = typeEditorHandlersInstances_.get(valueClassName);
		if ( th == null )
		{
			th = createHandlerForType(clazz, choice);
			typeEditorHandlersInstances_.put( valueClassName, th );
		}
		return th;
	}

	static
	{
		nf_ = NumberFormat.getInstance();
		nf_.setGroupingUsed(false);
	}

	public Font getFont()
	{
		return font_;
	}

	/**
	 * Get the common NumberFormat.
	 * Can be overwritten in each property by field {@link PropertyValue#nf_}
	 */
	public NumberFormat getNumberFormat()
	{
		return nf_;
	}


	/**
	 * Returns a configured component for the value type.<br>
	 *
	 * @param value The property value.
	 * @return The configured component.
	 */
	public <T> ValueTypeHandler getHandler(PropertyValue<T> value, boolean newInstance)
	{
		ValueTypeHandler h;
		boolean choice = value.possibleValues_ != null && !value.possibleValues_.isEmpty();
		if ( newInstance )
		{
			h = createHandlerForType(value.valueClazz_, choice);
		}
		else
		{
			h = getHandlerForType(value.valueClazz_, choice);
		}
		h.initEditor(value, this);
		return h;
	}

}
