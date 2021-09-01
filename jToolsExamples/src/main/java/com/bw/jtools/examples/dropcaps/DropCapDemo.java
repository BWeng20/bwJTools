package com.bw.jtools.examples.dropcaps;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.image.BlendComposite;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Demonstrates {@link com.bw.jtools.ui.dropcaps.JDropCapsLabel}.<br>
 * The real stuff is done inside {@link JDropCapPanel}. This class only wrapps several panels.
 */
public class DropCapDemo
{
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

		JPanel contentPanel = new JPanel( new BorderLayout() );
		frame.setContentPane(contentPanel);

		JPanel mainPanel = new JPanel( new FlowLayout(FlowLayout.CENTER) );
		mainPanel.setBackground(new Color(215,235, 190));

		contentPanel.add(new JScrollPane(mainPanel), BorderLayout.CENTER);

		// This panel will use the same font (x5 larger) to show the drop-caps.
		JDropCapPanel dropCap = new JDropCapPanel("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.\n" +
				"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata " +
				"sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
				"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

		// The text will use a simple gradient paint.
		dropCap.getDropCap().setForegroundPaint(new GradientPaint(-5,-5, new Color(120, 25, 25) , 400,300,new Color(0,50, 0)));

		mainPanel.add( dropCap );

		dropCap =
				new JDropCapPanel("Morem ipsum dolor sit amet, consetetur sadipscing elitr, " +
						"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.\n" +
						"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata " +
						"sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
						"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
						"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

		// Paint the text with a image to give them some fancy structure
		BufferedImage paintImage = IconTool.getImage(DropCapDemo.class, "Paint.png");
		Paint dcp = new TexturePaint(paintImage, new Rectangle(0,0,100,100));
		dropCap.getDropCap().setForegroundPaint(new TexturePaint(paintImage, new Rectangle(0,0,100,100)));

		// Use a set of letter-images for the drop-caps.
		dropCap.getDropCap().setInitialSet("goudy", 55, 95, true);
		// Colorized the drop-caps images with red (we can re-use the same images for all colors).
		dropCap.getDropCap().setDropCapColor(new Color( 100, 5, 5));
		// The same texture as for the normal text will be used to mask the image and
		// give the image the same structure.
		dropCap.getDropCap().setDropCapPaint(dcp,new BlendComposite(BlendComposite.Mode.MASK_GRAY));
		// dropCap.getDropCap().setOpaque(true);

		mainPanel.add( dropCap);

		JPanel status = new JPanel(new FlowLayout(FlowLayout.LEADING));
		status.add( new JLAFComboBox());
		contentPanel.add( status, BorderLayout.SOUTH);

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
