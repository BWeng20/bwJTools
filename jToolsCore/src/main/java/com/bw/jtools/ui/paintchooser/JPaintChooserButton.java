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
package com.bw.jtools.ui.paintchooser;

import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JChooserButtonBase;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.icon.JPaintIcon;

import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Paint;

/**
 * Convenience button to show a paint chooser.
 */
public class JPaintChooserButton extends JChooserButtonBase<Paint>
{
	private JPaintIcon paintIcon_;
	private boolean showValue_ = true;

	/**
	 * Gets the paint icon.
	 *
	 * @return The icon.
	 */
	public JPaintIcon getPaintIcon()
	{
		if (paintIcon_ == null)
		{
			paintIcon_ = new JPaintIcon(13, 13, null);
		}
		return paintIcon_;
	}

	/**
	 * Sets the paint.
	 *
	 * @param p The new paint
	 */
	@Override
	public void setValue(Paint p)
	{
		super.setValue(p);
		updateText();
		getPaintIcon().setPaint(p);
	}

	protected void updateText()
	{
		Paint c = getValue();
		if (c != null && showValue_)
		{
			setText( UITool.paintToString(c));
		}
		else
			setText(null);

	}

	/**
	 * Creates a new Color-Button
	 */
	public JPaintChooserButton()
	{
		super(I18N.getText("colorchooser.defaultDialogTitle"));
		setIcon(getPaintIcon());
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
	protected Paint showChooserDialog()
	{
		return JPaintChooser.showDialog(this,
				getDialogTitle(),
				getValue());
	}

}
