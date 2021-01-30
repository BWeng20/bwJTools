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
package com.bw.jtools.ui;

import com.bw.jtools.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

/**
 * ComboBox for LAF selection.<br>
 * The combo box installs the selected LAF on change.<br>
 * This class is mainly designed for LAF tests, e.g. by adding the element
 * in some unused space in the status-bar of an application under development.
 */
public class JLAFComboBox extends JComboBox<String>
{
    /**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 6612891946735768684L;
	
	protected List<UIManager.LookAndFeelInfo> lafs_ = new ArrayList<>();

    /**
     * Creates a new LAF ComboBox.<br>
     * The list is filled with all available LAFs.
     */
    public JLAFComboBox()
    {
        final UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        for ( UIManager.LookAndFeelInfo laf : lafs )
        {
            lafs_.add( laf );
            addItem( laf.getName() );
        }

        String LAF = UIManager.getLookAndFeel().getName();
        setSelectedItem( LAF );

        addItemListener(ie -> {
            String lafName = (String)getSelectedItem();
            for ( UIManager.LookAndFeelInfo laf : lafs_ )
                if ( laf.getName().equals(lafName) )
                {
                    if ( !UIManager.getLookAndFeel().getName().equals(lafName) )
                    {
                        try
                        {
                            UIManager.setLookAndFeel(laf.getClassName());

                            Window[] ws = JFrame.getOwnerlessWindows();
                            for ( Window w : ws )
                            {
                                SwingUtilities.updateComponentTreeUI(w);
                            }
                        }
                        catch (Exception ex)
                        {
                            Log.error("Faield to install "+lafName, ex);
                        }
                    }
                    break;
                }
        });
    }

    @Override
    public void updateUI()
    {
        super.updateUI();
        String lafName = (String)getSelectedItem();
        if ( lafName != null )
        {
            String name = UIManager.getLookAndFeel().getName();
            if ( !name.equals(lafName ))
            {
                setSelectedItem(name);
            }
        }
    }
}
