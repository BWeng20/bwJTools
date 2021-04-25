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

import java.awt.*;

/**
 * Holds configuration data for the preview-handlers.<br>
 * An instance of this class is bound to one instance of JFileChooserPreview and shared
 * with all connected preview-handlers.
 */
public abstract class PreviewConfig
{
	/**
	 * The width of the preview area.
	 */
	public int previewWidth_;
	/**
	 * I18N text to show on loading.
	 */
	public String loadingText_;

	/**
	 * Image to show on error.
	 *
	 * @see JFileChooserPreview#setErrorImage(Image)
	 */
	public Image errorImage_;

	/**
	 * Message to show on error.
	 *
	 * @see JFileChooserPreview#setErrorText(String)
	 */
	public String errorText_;

	/**
	 * Image to show on loading.
	 *
	 * @see JFileChooserPreview#setLoadingImage(Image)
	 */
	public Image loadingImage_;

	/**
	 * Delay until loading test/image is shown.
	 *
	 * @see JFileChooserPreview#setLoadingDisplayDelay(int)
	 */
	public int loadingDisplayDelay_ = 200;

	/**
	 * Called by preview proxies if the preview should be updated.<br>
	 * Can be called also from outside the UI-thread.<br>
	 * Implemented by JFileChooserPreview.
	 */
	public abstract void update(PreviewProxy proxy);
}
