package com.bw.jtools.ui.filechooserpreview;

import com.bw.jtools.ui.image.ImageTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
