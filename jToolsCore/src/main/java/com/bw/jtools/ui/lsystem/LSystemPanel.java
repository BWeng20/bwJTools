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
package com.bw.jtools.ui.lsystem;

import com.bw.jtools.lsystem.LSystem;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**
 * Panel to show an l-system.
 */
public class LSystemPanel extends JPanel
{
    /**
     * Counts paint calls. Can be used to show some fps indicator.
     */
    public int paintCount_ = 0;
    private double scale_ = 1;
    private Path2D.Double path_;
    private boolean autoScale_ = true;
    private boolean drawBorder_ = false;
    private LSystem lsystem;

    /**
     * Gets true if l-system border is drawn.
     */
    public boolean isDrawBorder()
    {
        return drawBorder_;
    }

    /**
     * Draws a border around the L-system to indicate current bounds.
     */
    public void setDrawBorder(boolean drawBorder_)
    {
        if (this.isDrawBorder() != drawBorder_)
        {
            this.drawBorder_ = drawBorder_;
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    public void updateLSystem()
    {
        path_ = null;
        SwingUtilities.invokeLater(this::repaint);
    }

    public boolean isAutoScale()
    {
        return autoScale_;
    }

    public void setAutoScale(boolean autoScale)
    {
        this.autoScale_ = autoScale;
        SwingUtilities.invokeLater(this::repaint);
    }

    /**
     * Gets the l-system
     */
    public LSystem getLSystem()
    {
        return lsystem;
    }

    public void setLSystem(LSystem lsystem)
    {
        this.lsystem = lsystem;
        path_ = null;
        SwingUtilities.invokeLater(this::repaint);
    }

    public LSystemPanel()
    {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        final Graphics2D g2 = (Graphics2D)g.create();
        try
        {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (lsystem != null)
            {
                if ( path_ == null )
                {
                    path_ = lsystem.getPath(0, 0);
                }
                Rectangle r = path_.getBounds();
                Dimension d = getSize();
                if (autoScale_)
                {
                    double xf = (d.width - 20) / r.getWidth();
                    double yf = (d.height - 20) / r.getHeight();
                    scale_ = Math.min(xf, yf);
                }
                g2.translate(10, 10);
                AffineTransform a = AffineTransform.getScaleInstance(scale_, scale_);
                a.translate(-r.x, -r.y);
                g2.setPaint(Color.BLUE);
                g2.draw(a.createTransformedShape(path_));
                if (drawBorder_)
                {
                    g2.setPaint(Color.RED);
                    g2.draw(a.createTransformedShape(r));
                }
            }
        } finally
        {
            g2.dispose();
            ++paintCount_;
        }

    }
}
