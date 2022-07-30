/*
 * The MIT License
 *
 * Copyright 2020 Bernd Wengenroth.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bw.jtools.ui.profiling.calltree;

import com.bw.jtools.profiling.callgraph.CallNode;
import com.bw.jtools.profiling.callgraph.NodeDetail;
import com.bw.jtools.profiling.measurement.AbstractMeasurementSource;
import com.bw.jtools.ui.I18N;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.text.NumberFormat;

/**
 *
 */
public class ProfilingCallTreeRenderer extends DefaultTreeCellRenderer
{

    /**
	 * SerialVersion
	 */
	private static final long serialVersionUID = 5588863180431962205L;

	public ProfilingCallTreeRenderer(NumberFormat nf)
    {
        mf = new MessageFormat(I18N.getText("callgraph.tree.value"));
        this.nf = nf;
        text.setFont(new java.awt.Font("Dialog", 1, 14));
        panel.setLayout(new BorderLayout());
        panel.add(text, BorderLayout.NORTH);
        panel.add(info, BorderLayout.CENTER);
        panel.add(details, BorderLayout.SOUTH);

        panel.doLayout();
    }

    private final NumberFormat nf;

    private final JPanel panel = new JPanel();
    private final JLabel text = new JLabel();
    private final JLabel info = new JLabel();
    private final JLabel details = new JLabel();
    private final StringBuilder sb = new StringBuilder(100);
    private final MessageFormat mf;

    private final Border noFocusBorder = BorderFactory.createEmptyBorder(3, 5, 3, 5);
    private final Border focusBorder = BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(2, 4, 2, 4));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus)
    {

        final ProfilingTreeNode tnode = (ProfilingTreeNode)value;
        CallNode node = tnode.node;

        text.setText(tnode.text==null ? node.name : tnode.text);

        if (node.value != null)
        {
            info.setText(
                    mf.format( new Object[] { node.calls,
                        AbstractMeasurementSource.format(nf, node.value),
                        AbstractMeasurementSource.format(nf, node.getNetMeasurement())} ));
        }
        else
        {
            info.setText("");
        }

        sb.setLength(0);
        if (node.details != null)
        {
            for (NodeDetail d : node.details)
            {
                if (sb.length() > 0)
                {
                    sb.append(" / ");
                }
                sb.append(AbstractMeasurementSource.format(nf, d.value));
            }
        }
        details.setText(sb.toString());


        Color fc = selected ? getTextSelectionColor() : getTextNonSelectionColor();
        Color bc = selected ? getBackgroundSelectionColor() : getBackgroundNonSelectionColor();

        panel.setForeground(fc);
        panel.setBackground(bc);

        panel.setBorder( hasFocus ? focusBorder : noFocusBorder);

        return panel;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return panel.getPreferredSize();
    }

}
