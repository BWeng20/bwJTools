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
package com.bw.jtools.ui.filechooserpreview;

import com.bw.jtools.Log;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.pathchooser.JPathChooser;
import com.bw.jtools.ui.pathchooser.PathInfo;
import com.bw.jtools.ui.pathchooser.PathSelectionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Preview accessory for some known file-types in a {@link javax.swing.JFileChooser JFileChooser} or
 * a {@link JPathChooser}.<br>
 * Can be used via {@link javax.swing.JFileChooser#setAccessory(JComponent)} to add a preview for files.
 * <br>
 * As default an image- and a text-preview handler is added. Other previews can be added via the
 * PreviewHandler interface.
 * <br>
 * Previews are loaded in background and weakly cached globally across all instances of this class.
 * Different preview-sizes may result in different versions in cache - mainly for images previews.<br>
 * <br>
 * If running in Windows(c), "lnk"-files are handled via {@link IOTool#resolveWindowsLinkFile(File)} which works <i>not</i>
 * platform independent and may break with future java versions.<br>
 * <i>Example:</i>
 * <code>
 * JFileChooser fileChooser = new JFileChooser();
 * fileChooser.setFileHidingEnabled(true);
 * JFileChooserPreview preview = new JFileChooserPreview(300, JFileChooserPreview.defaultHandlers());
 * // you can also configure the default preview-handlers:
 * preview.getPreviewHandler(TextPreviewHandler.class).setPreviewTextLength(2048);
 * // Or set some fancy border_
 * preview.setBorder( BorderFactory.createTitledBorder("Preview") );
 * // Then hook the preview inside the chooser.
 * preview.install(fileChooser);
 * // Now show it.
 * File file = fileChooser.showDialog(null, "DEMO");
 * </code>
 */
public class JFileChooserPreview extends JPanel
{
	protected JFileChooser fileChooser_;
	protected JPathChooser pathChooser_;

	/**
	 * List of preview-handlers.
	 */
	protected final List<PreviewHandler> previewHandler_ = new ArrayList<>();
	protected final InfoPreviewHandler infoPreviewHandler_ = new InfoPreviewHandler();
	protected final InfoPreviewProxy infoPreviewProxy_ ;
	protected final PreviewHandler resolvePreviewHandler = new  ResolvePreviewHandler();


	/**
	 * Content preview area.
	 */
	protected JPanel contentArea_;

	/**
	 * Attribute preview area.
	 */
	protected JPanel attributeArea_;
	/**
	 * Layout for the attribute preview area.
	 */
	protected GridBagLayout attributeLayout_;

	protected JLabel emptyPreview_;
	protected List<JLabel> additionalAttributesLabels_ = new ArrayList<>();
	protected List<JTextComponent> additionalAttributesText_ = new ArrayList<>();
	protected JLabel attributeLabel_ModTime_;
	protected JTextArea attribute_ModTime_;
	protected JLabel attributeLabel_Size_;
	protected JTextArea attribute_Size_;

	protected DateTimeFormatter df_ = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
	protected NumberFormat nf_ = NumberFormat.getNumberInstance();
	protected Color defaultBackgroundColor_;

	/**
	 * true if os is windows.
	 */
	protected static final boolean IS_WINDOWS;

	static
	{
		boolean iswin;
		try
		{

			iswin = System.getProperty("os.name")
						  .toUpperCase()
						  .startsWith("WINDOWS");
		}
		catch (Exception ex)
		{
			iswin = false;
		}
		IS_WINDOWS = iswin;
	}

	/**
	 * Used for delayed "loading" display.
	 */
	protected Timer loading_;

	/**
	 * The currently pending proxy.
	 */
	protected PreviewProxy pendingProxy_;

	/**
	 * Space to the file-chooser.
	 */
	protected static final int LEFT_BORDER_WIDTH = 10;

	/**
	 * Preview config implementation, shared with the preview-handers.
	 */
	protected final PreviewConfig previewConfig_ = new PreviewConfig()
	{

		@Override
		public void update(PreviewProxy proxy, PreviewHandler handler)
		{
			JFileChooserPreview.this.setPreview(proxy, handler);
		}

		@Override
		public void setFile(File file)
		{
			JFileChooserPreview.this.setFile(file);
		}
	};

	/**
	 * Property Listener to react on selection changes.
	 *
	 * @see #install(JFileChooser)
	 */
	protected java.beans.PropertyChangeListener listener_ = changeEvent ->
	{
		String changeName = changeEvent.getPropertyName();
		if (changeName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
		{
			File file = (File) changeEvent.getNewValue();
			setFile(file);
		}
	};

	/**
	 * Path Listener to react on selection changes.
	 *
	 * @see #install(JPathChooser)
	 */
	protected PathSelectionListener pathListener_ = changeEvent ->
	{
		PathInfo pi = changeEvent.getSelectedPath();
		setFile(pi == null ? null : pi.getPath());
	};

	/**
	 * Create an array with all default  PreviewZHandlers.
	 */
	public static PreviewHandler[] defaultHandlers()
	{
		return new PreviewHandler[]{new TextPreviewHandler(), new ImagePreviewHandler(), new SVGPreviewHandler()};
	}

	/**
	 * Creates a new preview accessory.<br>
	 * After creation, use {@link #install(JFileChooser)} to connect it with a file-chooser.
	 *
	 * @param previewWidth The width of the image preview. Resulting area will be previewSize + 10 pixel border.
	 * @param handlers The handlers to use.
	 */
	public JFileChooserPreview(int previewWidth, PreviewHandler... handlers)
	{
		this( previewWidth, null, LEFT_BORDER_WIDTH, 0, handlers);
	}

	/**
	 * Creates a new preview accessory.<br>
	 * After creation, use {@link #install(JFileChooser)} to connect it with a file-chooser.
	 *
	 * @param previewWidth The width of the preview. Resulting area will be previewSize + 10 pixel border.
	 */
	public JFileChooserPreview(int previewWidth, String title, int leftBorder, int rightBorder, PreviewHandler... handlers)
	{
		super(new BorderLayout());
		if (leftBorder > 0)
			setBorder(BorderFactory.createEmptyBorder(0, leftBorder, 0, rightBorder));

		defaultBackgroundColor_ = UIManager.getDefaults().getColor("Label.background");
		super.setBackground(defaultBackgroundColor_);

		Font f = getFont();

		previewConfig_.previewWidth_ = previewWidth;
		setErrorImage(null);
		setErrorText(null);
		setLoadingText(null);

		emptyPreview_ = new JLabel("");

		contentArea_ = new JPanel(new BorderLayout());
		add(contentArea_, BorderLayout.CENTER);

		attributeLayout_ = new GridBagLayout();
		attributeArea_ = new JPanel(attributeLayout_);

		add(attributeArea_, BorderLayout.SOUTH);

		attributeLabel_ModTime_ = new JLabel(I18N.getText("filechooser.preview.modtime"));
		attribute_ModTime_ = createAttributeTextComponent();

		attributeLabel_Size_ = new JLabel(I18N.getText("filechooser.preview.size"));
		attribute_Size_ = createAttributeTextComponent();

		if ( title != null )
		{
			JLabel titleLabel = new JLabel(title);
			titleLabel.setFont( f.deriveFont(Font.PLAIN, f.getSize()*2));
			add( titleLabel, BorderLayout.NORTH);
		}

		for (PreviewHandler h : handlers)
			addPreviewHandler(h);

		infoPreviewHandler_.setConfiguration(previewConfig_);
		infoPreviewProxy_ = infoPreviewHandler_.createPreviewProxy(null,null);

		resolvePreviewHandler.setConfiguration(previewConfig_);

		setLoadingDisplayDelay(previewConfig_.loadingDisplayDelay_);

		nf_.setMaximumFractionDigits(1);
	}

	/**
	 * Install the preview into the chooser.<br>
	 *
	 * @param fileChooser The parent chooser or null.
	 */
	public void install(JFileChooser fileChooser)
	{
		uninstall();

		fileChooser_ = fileChooser;
		fileChooser.addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, listener_);
		fileChooser.setAccessory(this);
	}

	/**
	 * Install the preview into the chooser.<br>
	 *
	 * @param pathChooser The parent chooser or null.
	 */
	public void install(JPathChooser pathChooser)
	{
		uninstall();
		pathChooser_ = pathChooser;
		pathChooser_.addPathSelectionListener(pathListener_);
		pathChooser_.addAccessory(this);
	}


	/**
	 * Removed the preview from the current file-chooser.
	 */
	public void uninstall()
	{
		if (fileChooser_ != null)
		{
			fileChooser_.removePropertyChangeListener(listener_);
			if (this == fileChooser_.getAccessory())
			{
				fileChooser_.setAccessory(null);
			}
		}
		fileChooser_ = null;
		if (pathChooser_ != null)
		{
			pathChooser_.removePathSelectionListener(pathListener_);
			pathChooser_.removeAccessory(this);
		}
		pathChooser_ = null;
		setPreview(null, null );
	}


	/**
	 * Adds a new preview handler.
	 */
	public void addPreviewHandler(PreviewHandler handler)
	{
		previewHandler_.add(handler);
		handler.setConfiguration(previewConfig_);
	}

	/**
	 * Gets a preview handler by type.
	 */
	public <T extends PreviewHandler> T getPreviewHandler(Class<T> clazz)
	{
		Iterator<PreviewHandler> it = previewHandler_.iterator();
		while (it.hasNext())
		{
			PreviewHandler p = it.next();
			if (clazz.isAssignableFrom(p.getClass()))
				return (T) p;
		}
		return null;
	}

	/**
	 * Removes the preview handler by type.
	 * Will also remove any sub-class of the specified class.
	 *
	 * @param clazz The class to search for.
	 */
	public void removePreviewHandler(Class<? extends PreviewHandler> clazz)
	{
		Iterator<PreviewHandler> it = previewHandler_.iterator();
		while (it.hasNext())
		{
			PreviewHandler p = it.next();
			if (clazz.isAssignableFrom(p.getClass()))
				it.remove();
		}
	}

	/**
	 * Sets the image to show on error, if a preview is not
	 * possibly. Default is null.
	 *
	 * @param image The new image or null to show the error-text instead.
	 */
	public void setErrorImage(Image image)
	{
		previewConfig_.errorImage_ = image;
	}

	/**
	 * Sets the text to show on error, if a preview is not
	 * possibly. Default is I18N with key "filechooser.preview.error".<br>
	 * The text is shown only if no error-image is set.
	 *
	 * @param text The new text or null to restore to default.
	 */
	public void setErrorText(String text)
	{
		previewConfig_.errorText_ = (text == null) ? I18N.getText("filechooser.preview.error") : text;
	}

	/**
	 * Sets the image to show during background-loading.
	 * Default is null.
	 *
	 * @param image The new image or null to show the loading-text instead.
	 */
	public void setLoadingImage(Image image)
	{
		previewConfig_.loadingImage_ = image;
	}

	/**
	 * Sets the text to show during background-loading.
	 * Default is I18N with key "filechooser.preview.loading".
	 * The text is shown only if no loading-image is set.
	 *
	 * @param text The new text or null to restore default.
	 */
	public void setLoadingText(String text)
	{
		previewConfig_.loadingText_ = (text == null) ? I18N.getText("filechooser.preview.loading") : text;
	}

	/**
	 * Sets the delay to wait for the background-job to load the preview.
	 * Default is 200ms
	 *
	 * @param milliSeconds Milliseconds to wait until Loading text/image is shown.
	 */
	public void setLoadingDisplayDelay(int milliSeconds)
	{
		if (loading_ == null || previewConfig_.loadingDisplayDelay_ != milliSeconds)
		{
			previewConfig_.loadingDisplayDelay_ = milliSeconds;
			if (milliSeconds > 0)
			{
				if (loading_ == null)
				{
					loading_ = new Timer(previewConfig_.loadingDisplayDelay_, e ->
					{
						PreviewProxy proxy = infoPreviewHandler_.createPreviewProxy(null, previewConfig_.loadingText_);
						setPreview(proxy, infoPreviewHandler_);
					});
					loading_.setRepeats(false);
				}
				else
				{
					if (loading_.isRunning())
						loading_.stop();
					loading_.setInitialDelay(milliSeconds);
				}
			}
		}
	}

	/**
	 * Helper to create a text component for an attribute.
	 */
	protected JTextArea createAttributeTextComponent()
	{
		JTextArea t = new JTextArea();
		UIDefaults defaults = UIManager.getDefaults();
		t.setFont(defaults.getFont("Label.font"));
		t.setBackground(defaultBackgroundColor_);
		t.setEditable(false);
		return t;
	}

    @Override
	public void setBackground(Color col )
	{
		// Ignored calls from super-ctor.
		if ( defaultBackgroundColor_ != null )
		{
			if (col == null)
			{
				Container ct = getParent();
				if (ct != null)
					col = ct.getBackground();
			}
			if (col != null && !col.equals(defaultBackgroundColor_))
			{
				defaultBackgroundColor_ = col;
				super.setBackground(col);
				attributeArea_.setBackground(col);
				attribute_Size_.setBackground(col);
				attribute_ModTime_.setBackground(col);
				for (JLabel t : additionalAttributesLabels_)
					t.setBackground(col);
				for (JTextComponent t : additionalAttributesText_)
					t.setBackground(col);
				contentArea_.setBackground(col);
			}
		}
		else
			super.setBackground(col);
	}

	/**
	 * Updates the preview for the selected file.<br>
	 * For un-supported files the preview will be cleared.
	 *
	 * @param file The currently selected file.
	 */
	protected void setFile(File file)
	{
		setFile(file.toPath());
	}

	/**
	 * Updates the previews for the selected path.<br>
	 * For un-supported files the preview will be cleared.
	 *
	 * @param path The currently selected path.
	 */
	protected void setFile(Path path)
	{
		PreviewProxy proxy = null;
		PreviewHandler handler = null;
		try
		{
			if (path != null)
			{
				PreviewHandler ph = null;
				final Path normalizedFile = path.normalize();

				final String filename = normalizedFile.getFileName()
													  .toString();
				// Try to resolve windows "lnk" files, mainly for the "recent"-view.
				if (IS_WINDOWS && filename.toUpperCase()
										  .endsWith(".LNK"))
				{
					handler = resolvePreviewHandler;
					proxy = resolvePreviewHandler.createPreviewProxy(normalizedFile, filename);

				}
				else
				{
					for ( int i=0 ; i<previewHandler_.size(); ++i)
					{
						ph = previewHandler_.get(i);
						proxy = ph.getPreviewProxy(path, normalizedFile.toUri().toString());
						if (proxy != null)
						{
							handler = ph;
							break;
						}
					}
				}
				if (null != proxy)
				{
					if ( proxy.needsUpdate())
					{
						if (Log.isDebugEnabled())
							Log.debug("updatePreview " + proxy);
						ph.updatePreviewProxy(proxy);
					}

					if (pendingProxy_ != null && pendingProxy_ != proxy)
					{
						pendingProxy_.activeAndPending = false;
					}
					pendingProxy_ = proxy;
				}
			}
		}
		catch (Exception e)
		{
		}
		setPreview(proxy, handler);
	}

	/**
	 * Updates the preview.
	 *
	 * @param proxy The proxy to show or null.
	 */
	protected void setPreview(final PreviewProxy proxy, final PreviewHandler handler)
	{
		if (loading_ != null && loading_.isRunning()) loading_.stop();

		Runnable r = () ->
		{
			if (Log.isDebugEnabled())
				Log.debug("setPreview " + proxy);
			if (proxy == null)
			{
				contentArea_.removeAll();
				attributeArea_.removeAll();
			}
			else
			{
				String message2Show = null;

				synchronized (proxy)
				{
					if (proxy.complete)
					{
						message2Show = proxy.message_;
					}
					else
					{
						if (loading_ != null &&
								(previewConfig_.loadingImage_ != null || previewConfig_.loadingText_ != null))
						{
							loading_.start();
						}
						else
						{
							message2Show = previewConfig_.loadingText_;
						}
					}
				}

				Component c;
				if (message2Show != null)
				{
					infoPreviewProxy_.message_ = message2Show;
					c = infoPreviewHandler_.getPreviewComponent(infoPreviewProxy_);
				}
				else
				{
					infoPreviewProxy_.message_ = null;
					c = handler.getPreviewComponent(proxy);
				}
				contentArea_.removeAll();
				contentArea_.add(BorderLayout.CENTER, c);

				// Set-up attributes
				List<JComponent> attributes = new ArrayList<>();
				if (proxy.lastMod_ != 0)
				{
					attribute_ModTime_.setText(df_.withZone(ZoneId.systemDefault())
												  .format(LocalDateTime.ofInstant(Instant.ofEpochMilli(proxy.lastMod_), ZoneId.systemDefault())));
					attributes.add(attributeLabel_ModTime_);
					attributes.add(attribute_ModTime_);
				}
				if (proxy.size_ > -1)
				{
					attribute_Size_.setText(UITool.formatStorageSizeBinary(nf_, proxy.size_));
					attributes.add(attributeLabel_Size_);
					attributes.add(attribute_Size_);
				}

				// Set additional attribute values
				for (int ai = 0; ai < proxy.additionalInformation_.size(); ++ai)
				{
					PreviewProxy.InfoEntry ie = proxy.additionalInformation_.get(ai);

					// Fill up component cache as needed.
					if (ai <= additionalAttributesLabels_.size())
						additionalAttributesLabels_.add(new JLabel());

					if (ai <= additionalAttributesText_.size())
						additionalAttributesText_.add(createAttributeTextComponent());

					JLabel label = additionalAttributesLabels_.get(ai);
					JTextComponent text = additionalAttributesText_.get(ai);

					label.setText(ie.name);
					text.setText(ie.value);
					attributes.add(label);
					attributes.add(text);
				}

				// Remove components
				final int attributesNeeded = attributes.size();
				while (attributesNeeded < attributeArea_.getComponentCount())
					attributeArea_.remove(attributeArea_.getComponentCount() - 1);

				// Add components
				if (attributesNeeded > attributeArea_.getComponentCount())
				{
					GridBagConstraints gc = new GridBagConstraints();
					gc.gridy = attributeArea_.getComponentCount() / 2;
					gc.insets = new Insets(0, 0, 0, 5);
					while (attributesNeeded > attributeArea_.getComponentCount())
					{
						gc.anchor = GridBagConstraints.WEST;
						gc.gridx = 0;
						gc.weightx = 0;
						gc.insets.right = 5;
						attributeArea_.add(attributes.get(attributeArea_.getComponentCount()), gc);
						gc.anchor = GridBagConstraints.NORTHWEST;
						gc.gridx = 1;
						gc.weightx = 1;
						gc.insets.right = 0;
						attributeArea_.add(attributes.get(attributeArea_.getComponentCount()), gc);
						++gc.gridy;
					}
				}
			}
			validate();
		};
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
			SwingUtilities.invokeLater(r);
	}

	/**
	 * To prevent users to do some silly stuff via "setMaximumSize" etc.,
	 * this method <i>always</i> returns the configured preview-width + border.
	 */
	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();
		int w = previewConfig_.previewWidth_;
		Border b = getBorder();
		if (b != null)
		{
			Insets i = getBorder().getBorderInsets(this);
			w += i.left + i.right;
		}
		d.width = w;
		return d;
	}

}
