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

import com.bw.jtools.ui.fontchooser.JFontChooser;

import javax.swing.JButton;
import java.awt.Font;

/**
 * Button to show and change some font setting.
 */
public class JFontButton extends JChooserButtonBase<Font>
{
	protected static Font getDefaultFont()
	{
		return javax.swing.UIManager.getDefaults()
									.getFont("TextField.font");
	}

	public JFontButton()
	{
		this(getDefaultFont());
	}

	public JFontButton(Font font)
	{
		this(font, I18N.getText("fontchooser.defaultDialogTitle"));
	}

	public JFontButton(Font font, String dialogTitle)
	{
		super(dialogTitle);
		setValue(font);
		setText(I18N.getText("fontchooser.button"));
		setHorizontalAlignment(JButton.CENTER);
		setOpaque(false);
		setBorderPainted(true);
	}

	protected Font showChooserDialog()
	{
		return JFontChooser.showDialog(this, getDialogTitle(), getValue());
	}

}
