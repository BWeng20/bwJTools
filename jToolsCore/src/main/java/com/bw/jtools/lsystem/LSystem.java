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
package com.bw.jtools.lsystem;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Stack;

/**
 * Implements a Lindenmayer (L-) System to generate or rewrite paths.
 */
public class LSystem
{
    private final LSystemConfig config_;
    private int generations_ = 0;
    private String current_;
    private final StringBuilder sb = new StringBuilder();

    /**
     * Creates a new L-System.
     *
     * @param config Configuration.
     */
    public LSystem(LSystemConfig config)
    {
        current_ = config.axiom_;
        config_ = config;

    }

    /**
     * Run one generation
     */
    public void generation()
    {
        sb.setLength(0);
        final int N = current_.length();
        for (int i = 0; i < N; ++i)
        {
            final Character c = current_.charAt(i);
            final String m = config_.rules_.get(c);
            if (m == null)
                sb.append(c);
            else
                sb.append(m);
        }
        current_ = sb.toString();
        ++generations_;
    }

    public LSystemConfig getConfig()
    {
        return config_;
    }

    /**
     * Resets the system to generation 0
     */
    public void reset()
    {
        current_ = config_.axiom_;
        generations_ = 0;
    }

    /**
     * Creates a path from current state, starting at the given point.
     */
    public Path2D.Double getPath(Point2D.Double start)
    {
        return getPath(start.x, start.y);
    }

    private static double normalizeRad(double radians)
    {
        return Math.atan2(Math.sin(radians), Math.cos(radians));
    }

    /**
     * Creates a path from current state, starting at the given point.
     */
    public Path2D.Double getPath(double startX, double startY)
    {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(startX, startY);

        double theta = 0;
        double deltaX = 0;
        double deltaY = config_.deltaY_;
        Stack<double[]> stack = new Stack<>();
        for (char c : current_.toCharArray())
        {
            List<LSystemGraphicCommand> cmds = config_.commands_.get(c);
            if (cmds != null)
            {
                for (LSystemGraphicCommand cmd : cmds)
                {
                    switch (cmd)
                    {
                        case DRAW_FORWARD:
                            startX = startX + deltaX;
                            startY = startY + deltaY;
                            p.lineTo(startX, startY);
                            break;
                        case DRAW_LEAF:
                            p.curveTo(startX - deltaX / 2, startY + deltaY / 2,
                                    startX + deltaX / 2, startY + deltaY / 2,
                                    startX, startY);
                            break;
                        case MOVE_FORWARD:
                            startX = startX + deltaX;
                            startY = startY + deltaY;
                            p.moveTo(startX, startY);
                            break;
                        case TURN_COUNTERCLOCKWISE:
                        {
                            theta = normalizeRad(theta - config_.angle_);
                            deltaX = Math.sin(theta) * config_.deltaX_;
                            deltaY = Math.cos(theta) * config_.deltaY_;
                        }
                        break;
                        case TURN_CLOCKWISE:
                        {
                            theta = normalizeRad(theta + config_.angle_);
                            deltaX = Math.sin(theta) * config_.deltaX_;
                            deltaY = Math.cos(theta) * config_.deltaY_;
                        }
                        break;
                        case PUSH_ON_STACK:
                            stack.add(new double[]{startX, startY, theta});
                            break;
                        case POP_FROM_STACK:
                        {
                            double[] s = stack.pop();
                            startX = s[0];
                            startY = s[1];
                            theta = s[2];
                            deltaX = Math.cos(theta) * config_.deltaX_;
                            deltaY = Math.sin(theta) * config_.deltaY_;
                            p.moveTo(startX, startY);
                        }
                        break;
                        case POP_ANGLE_FROM_STACK:
                        {
                            double[] s = stack.pop();
                            theta = s[2];
                            deltaX = Math.cos(theta) * config_.deltaX_;
                            deltaY = Math.sin(theta) * config_.deltaY_;
                        }
                        break;
                        case POP_POS_FROM_STACK:
                        {
                            double[] s = stack.pop();
                            startX = s[0];
                            startY = s[1];
                            p.moveTo(startX, startY);
                        }
                        break;
                    }
                }
            }
        }
        return p;
    }

    public String getCurrent()
    {
        return current_;
    }

    /**
     * Number of calls to {@link #generation()} since last reset.
     */
    public int getGenerations()
    {
        return generations_;
    }

}