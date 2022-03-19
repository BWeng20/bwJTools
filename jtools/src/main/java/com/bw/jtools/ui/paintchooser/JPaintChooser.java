package com.bw.jtools.ui.paintchooser;

import com.bw.jtools.ui.I18N;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.Window;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class JPaintChooser extends JPanel
{
	protected Paint chosenPaint_;

	protected JColorChooser colorChooser_;
	protected JPanel textureChooser_;
	protected JPanel gradientChooser_;

	protected String textureName_ = I18N.getText("paintchooser.mode.texture");
	protected String gradientName_ = I18N.getText("paintchooser.mode.gradient");
	protected String colorName_ = I18N.getText("paintchooser.mode.color");

	protected CardLayout cardLayout_;
	protected JPanel modepane_;

	protected HashMap<String, JComponent> mode2Panels = new LinkedHashMap<>();
	protected JComboBox<String> mode;

	public JPaintChooser()
	{
		setLayout(new GridBagLayout());

		JLabel modeLabel = new JLabel(I18N.getText("paintchooser.type"));
		mode = new JComboBox<>();
		modeLabel.setLabelFor(mode);

		colorChooser_ = new JColorChooser();
		colorChooser_.setPreviewPanel(new JPanel());
		textureChooser_ = new JTextureChooser();
		gradientChooser_ = new JPanel();

		GridBagConstraints gc = new GridBagConstraints();

		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.NONE;

		JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		modePanel.add(modeLabel);
		modePanel.add(mode);
		add(modePanel, gc);

		gc.gridx = 0;
		++gc.gridy;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;

		cardLayout_ = new CardLayout();
		modepane_ = new JPanel(cardLayout_);

		add(modepane_, gc);

		mode2Panels.put(colorName_, colorChooser_);
		mode2Panels.put(gradientName_, gradientChooser_);
		mode2Panels.put(textureName_, textureChooser_);

		for (Map.Entry<String, JComponent> entry : mode2Panels.entrySet())
		{
			mode.addItem(entry.getKey());
			modepane_.add(entry.getValue(), entry.getKey());
		}

		mode.addItemListener(e ->
		{
			updateMode();
		});

		mode.setSelectedIndex(0);
		updateMode();


	}

	protected void updateMode()
	{
		JTabbedPane p;
		SwingUtilities.invokeLater(() ->
		{
			String name = (String) mode.getSelectedItem();
			if (name != null)
			{
				cardLayout_.show(modepane_, name);
			}
		});
	}

	public Paint getSelectedPaint()
	{
		final String activeMode = (String) mode.getSelectedItem();
		if (activeMode == colorName_)
			return colorChooser_.getColor();
		else if (activeMode == gradientName_)
			return colorChooser_.getColor();
		else if (activeMode == textureName_)
			return colorChooser_.getColor();
		return null;
	}

	public void setSelectedPaint(Paint p)
	{
		chosenPaint_ = null;

		if (p == null)
		{
			mode.setSelectedItem(colorName_);
			colorChooser_.setColor(Color.BLACK);
		}
		else if (p instanceof Color)
		{
			mode.setSelectedItem(colorName_);
			colorChooser_.setColor((Color) p);
		}
		else if (p instanceof TexturePaint)
		{
			mode.setSelectedItem(textureName_);
			ImageIcon ic = new ImageIcon(((TexturePaint) p).getImage());
		}
		else
			throw new IllegalArgumentException("Paint of type " + p.getClass()
																   .getSimpleName() + " is not supported.");

		updateMode();
	}


	/**
	 * Opens a paint chooser dialog.
	 *
	 * @param component    The component that triggers the chooser.
	 * @param title        The title to show.
	 * @param initialPaint The initial paint to select or null.
	 * @return the selected paint or <code>null</code> if the user opted out.
	 */
	public static Paint showDialog(Component component,
								   String title, Paint initialPaint)
	{
		// Initialization of font list may take several seconds.
		// Show a wait-cursor.
		Window w = component == null ? null : component instanceof Window ? (Window) component : SwingUtilities.getWindowAncestor(component);
		Cursor cur = null;
		Cursor waitCursor = null;
		if (w != null)
		{
			waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
			cur = w.getCursor();
			w.setCursor(waitCursor);
		}

		JPaintChooser chooserPane = new JPaintChooser();
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
			chooserPane.chosenPaint_ = chooserPane.getSelectedPaint();
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

		if (cur != null && w.getCursor() == waitCursor)
		{
			w.setCursor(cur);
		}
		chooserDialog.setVisible(true);
		return chooserPane.chosenPaint_;
	}

	public static void main(String[] args)
	{
		System.out.println("Locale " + Locale.getDefault());
		System.out.println("Selected paint " + showDialog(null, "Text", Color.BLACK));
		System.exit(0);
	}

}
