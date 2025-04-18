/*
 *  (c) copyright 2022 Bernd Wengenroth
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 * 
 */
package com.bw.jtools.geometry;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public class ClosestPoint
{
  
    /**
     * Get closest point on the border of a shape.
     */
    public static Point2D.Double onShape(Point2D.Double rp, Shape shape, double flatness)
    {
        //@TODO: find some smarter solution.
        
        Point2D.Double p0 = new Point2D.Double();
        Point2D.Double p1 = new Point2D.Double();
        Point2D.Double p2 = new Point2D.Double();
        Point2D.Double r = new Point2D.Double();
        Point2D.Double bestp = new Point2D.Double(rp.x, rp.y);
        
        double d,bestd = Double.MAX_VALUE, dx,dy;
        
        double[] seg = new double[6];
        for ( PathIterator pi = shape.getPathIterator(null, flatness); !pi.isDone(); pi.next() )
        {
                final int type = pi.currentSegment(seg);
                switch (type)
                {
                    case PathIterator.SEG_MOVETO:
                        p0.x = p2.x = seg[0];
                        p0.y = p2.y = seg[1];
                        continue;
                    case PathIterator.SEG_LINETO:
                        p1.x = p2.x;
                        p1.y = p2.y;
                        p2.x = seg[0];
                        p2.y = seg[1];              
                        break;
                    case PathIterator.SEG_CLOSE:
                        p1.x = p2.x;
                        p1.y = p2.y;
                        p2.x = p0.x;
                        p2.y = p0.y;
                        break;
                }
                onLineSegment( rp, p1, p2, r );
                dx = rp.x - r.x;
                dy = rp.y - r.y;
                d = dx * dx + dy * dy;
                if ( d < bestd )
                {
                    bestd = d;
                    bestp.x = r.x;
                    bestp.y = r.y;                    
                }
        }
        return bestp;
    }

    /**
     * Get closest point from p1 to line segment lp1,lp2
     */
    public static Point onLineSegment(Point p1, Point lp1, Point lp2)
    {
        int A = p1.x - lp1.x;
        int B = p1.y - lp1.y;
        int C = lp2.x - lp1.x;
        int D = lp2.y - lp1.y;

        long dot = A * C + B * D;
        long len_sq = C * C + D * D;
        double param = (len_sq == 0) ? -1: (dot / len_sq);

        Point result;
        if (param < 0)
            result = new Point(lp1);
        else if (param > 1)
            result = new Point(lp2);
        else
            result = new Point((int) (0.5 + lp1.x + param * C), (int) (0.5 + lp1.y + param * D));
        return result;
    }

    /**
     * Get closest point from p1 to line segment lp1,lp2
     */
    public static Point2D.Double onLineSegment(Point2D.Double p1, Point2D.Double lp1, Point2D.Double lp2)
    {
        Point2D.Double result = new Point2D.Double();
        onLineSegment(p1, lp1, lp2, result);
        return result;
    }
    
    /**
     * Get closest point from p1 to line segment lp1,lp2
     */
    public static void onLineSegment(Point2D.Double p1, Point2D.Double lp1, Point2D.Double lp2, Point2D.Double result)
    {
        double A = p1.x - lp1.x;
        double B = p1.y - lp1.y;
        double C = lp2.x - lp1.x;
        double D = lp2.y - lp1.y;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = (len_sq == 0) ? -1: (dot / len_sq);

        if (param < 0)
        {
            result.x = lp1.x;
            result.y = lp1.y;
        }
        else if (param > 1)
        {
            result.x = lp2.x;
            result.y = lp2.y;
        }
        else
        {
            result.x = lp1.x + param * C;
            result.y = lp1.y + param * D;
        }
    }
    
    
    /**
     * Get closest point from p1 to path p2. p2 have to contain at least one
     * point.
     */
    public static Point onPath(Point p1, Point... p2)
    {

        Point best = null;
        if (p2.length > 0)
        {
            best = p2[0];
            if (p2.length > 1)
            {
                long bestD = Distance.squared(p1, best);

                for (int i = 1; i < p2.length; ++i)
                {
                    Point candidate = onLineSegment(p1, p2[i - 1], p2[i]);
                    final long d = Distance.squared(p1, candidate);
                    if (d < bestD)
                    {
                        bestD = d;
                        best = candidate;
                    }
                }
            }
        }
        return best;
    }
}
