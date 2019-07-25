/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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
package com.bw.jtools.reports;

import java.util.ArrayList;

/**
 * This class represents the basic element of a "report".<br>
 * Each element can have several sub-elements.
 */
public class ReportElement
{
    protected final ArrayList<ReportElement> elements = new ArrayList<>();

    /**
     * Adds a sub-element to this element.
     * @param e The element to add.
     * @return  Returns this for chaining.
     */
    public ReportElement add(ReportElement e) throws IllegalArgumentException
    {
        elements.add(e);
        return this;
    }

    /**
     * Convenience replacement for "add( new ReportText(text) )".
     * Can be used if no formatting options needs to be applied on the text.
     *
     * @param text Text that should be added.
     * @return this (for chaining).
     */
    public ReportElement add(String text) throws IllegalArgumentException
    {
        return add(new ReportText(text));
    }

    /**
     * Renders the element via a renderer.<br>
     * In this case simply iterate through all elements and calls "render" on it.
     * @param renderer The renderer to use.
     */
    public void render( ReportRenderer renderer )
    {
        for (ReportElement re : elements )
        {
            re.render(renderer);
        }
    }

}
