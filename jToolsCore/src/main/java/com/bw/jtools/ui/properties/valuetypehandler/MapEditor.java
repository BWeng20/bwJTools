package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.properties.PropertyEditorComponents;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;
import com.bw.jtools.ui.properties.table.PropertyNode;
import com.bw.jtools.ui.properties.table.PropertyTable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;

public class MapEditor<K, V> extends JScrollPane
{
    private Map<K, PropertyValue<V>> value_;
    private Map<K, V> chosenMap_;
    private Constructor<? extends Map<K, V>> mapCtor_;
    private Map<String, K> propertyNameToKey = new HashMap<>();
    protected PropertyTable props_;

    public MapEditor()
    {
    }

    public void init(Map<K, V> initialMap) throws NoSuchMethodException
    {
        mapCtor_ = (Constructor<? extends Map<K, V>>) initialMap.getClass()
                .getDeclaredConstructor();
        // Use "Linked" to keep original order.
        value_ = new LinkedHashMap<>();

        PropertyGroup root = new PropertyGroup("");
        for (Map.Entry<K, V> e : initialMap.entrySet())
        {
            K key = e.getKey();
            String keys = String.valueOf(key);
            propertyNameToKey.put(keys, key);
            value_.put(key, root.addProperty(keys, e.getValue()));
        }

        props_ = new PropertyTable();
        DefaultTreeModel model = props_.getTreeModel();
        model.setRoot(new PropertyGroupNode(root));

        setViewportView(props_);
    }

    static PropertyEditorComponents newKeyEditorComponents_ = new PropertyEditorComponents();

    public static <K, V> Map<K, V> showDialog(Component component,
                                              String title, Map<K, V> initialMap,
                                              final Class<K> keyClass, final Class<V> valueClass)
    {
        Window w = component == null ? null : component instanceof Window ? (Window) component : SwingUtilities.getWindowAncestor(component);
        final MapEditor<K, V> mapPane = new MapEditor<>();
        if (initialMap == null) initialMap = new LinkedHashMap<>();
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
                    mapPane.chosenMap_ = mapPane.mapCtor_.newInstance();
                    mapPane.value_.forEach((key, value) -> mapPane.chosenMap_.put(key, value.getValue()));
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                dialog.setVisible(false);
            });

            JButton cancel = new JButton(I18N.getText("button.cancel"));
            cancel.addActionListener(e -> dialog.setVisible(false));

            final JButton add = new JButton("+");
            add.addActionListener(e ->
            {
                final JDialog dlg = new JDialog(
                        SwingUtilities.getWindowAncestor(add), Dialog.ModalityType.APPLICATION_MODAL);
                final List<K> input = new ArrayList<>();
                PropertyValue<K> keyProp = new PropertyValue<>("", keyClass);
                ValueTypeHandler<K> keyHandler = newKeyEditorComponents_.getHandler(keyProp, false);

                JPanel inputPane = new JPanel(new BorderLayout());
                inputPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                JLabel label = new JLabel(I18N.getText("property.map.new_key"));
                label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                inputPane.add(label, BorderLayout.WEST);
                Component ct = keyHandler.getComponent();
                inputPane.add(ct, BorderLayout.CENTER);
                label.setLabelFor(ct);
                JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JButton keyOk = new JButton(I18N.getText("button.ok"));
                keyOk.addActionListener(e1 -> {
                    input.add( keyHandler.getCurrentValueFromEditor());
                    dlg.setVisible(false);
                });

                JButton keyCancel = new JButton(I18N.getText("button.cancel"));
                keyCancel.addActionListener(e1 -> {
                    input.clear();
                    dlg.setVisible(false);
                });
                buttonPane.add(keyOk);
                buttonPane.add(keyCancel);

                JPanel keyCPane = new JPanel(new BorderLayout());
                keyCPane.add(inputPane, BorderLayout.CENTER);
                keyCPane.add(buttonPane, BorderLayout.SOUTH);

                dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dlg.setContentPane(keyCPane);

                dlg.pack();
                dlg.setLocationRelativeTo(mapPane);
                dlg.setVisible(true);

                if (!input.isEmpty() && input.get(0) != null)
                {
                    try
                    {
                        PropertyGroupNode root = (PropertyGroupNode) mapPane.props_
                                .getTreeModel()
                                .getRoot();

                        K key = input.get(0);
                        String keyS = key.toString();
                        // @TODO: This will allow only simple types (that work via PropertyValue
                        //        base class) as values.
                        //        To enable all value types (e.g. map) with specific property classes
                        //        we need some property-prototype or -factory for new instances.
                        PropertyValue<V> prop = new PropertyValue<>(keyS, valueClass);
                        root.addProperty(prop);
                        mapPane.propertyNameToKey.put(keyS, key);
                        mapPane.value_.put(key, prop);
                        mapPane.props_.getTreeModel()
                                .nodeStructureChanged(root);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });

            JButton remove = new JButton("-");
            remove.addActionListener(e ->
            {
                PropertyNode node = mapPane.props_.getSelectedNode();
                if (node != null)
                {
                    K key = mapPane.propertyNameToKey.remove(node.property_.key_);
                    mapPane.value_.remove(key);
                    int idx = mapPane.props_.getSelectionModel().getMinSelectionIndex();
                    mapPane.props_.getTreeModel()
                            .removeNodeFromParent(node);
                    if ( idx >= 0 ) {
                        idx = Math.min( idx , mapPane.value_.size()-1);
                        if ( idx >= 0 )
                        {
                            mapPane.props_.getSelectionModel().setSelectionInterval(idx, idx);
                        }
                    }
                }
            });
            remove.setEnabled(false);

            mapPane.props_.getSelectionModel()
                    .addListSelectionListener(e ->
                            remove.setEnabled(mapPane.props_.getSelectedNode() != null));

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
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Map<K, V> r = mapPane.chosenMap_;
        mapPane.chosenMap_ = null;
        return r;
    }

}
