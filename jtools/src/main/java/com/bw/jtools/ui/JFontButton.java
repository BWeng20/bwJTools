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
package com.bw.jtools.ui;

import com.bw.jtools.properties.PropertyFontValue;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class JFontButton extends JButton
{
	public JFontButton()
	{
		super();
		setOpaque(false);
		setBorderPainted(true);

		setMargin(new Insets(0, 0, 0, 0));
		setHorizontalAlignment(JButton.LEFT);

		addActionListener( (ae) ->
			{
			}
		);

	}

	private Font selectedFont_;

	public Font getSelectedFont()
	{
		return selectedFont_;
	}

	public void setSelectedFont(Font f)
	{
		selectedFont_ = f;
		setText(PropertyFontValue.toString(selectedFont_));
	}


	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();

		Font f = getFont();
		Rectangle2D b = f.getMaxCharBounds(new FontRenderContext( null, false, false));
		int minWidth = (int)(b.getWidth()*5);
		int minHeigth = (int)(b.getHeight()+6);

		if ( d.width < minWidth) d.width = minWidth;
		if ( d.height < minHeigth) d.height = minHeigth;

		return d;
	}
}
