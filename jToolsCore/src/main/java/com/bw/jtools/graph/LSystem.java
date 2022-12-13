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
package com.bw.jtools.graph;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Stack;

/**
 * Implements a Lindenmayer System to rewrite paths.
 */
public class LSystem {
    private String current_;
    private final double angle_;
    private final Map<Character, String> rules_;
    private final StringBuilder sb = new StringBuilder();

    public LSystem(String axiom, double angle, Map<Character, String> rules) {
        current_ = axiom;
        angle_ = angle;
        rules_ = rules;
    }

    public void generation() {
        sb.setLength(0);
        final int N = current_.length();
        for (int i = 0; i < N; ++i) {
            final Character c = current_.charAt(i);
            final String m = rules_.get(c);
            if (m == null)
                sb.append(c);
            else
                sb.append(m);
        }
        current_ = sb.toString();
    }

    public Path2D.Double getPath(Point2D.Double start) {
        return getPath(start.x, start.y);
    }

    public Path2D.Double getPath(double startX, double startY) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(startX, startY);
        double deltaX = 20;
        double deltaT;
        double deltaY = 0;
        double theta = Math.toRadians(90);
        Stack<double[]> stack = new Stack<>();
        for (char c : current_.toCharArray()) {
            startX = Math.round(startX);
            startY = Math.round(startY);
            switch (c) {
                case 'F':
                    startX += deltaX;
                    startY += deltaY;
                    p.lineTo(startX, startY);
                    break;
                case 'f':
                    startX += deltaX;
                    startY += deltaY;
                    p.moveTo(startX, startY);
                    break;
                case '-':
                    // Rotate counterclockwise
                {
                    final double cos = Math.cos(theta);
                    final double sin = Math.sin(theta);
                    deltaT = (deltaX * cos) - (deltaY * sin);
                    deltaY = (deltaX * sin) + (deltaY * cos);
                    deltaX = deltaT;
                }
                break;
                case '+': {
                    final double cos = Math.cos(theta);
                    final double sin = Math.sin(theta);
                    deltaT = (deltaX * cos) + (deltaY * sin);
                    deltaY = (deltaY * cos) - (deltaX * sin);
                    deltaX = deltaT;
                }
                break;
                case '[':
                    stack.add(new double[]{startX, startY, deltaX, deltaY});
                    break;
                case ']':
                    double[] s = stack.pop();
                    startX = s[0];
                    startY = s[1];
                    deltaX = s[2];
                    deltaY = s[3];
                    break;
            }
        }
        //@TODO
        return p;
    }

}
