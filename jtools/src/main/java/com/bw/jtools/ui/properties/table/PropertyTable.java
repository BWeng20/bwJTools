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

import org.netbeans.swing.outline.Outline;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Table to show Property data, grouped by collapsible sections.<br>
 * <br>
 * It's designed to work in UIs where you need to show a huge amount of settings.<br>
 * At least this was my reason to create it.<br>
 * As reference please see also the example com.bw.jtools.examples.propertytable.PropertyTableDemo.
 * <br>
 * There is no "introspector" that builds a table from some object via reflection.
 * A lot more effort needed to be done to support all kinds of data (e.g. lists).<br>
 * For all wise guys that now say: "Hey... That's easy, use java.beans.PropertyEditor!!!!".<br>
 * No!!! I tried. The PropertyEditor-Framework is not really good for inplace-editing inside a swing-based table.<br>
 * <br>
 * A future version of this class may support some API to easily extent it for new data-types.
 * But not now.<br>
 * If needed, do it, the editor can easily be extended.<br>
 * <b>Check the Renderer and Editor-Classes!</b><br>
 * <br>
 * This class is based on a Netbeans widget - which can be used without any other Netbeans stuff around.<br>
 * The project contains a gradle reference to get this lib.<br>
 * Outline use a "JTree" column to show a collapsible tree structure. As
 * this table used only two levels (groups and properties), the layout of the first column is changed back to look like a table.<br>
 * <br>
 * <b>Edit-support is build-in for these data-types:</b>
 * <table style="vertical-align:top">
 * <caption></caption>
 * <tr><td><b>Type        </b></td><td><b>Used Editor</b></td></tr>
 * <tr><td><i>String      </i></td><td>A text field.</td></tr>
 * <tr><td><i>Number      </i></td><td>A text field and the default number-format to render and parse the numbers.<br>
 *                                     Can be used with Float, Double, Integer, Long e.t.c. </td></tr>
 * <tr><td><i>Boolean     </i></td><td>Combo-box with an empty entry, "True" and "False" for data that can be null.<br>
 *                                     A check-box for data that doesn't allow nulls.</td></tr>
 * <tr><td><i>Enumerations</i></td><td>A combo-box with an empty entry (if null-capable) and all declared values.</td></tr>
 * <tr><td><i>Color       </i></td><td>A small icon to indicate the current color and some text to show the RGB values.<br>
 *                                     If clicked, the Swing Color-Chooser is shown.
 *                                     </td></tr>
 * </table>
 * <br>
 * All properties are marked as "nullable" per default. This can be changed by setting a member of PropertyValue<br>
 * <br>
 * Check out the example for this class com.bw.jtools.examples.propertytable.PropertyTableDemo.
 */
public final class PropertyTable extends Outline
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 2469783456629141389L;

	/**
	 * In an "Outline" the columns starts left of the tree column.
	 * So for some API calls you need to add "+1" to the values below.
	 */
	public static final int COLUMN_VALUE = 0;
	public static final int COLUMN_COUNT = 1;

	/**
	 * True if the table is basically editable, what means that
	 * there may cells that can be edited.
	 *
	 * @return True if the table is editable.
	 */
	public boolean isEditable()
	{
		return editable_;
	}

	/**
	 * Sets editable mode.
	 *
	 * @param editable True to make the table basically editable.
	 */
	public void setEditable(boolean editable)
	{
		editable_ = editable;
	}

	/**
	 * Gets the currently selected node.
	 *
	 * @return The currently selected node or null.
	 */
	public PropertyNode getSelectedNode()
	{
		int row = getSelectedRow();
		if (row >= 0)
		{
			Object nodeValue = getValueAt(row, 0);
			if (nodeValue instanceof PropertyNode)
			{
				return (PropertyNode) nodeValue;
			}
		}
		return null;
	}

	/**
	 * Gets the Tree Model
	 *
	 * @return The Tree-Model.
	 */
	public DefaultTreeModel getTreeModel()
	{
		return ((PropertyOutlineModel) getOutlineModel()).getDefaultTreeModel();
	}

	/**
	 * Gets the Table Model
	 *
	 * @return The Table-Model.
	 */
	public PropertyTableModel getTableModel()
	{
		return ((PropertyOutlineModel) getOutlineModel()).getDefaultTableModel();
	}

	/**
	 * Expands all groups on first level.
	 */
	public void expandAll()
	{
		PropertyGroupNode root = (PropertyGroupNode) getTreeModel().getRoot();
		if (root != null)
		{
			final int N = root.getChildCount();
			for (int i = 0; i < N; ++i)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
				if (node instanceof PropertyGroupNode)
				{
					expandPath(new TreePath(node.getPath()));
				}
			}
		}
	}

	@Override
	protected JTableHeader createDefaultTableHeader()
	{
		return new PropertyTableHeader(columnModel);
	}

	/**
	 * Constructs a new Table.
	 */
	public PropertyTable()
	{

		// To solve all inconsistences accross all LookAndFeels, set the background to a fixed value:
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		// defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
		defaults.put("Table.background", Color.WHITE);

		// setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		editable_ = true;

		new PropertyColumnResizeAdapter(this);

		setRootVisible(false);
		setRenderDataProvider(new PropertyTableDataProvider());
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);
		setSurrendersFocusOnKeystroke(true);
		setFont(new java.awt.Font("Verdana", Font.PLAIN, 10));
		setShowGrid(true);
		setGridColor(new Color(200, 200, 200));
		setIntercellSpacing(new Dimension(1, 1));
		setFillsViewportHeight(true);
		setAutoscrolls(true);

		PropertyOutlineModel mdl = PropertyOutlineModel.createOutlineModel();
		mdl.getDefaultTableModel()
		   .setEditable(editable_);

		setModel(mdl);
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final TableColumnModel colModel = getColumnModel();

		PropertyCellRenderer renderer = new PropertyCellRenderer(this);
		PropertyCellEditor editor = new PropertyCellEditor(this);

		TableColumn column = colModel.getColumn(0);
		column.setCellRenderer(renderer);
		column.setCellEditor(editor);

		column = colModel.getColumn(PropertyTable.COLUMN_VALUE + 1);
		column.setCellRenderer(renderer);
		column.setCellEditor(editor);

		// Setup copy & paste support
		InputMap imap = this.getInputMap(JComponent.WHEN_FOCUSED);

		imap.put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler.getCopyAction()
																  .getValue(Action.NAME));
		imap.put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler.getPasteAction()
																  .getValue(Action.NAME));

		// Enable "edit" on space key.
		// Because Buttons will only fire if "edit" is execute twice, a newaction is used instead of "startEditing".
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "editMe");
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editMe");
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "editMe");

		ActionMap am = this.getActionMap();
		Action editAction = new AbstractAction()
		{
			/**
			 * Generated Serial Version
			 */
			private static final long serialVersionUID = 2889693532750609569L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				final int row = getSelectedRow();
				final int col = getSelectedColumn();

				if (!PropertyTable.this.isEditing())
				{
					if (PropertyTable.this.editCellAt(row, col))
					{
						Component c = PropertyTable.this.getEditorComponent();
						if (c instanceof JButton)
						{
							((JButton) c).doClick(100);
						}
					}
				}
			}

		};
		am.put("editMe", editAction);
	}

	@Override
	public void updateUI()
	{
		super.updateUI();
	}


	private boolean editable_;


}
