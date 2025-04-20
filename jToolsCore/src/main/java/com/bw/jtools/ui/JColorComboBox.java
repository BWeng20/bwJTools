package com.bw.jtools.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Convenience ComboBox to select a color from a fixed list.
 */
public class JColorComboBox extends JComboBox<Color> {
    public JColorComboBox(Collection<Color> colors) {
        this(colors.toArray(new Color[0]));
    }

    public JColorComboBox(Color[] colors) {
        super(colors);
        setRenderer(new JColorListCellRenderer());
    }

    public JColorComboBox(Color[] colors, String[] names) {
        super(colors);
        setRenderer(new JColorListCellRenderer());
        for (int i = 0; i < colors.length && i < names.length; ++i) {
            setColorName(colors[i], names[i]);
        }
    }


    /**
     * Set some human-readable name for a color
     *
     * @param c    The color (identified by RGB value)
     * @param name The name
     */
    public void setColorName(Color c, String name) {
        ((JColorListCellRenderer) getRenderer()).setColorName(c.getRGB(), name);
    }

    /**
     * Set some human-readable name for a color
     *
     * @param rgb  The color RGB value
     * @param name The name
     */
    public void setColorName(int rgb, String name) {
        ((JColorListCellRenderer) getRenderer()).setColorName(rgb, name);
    }


    public Color getSelectedColor() {
        return (Color) getSelectedItem();
    }


}
