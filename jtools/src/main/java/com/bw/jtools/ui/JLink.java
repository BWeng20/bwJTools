/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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
package com.bw.jtools.ui;

import com.bw.jtools.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

/**
 * Label with "link" functionality.<br>
 * If clicked, the class requested to "browse" the link via the "Desktop"
 * interface.<br>
 * In case of web-links the standard web-browser will open and show the
 * referenced page.<br>
 * Check java.awt.Desktop.isDesktopSupported() and java.awt.Desktop.browse().
 */
public class JLink extends JLabel
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 2361958659238654569L;

	URI uri_;
	boolean force_disable_ = true;
	boolean enabled_ = true;
	String alias_;

	String col_ = "blue";
	boolean mouse_entered_ = false;

	static boolean log_desktop_support_missing_ = true;

	/**
	 * Sets the URI to link to.
	 * 
	 * @param uri The URI call on click.
	 */
	public void setUri(String uri)
	{
		try
		{
			col_ = "blue";
			uri_ = new URI(uri);
			setToolTipText(uri);
			force_disable_ = !Desktop.isDesktopSupported();
		} catch (Exception uriEx)
		{
			setToolTipText(null);
			Log.error("Malformed URI " + uri, uriEx);
			force_disable_ = true;
		}
		if (force_disable_)
			super.setEnabled(false);
		else
			super.setEnabled(enabled_);
	}

	/**
	 * Sets the shown Alias.
	 * 
	 * @param name The alias to show instead of the full link.
	 */
	public void setAlias(String name)
	{
		alias_ = name;
		update_text();
	}

	private void update_text()
	{
		setText(mouse_entered_ ? "<HTML><FONT color='" + col_ + "'><u>" + alias_ + "</u></FONT></HTML>"
		        : "<HTML><FONT color='" + col_ + "'>" + alias_ + "</FONT></HTML>");
	}

	/**
	 * Create a link label with URI and alias text.
	 * 
	 * @param uri  The URI to link to.
	 * @param text The shown text.
	 */
	public JLink(String uri, String text)
	{
		this();
		setUri(uri);
		setAlias(text);
	}

	/**
	 * Creates a link label with initially empty URI and alias text.
	 */
	public JLink()
	{
		if (Desktop.isDesktopSupported())
		{
			force_disable_ = false;
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e)
				{
					try
					{
						Desktop.getDesktop().browse(uri_);
						col_ = "#660099";
						update_text();

					} catch (Exception exp)
					{
						Log.error("Failed to open " + uri_, exp);
					}
				}

				public void mouseEntered(MouseEvent e)
				{
					mouse_entered_ = true;
					update_text();
				}

				public void mouseExited(MouseEvent e)
				{
					mouse_entered_ = false;
					update_text();
				}

			});
		} else
		{
			force_disable_ = true;
			// Report this only once.
			if (log_desktop_support_missing_)
			{
				log_desktop_support_missing_ = false;
				Log.error("No Desktop Support available, link buttons will be disabled.");
			}
		}

	}

	public void setEnabled(boolean enabled)
	{
		enabled_ = enabled;
		super.setEnabled(enabled && force_disable_);
	}

}
