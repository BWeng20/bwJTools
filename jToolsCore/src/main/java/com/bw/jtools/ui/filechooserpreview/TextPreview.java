package com.bw.jtools.ui.filechooserpreview;

import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TextPreview extends JTextArea
{
	private int visibleRows_ = -1;
	private int visibleCols_ = -1;

	public TextPreview()
	{
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				visibleRows_ = -1;
			}
		});
	}

	public int getVisibleRows()
	{
		ensureVisibleStats();
		return visibleRows_;
	}

	public int getVisibleColumns()
	{
		ensureVisibleStats();
		return visibleCols_;
	}

	private void ensureVisibleStats()
	{
		if ( visibleRows_ == -1)
		{
			final FontMetrics fontMetrics = getFontMetrics(getFont());

			final Dimension s = getSize();
			final int averageCharWith = fontMetrics.charWidth('X');
			final int charHeight = fontMetrics.getHeight();

			visibleCols_ = (s.width+averageCharWith-1) / averageCharWith;
			visibleRows_ = (s.height+charHeight-1) / charHeight;
		}
	}


}
