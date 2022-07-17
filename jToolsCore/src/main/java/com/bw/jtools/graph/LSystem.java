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

/**
 * Implements a Lindenmayer System to rewrite paths.
 */
public class LSystem
{
    private String current_;
    private final double angle_;
    private final Map<String, String> rules_;
    
    public LSystem( String axiom, double angle, Map<String,String> rules ){
        current_ = axiom;
        angle_ = angle;
        rules_ = rules;
    }
    
    public void generation()
    {
        for ( Map.Entry<String, String> rule : rules_.entrySet())
        {
            current_ = current_.replace(rule.getKey(), rule.getValue());
        }
    }
    
    public Path2D.Double getPath(Point2D.Double start)
    {
        return getPath(start.x, start.y);
    }
    
    public Path2D.Double getPath(double startX, double startY)
    {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(startX, startY);
        for ( char c : current_.toCharArray())
        {
            switch ( c )
            {
                case 'F':
                case 'f':
                case '-':
                case '+':
                case '[':
                case ']':
            }
        }
        //@TODO
        return p;
    }
    
}
