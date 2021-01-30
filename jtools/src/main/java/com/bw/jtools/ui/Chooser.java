/*
 * (c) copyright Bernd Wengenroth
 *
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
package com.bw.jtools.ui;

import com.bw.jtools.ui.icon.IconTool;

import javax.swing.*;
import java.awt.*;

/**
 * A generic dialog to prompt the user for some choice to make.
 */
public class Chooser extends JDialog {

    /**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 6208669340733620989L;

	/**
     * Creates new Chooser.
     *
     * @param title Title of the dialog.
     */
    protected Chooser(String title)
    {
        super((Frame)null, title, true);
        initComponents();
    }

    /**
     * If contained in the result, user selected "don't ask me again".
     */
    public static final int DONT_ASK_AGAIN = 64;

    /** The selected answer. */
    private int choice = -1;

    /**
     * Shows a chooser with the specified options.
     * @param Title The title of the dialog.
     * @param Text The message to show. Check documentation about java.swing.JLabel for format options.
     * @param Options Up to <b>three</b> options to choose by user.
     * @return Bitwise combination of
     *         <ul>
     *         <li>The 1-based index of the selected Option or 0.
     *         <li>DONT_ASK_AGAIN if user checked "don't ask again".
     *         </ul>
     *
     */
    static public int question( String Title, String Text, String... Options )
    {
        Chooser chooser = new Chooser(Title);

        if ( Text != null ) chooser.Question.setText(Text); else chooser.Question.setVisible(false);
        if ( Options.length > 0 && Options[0] != null ) chooser.Answer_One.setText( Options[0] ); else chooser.Answer_One.setVisible(false);
        if ( Options.length > 1 && Options[1] != null ) chooser.Answer_Two.setText( Options[1] ); else chooser.Answer_Two.setVisible(false);
        if ( Options.length > 2 && Options[2] != null ) chooser.Answer_Three.setText( Options[2] ); else chooser.Answer_Three.setVisible(false);

        chooser.pack();
        chooser.setLocationRelativeTo(null);
        chooser.setVisible(true);

        if ( chooser.dontAskAgain.isSelected() )
            chooser.choice = chooser.choice | DONT_ASK_AGAIN;
        chooser.dispose();

        return chooser.choice;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents()
    {

        Question = new javax.swing.JLabel();
        Buttons = new javax.swing.JPanel();
        dontAskAgain = new javax.swing.JCheckBox();
        Choices = new javax.swing.JPanel();
        Answer_One = new javax.swing.JButton();
        Answer_Two = new javax.swing.JButton();
        Answer_Three = new javax.swing.JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(IconTool.getAppSmallImage());
        setResizable(false);
        getContentPane().setLayout(new java.awt.BorderLayout(30, 30));

        Question.setText("Question");
        Question.setToolTipText("");
        Question.setVerticalAlignment(SwingConstants.TOP);
        Question.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        Question.setVerifyInputWhenFocusTarget(false);
        Question.setVerticalTextPosition(SwingConstants.TOP);
        getContentPane().add(Question, BorderLayout.CENTER);

        Buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        Buttons.setLayout(new java.awt.BorderLayout());

        dontAskAgain.setText("Don't ask again");
        Buttons.add(dontAskAgain, BorderLayout.SOUTH);

        Choices.setLayout(new GridLayout(1, 0, 10, 0));

        Answer_One.setText("ONE");
        Answer_One.setToolTipText("");
        Answer_One.addActionListener(evt -> {
            choice = 3;
            setVisible(false);
        });
        Choices.add(Answer_One);

        Answer_Two.setText("TWO");
        Answer_Two.addActionListener(evt -> {
            choice = 2;
            setVisible(false);
        });
        Choices.add(Answer_Two);

        Answer_Three.setText("THREE");
        Answer_Three.addActionListener(evt -> {
            choice = 1;
            setVisible(false);
        });
        Choices.add(Answer_Three);

        Buttons.add(Choices, BorderLayout.NORTH);

        getContentPane().add(Buttons, BorderLayout.SOUTH);

        pack();
    }

    private JButton Answer_One;
    private JButton Answer_Three;
    private JButton Answer_Two;
    private JPanel Buttons;
    private JPanel Choices;
    private JLabel Question;
    private JCheckBox dontAskAgain;
}
