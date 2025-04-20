/*
 *  (c) copyright 2023 Bernd Wengenroth
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

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

/**
 * Methods to get a point on shapes.
 */
public class PointOn {
    /**
     * Point on a curve at some offset.
     *
     * @param c      The curve.
     * @param offset The relative offset between 0 and 1.
     * @return The point on the curve.
     */
    public static Point2D.Double offset(CubicCurve2D.Double c, double offset) {
        final double offsetN = 1 - offset;
        final double offsetOOO = offset * offset * offset;
        final double offset3OON = 3 * offset * offset * offsetN;
        final double offsetNN = offsetN * offsetN;

        final double offsetNNN = offsetNN * offsetN;
        final double offset3ONN = 3 * offset * offsetNN;
        return new Point2D.Double(
                offsetNNN * c.x1 + offset3ONN * c.ctrlx1 + offset3OON * c.ctrlx2 + offsetOOO * c.x2,
                offsetNNN * c.y1 + offset3ONN * c.ctrly1 + offset3OON * c.ctrly2 + offsetOOO * c.y2);
    }

}
