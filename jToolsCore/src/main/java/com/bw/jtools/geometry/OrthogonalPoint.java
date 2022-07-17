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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Helper to calculate orthogonal points.
 */
public class OrthogonalPoint
{
    /**
     * Get a point that stands orthogonal on the end-point of the line and has the given distance.
     * @param line The reference line.
     * @param distance The distance.
     * @return The orthogonal point
     */
    static public Point2D.Float get( final Line2D.Float line, final float distance )
    {
        float dx = line.x1 - line.x2;
        float dy = line.y1 - line.y2;
        final float normFactor = (float)(distance / Math.hypot( dx, dy));
        return new Point2D.Float(line.x2 + dy * normFactor , line.y2 + dx * normFactor);
    }

    /**
     * Get a point that stands orthogonal on the end-point of the line and has the given distance.
     * @param x1 X coordinate of start point.
     * @param y1 Y coordinate of start point.
     * @param x2 X coordinate of end point.
     * @param y2 Y coordinate of end point.
     * @param distance The distance.
     * @return The orthogonal point
     */
    static public Point2D.Float get( final float x1, final float y1, final float x2, final float y2, final float distance )
    {
        float dx = x1 - x2;
        float dy = y1 - y2;
        final float normFactor = (float)(distance / Math.hypot( dx, dy));
        return new Point2D.Float(x2 + dy * normFactor , y2 + dx * normFactor);
    }

    /**
     * Get a point that stands orthogonal on the end-point of the line and has the given distance.
     * @param x1 X coordinate of start point.
     * @param y1 Y coordinate of start point.
     * @param x2 X coordinate of end point.
     * @param y2 Y coordinate of end point.
     * @param distance The distance.
     * @return The orthogonal point
     */
    static public Point2D.Double get( final double x1, final double y1, final double x2, final double y2, final double distance )
    {
        double dx = x1 - x2;
        double dy = y1 - y2;
        final double normFactor = distance / Math.hypot( dx, dy);
        return new Point2D.Double(x2 + dy * normFactor , y2 + dx * normFactor);
    }

    /**
     * Get a point that stands orthogonal on the end-point of the line and has the given distance.
     * @param x1 X coordinate of start point.
     * @param y1 Y coordinate of start point.
     * @param x2 X coordinate of end point.
     * @param y2 Y coordinate of end point.
     * @param distance The distance.
     * @param point The point to store the result.
     */
    static public void get( final double x1, final double y1, final double x2, final double y2, final double distance, Point2D.Double point )
    {
        double dx = x1 - x2;
        double dy = y1 - y2;
        final double normFactor = distance / Math.hypot( dx, dy);
        point.x = x2 + dy * normFactor;
        point.y = y2 + dx * normFactor;
    }
}
