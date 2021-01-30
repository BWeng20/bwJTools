package com.bw.jtools.ui.paintchooser;

import com.bw.jtools.ui.I18N;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class JTextureChooser extends JPanel
{
    protected Rectangle2D chosenAnchorRect_;
    protected int chosenTransparency_ = 0;
    protected BufferedImage chosenImage_;

    protected  TexturePaint chosenPaint_;

    public JTextureChooser()
    {
        setLayout(new GridBagLayout());

        JLabel modeLabel = new JLabel("Image");

        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx =0;
        gc.gridy =0;
        add( modeLabel, gc );
        gc.gridx =1;

        gc.gridx =0;
        ++gc.gridy;


    }

    public TexturePaint getSelectedPaint()
    {
        if ( chosenImage_ != null )
        {
            return new TexturePaint(chosenImage_,chosenAnchorRect_);
        }
        else
            return null;
    }

    public void setSelectedPaint(TexturePaint p)
    {
        if ( p == null )
        {
            chosenAnchorRect_ = null;
            chosenImage_ = null;
        }
        else
        {
            chosenAnchorRect_ = p.getAnchorRect();
            chosenImage_ = p.getImage();
        }
        chosenPaint_ = null;
    }



    /**
     * Opens a chooser dialog to create a TexturePaint.
     * @param component The component that triggers the chooser.
     * @param title The title to show.
     * @param initialPaint The initial paint to select or null.
     * @return the selected paint or <code>null</code> if the user opted out.
     */
    public static Paint showDialog(Component component,
                                  String title, TexturePaint initialPaint)
    {
        Window w = component == null ? null : component instanceof Window ? (Window)component : SwingUtilities.getWindowAncestor(component);

        JTextureChooser chooserPane = new JTextureChooser();
        JDialog chooserDialog = new JDialog(w, title, Dialog.ModalityType.APPLICATION_MODAL);
        chooserDialog.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

        JPanel c = new JPanel();
        chooserDialog.setContentPane(c);
        c.setLayout(new BorderLayout());
        c.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        c.add(chooserPane, BorderLayout.CENTER);

        JButton ok = new JButton(I18N.getText("button.ok"));
        ok.addActionListener(e ->
        {
            chooserPane.chosenPaint_ = chooserPane.getSelectedPaint();
            chooserDialog.setVisible(false);
        });

        JButton cancel = new JButton(I18N.getText("button.cancel"));
        cancel.addActionListener(e -> chooserDialog.setVisible(false));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(ok);
        buttons.add(cancel);
        c.add(buttons, BorderLayout.SOUTH);
        chooserDialog.pack();

        chooserPane.setSelectedPaint( initialPaint );
        chooserDialog.setLocationRelativeTo(component);

        chooserDialog.setVisible(true);
        return chooserPane.chosenPaint_;
    }

    public static void main(String[] args)
    {
        showDialog(null, "Text", null);
    }

}
