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

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.util.Arrays;

public class JFontChooser extends JComponent
{
	private JInputList fontNames_;
	private JCheckBox boldCheck_;
	private JCheckBox italicCheck_;
	private JInputList sizes_;

	private static JDialog chooserDialog_;
	private static Font choosenFont_;
	private static JFontChooser chooserPane_;

	public JFontChooser()
	{
		setLayout(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		fontNames_ = new JInputList(Arrays.asList(ge.getAvailableFontFamilyNames()),  30 );
		sizes_ = new JInputList(
				Arrays.asList(new String[] { "8", "9", "10", "11", "12", "14", "16","18", "20", "22", "24", "26", "28", "36", "48", "72" }),
				5 );
		boldCheck_ = new JCheckBox("Bold");
		italicCheck_ = new JCheckBox("Italic");

		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0.5;
		gc.weighty = 1;
		gc.gridwidth = 2;
		gc.gridheight= 1;
		gc.insets = new Insets(0,0,0,5);
		gc.fill = GridBagConstraints.BOTH;
		add( fontNames_, gc);
		gc.insets = new Insets(0,0,0,0);;
		gc.gridx = 2;
		add( sizes_, gc);

		gc.gridx = 0;
		gc.gridy = 2;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.fill = GridBagConstraints.VERTICAL;
		add( boldCheck_, gc);
		gc.gridx = 1;
		add( italicCheck_, gc);

	}

	public void setSelectedFont(Font f)
	{
		fontNames_.setSelected(f.getFamily());
		sizes_.setSelected(String.valueOf(f.getSize()));
		boldCheck_.setSelected(f.isBold());
		italicCheck_.setSelected(f.isItalic());
	}

	public Font getSelectedFront()
	{
		Font f;
		String name = fontNames_.getSelected();
		if ( name != null )
		{
			int style = 0;
			if (boldCheck_.isSelected()) style |= Font.BOLD;
			if (italicCheck_.isSelected()) style |= Font.ITALIC;

			f = new Font(name, style, Integer.parseInt(sizes_.getSelected()));
		}
		else
			f = null;
		return f;
	}


	/**
	 *
	 * @param component
	 * @param title
	 * @param initialFont
	 * @return the selected font or <code>null</code> if the user opted out.
	 */
	public static Font showDialog(Component component,
								   String title, Font initialFont)
	{
		if ( chooserDialog_ == null )
		{
			chooserPane_ = new JFontChooser();
			chooserDialog_ = new JDialog(SwingUtilities.getWindowAncestor(component), title, Dialog.ModalityType.APPLICATION_MODAL);
			chooserDialog_.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
			JPanel c = new JPanel();
			chooserDialog_.setContentPane(c);
			c.setLayout(new BorderLayout());
			c.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			c.add(chooserPane_, BorderLayout.CENTER);

			JButton ok = new JButton("OK");
			ok.addActionListener(e ->
			{
				choosenFont_ = chooserPane_.getSelectedFront();
				chooserDialog_.setVisible(false);
			});

			JButton cancel = new JButton("CANCEL");
			cancel.addActionListener(e ->
			{
				chooserDialog_.setVisible(false);
			});

			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
			buttons.add(ok);
			buttons.add(cancel);
			c.add(buttons, BorderLayout.SOUTH);
			chooserDialog_.pack();
		}
		if ( initialFont != null )
			chooserPane_.setSelectedFont( initialFont );
		choosenFont_ = null;
		chooserDialog_.setVisible(true);
		return choosenFont_;
	}
}
