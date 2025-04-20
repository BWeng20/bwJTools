package com.bw.jtools.ui;

import com.bw.jtools.ui.icon.JPaintIcon;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A List-Renderer that displays Color or Paint values.
 */
public class JColorListCellRenderer extends DefaultListCellRenderer {

    JPaintIcon icon = new JPaintIcon();
    Map<Integer, String> colorNames = new HashMap<>();

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Paint) {
            Paint p = (Paint) value;
            icon.setPaint(p);
            setIcon(icon);
            if (p instanceof Color) {
                int rgb = ((Color) p).getRGB();
                String name = colorNames.get(rgb);
                if (name == null) {
                    name = String.format("#%X", rgb);
                    colorNames.put(rgb, name);
                }
                setText(name);
            }
        } else {
            setIcon(null);
            setText(value == null ? "" : value.toString());
        }

        return this;
    }

    /**
     * Set some human-readable name for a color
     *
     * @param rgb  The color RGB value
     * @param name The name
     */
    public void setColorName(int rgb, String name) {
        colorNames.put(0xFF000000 | rgb, name);
    }
}
