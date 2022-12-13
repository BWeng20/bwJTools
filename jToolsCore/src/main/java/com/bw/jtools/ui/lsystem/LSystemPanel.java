/*
 * (c) copyright 2022 Bernd Wengenroth
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

import com.bw.jtools.graph.LSystem;
import com.bw.jtools.graph.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Panel to show a l-system.
 */
public class LSystemPanel extends JPanel {

    /**
     * Counts paint calls. Can be used to show some fps indicator.
     */
    public int paintCount_ = 0;

    private double scale_ = 1;
    private Point2D.Double graphOrigin_ = new Point2D.Double(0,0);

    public LSystem getLSystem() {
        return lsystem;
    }

    public void setLSystem(LSystem lsystem) {
        this.lsystem = lsystem;
    }

    private LSystem lsystem;


    @Override
    protected void paintComponent(Graphics g)
    {
            try
            {
                final Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.scale(scale_, scale_);

                final Rectangle clipping = g2.getClipBounds();
                g2.setPaint(getBackground());
                g2.fillRect(clipping.x, clipping.y, clipping.width, clipping.height);
                g2.setPaint(Color.BLUE);

                if ( lsystem != null ) {
                    Path2D.Double p = lsystem.getPath(0, 0);
                    Rectangle r = p.getBounds();
                    g.translate( r.x + (int)(graphOrigin_.x),
                            r.y + (int)(graphOrigin_.y) );
                    g2.draw(p);
                }

            } finally
            {
                ++paintCount_;
            }

    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension d;
        if (isPreferredSizeSet())
        {
            d = super.getPreferredSize();
        } else
        {
            if (lsystem != null)
            {
                Rectangle r = lsystem.getPath( 0, 0).
                        getBounds();
                return new Dimension(r.width+20, r.height+20);
            } else
            {
                d = getMinimumSize();
            }
        }
        return d;
    }
}
