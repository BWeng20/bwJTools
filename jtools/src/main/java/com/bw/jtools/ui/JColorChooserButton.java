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

import com.bw.jtools.ui.icon.JColorIcon;

import javax.swing.*;
import java.awt.*;

/**
 * Convenience button to show a color chooser.
 */
public class JColorChooserButton extends JChooserButtonBase<Color>
{
	private JColorIcon colorIcon_;
	private boolean showValue_ = true;

	/**
	 * Gets the color icon.
	 *
	 * @return The icon.
	 */
	public JColorIcon getColorIcon()
	{
		if (colorIcon_ == null)
		{
			colorIcon_ = new JColorIcon(13, 13, null);
		}
		return colorIcon_;
	}

	/**
	 * Sets the color.
	 *
	 * @param c The new color
	 */
	@Override
	public void setValue(Color c)
	{
		super.setValue(c);
		updateText();
		getColorIcon().setColor(c);
	}

	protected void updateText()
	{
		Color c = getValue();
		if (c != null && showValue_)
		{
			StringBuilder sb = new StringBuilder(20);
			sb.append(c.getRed())
			  .append(",");
			sb.append(c.getGreen())
			  .append(",");
			sb.append(c.getBlue());
			setText(sb.toString());
		}
		else
			setText(null);

	}

	/**
	 * Creates a new Color-Button
	 */
	public JColorChooserButton()
	{
		super(I18N.getText("colorchooser.defaultDialogTitle"));
		setIcon(getColorIcon());
		setIconTextGap(5);
		setOpaque(false);
		setBorderPainted(true);
		setHorizontalAlignment(JButton.LEFT);
	}

	@Override
	public void updateUI()
	{
		setMargin(null);
		super.updateUI();

		Insets i = getMargin();
		setMargin(new Insets(i.top, 5, i.bottom, 5));
	}

	/**
	 * Controls display of the color RGB-value.
	 */
	public void setShowValue(boolean enable)
	{
		if (showValue_ != enable)
		{
			showValue_ = enable;
			setBorderPainted(showValue_);
			updateText();
			invalidate();
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		if (showValue_)
		{
			Dimension d = super.getPreferredSize();

			Font font = getFont();
			FontMetrics fm = getFontMetrics(font);

			return new Dimension(Math.max(d.width, fm.stringWidth("255,255,255") + 35), d.height);
		}
		else
			return new Dimension(35, 35);
	}

	@Override
	protected Color showChooserDialog()
	{
		return JColorChooser.showDialog(this,
				getDialogTitle(),
				getValue());
	}

}
