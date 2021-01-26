package com.bw.jtools.examples.dropcaps;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.graphic.IconTool;
import com.bw.jtools.ui.JCardBorder;
import com.bw.jtools.ui.dropcaps.JDropCapsLabel;
import com.bw.jtools.ui.graphic.MaskComposite;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;

public class DropCapDemo
{

	JDropCapsLabel dropCap2;

	public DropCapDemo() throws IOException
	{
		Application.initialize(DropCapDemo.class);

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		JFrame frame = new JFrame( "Drop Cap Label Demonstration" );

		JPanel mainPanel = new JPanel( new FlowLayout(FlowLayout.CENTER) );
		mainPanel.setBackground(new Color(215,235, 190));

		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());

		JPanel ctrl = new JPanel(new FlowLayout());

		cp.add( mainPanel, BorderLayout.CENTER );
		cp.add( ctrl, BorderLayout.SOUTH );

		JCardBorder cardBorder_ = new JCardBorder(0.997f);
		Border border = BorderFactory.createCompoundBorder( cardBorder_, BorderFactory.createEmptyBorder(10,10,10,10) );

		JDropCapsLabel dropCap =
			new JDropCapsLabel("Mattis Bernd. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.\n" +
				"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata " +
				"sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
				"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
		dropCap.setBorder( border );
		dropCap.setBackground(Color.WHITE);
		dropCap.setOpaque(true);
		dropCap.setForegroundPaint(new GradientPaint(-5,-5, new Color(120, 25, 25) , 400,300,new Color(0,50, 0)));
		mainPanel.add( dropCap);

		BufferedImage paintImage = IconTool.getImage(DropCapDemo.class, "Paint.png");

		dropCap =
				new JDropCapsLabel("Mattis Lena. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
						"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.\n" +
						"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata " +
						"sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
						"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
						"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
		dropCap.setInitialSet("goudy", 55, 95, true);
		dropCap.setBorder( border );
		dropCap.setBackground(Color.WHITE);
		dropCap.setForegroundPaint(new TexturePaint(paintImage, new Rectangle(0,0,100,100)));

		// For drop caps use the same texture paint, but with a red-colored image
		dropCap.setDropCapColor(new Color( 100, 5, 5));

		Paint dcp = new TexturePaint(paintImage, new Rectangle(0,0,100,100));
		dropCap.setDropCapPaint(dcp,null);
		dropCap.setOpaque(true);

		dropCap2 = dropCap;

		mainPanel.add( dropCap);
		mainPanel.add( new JLabel(new ImageIcon(paintImage)));

		JComboBox<String> dropCapModes = new JComboBox<>();

		LinkedHashMap<String, Composite> map = new LinkedHashMap<>();
		map.put( "None", null );
		map.put( "MaskAlpha", new MaskComposite() );
		map.put( "Clear", AlphaComposite.Clear);
		map.put( "Xor", AlphaComposite.Xor);
		map.put( "Dst", AlphaComposite.Dst);
		map.put( "DstAtop", AlphaComposite.DstAtop);
		map.put( "DstIn", AlphaComposite.DstIn);
		map.put( "DstOut", AlphaComposite.DstOut);
		map.put( "SrcAtop", AlphaComposite.SrcAtop);
		map.put( "DstOver", AlphaComposite.DstOver);
		map.put( "Src", AlphaComposite.Src);
		map.put( "SrcIn", AlphaComposite.SrcIn);
		map.put( "SrcOut", AlphaComposite.SrcOut);

		for ( String m : map.keySet())
			dropCapModes.addItem( m );


		dropCapModes.addItemListener(e ->
		{
			String c = (String)dropCapModes.getSelectedItem();
			if ( c != null )
			{
				dropCap2.setDropCapPaint(dcp, map.get(c));

			}
		});

		ctrl.add(dropCapModes);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImages( IconTool.getAppIconImages() );
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) throws IOException
	{
		Log.setLevel(Log.DEBUG);
		new DropCapDemo();
	}

}
