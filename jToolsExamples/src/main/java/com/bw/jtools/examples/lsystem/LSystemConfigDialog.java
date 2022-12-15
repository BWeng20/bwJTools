package com.bw.jtools.examples.lsystem;

import com.bw.jtools.properties.PropertyBooleanValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.ui.lsystem.LSystemPanel;
import com.bw.jtools.ui.properties.PropertyDialogBase;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

public class LSystemConfigDialog extends PropertyDialogBase
{

    LSystemPanel lSystemPanel_;

    public LSystemConfigDialog(LSystemPanel lpanel)
    {
        super(SwingUtilities.getWindowAncestor(lpanel), ModalityType.MODELESS);
        this.lSystemPanel_ = lpanel;
        init();
    }


    protected void init()
    {

        DefaultTreeModel model = table_.getTreeModel();

        PropertyGroup p = new PropertyGroup("Visual Settings");

        addProperty(p, new PropertyBooleanValue("Border", lSystemPanel_.isDrawBorder()), value ->
        {
            Boolean n = value.getValue();
            lSystemPanel_.setDrawBorder(n);
        });


        PropertyGroupNode root = new PropertyGroupNode(null);
        root.add(new PropertyGroupNode(p));

        model.setRoot(root);
        table_.expandAll();

    }

}
