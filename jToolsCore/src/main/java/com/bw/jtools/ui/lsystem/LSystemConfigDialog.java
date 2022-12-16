package com.bw.jtools.ui.lsystem;

import com.bw.jtools.ui.properties.PropertyDialogBase;

import javax.swing.SwingUtilities;

public class LSystemConfigDialog extends PropertyDialogBase
{
    public LSystemConfigDialog(LSystemPanel lpanel)
    {
        super(SwingUtilities.getWindowAncestor(lpanel), ModalityType.MODELESS,
                new LSystemConfigPanel(lpanel));
    }
}
