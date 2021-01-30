package com.bw.jtools.examples.dropcaps;

import com.bw.jtools.ui.JCardBorder;
import com.bw.jtools.ui.dropcaps.JDropCapsLabel;
import com.bw.jtools.ui.dropcaps.JDropCharConfig;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.JIconButton;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Demonstrates use of {@link com.bw.jtools.ui.dropcaps.JDropCapsLabel} together with some configuration controls.
 */
public class JDropCapPanel extends JPanel
{

    // Borders are stateless and the same instance can be used accross several components.
    private JCardBorder cardBorder_ = new JCardBorder(0.997f);
    private Border border_ = BorderFactory.createCompoundBorder( cardBorder_, BorderFactory.createEmptyBorder(10,10,10,10) );

    private JDropCapsLabel dropCap_;

    public JDropCapPanel(String text)
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Configure card-border.
        setBorder( border_ );
        setBackground(Color.WHITE);
        setOpaque(true);

        // Create the drop-cap-label
        dropCap_ = new JDropCapsLabel(text);

        // Draw the text with a gradient paint
        // Using a generic "Paint" has much more options that use a simple color (which is also a "Paint").
        dropCap_.setForegroundPaint(new GradientPaint(-5,-5, new Color(120, 25, 25) , 400,300,new Color(0,50, 0)));

        // Add the label
        add( dropCap_);

        // Add the controls
        add(Box.createVerticalStrut(5));
        add( new JSeparator() );
        add(Box.createVerticalStrut(5));

        JPanel dropCardButtons = new JPanel();
        dropCardButtons.setOpaque(false);
        dropCardButtons.setLayout(new BoxLayout(dropCardButtons, BoxLayout.X_AXIS));

        Icon ic = IconTool.getIcon("Settings.png");
        JIconButton settings = new JIconButton(ic);
        settings.addActionListener( e -> JDropCharConfig.showDialog(dropCap_,"Config"));
        dropCardButtons.add(settings);
        dropCardButtons.add(Box.createHorizontalGlue());


        add( dropCardButtons );
    }

    public JDropCapsLabel getDropCap()
    {
        return dropCap_;
    }

}
