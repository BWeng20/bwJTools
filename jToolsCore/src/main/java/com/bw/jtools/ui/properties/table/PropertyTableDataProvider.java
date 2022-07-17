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

import com.bw.jtools.properties.PropertyValue;
import org.netbeans.swing.outline.RenderDataProvider;

import java.awt.Color;

/**
 * Implementation of a "RenderDataProvider".
 */
public class PropertyTableDataProvider implements RenderDataProvider
{
	@Override
	public java.awt.Color getBackground(Object o)
	{
		return null;
	}

	@Override
	public String getDisplayName(Object o)
	{
		if (o instanceof PropertyNode)
		{
			PropertyValue value = ((PropertyNode) o).property_;
			if (value.displayName_ != null)
				return value.displayName_;
		}
		return "";
	}

	@Override
	public java.awt.Color getForeground(Object o)
	{
		return Color.BLACK;
	}

	@Override
	public javax.swing.Icon getIcon(Object o)
	{
		return null;
	}

	@Override
	public String getTooltipText(Object o)
	{
		return null;
	}

	@Override
	public boolean isHtmlDisplayName(Object o)
	{
		return false;
	}

}
