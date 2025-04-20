package com.bw.jtools.examples.graph;

import com.bw.jtools.ui.JColorComboBox;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.graph.Leaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LeafDemo extends JComponent {

    final BasicStroke stroke = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            10.0f, null, 0.0f);
    List<Leaf> leaves = new ArrayList<>();

    double scale = 10;
    double leafWidth = 15;
    double leafHeight = 23;
    Color color = new Color(0x366735);

    public LeafDemo() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                regenerate();
            }
        });
        regenerate();
    }

    public void regenerate() {
        leaves.clear();
        double theta = 0;
        int N = 5;
        for (int count = 0; count < N; ++count) {
            leaves.add(createLeaf(theta));
            theta += Math.toRadians(360.0 / N);
        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) Math.ceil(scale * (2 + leafWidth)), (int) Math.ceil(scale * (2 + leafHeight)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.translate(getWidth() / 2, (getHeight() - (scale * leafHeight)) / 2);
        g2.scale(scale, scale);
        g2.setColor(Color.BLACK);
        g2.setStroke(stroke);
        g2.drawRect((int) (-leafWidth / 2) - 1, 0, 1 + (int) leafWidth, (int) leafHeight);

        for (Leaf f : leaves) {
            f.draw(g2);

        }
    }

    public static void main(String[] args) {
        LeafDemo panel = new LeafDemo();
        JButton gen = new JButton("Generate");
        JColorComboBox colors = new JColorComboBox(new Color[]{
                new Color(0x366735), // RAL 6001 Emerald green
                new Color(0xEBD78D), // RAL 095 85 40 Natural yellow
                new Color(0xD2AA6D), // RAL 1002 Sand yellow
                new Color(0xFFB200), // RAL 2007 Luminous bright orange
                new Color(0x9D622B), // RAL 8001 Ochre brown
                new Color(0x8D4931), // RAL 8004 Copper brown
                new Color(0x623836)  // RAL 030 30 20 - Autumn leaf red
        });

        colors.setColorName(0x366735, "Emerald green");
        colors.setColorName(0xEBD78D, "Natural yellow");
        colors.setColorName(0xD2AA6D, "Sand yellow");
        colors.setColorName(0xFFB200, "Luminous bright orange");
        colors.setColorName(0x9D622B, "Ochre brown");
        colors.setColorName(0x8D4931, "Copper brown");
        colors.setColorName(0x623836, "Autumn leaf red");

        colors.setSelectedItem(panel.color);

        gen.addActionListener(e -> {
            panel.regenerate();
        });

        colors.addItemListener(e -> {
            Color c = colors.getSelectedColor();
            panel.color = c;
            for (Leaf f : panel.leaves) {
                f.setColor(c.darker());
                f.setFillColor(c);
            }
            panel.repaint();

        });

        JFrame f = new JFrame("Test");
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(panel), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(gen);
        buttons.add(colors);

        mainPanel.add(buttons, BorderLayout.SOUTH);
        f.setContentPane(mainPanel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);

    }

    private Leaf createLeaf(double theta) {
        Leaf l = new Leaf(leafWidth, leafHeight * 9 / 10.0, leafHeight / 10.0,
                (int) (3 + Math.round(Math.random() * 2)), null,
                UITool.multiplyColor(color, 0.7f), color);
        l.rotate(theta);
        return l;
    }

}
