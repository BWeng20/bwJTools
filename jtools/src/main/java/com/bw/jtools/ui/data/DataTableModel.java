/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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
package com.bw.jtools.ui.data;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Abstract base for all log-models.
 */
public abstract class DataTableModel extends  DefaultTableModel
{

    /**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -4501330366939433299L;

	/**
     * Creates a model with specified columns and rows.
     * @param columns Column Names
     * @param rowCount Number of initial rows.
     */
    public DataTableModel( Object[] columns, int rowCount )
    {
        super( columns, rowCount );
    }

    /**
     * Gets the implementation specific cell renderer of the log type.
     * @param colIndex Index of column.
     * @return The cell renderer.
     */
    public abstract TableCellRenderer getCellRenderer( int colIndex );

}
