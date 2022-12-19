package com.bw.jtools.ui;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class JLimitedText extends JTextField
{
    public static boolean beepIfExceeded_ = true;

    private static final class LengthFilter extends DocumentFilter
    {
        private int charLimit_;

        public LengthFilter(int charLimit)
        {
            this.charLimit_ = charLimit;
        }

        private boolean allowed(int newLength)
        {
            if (newLength <= charLimit_)
            {
                return true;
            } else if (beepIfExceeded_)
            {
                Toolkit.getDefaultToolkit().beep();
            }
            return false;
        }

        public void insertString(FilterBypass f, int offs,
                                 String input, AttributeSet a) throws BadLocationException
        {
            if (allowed(f.getDocument().getLength() + input.length()))
            {
                super.insertString(f, offs, input, a);
            }
        }

        public void replace(FilterBypass f, int offs, int length,
                            String str, AttributeSet a)
                throws BadLocationException
        {
            if (allowed(f.getDocument().getLength() + str.length() - length))
            {
                super.replace(f, offs, length, str, a);
            }
        }
    }

    public JLimitedText()
    {
    }

    public JLimitedText(String text, int maxCharacters)
    {
        super(text);
        setMaxCharacters(maxCharacters);
    }

    public JLimitedText(int maxCharacters)
    {
        this();
        setMaxCharacters(maxCharacters);
    }

    /**
     * Set maximum characters.
     *
     * @param maxCharacters -1 to turn filter off.
     */
    public void setMaxCharacters(int maxCharacters)
    {
        AbstractDocument doc = ((AbstractDocument) getDocument());
        if (maxCharacters < 0)
        {
            doc.setDocumentFilter(null);
        } else
        {
            LengthFilter lf = (LengthFilter) doc.getDocumentFilter();
            if (lf == null)
            {
                lf = new LengthFilter(maxCharacters);
                doc.setDocumentFilter(lf);
            } else
            {
                lf.charLimit_ = maxCharacters;
                String t = getText();
                if (t.length() > maxCharacters)
                {
                    setText(t.substring(0, maxCharacters));
                }
            }
        }
    }
}