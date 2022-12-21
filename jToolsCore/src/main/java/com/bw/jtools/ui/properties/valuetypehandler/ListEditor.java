package com.bw.jtools.ui.properties.valuetypehandler;

import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.I18N;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ListEditor<V> extends JScrollPane
{
    private List<PropertyValue<V>> value_;
    private List<PropertyValue<V>> chosenList_;
    private Constructor<? extends List<PropertyValue<V>>> listCtor_;
    protected JList<PropertyValue<V>> props_;

    public ListEditor()
    {
    }

    public void init(List<PropertyValue<V>> initialList) throws NoSuchMethodException
    {
        listCtor_ = (Constructor<? extends List<PropertyValue<V>>>) initialList.getClass()
                .getDeclaredConstructor();
        value_ = new ArrayList<>(initialList);

        DefaultListModel<PropertyValue<V>> model = new DefaultListModel<>();
        props_ = new JList<>(model);
        model.addAll(value_);
        setViewportView(props_);
    }

    public static <V> List<PropertyValue<V>> showDialog(Component component,
                                                        String title, List<PropertyValue<V>> initialMap,
                                                        final Class<V> valueClass)
    {
        Window w = component == null ? null : component instanceof Window ? (Window) component : SwingUtilities.getWindowAncestor(component);
        final ListEditor<V> listPane = new ListEditor<>();
        if (initialMap == null) initialMap = new ArrayList<>();
        try
        {
            // Copy map
            listPane.init(initialMap);

            final JDialog dialog = new JDialog(w, title, Dialog.ModalityType.APPLICATION_MODAL);

            JPanel c = new JPanel();
            c.setLayout(new BorderLayout());
            c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            c.add(listPane, BorderLayout.CENTER);

            JButton ok = new JButton(I18N.getText("button.ok"));
            ok.addActionListener(ae ->
            {
                try
                {
                    listPane.chosenList_ = listPane.listCtor_.newInstance();
                    listPane.chosenList_.addAll(listPane.value_);
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
                // @TODO
            });

            JButton remove = new JButton("-");
            remove.addActionListener(e ->
            {
                // @TODO
            });
            remove.setEnabled(false);

            listPane.props_.getSelectionModel()
                    .addListSelectionListener(e ->
                            remove.setEnabled(listPane.props_.getSelectedIndex() != -1));

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
        List<PropertyValue<V>> r = listPane.chosenList_;
        listPane.chosenList_ = null;
        return r;
    }

}
