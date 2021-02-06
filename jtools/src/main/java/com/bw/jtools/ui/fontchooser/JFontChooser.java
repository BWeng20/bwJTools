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
package com.bw.jtools.ui.fontchooser;

import com.bw.jtools.Log;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.properties.PropertyFontValue;
import com.bw.jtools.ui.*;
import com.bw.jtools.ui.JInputList;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ItemListener;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * A font selection dialog, similar to the swing color-chooser.<br>
 * As component it can be used inside other panels or standalone via the 'showDialog' method.
 */
public class JFontChooser extends JComponent
{
	/** The list of fonts. */
	private JInputList<Font> fontNames_;

	/** "bold" setting. */
	private JCheckBox boldCheck_;

	/** "italic" setting. */
	private JCheckBox italicCheck_;

	/** Selection of font size. Can be selected and manually set. */
	private JInputList<String> sizes_;

	/** Example text to show the resulting font. */
	private JTextPane demo_;

	/** List of available fonts from system. */
	private static List<Font> systemFonts = new ArrayList<>();

	/** List of available fonts from class path. */
	private static Map<String,Font> resourceFonts_ = new HashMap<>();

	private static final String symbolDemoText_;
	private static final String demoText_;

	public final static int DEFAULT_FONT_SIZE = 18;

	static
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20; ++i)
		{
			sb.append((char) (0xF041 + i)).append(' ');
		}
		symbolDemoText_ = sb.toString();
		demoText_ = I18N.getText( "fontchooser.demotext" );
	}

	/** The selected font. */
	protected Font chosenFont_;

	/**
	 * Get all available fonts.
	 */
	public static List<Font> getAvailableFonts()
	{
		List<Font> fonts = new ArrayList<>(100);
		synchronized (systemFonts)
		{
			if (systemFonts.isEmpty())
			{
				String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
				for (String name : fontNames)
				{
					systemFonts.add(new Font(name, Font.PLAIN, DEFAULT_FONT_SIZE));
				}
				addFontsFromResources(JFontChooser.class.getClassLoader(), "com/bw/jtools/ui/fonts");
			}
			fonts.addAll(systemFonts);
		}
		synchronized (resourceFonts_)
		{
			fonts.addAll(resourceFonts_.values());
		}
		fonts.sort((f1, f2) -> f1.getFontName().compareTo(f2.getFontName()));
		return fonts;
	}

	/**
	 * Adds True-Type and Open-Type fonts from resources.<br>
	 * Font integrated in jtools are added automatically.
	 * @param loader The Classloader to scan.
	 * @param startPackage A start prefix in resource-syntax, e.g. "com/myapp".
	 */
	public static void addFontsFromResources( ClassLoader loader, String startPackage )
	{
		try
		{
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

			List<URI> fontURIs = IOTool.scanClasspath(loader, startPackage, "(?i).+\\.(ttf|otf)");
			for ( URI uri : fontURIs )
			{
				URL url = uri.toURL();
				try
				{
					Font f = null;
					try (InputStream is = url.openStream())
					{
						f = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont( Font.PLAIN, DEFAULT_FONT_SIZE);
					}
					ge.registerFont(f);

					synchronized (resourceFonts_)
					{
						resourceFonts_.put( f.getFontName(), f );
					}
					if ( Log.isDebugEnabled() )
						Log.debug( "Loaded font '"+f.getFontName()+"' from "+url.toExternalForm());
				}
				catch( Exception e)
				{
					Log.error( "Failed to load font "+url.toExternalForm(), e);
				}
			}
		}
		catch (PatternSyntaxException pe)
		{
			Log.error("Internal RegExp Error", pe);
		}
		catch (Exception e)
		{
			Log.error( "Failed to scan classpath", e);
		}
	}

	/**
	 * Creates a font-chooser panel.<br>
	 * To select a font via dialog use {@link #showDialog(Component, String, Font) showDialog}.
	 */
	public JFontChooser()
	{
		setLayout(new GridBagLayout());

		fontNames_ = new JInputList<Font>(getAvailableFonts(), 30,
				(item) -> item == null ? "" : item.getFamily());

		ListSelectionListener ll = e -> updateDemo();

		fontNames_.setListCellRenderer(new FontCellRenderer() );
		fontNames_.addSelectionListener(ll);

		sizes_ = new JInputList<String>(
				Arrays.asList(new String[] { "8", "9", "10", "11", "12", "14", "16","18", "20", "22", "24", "26", "28", "36", "48", "72" }),
				5 );

		sizes_.setSelected("12");
		sizes_.addSelectionListener(ll);

		boldCheck_ = new JCheckBox("Bold");
		italicCheck_ = new JCheckBox("Italic");

		ItemListener il = e -> updateDemo();

		boldCheck_.addItemListener(il);
		italicCheck_.addItemListener(il);

		demo_ = new JTextPane();
		demo_.setEnabled(false);
		demo_.setForeground(Color.BLACK);
		demo_.setDisabledTextColor(Color.BLACK);
		demo_.setEditorKit(new CenterEditorKit());

		demo_.setText( demoText_ );

		StyledDocument doc=(StyledDocument)demo_.getDocument();
		SimpleAttributeSet attrs=new SimpleAttributeSet();
		StyleConstants.setAlignment(attrs,StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0,doc.getLength()-1,attrs,false);

		demo_.setBackground( Color.WHITE);
		demo_.setOpaque(true);
		demo_.setBorder( BorderFactory.createLineBorder(UIManager.getLookAndFeelDefaults().getColor("Button.shadow"), 1));

		GridBagConstraints gc = new GridBagConstraints();
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
		gc.gridwidth = 1;
		gc.insets = new Insets(0,0,0,0);
		gc.gridx = 2;
		add( sizes_, gc);

		gc.gridx = 0;
		gc.gridy = 2;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add( boldCheck_, gc);
		gc.gridx = 1;
		add( italicCheck_, gc);

		gc.gridx = 0;
		gc.gridy = 3;
		gc.gridwidth = 3;
		gc.fill = GridBagConstraints.BOTH;

		demo_.setPreferredSize(new Dimension(500,200));
		add( demo_, gc);

	}

	/**
	 * Sets the font selection.
	 * @param f The Font to select.
	 */
	public void setSelectedFont(Font f)
	{
		chosenFont_ = null;
		fontNames_.setSelected(f);
		if ( f == null )
		{
			sizes_.setSelected(null);
			boldCheck_.setSelected(false);
			italicCheck_.setSelected(false);
		}
		else
		{
			sizes_.setSelected(String.valueOf(f.getSize()));
			boldCheck_.setSelected(f.isBold());
			italicCheck_.setSelected(f.isItalic());
		}
		updateDemo();
	}

	/**
	 * Creates the font according to the current settings.
	 * @return The font or null if some settings are missing.
	 */
	public Font getSelectedFront()
	{
 		Font f = fontNames_.getSelectedItem();
		String sizeText = sizes_.getEditedValue();

		if ( f != null && sizeText != null )
		{
			int style = 0;
			if (boldCheck_.isSelected()) style |= Font.BOLD;
			if (italicCheck_.isSelected()) style |= Font.ITALIC;

			try
			{
				f = new Font(f.getFamily(), style, Integer.parseInt(sizeText));
			}
			catch ( Exception e)
			{
			}

		}
		return f;
	}

	/**
	 * Updates the demonstration label.
	 */
	protected void updateDemo()
	{
		Font f = getSelectedFront();
		if ( f != null )
		{
			if ( f.canDisplayUpTo(demoText_) != -1)
			{
				if ( demo_.getText().equals(demoText_))
				{
					demo_.setText(symbolDemoText_);
				}
			}
			else
			{
				if ( demo_.getText().equals(symbolDemoText_))
					demo_.setText(demoText_);
			}
			demo_.setFont(f);
		}
	}

	/**
	 * OPens a font chooser dialog.
	 * @param component The component that triggers the chooser.
	 * @param title The title to show.
	 * @param initialFont The initial font to select or null.
	 * @return the selected font or <code>null</code> if the user opted out.
	 */
	public static Font showDialog(Component component,
								   String title, Font initialFont)
	{
		// Initialization of font list may take several seconds.
		// Show a wait-cursor.
		Window w = component instanceof Window ? (Window)component : SwingUtilities.getWindowAncestor(component);
		Cursor cur = null;
		Cursor waitCursor = null;
		if (w != null)
		{
			waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
			cur = w.getCursor();
			w.setCursor(waitCursor);
		}

		JFontChooser chooserPane = new JFontChooser();
		JDialog chooserDialog = new JDialog(w, title, Dialog.ModalityType.APPLICATION_MODAL);

		chooserDialog.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		JPanel c = new JPanel();
		chooserDialog.setContentPane(c);
		c.setLayout(new BorderLayout());
		c.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		c.add(chooserPane, BorderLayout.CENTER);

		JButton ok = new JButton(I18N.getText("button.ok"));
		ok.addActionListener(e ->
		{
			chooserPane.chosenFont_ = chooserPane.getSelectedFront();
			chooserDialog.setVisible(false);
		});

		JButton cancel = new JButton(I18N.getText("button.cancel"));
		cancel.addActionListener(e -> chooserDialog.setVisible(false));

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(ok);
		buttons.add(cancel);
		c.add(buttons, BorderLayout.SOUTH);
		chooserDialog.pack();

		chooserPane.setSelectedFont( initialFont );
		chooserDialog.setLocationRelativeTo(component);

		if (cur != null && w.getCursor() == waitCursor )
		{
			w.setCursor(cur);
		}

		chooserDialog.setVisible(true);
		return chooserPane.chosenFont_;
	}
}
