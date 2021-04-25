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
package com.bw.jtools.ui.datatable;

import com.bw.jtools.Log;
import com.bw.jtools.ui.I18N;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Data model for Data Table that connects to the internal Log-Interface.<br>
 * Can be used to show the internal log of the running application.<br>
 * The data model should be created before the application starts to collect
 * also start-up logs.<br>
 * The model can later be connected to some DataTable.
 */
public class LoggerDataModel extends DataTableModel
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 7840030172914321289L;

	/**
	 * Logger back-end that feeds the log-table-model.
	 */
	protected class LogTableLogger extends Log.LoggerFacade
	{
		Calendar calendar_;

		public LogTableLogger()
		{
			calendar_ = Calendar.getInstance();
		}

		@Override
		public void error(CharSequence msg)
		{
			LoggerDataModel.this.addRow(new Object[]{calendar_.getTime(), Log.ERROR, msg});
		}

		@Override
		public void warn(CharSequence msg)
		{
			LoggerDataModel.this.addRow(new Object[]{calendar_.getTime(), Log.WARN, msg});
		}

		@Override
		public void info(CharSequence msg)
		{
			LoggerDataModel.this.addRow(new Object[]{calendar_.getTime(), Log.INFO, msg});
		}

		@Override
		public void debug(CharSequence msg)
		{
			LoggerDataModel.this.addRow(new Object[]{calendar_.getTime(), Log.DEBUG, msg});
		}

	}

	LogTableLogger logger_;

	/**
	 * Creates a new Log Model.<br>
	 * The model starts to collect logs immediately after this call.
	 */
	public LoggerDataModel()
	{
		super(new Object[]
				{
						I18N.getText("logtable.column.time"), I18N.getText("logtable.column.level"), I18N.getText("logtable.column.message")
				}, 0);

		logger_ = new LogTableLogger();
		Log.addLogger(logger_);
	}

	/**
	 * Gets the internal logger that feeds thus model.
	 *
	 * @return The Logger.
	 */
	public Log.LoggerFacade getLogger()
	{
		return logger_;
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	private DefaultTableCellRenderer defaultCellRenderer_ = new DefaultTableCellRenderer();
	private DateRenderer dateRenderer_ = new DateRenderer();
	private LevelRenderer levelRenderer_ = new LevelRenderer();

	public static class DateRenderer extends DefaultTableCellRenderer
	{
		/**
		 * Generated Serial Version
		 */
		private static final long serialVersionUID = -4804789855338153948L;

		public DateFormat format_;

		public DateRenderer()
		{
			super();
			format_ = new SimpleDateFormat("yy-MM-dd HH:mm:ss.S");
		}

		public void setValue(Object value)
		{
			setText((value == null) ? "" : format_.format(value));
		}
	}

	public static class LevelRenderer extends DefaultTableCellRenderer
	{
		/**
		 * Generated Serial Version
		 */
		private static final long serialVersionUID = -8735962220537799583L;

		final String DEBUG_LABEL = "DBG";
		final String INFO_LABEL = "INF";
		final String WARN_LABEL = "WRN";
		final String ERROR_LABEL = "ERR";


		public LevelRenderer()
		{
		}

		public void setValue(Object value)
		{
			if (value instanceof Number)
			{
				switch (((Number) value).intValue())
				{
					case Log.DEBUG:
						setText(DEBUG_LABEL);
						break;
					case Log.INFO:
						setText(INFO_LABEL);
						break;
					case Log.ERROR:
						setText(ERROR_LABEL);
						break;
					case Log.WARN:
						setText(WARN_LABEL);
						break;
					default:
						setText(value.toString());
				}
			}
			else
				setText(value == null ? "" : String.valueOf(value));
		}
	}

	@Override
	public TableCellRenderer getCellRenderer(int colIndex)
	{
		switch (colIndex)
		{
			case 0:
				return dateRenderer_;
			case 1:
				return levelRenderer_;
			default:
				return defaultCellRenderer_;
		}
	}

	/**
	 * Sets the format for the date/time column.
	 *
	 * @param df The new date-time-format to use.
	 */
	public void setDateFormat(DateFormat df)
	{
		if (df != null)
		{
			dateRenderer_.format_ = df;
			fireTableDataChanged();
		}
	}


}
