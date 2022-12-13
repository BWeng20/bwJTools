package com.bw.jtools.examples.lsystem;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.examples.graph.JGraphDemo;
import com.bw.jtools.graph.LSystem;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.lsystem.LSystemPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class LSystemDemo {
    JFrame frame;
    JButton optionButton;

    public LSystemDemo()
    {
        Application.initialize(LSystemDemo.class);

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        frame = new JFrame("L-System Demonstration");

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        LSystemPanel lpanel = new LSystemPanel();

        LSystem lsys = new LSystem("X",
                0, Map.of('X', "YF-", 'Y', "YX-"));

        lpanel.setLSystem(lsys);

        lpanel.setMinimumSize(new Dimension(100,100));
        JScrollPane graphPanel = new JScrollPane(lpanel);
        graphPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        graphPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(graphPanel, BorderLayout.CENTER);

        JPanel statusLine = new JPanel(new BorderLayout(10, 0));
        statusLine.add(new JLAFComboBox(), BorderLayout.WEST);

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton gen = new JButton("+");
        gen.addActionListener(e -> {
            lsys.generation();
            graphPanel.revalidate();
            graphPanel.repaint();
        });
        ctrl.add(gen);

        optionButton = new JButton("\u270E"); // Unicode Pencil
        optionButton.addActionListener(e -> showOptions());
        ctrl.add(optionButton);

        statusLine.add( ctrl, BorderLayout.EAST);

        JLabel fps = new JLabel("...");
        statusLine.add(fps, BorderLayout.CENTER);

        mainPanel.add(statusLine, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImages(IconTool.getAppIconImages());
        frame.pack();

        // Restore window-position and dimension from preferences.
        SettingsUI.loadWindowPosition(frame);
        SettingsUI.storePositionAndFlushOnClose(frame);

        frame.setVisible(true);

        Timer fpsTimer = new Timer(1000, e ->
        {
            if (lpanel.paintCount_ > 0)
            {
                fps.setText(String.valueOf(lpanel.paintCount_) + " fps");
            } else
            {
                fps.setText("...");
            }
            lpanel.paintCount_ = 0;
        });
        fpsTimer.start();

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {

                fpsTimer.stop();
            }
        });

        Log.info("Started");
    }

    private void showOptions() {
    }

    static public void main(String args[])
    {
        new LSystemDemo();
    }
}
