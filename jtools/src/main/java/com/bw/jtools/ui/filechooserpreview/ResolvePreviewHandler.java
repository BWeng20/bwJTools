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

import com.bw.jtools.io.IOTool;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles windows lnk files.
 */
public class ResolvePreviewHandler extends InfoPreviewHandler
{

	/**
	 * Creates a new handler.
	 */
	protected ResolvePreviewHandler()
	{
	}

	/**
	 * Worker to resolve windows lnk Files.
	 */
	protected class ResolveSwingWorker extends SwingWorker<File, Object>
	{
		protected final PreviewProxy proxy_;

		protected ResolveSwingWorker(PreviewProxy proxy)
		{
			proxy_ = proxy;
		}


		@Override
		protected File doInBackground() throws Exception
		{
			return IOTool.resolveWindowsLinkFile(proxy_.file_.toFile());
		}

		@Override
		protected void done()
		{
			if (proxy_.activeAndPending)
			{
				try
				{
					config_.setFile(get());
				}
				catch (Exception e)
				{
				}
			}
		}
	}


	@Override
	protected InfoPreviewProxy createPreviewProxy(Path file, String canonicalPath)
	{
		InfoPreviewProxy proxy = new InfoPreviewProxy(this);
		proxy.activeAndPending = true;
		proxy.name_ = file.getFileName().toString();;
		proxy.file_ = file;

		new ResolveSwingWorker(proxy).execute();

		return proxy;
	}
}
