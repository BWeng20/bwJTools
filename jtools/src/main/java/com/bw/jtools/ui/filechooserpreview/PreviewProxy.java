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

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to store and track the loading state of a preview for each file.<br>
 * Instances of this class are weakly cached.<br>
 * Preview-handlers-implementations which need addition data may create sub-classes of this class.
 */
public class PreviewProxy
{
	protected static class InfoEntry
	{
		public String name;
		public String value;
	}

	private static final NumberFormat nf;

	static
	{
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	/**
	 * True if loading is complete.
	 */
	public boolean complete = false;

	/**
	 * True if file is currently the active preview and still working.
	 */
	public boolean activeAndPending = false;

	/**
	 * The file name
	 */
	public String name_;

	/**
	 * The file url
	 */
	public String uri_;

	public long size_ = -1;
	public long lastMod_;

	/**
	 * Image content.
	 */
	public Image imageContent_;
	/**
	 * Text content.
	 */
	public String textContent_;

	/**
	 * Text to show if content is not available.
	 */
	public String message_;

	public List<InfoEntry> additionalInformation_ = new ArrayList<>();

	protected PreviewConfig config_;

	public final long createTimeMS_ = System.currentTimeMillis();

	protected PreviewProxy()
	{
	}

	/**
	 * Explicit Clean-Up.
	 * May be called to speed up gc.
	 */
	protected void dispose()
	{
		additionalInformation_.clear();
		imageContent_ = null;
		config_ = null;
	}

	/**
	 * Logs a message with file name and time since initialisation.<br>
	 * Example: Profile.jpg [13.23s] Loaded
	 */
	protected void log(String msg)
	{
		if (Log.isDebugEnabled())
		{
			final long t = System.currentTimeMillis();
			Log.debug(name_ + " [" + nf.format((t - createTimeMS_) / 1000f) + "s] " + msg);
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(100);
		sb.append(uri_ == null ? name_ : uri_)
		  .append(" complete:")
		  .append(complete)
		  .append(" active:")
		  .append(activeAndPending)
		  .append(" image:");
		if (imageContent_ == null)
			sb.append("none");
		else
		{
			sb.append(imageContent_.getClass()
								   .getSimpleName())
			  .append(' ');
			sb.append(imageContent_.getWidth(null))
			  .append('x')
			  .append(imageContent_.getHeight(null));
		}
		if (textContent_ != null)
			sb.append(" Text:")
			  .append(textContent_, 0, Math.min(10, textContent_.length()));
		if (message_ != null)
			sb.append(" Message:")
			  .append(message_, 0, Math.min(10, message_.length()));
		return sb.toString();
	}

}
