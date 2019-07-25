package com.bw.jtools.ui.properties;

import com.bw.jtools.ui.IconCache;
import com.bw.jtools.ui.JColorIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultMutableTreeNode;
import org.netbeans.swing.outline.Outline;

/**
 * Custom Cell Renderer to support different value types in the same column.
 * See @PropertyCellEditor for supported types.
 */
public class PropertyCellRenderer implements TableCellRenderer
{

    protected final Border noBorder_;
    protected final JLabel text_;
    protected final JLabel emptyLabel_;
    protected final Font font_;
    protected final Icon closed;
    protected final Icon open;
    protected final Icon empty;
    protected final JLabel groupHandle_;
    protected final Color groupBackground;
    protected final JCheckBox booleanBox_;

    protected final JLabel color_;
    protected final JColorIcon colorIcon_;


    protected final NumberFormat nf_;

    public PropertyCellRenderer(PropertyTable table)
    {
        table_ = table;
        font_ = new java.awt.Font("SansSerif", Font.PLAIN, 11);

        noBorder_ = BorderFactory.createEmptyBorder(0, 5, 0, 0);

        emptyLabel_ = new JLabel("");
        emptyLabel_.setOpaque(true);

        text_ = new JLabel();
        text_.setOpaque(true);
        text_.setFont(font_);

        groupHandle_= new JLabel();
        groupHandle_.setOpaque(true);
        groupHandle_.setFont(font_);

        booleanBox_= new JCheckBox();
        booleanBox_.setOpaque(true);
        booleanBox_.setFont(font_);

        colorIcon_ = new JColorIcon(13, 13, null);

        color_ = new JLabel();
        color_.setIcon(colorIcon_);
        color_.setOpaque(true);
        color_.setFont(font_);

        closed = IconCache.getIcon( PropertyTable.class, "group_closed.png" );
        open   = IconCache.getIcon( PropertyTable.class, "group_open.png" );

        // An empty icon for leafs, only to ensure all items are aligned.
        empty  = IconCache.getIcon( PropertyTable.class, "group_empty.png" );

        nf_ = NumberFormat.getInstance();

        groupBackground = new Color( 200,200,200 );
    }

    private final PropertyTable table_;

    public static String toString( Color col )
    {
        StringBuilder sb = new StringBuilder(20);
        sb.append(col.getRed()).append(",");
        sb.append(col.getGreen()).append(",");
        sb.append(col.getBlue());
        return sb.toString();
    }


    /**
     * Gets different widgets to handle the different data-types.
     * PropertyGroupNode are shown with an expand-icons and different background.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        PropertyTable etable = (PropertyTable) table;

        column = table.convertColumnIndexToModel(column);
        if ( etable instanceof Outline )
           --column;

        JComponent comp = null;
        Color cellForeground = null;
        Color cellBackground = null;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        boolean group = (value instanceof PropertyGroup);

        if (value != null && column == -1)
        {
            AbstractLayoutCache lc = table_.getLayoutCache();
            boolean expanded = lc.isExpanded(lc.getPathForRow(row));

            if ( group )
                groupHandle_.setIcon( expanded ? open : closed );
            else
                groupHandle_.setIcon( empty );

            groupHandle_.setText( group
                    ? ((PropertyGroup)node).displayName_
                    : ((PropertyValue)node).displayName_ );

            comp = groupHandle_;

        }
        else if (column == PropertyTable.COLUMN_VALUE)
        {
            if ( !group && node != null)
            {
                PropertyValue propVal = (PropertyValue)node;

                final Object val = node.getUserObject();

                if ( val instanceof Number )
                {
                    text_.setText( (propVal.nf_ == null ? nf_: propVal.nf_).format(val) );
                    comp = text_;
                }
                else if ( val instanceof Color )
                {
                    Color c = (Color)val;
                    colorIcon_.setColor(c);
                    color_.setText( toString( c ));
                    comp = color_;
                }
                else if ( propVal.valueClazz_ == Boolean.class && !propVal.nullable_ )
                {
                    booleanBox_.setSelected(  val != null && ((Boolean)val).booleanValue() );
                    comp = booleanBox_;
                }
                else
                {
                    final String sval = val != null ? String.valueOf(val): "";
                    text_.setText(sval);
                    comp = text_;
                }
            }
        }

        if (comp == null)
        {
            comp = emptyLabel_;
        }

        // Setting colors and border.
        // As this is look&feel depended, we have to use the UIManager to get the values.
        if ( cellForeground == null )
        {

            if (isSelected)
            {
                cellForeground = UIManager.getColor("Table.selectionForeground");
                cellBackground = UIManager.getColor("Table.selectionBackground");
            }
            else
            {
                if (group)
                    cellBackground = groupBackground;
                else
                    cellBackground = UIManager.getColor("Table.background");

                cellForeground = UIManager.getColor("Table.foreground");
            }

        }
        comp.setForeground(cellForeground);
        comp.setBackground(cellBackground);

        if (column == PropertyTable.COLUMN_VALUE)
        {
            comp.setBorder(noBorder_);
        }
        else
        {
            comp.setBorder(null);
        }
        return comp;

    }
}
