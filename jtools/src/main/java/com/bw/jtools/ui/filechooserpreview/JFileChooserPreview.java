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
import com.bw.jtools.ui.I18N;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Preview accessory for some known file-types in a {@link javax.swing.JFileChooser JFileChooser}.<br>
 * Can be used via {@link javax.swing.JFileChooser#setAccessory(JComponent)} to add a preview for files.
 * <br>
 * As default a image- and a text-preview handler is added. Other previews can be added via the
 * PreviewHandler interface.
 * <br>
 * Previews are loaded in background and weakly cached globally across all instances of this class.
 * Different preview-sizes may result in different versions in the case - mainly for images previews.<br>
 *
 * <br>
 * Will not work with Windows (c) LAF for "lnk" files as there is not platform independent way
 * (beside some libs out there) to simply follow these "links".<br>
 * <i>Example:</i>
 * <code>
 * JFileChooser fileChooser = new JFileChooser();
 * fileChooser.setFileHidingEnabled(true);
 * JFileChooserPreview preview = new JFileChooserPreview(300);
 * // you can also configure the default preview-handler:
 * preview.getPreviewHandler(TextPreviewHandler.class).setPreviewTextLength(2048);
 * // Hook the preview inside the chooser.
 * preview.install(fileChooser);
 * File file = fileChooser.showDialog(null, "DEMO");
 * <p>
 * // If preview should be detached from the chooser...
 * preview.uninstall();
 * </code>
 *
 */
public class JFileChooserPreview extends JPanel
{
    protected JFileChooser fileChooser_;

    /** Content preview area. */
    protected JPanel contentArea_;

    /** Card layout for the content preview area to switch between text and image based previews. */
    protected CardLayout contentAreaCardLayout_;

    /** Label inside the content-area to show images and alternative messages. */
    protected JLabel previewLabel_;
    /** Icon used inside {@link #previewLabel_} to images. */
    protected ImageIcon previewIcon_;
    /** TextArea to show text based content previews. */
    protected JTextArea previewText_;

    /**
     * Used for delayed "loading" display.
     */
    protected Timer loading_;

    /** The currently pending proxy. */
    protected PreviewProxy pendingProxy_;

    /**
     * Component name for the text based preview inside the content area.
     * Used together with {@link #contentAreaCardLayout_}.
     */
    protected static final String CONTENT_PREVIEW_TEXT = "TEXT";
    /**
     * Component name for the image based preview inside the content area.
     * Used together with {@link #contentAreaCardLayout_}.
     */
    protected static final String CONTENT_PREVIEW_IMAGE = "IMAGE";

    /** Space to the file-chooser. */
    protected static final int  LEFT_BORDER_WIDTH = 10;

    /**
     * Preview config implementation, shared with the preview-handers.
     */
    protected final PreviewConfig previewConfig_ = new PreviewConfig() {

        @Override
        public void update(PreviewProxy proxy)
        {
            setPreview(proxy);
        }
    };

    /**
     * Property Listener to react on selection changes.
     *
     * @see #install(JFileChooser)
     */
    protected java.beans.PropertyChangeListener listener_ = changeEvent -> {
        String changeName = changeEvent.getPropertyName();
        if (changeName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
        {
            File file = (File) changeEvent.getNewValue();
            updatePreview(file);
        }
    };

    /**
     * Creates a new preview.<br>
     *
     * @param previewWidth The width of the image preview. Resulting area will be previewSize + 10 pixel border.
     */
    public JFileChooserPreview(int previewWidth)
    {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, LEFT_BORDER_WIDTH, 0, 0));

        previewConfig_.previewWidth_ = previewWidth;
        setErrorImage(null);
        setErrorText(null);
        setLoadingText(null);

        previewLabel_ = new JLabel();
        previewLabel_.setHorizontalAlignment(JLabel.CENTER);
        previewLabel_.setVerticalAlignment(JLabel.CENTER);
        previewLabel_.setPreferredSize(new Dimension(previewWidth, previewWidth));
        Font f = previewLabel_.getFont();
        previewLabel_.setFont(f.deriveFont(Font.PLAIN, f.getSize() * 2));

        previewText_ = new JTextArea();
        previewText_.setEditable(false);
        previewText_.setDisabledTextColor( previewText_.getForeground() );

        contentArea_ = new JPanel(contentAreaCardLayout_ =new CardLayout());
        contentArea_.add(previewLabel_, CONTENT_PREVIEW_IMAGE);
        contentArea_.add(previewText_, CONTENT_PREVIEW_TEXT);

        contentAreaCardLayout_.show(contentArea_, CONTENT_PREVIEW_IMAGE);
        add(contentArea_, BorderLayout.CENTER);

        addPreviewHandler(new ImagePreviewHandler());
        addPreviewHandler(new TextPreviewHandler());

        setLoadingDisplayDelay(previewConfig_.loadingDisplayDelay_);

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
        setPreview(null);
    }

    /** List of preview-handlers. */
    protected List<PreviewHandler> previewHandler_ = new ArrayList<>();

    /**
     * Adds a new preview handler.
     */
    public void addPreviewHandler( PreviewHandler handler)
    {
        previewHandler_.add( handler );
        handler.setConfiguration(previewConfig_);
    }

    /**
     * Gets a preview handler by type.
     */
    public <T extends PreviewHandler> T getPreviewHandler( Class<T> clazz)
    {
        Iterator<PreviewHandler> it = previewHandler_.iterator();
        while ( it.hasNext() )
        {
            PreviewHandler p = it.next();
            if ( clazz.isAssignableFrom(  p.getClass() ))
                return (T)p;
        }
        return null;
    }

    /**
     * Removes the preview handler by type.
     * Will also remove any sub-class of the specified class.
     * @param clazz The class to search for.
     */
    public void removePreviewHandler( Class<? extends PreviewHandler> clazz)
    {
        Iterator<PreviewHandler> it = previewHandler_.iterator();
        while ( it.hasNext() )
        {
            PreviewHandler p = it.next();
            if ( clazz.isAssignableFrom(  p.getClass() ))
                it.remove();
        }
    }

    /**
     * Sets the image to show on error, if a preview is not
     * possibly. Default is null.
     * @param image The new image or null to show the error-text instead.
     */
    public void setErrorImage( Image image )
    {
        previewConfig_.errorImage_ = image;
    }

    /**
     * Sets the text to show on error, if a preview is not
     * possibly. Default is I18N with key "filechooser.preview.error".<br>
     * The text is shown only if no error-image is set.
     * @param text The new text or null to restore to default.
     */
    public void setErrorText( String text )
    {
        previewConfig_.errorText_ = (text == null)? I18N.getText("filechooser.preview.error") : text;
    }

    /**
     * Sets the image to show during background-loading.
     * Default is null.
     * @param image The new image or null to show the loading-text instead.
     */
    public void setLoadingImage( Image image )
    {
        previewConfig_.loadingImage_ = image;
    }

    /**
     * Sets the text to show during background-loading.
     * Default is I18N with key "filechooser.preview.loading".
     * The text is shown only if no loading-image is set.
     * @param text The new text or null to restore default.
     */
    public void setLoadingText( String text )
    {
        previewConfig_.loadingText_ = (text == null)? I18N.getText("filechooser.preview.loading") : text;
    }

    /**
     * Sets the delay to wait for the background-job to load the preview.
     * Default is 200ms
     * @param milliSeconds Milliseconds to wait until Loading text/image is shown.
     */
    public void setLoadingDisplayDelay( int milliSeconds )
    {
        if ( loading_ == null || previewConfig_.loadingDisplayDelay_ != milliSeconds)
        {
            previewConfig_.loadingDisplayDelay_ = milliSeconds;
            if ( milliSeconds > 0)
            {
                if (loading_ == null)
                {
                    loading_ = new Timer(previewConfig_.loadingDisplayDelay_, e -> {
                        PreviewProxy proxy = new PreviewProxy();
                        proxy.name_ = "Loading";
                        proxy.complete = true;
                        proxy.imageContent_ = previewConfig_.loadingImage_;
                        proxy.message_ = previewConfig_.loadingText_;
                        setPreview(proxy);
                    });
                    loading_.setRepeats(false);
                } else
                {
                    if (loading_.isRunning())
                        loading_.stop();
                    loading_.setInitialDelay(milliSeconds);
                }
            }
        }
    }

    /**
     * Updates the preview for the selected file.<br>
     * For un-supported files the preview will be cleared.
     *
     * @param file The currently selected file.
     */
    protected void updatePreview(File file)
    {
        PreviewProxy proxy = null;
        try
        {
            if (file != null)
            {
                final String canonicalPath = file.getCanonicalPath();

                for ( PreviewHandler ph : previewHandler_)
                {
                    proxy = ph.getPreviewProxy(file, canonicalPath);
                    if ( null != proxy )
                    {
                        if ( pendingProxy_ != null && pendingProxy_ != proxy )
                        {
                            pendingProxy_.activeAndPending = false;
                        }
                        pendingProxy_ = proxy;
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
        }
        setPreview(proxy);
    }

    /**
     * Updates the preview.
     *
     * @param proxy The proxy to show or null.
     */
    protected void setPreview(final PreviewProxy proxy)
    {
        if ( loading_ != null && loading_.isRunning() )  loading_.stop();

        Runnable r = () -> {
            if ( Log.isDebugEnabled() )
                Log.debug( "setPreview "+proxy);
            if (proxy == null)
            {
                previewLabel_.setIcon(null);
                previewLabel_.setText(null);
                contentAreaCardLayout_.show(contentArea_, CONTENT_PREVIEW_IMAGE);
            }
            else
            {
                Image image2Show = null;
                String message2Show = null;
                String text2Show = null;

                synchronized (proxy)
                {
                    if (proxy.complete)
                    {
                        image2Show = proxy.imageContent_;
                        text2Show =  proxy.textContent_;
                        message2Show = proxy.message_;
                    }
                    else
                    {
                        if ( loading_ != null &&
                                ( previewConfig_.loadingImage_ != null || previewConfig_.loadingText_ != null))
                        {
                            loading_.start();
                        }
                        else
                        {
                            image2Show = previewConfig_.loadingImage_;
                            text2Show = null;
                            message2Show = previewConfig_.loadingText_;
                        }
                    }
                }

                if ( image2Show != null )
                {
                    previewLabel_.setText(null);
                    if (previewIcon_ == null)
                        previewIcon_ = new ImageIcon(image2Show);
                    else
                        previewIcon_.setImage(image2Show);
                    previewLabel_.setIcon(previewIcon_);
                    contentAreaCardLayout_.show(contentArea_, CONTENT_PREVIEW_IMAGE);
                }
                else if ( text2Show != null )
                {
                    previewText_.setText(text2Show);
                    contentAreaCardLayout_.show(contentArea_, CONTENT_PREVIEW_TEXT);
                }
                else if ( message2Show != null )
                {
                    previewLabel_.setText(message2Show);
                    previewLabel_.setIcon(null);
                    contentAreaCardLayout_.show(contentArea_, CONTENT_PREVIEW_IMAGE);
                }
                else
                {
                    previewLabel_.setText(null);
                    previewLabel_.setIcon(null);
                    contentAreaCardLayout_.show(contentArea_, CONTENT_PREVIEW_IMAGE);
                }
            }
        };
        if ( SwingUtilities.isEventDispatchThread() )
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
        d.width = previewConfig_.previewWidth_+ LEFT_BORDER_WIDTH;
        return d;
    }

    /**
     * With this main method, the class can be used to select a file from a batch script
     * (ok, there are better options out there, so take this as example).
     */
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
        }

        // Be very quite
        Log.setLevel(Log.DEBUG);
        JFileChooser ch = new JFileChooser();
        ch.setFileHidingEnabled(true);
        ch.setFileSelectionMode(JFileChooser.FILES_ONLY);

        JFileChooserPreview preview = new JFileChooserPreview(300);
        // Suppress display of "loading"
        preview.setLoadingDisplayDelay(200);
        preview.install(ch);
        ch.showDialog(null, "OK");
        preview.uninstall();
        File f = ch.getSelectedFile();
        if ( f == null )
        {
            System.exit(1);
        }
        else
        {
            System.out.println(f.getPath());
            System.exit(0);
        }
    }

}
