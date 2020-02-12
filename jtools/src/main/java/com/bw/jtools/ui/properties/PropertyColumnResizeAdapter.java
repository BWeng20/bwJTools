package com.bw.jtools.ui.properties;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Container;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * Mouse handler that enabled the user to resizes columns via Drag &amp; Drop
 * in the <b>main table area</b>.
 *
 * Per default JTable-columns can only be resized via D&amp;D in the table-header.
 * The Property-table should not have any header, so I needed to re-implement
 * the JTable mouse-handler (check for BasicTableHeaderUI.MouseInputHandler).
 *
 *
 * Some remarks:<br>
 * Column resize only works if a header instance is present - simply to
 * store the currently resized column to prevent the table from re-adjusting
 * after a size change. This means that JTable.setTableHeader(null) can't be
 * used to disable the header. Instead getTableHeader().setUI(null) has
 * to be used to disable it. By this trick the table has still the instance
 * to track resize-operation, but the header is not visible.
 */
public class PropertyColumnResizeAdapter extends MouseInputAdapter
{
    private int mouseXOffset;
    private JTable table;
    private JTableHeader header;

    public static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    public static Cursor normalCursor ;
    private boolean resizeCursorSet = false;

    private TableColumn resizingColumn;

    /**
     * Create a Resize-Adapter for the table.
     * The adapter installs itself into the table.
     * @param table The table to resize.
     */
    public PropertyColumnResizeAdapter( JTable table )
    {
        this.table = table;
        this.header = table.getTableHeader();
        table.addMouseListener(this);
        table.addMouseMotionListener(this);
    }


    private TableColumn getResizingColumn(Point p)
    {
        return getResizingColumn(p, table.columnAtPoint(p));
    }

    private TableColumn getResizingColumn(Point p, int column)
    {
        if (column == -1)
        {
            return null;
        }
        Rectangle r = table.getCellRect( 0, column, true );
        if ( Math.abs(p.x-r.x)>3 && Math.abs(p.x-(r.x+r.width))>3  )
        {
            return null;
        }
        int midPoint = r.x + r.width / 2;
        int columnIndex;
        if (table.getComponentOrientation().isLeftToRight())
        {
            columnIndex = (p.x < midPoint) ? column - 1 : column;
        }
        else
        {
            columnIndex = (p.x < midPoint) ? column : column - 1;
        }
        if (columnIndex == -1)
        {
            return null;
        }
        return table.getColumnModel().getColumn(columnIndex);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        Point p = e.getPoint();

        int index = table.columnAtPoint(p);

        if (index != -1)
        {
            // The last 3 pixels + 3 pixels of next column are for resizing
            resizingColumn = getResizingColumn(p, index);
            header.setResizingColumn(resizingColumn);
            if (resizingColumn != null)
            {
                if (table.getComponentOrientation().isLeftToRight())
                {
                    mouseXOffset = p.x - resizingColumn.getWidth();
                }
                else
                {
                    mouseXOffset = p.x + resizingColumn.getWidth();
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if ((getResizingColumn(e.getPoint()) != null))
        {
            if ( !resizeCursorSet )
            {
                resizeCursorSet = true;
                normalCursor = table.getCursor();
                table.setCursor(resizeCursor);
            }
        }
        else if (resizeCursorSet)
        {
            resizeCursorSet = false;
            table.setCursor(normalCursor);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        int mouseX = e.getX();

        boolean headerLeftToRight = table.getComponentOrientation().isLeftToRight();

        if (resizingColumn != null)
        {
            int oldWidth = resizingColumn.getWidth();
            int newWidth;
            if (headerLeftToRight)
            {
                newWidth = mouseX - mouseXOffset;
            }
            else
            {
                newWidth = mouseXOffset - mouseX;
            }
            resizingColumn.setWidth(newWidth);

            Container container = table.getParent().getParent();

            if ( (container == null) || !(container instanceof JScrollPane))
            {
                return;
            }

            if (!container.getComponentOrientation().isLeftToRight()
                    && !table.getComponentOrientation().isLeftToRight())
            {
                JViewport viewport = ((JScrollPane) container).getViewport();
                int viewportWidth = viewport.getWidth();
                int diff = newWidth - oldWidth;
                int newTableWidth = table.getWidth() + diff;

                /* Resize a table */
                Dimension tableSize = table.getSize();
                tableSize.width += diff;
                table.setSize(tableSize);

                if ((newTableWidth >= viewportWidth)
                        && (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF))
                {
                    Point p = viewport.getViewPosition();
                    p.x = Math.max(0, Math.min(newTableWidth - viewportWidth,
                            p.x + diff));
                    viewport.setViewPosition(p);
                    mouseXOffset += diff;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        resizingColumn = null;
        header.setResizingColumn(null);
     }



}
