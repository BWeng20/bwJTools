/*
 * (c) copyright 2019 Bernd Wengenroth
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
package com.bw.jtools.examples.data;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.graphic.IconTool;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.data.LoggerDataModel;
import com.bw.jtools.ui.data.DataTablePanel;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class DataDemo extends JFrame
{
    /**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 3507746476032767071L;
	
	static LoggerDataModel model_ ;
    static boolean running = true;

    public static void main( String args[] )
    {
        model_ = new LoggerDataModel();
        new DataDemo();
        Log.info("Demo Started.");
    }

    DataTablePanel logPanel;

    public DataDemo()
    {
        super("Data Table Demonstration");

        // Initialize library.
        Application.initialize(getClass());

        // The librarie has now initialized itself from defaultsettings.properties.
        // (located in same package as the main-class).

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            Log.error("Failed to initialize LAF.", e);
        }

        JPanel panel = new JPanel(new BorderLayout());


        logPanel = new DataTablePanel(model_);
        logPanel.setPreferencePrefix("mydatapanel.");

        panel.add( logPanel, BorderLayout.CENTER );
        panel.add( new JLAFComboBox(), BorderLayout.SOUTH );

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImages( IconTool.getAppIconImages() );

        setContentPane(panel);
        logPanel.loadPreferences();

        // It's important to register this listener brfore calling
        // SettingsUI.storePositionAndFlushOnClose
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                logPanel.storePreferences();
                running = false;
            }

        });

        pack();

        // Restore window-position and dimension from prefences.
        SettingsUI.loadWindowPosition(this);
        SettingsUI.storePositionAndFlushOnClose( this );

        setVisible(true);

        new Thread(() ->
        {
            int c = 0;
            while ( running )
            {
                try
                {
                    Thread.sleep(500);
                    ++c;
                    Log.info( "Worker Thread #"+c );
                } catch ( Exception e)
                {
                    Log.error("Thread.sleep interrupted", e);
                }
            }
        }).start();

    }

}
