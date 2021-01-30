package com.bw.jtools.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonListener;
import java.awt.*;

/**
 * A button that shows an icon, nothing more and is identical across all LAFs.
 */
public class JIconButton extends AbstractButton
{
    Icon icon_;

    public void updateUI()
    {
    }

    public JIconButton(Icon icon)
    {
        icon_ = icon;
        setModel(new DefaultButtonModel());

        BasicButtonListener listener = new BasicButtonListener(this);

        addMouseListener(listener);
        addMouseMotionListener(listener);
        addFocusListener(listener);

        setBorderPainted(false);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        Rectangle r = getBounds();
        if ( isOpaque() )
        {
            g2.setColor(getBackground());
            g2.fillRect(r.x, r.y, r.width, r.height);
        }
        int x = (r.width-icon_.getIconWidth())/2;
        int y = (r.height-icon_.getIconHeight())/2;
        if ( model.isPressed() && model.isArmed() )
        {
            icon_.paintIcon(this, g, x+1,y+1);
        }
        else
        {
            icon_.paintIcon(this, g, x, y);
        }
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( icon_.getIconWidth()+2, icon_.getIconHeight()+2 );
    }

    @Override
    public Dimension getMaximumSize()
    {
        return new Dimension( icon_.getIconWidth()+5, icon_.getIconHeight()+5 );
    }

    @Override
    public Dimension getMinimumSize()
    {
        return new Dimension( icon_.getIconWidth()+1, icon_.getIconHeight()+1 );
    }
}
