/*
 * The MIT License
 *
 * Copyright 2019-2020 Bernd Wengenroth.
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
package com.bw.jtools.profiling.callgraph;

import java.text.NumberFormat;

/**
 * Call graph renderer to output a freemind/freeplane-file.
 */
public class FreeMindGraphRenderer extends AbstractCallGraphRenderer
{
    private boolean root = true;
    private CallEdge edge = null;

    public FreeMindGraphRenderer(NumberFormat nf)
    {
        super(nf);
    }

    @Override
    protected void start(CallNode root)
    {
        sb.append("<map version=\"freeplane 1.7.0\">\n");
        edge = null;
    }

    @Override
    protected void startNode(CallNode node)
    {
        sb.append("<node STYLE=\"");
        if ( root ) {
            sb.append("oval");
        } else {
            sb.append("bubble");
        }

        sb.append("\">\n");
        sb.append("<richcontent TYPE=\"NODE\"><html><head/><body>\n<p><b><font size=\"3\">");
        appendEscaped( node.name );
        sb.append("</font></b></p>\n");

        if ( node.value != null )
        {
            if ( edge != null && edge.value != null )
            {
                sb.append( "<p>");
                appendEscaped( renderValue(edge.value) );
                sb.append(" in ").append(edge.count).append( " call");
                if ( edge.count>1) {
                    sb.append('s');
                }
                sb.append(".</p>");
            }
            sb.append( "<p>Global: ");
            appendEscaped( renderValue(node.value) );
            sb.append( " / <b>Net ");
            appendEscaped( renderValue(node.getNetMeasurement()) );
            sb.append("</b> in ").append(node.calls).append( " call");
            if ( node.calls>1) {
                sb.append('s');
            }
            sb.append(".</p>\n");
        }
        sb.append("</body></html></richcontent>");

        if( edge != null && edge.value == null && !root )
        {
            // Top-level-call that is not root -> multiple roots in one graph,
            // add a cloud around each top-level call.
            sb.append("<cloud COLOR=\"#ffffff\" SHAPE=\"ARC\"/>");
        }
        sb.append("\n");

        if ( root )
        {
            root = false;
            // sb.append("<hook NAME=\"MapStyle\" layout=\"OUTLINE\"><font SIZE=\"16\"/></hook>\n" );
        }

    }

    @Override
    protected void endNode(CallNode node)
    {
        sb.append("</node>\n");
    }

    @Override
    protected void startEdge(CallEdge edge)
    {
        this.edge = edge;
    }

    @Override
    protected void endEdge(CallEdge edge)
    {
        edge = null;
    }

    @Override
    protected void end(CallNode root)
    {
        sb.append("</map>");
    }

    private void appendEscaped(String text)
    {
        // Only relevant codes, we don't want to be fully html compliant .
        char data[] = text.toCharArray();
        for (char c : data )
        {
            switch ( c )
            {
            case '<' : sb.append("&lt;"); break;
            case '>' : sb.append("&gt;"); break;
            case '&' : sb.append("&amp;"); break;
            case '"' : sb.append("&quot;"); break;
            case '\'': sb.append("&apos;"); break;
            case '\n': sb.append("&#xa;"); break;
            default:
                sb.append( c );
            }
        }
    }

}
