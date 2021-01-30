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
package com.bw.jtools.ui.datatable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exporter implementation using Apache CSV.
 */
public class DataCSVExporter implements IDataExporter
{
    public DataCSVExporter()
    {
        // Ensure that we can use CSV functionality.
        @SuppressWarnings("unused")
		CSVFormat f = CSVFormat.EXCEL;
    }

    @Override
    public void export( File file, DataTableModel model ) throws IOException
    {
            FileWriter writer = new FileWriter(file, false);

            final int CN = model.getColumnCount();
            String[] columns = new String[ CN ];
            for ( int ci=0 ; ci<CN; ++ci )
                columns[ci] = model.getColumnName(ci);


            CSVFormat csvFormat = CSVFormat.RFC4180.withHeader( columns );

            CSVPrinter printer = csvFormat.print(writer);

            final int rowCount = model.getRowCount();
            Object[] row = new Object[ CN ];
            for (int rIdx = 0; rIdx < rowCount; ++rIdx)
            {
                for ( int ci=0 ; ci<CN; ++ci )
                    row[ci] = model.getValueAt(rIdx, ci);

                printer.printRecord( row );
            }
            printer.flush();
            printer.close();
    }
}
