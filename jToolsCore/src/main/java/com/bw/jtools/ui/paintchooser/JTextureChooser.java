package com.bw.jtools.ui.paintchooser;

import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JPaintViewport;
import com.bw.jtools.ui.filechooserpreview.ImagePreviewHandler;
import com.bw.jtools.ui.filechooserpreview.JFileChooserPreview;
import com.bw.jtools.ui.pathchooser.JPathChooser;
import com.bw.jtools.ui.pathchooser.PathChooserMode;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class JTextureChooser extends JPanel
{
	protected TexturePaint currentPaint_;
	protected Rectangle2D currentAnchorRect_;
	protected TexturePaint chosenPaint_;

	protected JPathChooser pathChooser_;
	protected JPaintViewport paintViewPort;

	public JTextureChooser()
	{
		setLayout(new GridBagLayout());

		JButton browse = new JButton("Select Image");

		GridBagConstraints gc = new GridBagConstraints();

		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridheight = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.BOTH;

		JScrollPane sp = new JScrollPane();
		add(sp, gc);

		paintViewPort = new JPaintViewport();
		sp.setViewport(paintViewPort);

		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.gridheight = 1;
		++gc.gridx;
		add(browse, gc);
		++gc.gridy;

		browse.addActionListener(e ->
		{
			if (pathChooser_ == null)
			{
				pathChooser_ = new JPathChooser();
				pathChooser_.showJreFileSystems();
				pathChooser_.setFileSelectionMode(PathChooserMode.FILES_ONLY);
				JFileChooserPreview preview = new JFileChooserPreview(300, "Preview", 5, 5,
						new ImagePreviewHandler());
				preview.install(pathChooser_);
			}
			if (pathChooser_.showDialog(this, "Select Image", "Select"))
			{
				try
				{
					URL url = pathChooser_.getSelectedPath()
										  .toUri()
										  .toURL();
					BufferedImage image = ImageIO.read(url);
					Rectangle2D r = currentAnchorRect_;
					if (r == null)
						r = new Rectangle2D.Double(0, 0, (double) image.getWidth(),
								(double) image.getHeight());
					currentPaint_ = new TexturePaint(image, r);
					updateViewport();

				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}

		});

	}

	public TexturePaint getSelectedPaint()
	{
		return chosenPaint_;
	}

	public void setSelectedPaint(TexturePaint p)
	{
		currentPaint_ = p;
		if (p != null)
		{
			currentAnchorRect_ = p.getAnchorRect();
			// rectX.setText( Double.toString(currentAnchorRect_.getX()));
		}
		chosenPaint_ = null;
		updateViewport();
	}

	protected void updateViewport()
	{
		if (currentPaint_ == null)
			paintViewPort.setBackgroundPaint(getBackground());
		else
			paintViewPort.setBackgroundPaint(currentPaint_);
		repaint();
	}


	/**
	 * Opens a chooser dialog to create a TexturePaint.
	 *
	 * @param component    The component that triggers the chooser.
	 * @param title        The title to show.
	 * @param initialPaint The initial paint to select or null.
	 * @return the selected paint or <code>null</code> if the user opted out.
	 */
	public static Paint showDialog(Component component,
								   String title, TexturePaint initialPaint)
	{
		Window w = component == null ? null : component instanceof Window ? (Window) component : SwingUtilities.getWindowAncestor(component);

		JTextureChooser chooserPane = new JTextureChooser();
		JDialog chooserDialog = new JDialog(w, title, Dialog.ModalityType.APPLICATION_MODAL);
		chooserDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JPanel c = new JPanel();
		chooserDialog.setContentPane(c);
		c.setLayout(new BorderLayout());
		c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c.add(chooserPane, BorderLayout.CENTER);

		JButton ok = new JButton(I18N.getText("button.ok"));
		ok.addActionListener(e ->
		{
			chooserPane.chosenPaint_ = chooserPane.currentPaint_;
			chooserDialog.setVisible(false);
		});

		JButton cancel = new JButton(I18N.getText("button.cancel"));
		cancel.addActionListener(e -> chooserDialog.setVisible(false));

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(ok);
		buttons.add(cancel);
		c.add(buttons, BorderLayout.SOUTH);
		chooserDialog.pack();

		chooserPane.setSelectedPaint(initialPaint);
		chooserDialog.setLocationRelativeTo(component);

		chooserDialog.setVisible(true);
		return chooserPane.chosenPaint_;
	}

	public static void main(String[] args)
	{
		showDialog(null, "Text", null);
	}

}
