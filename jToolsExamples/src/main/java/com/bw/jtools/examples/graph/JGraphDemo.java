package com.bw.jtools.examples.graph;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.graph.Graph;
import com.bw.jtools.graph.GraphUtil;
import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.graph.*;
import com.bw.jtools.ui.graph.impl.*;
import com.bw.jtools.ui.icon.IconTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class JGraphDemo
{

    JFrame frame;
    JButton optionButton;
    final GraphPanel gpanel = new GraphPanel();
    VisualSettings settings = new VisualSettings();

    static public void main(String[] args)
    {
        new JGraphDemo();
    }

    int count_ = 1;

    static Node node(String text)
    {
        return GraphUtil.createTextNode(text);
    }

    public JGraphDemo()
    {
        Application.initialize(JGraphDemo.class);

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        frame = new JFrame("Graph Demonstration");

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        Layout layout = new TreeLayout(new TreeRectangleGeometry());

        DecoratorNodeVisual v = new DecoratorNodeVisual(new NodeLabelVisual(layout, settings));
        ShapeEdgeVisual ev = new ShapeEdgeVisual(layout, settings);

        gpanel.setNodeVisual(v);
        gpanel.setEdgeVisual(ev);
        Graph g = gpanel.getGraph();

        URL urlHugo = JGraphDemo.class.getResource("icons/Hugo.png");
        URL urlUrsel = JGraphDemo.class.getResource("icons/Ursel.png");
        URL urlGitte = JGraphDemo.class.getResource("icons/Gitte.png");
        URL urlBrotie = JGraphDemo.class.getResource("icons/Brotie.png");

        v.getGeometry().beginUpdate();

        Node root = node("Grandson");

        g.setRoot(root);
        Node son = node("<html><b>Son</b><br><p style='color:Blue;'>Brotie <img  height=\"123\" width=\"139\" src=\"" + urlBrotie + "\" alt=\"Img\"></p></html>");
        Node father = node("Father");
        Node mother = node("<html><b>Mother</b><br><p style='color:Blue;'>Gitte <img  height=\"200\" width=\"196\" src=\"" + urlGitte + "\" alt=\"Img\"></p></html>");

        g.addEdge(root, son);
        g.addEdge(son, father);
        g.addEdge(son, mother);
        g.addEdge(mother, node("<html><b>Grandmother</b><br><p style='color:Blue;'>Ursel<img  height=\"220\" width=\"97\" src=\"" + urlUrsel + "\" alt=\"Img\"></p></html>"));
        g.addEdge(mother, node("<html><b>Grandfather</b><br><p style='color:Blue;'>Hugo<img  height=\"135\" width=\"198\" src=\"" + urlHugo + "\" alt=\"Img\"></p></html>"));
        g.addEdge(father, node("Grandmother"));
        g.addEdge(father, node("Grandfather"));

        v.addDecorator(son, new CloudNodeDecorator(v.getGeometry()));
        v.addDecorator(mother, new ShapeNodeDecorator(v.getGeometry(), DecoratorShape.LEAVES));

        JScrollPane graphPanel = new JScrollPane(gpanel);
        graphPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        graphPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(graphPanel, BorderLayout.CENTER);

        v.getGeometry().endUpdate();

        JPanel statusLine = new JPanel(new BorderLayout(10, 0));
        statusLine.add(new JLAFComboBox(), BorderLayout.WEST);

        optionButton = new JButton("\u270E"); // Unicode Pencil
        optionButton.addActionListener(e -> showOptions());
        statusLine.add(optionButton, BorderLayout.EAST);

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
            if (gpanel.paintCount_ > 0)
            {
                fps.setText(gpanel.paintCount_ + " fps");
            } else
            {
                fps.setText("...");
            }
            gpanel.paintCount_ = 0;
        });
        fpsTimer.start();

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (options != null)
                {
                    options.setVisible(false);
                    options.dispose();
                    options = null;
                }
                fpsTimer.stop();
            }
        });

        Log.info("Started");
    }

    GraphOptionDialog options;

    public void showOptions()
    {
        if (options != null && options.isVisible())
        {
            options.setVisible(false);
        } else
        {
            if (options == null)
            {
                options = new GraphOptionDialog(gpanel);
                options.pack();
            }

            UITool.placeOnTop(optionButton, options);
            options.setVisible(true);
        }
    }
}
