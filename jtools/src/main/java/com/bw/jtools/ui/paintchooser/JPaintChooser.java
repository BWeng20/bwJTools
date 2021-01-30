package com.bw.jtools.ui.paintchooser;

import com.bw.jtools.ui.I18N;

import javax.swing.*;
import java.awt.*;

public class JPaintChooser extends JPanel
{
    protected Paint chosenPaint_;

    protected JColorChooser colorChooser_;
    protected JPanel textureChooser_;

    public JPaintChooser()
    {
        setLayout(new GridBagLayout());

        JLabel modeLabel = new JLabel("Type");
        JComboBox<String> mode = new JComboBox<>();
        colorChooser_ = new JColorChooser();
        textureChooser_ = new JPanel();
        textureChooser_.setVisible(false);

        mode.addItem("Color");
        mode.addItem("Texture");
        modeLabel.setLabelFor(mode);

        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx =0;
        gc.gridy =0;
        add( modeLabel, gc );
        gc.gridx =1;
        add( mode, gc );

        gc.gridx =0;
        ++gc.gridy;

        add( colorChooser_);
        add( textureChooser_);

    }

    public Paint getSelectedPaint()
    {
        return null;
    }

    public void setSelectedPaint(Paint p)
    {
        chosenPaint_ = null;

        if ( p == null )
        {

        }
        else if ( p instanceof Color )
        {
            colorChooser_.setColor((Color)p);
        }
        else if ( p instanceof TexturePaint)
        {
            ImageIcon ic = new ImageIcon(((TexturePaint)p).getImage());
        }
        else
            throw new IllegalArgumentException("Paint of type "+p.getClass().getSimpleName()+" is not supported.");
    }



    /**
     * Opens a paint chooser dialog.
     * @param component The component that triggers the chooser.
     * @param title The title to show.
     * @param initialPaint The initial paint to select or null.
     * @return the selected paint or <code>null</code> if the user opted out.
     */
    public static Paint showDialog(Component component,
                                  String title, Paint initialPaint)
    {
        // Initialization of font list may take several seconds.
        // Show a wait-cursor.
        Window w = component == null ? null : component instanceof Window ? (Window)component : SwingUtilities.getWindowAncestor(component);
        Cursor cur = null;
        Cursor waitCursor = null;
        if (w != null)
        {
            waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
            cur = w.getCursor();
            w.setCursor(waitCursor);
        }

        JPaintChooser chooserPane = new JPaintChooser();
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

        if (cur != null && w.getCursor() == waitCursor )
        {
            w.setCursor(cur);
        }
        chooserDialog.setVisible(true);
        return chooserPane.chosenPaint_;
    }

    public static void main(String[] args)
    {
        showDialog(null, "Text", Color.BLACK);
    }

}
