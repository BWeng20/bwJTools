package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyStringValue;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;
import com.bw.jtools.ui.properties.table.PropertyNode;
import com.bw.jtools.ui.properties.table.PropertyTable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapEditor extends JScrollPane
{
	private Map<String, PropertyValue> value_;
	private Map choosenMap_;
	Constructor<? extends Map> mapCtor_;
	protected PropertyTable props_;

	public MapEditor()
	{
	}

	public void init(Map initialMap) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
	{
		mapCtor_ = initialMap.getClass()
							 .getDeclaredConstructor();
		// Use "Linked" to keep original order.
		value_ = new LinkedHashMap<>();

		PropertyGroup root = new PropertyGroup("");
		for (Object eo : initialMap.entrySet())
		{
			Map.Entry e = (Map.Entry) eo;
			Object key = e.getKey();
			if (key instanceof String)
			{
				String keys = (String) key;
				value_.put(keys, root.addProperty(keys, e.getValue()));
			}
			else
				throw new IllegalArgumentException(I18N.getText("property.map.illegalKeyType"));
		}

		props_ = new PropertyTable();
		DefaultTreeModel model = props_.getTreeModel();
		model.setRoot(new PropertyGroupNode(root));

		setViewportView(props_);
	}

	static MapEditor mapPane;

	public static Map showDialog(Component component,
								 String title, Map initialMap)
	{
		Window w = component == null ? null : component instanceof Window ? (Window) component : SwingUtilities.getWindowAncestor(component);
		mapPane = new MapEditor();
		if (initialMap == null) initialMap = new LinkedHashMap();
		try
		{
			// Copy map
			mapPane.init(initialMap);

			final JDialog dialog = new JDialog(w, title, Dialog.ModalityType.APPLICATION_MODAL);

			JPanel c = new JPanel();
			c.setLayout(new BorderLayout());
			c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			c.add(mapPane, BorderLayout.CENTER);

			JButton ok = new JButton(I18N.getText("button.ok"));
			ok.addActionListener(ae ->
			{
				try
				{
					mapPane.choosenMap_ = mapPane.mapCtor_.newInstance();
					mapPane.choosenMap_.putAll(mapPane.value_);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				dialog.setVisible(false);
			});

			JButton cancel = new JButton(I18N.getText("button.cancel"));
			cancel.addActionListener(e -> dialog.setVisible(false));

			JButton add = new JButton("+");
			add.addActionListener(e ->
			{
				String newProperty = JOptionPane.showInputDialog(mapPane, I18N.getText("property.map.new_key"));
				if (newProperty != null && !newProperty.isEmpty())
				{
					PropertyGroupNode root = (PropertyGroupNode) mapPane.props_.getTreeModel()
																			   .getRoot();
					root.addProperty(newProperty, "");
					mapPane.props_.getTreeModel()
								  .nodeStructureChanged(root);
				}
			});

			JButton remove = new JButton("-");
			remove.addActionListener(e ->
			{
				PropertyNode node = mapPane.props_.getSelectedNode();
				if (node != null)
				{
					mapPane.value_.remove(node.property_.key_);
					mapPane.props_.getTreeModel()
								  .removeNodeFromParent(node);
				}
			});
			remove.setEnabled(false);

			mapPane.props_.getSelectionModel()
						  .addListSelectionListener(e ->
						  {
							  remove.setEnabled(mapPane.props_.getSelectedNode() != null);
						  });

			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
			buttons.add(ok);
			buttons.add(add);
			buttons.add(remove);
			buttons.add(cancel);
			c.add(buttons, BorderLayout.SOUTH);

			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setContentPane(c);
			dialog.pack();
			dialog.setLocationRelativeTo(component);

			dialog.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Map r = mapPane.choosenMap_;
		mapPane = null;
		return r;
	}

}
