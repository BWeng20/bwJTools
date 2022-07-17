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
package com.bw.jtools.ui.pathchooser;

import com.bw.jtools.io.IOTool;
import com.bw.jtools.ui.JAutoCompleteTextField;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PathAutoCompletionModel implements JAutoCompleteTextField.AutoCompletionModel
{
	private final PathInfoProvider pathInfoProvider_;

	/**
	 * Creates a Autocompletion Model for a path provider.
	 * @param provider The provider
	 */
	public PathAutoCompletionModel( PathInfoProvider provider )
	{
		pathInfoProvider_ = provider;
	}

	@Override
	public boolean isWordCharacter( char c )
	{
		return true;
	}

	@Override
	public String getText2AddAfterCommit()
	{
		return null;
	}

	@Override
	public List<String> match(String prefix)
	{
		List<String> list = new ArrayList();
		try
		{
			int i = Math.max( prefix.lastIndexOf('/'), prefix.lastIndexOf('\\'));
			if ( i >= 0 && i<(prefix.length()-1)) {

				String dir = prefix.substring(0, i );
				String filePrefix = prefix.substring(i+1);

				Path dirPath;

				if ( IOTool.isUri(dir) )
				{
					URI uri = URI.create(dir);
					dirPath = Paths.get(uri);
				}
				else
				{
					dirPath = IOTool.getPath(dir);
				}

				PathInfo pi = pathInfoProvider_.createPathInfo(dirPath);
				if ( pi.isTraversable() )
				{
					for (PathInfo p : pathInfoProvider_.getChildren(pi))
					{
						if (p.getFileName().startsWith(filePrefix))
						{
							list.add( prefix.substring(0, i+1 )+ p.getFileName() );
						}
					}
				}
			}
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
}
