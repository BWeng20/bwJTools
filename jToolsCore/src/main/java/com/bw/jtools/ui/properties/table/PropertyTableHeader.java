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
package com.bw.jtools.ui.properties.table;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * Table-Header for PropertyTable that suppress display
 * by forcing UI to null.
 */
public class PropertyTableHeader extends JTableHeader
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -6150457632365752222L;

	public PropertyTableHeader(TableColumnModel model)
	{
		super(model);
		setUI(null);
	}

	@Override
	public void updateUI()
	{
	}


}
