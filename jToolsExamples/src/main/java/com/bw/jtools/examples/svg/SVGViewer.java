package com.bw.jtools.examples.svg;

import javax.swing.UIManager;

public class SVGViewer
{

	public static final String[][] DESCRIPTION =
			{
					{
							"en", "Starts SVGViewer example from jSVG."
					},
					{
							"de", "Startet das SVGViewer-Beispiel von jSVG."
					}
			};

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			com.bw.jtools.examples.SVGViewer.main(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
