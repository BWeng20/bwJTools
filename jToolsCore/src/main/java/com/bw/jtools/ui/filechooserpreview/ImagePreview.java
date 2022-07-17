package com.bw.jtools.ui.filechooserpreview;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

public class ImagePreview extends JLabel
{
	public ImagePreview()
	{
		super();
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
		Font f = getFont();
		setFont(f.deriveFont(Font.PLAIN, f.getSize() * 2));
	}

	public void setImage(Image image)
	{
		setText(null);
		if ( image == null)
			setIcon(null);
		else
		{
			setIcon(new ImageIcon(image));
		}
	}
}
