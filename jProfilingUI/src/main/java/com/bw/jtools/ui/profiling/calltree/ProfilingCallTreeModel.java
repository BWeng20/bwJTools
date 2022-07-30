/*
 * The MIT License
 *
 * Copyright 2020 Bernd Wengenroth.
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
package com.bw.jtools.ui.profiling.calltree;

import com.bw.jtools.profiling.callgraph.JSONCallGraphParser;
import com.bw.jtools.ui.UITool;

import javax.swing.tree.DefaultTreeModel;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tree Model for a Call-Graph.
 */
public class ProfilingCallTreeModel extends DefaultTreeModel
{
    /**
	 *  Generated Serial Version
	 */
	private static final long serialVersionUID = 5905885415975534799L;
	
	public ProfilingCallTreeModel(JSONCallGraphParser.GraphInfo graph)
    {
        super( graph == null ? null : new ProfilingTreeNode(graph.root, NumberFormat.getInstance()) );
    }

    /**
     * Visitor interface.
     * @see #visitNodes(NodeVisitor)
     */
    public static interface NodeVisitor
    {
        public void visit(ProfilingTreeNode node);
    }

    private static void walk( ProfilingTreeNode node, final NodeVisitor  v )
    {
        v.visit(node);
        final int N = node.getChildCount();
        for ( int i = 0 ; i<N ; ++i)
        {
            walk( (ProfilingTreeNode)node.getChildAt(i), v );
        }
    }

    /**
     * Walks the tree in pre-order and calls the visitor on each node.
     * @param v The visitor to call.
     */
    public final void visitNodes( NodeVisitor v )
    {
        ProfilingTreeNode root = (ProfilingTreeNode)getRoot();
        if ( root != null )
        {
            walk( root, v );
        }
    }

    private boolean updateNodeText(final ProfilingTreeNode node)
    {
        String newText;

        if ( showFullClassNames )
        {
            newText = node.node.name;
        }
        else
        {
            newText = node.node.name;
            int vl = newText.lastIndexOf('.' );
            if ( vl > 0 )
            {
                vl = newText.lastIndexOf('.', vl-1 );
                if ( vl > 0 )
                {
                    newText = newText.substring(vl+1);
                }
            }
        }

        if ( nameFilter != null )
        {
            StringBuilder sb = new StringBuilder(100);
            sb.append("<html>");

            Matcher m = nameFilter.matcher(newText);
            if ( m.find() )
            {
                int idx = 0;
                do
                {
                    final int next = m.start();
                    UITool.escapeHTML( newText, idx, next, sb ).append("<span bgcolor=F5964B>");
                    idx = m.end();
                    UITool.escapeHTML( newText, next, idx, sb ).append("</span>");
                } while ( m.find() );
                newText =
                        UITool.escapeHTML( newText, idx, newText.length(), sb )
                        .append("</html>").toString();
            }
            else
            {
                newText =
                        UITool.escapeHTML( newText, 0 , newText.length(), sb )
                        .append("</html>").toString();
            }
        }
        if ( !newText.equals( node.text ) )
        {
            node.text = newText;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets "show full class names".
     * @param show the new value.
     */
    public final void setShowFullClassNames( boolean show )
    {
        if ( show != showFullClassNames)
        {
            showFullClassNames = show;
            updateNodeTexts();
        }
    }

    /**
     * Gets "show full class names".
     * @return the current value.
     */
    public final boolean getShowFullClassNames( )
    {
        return showFullClassNames;
    }

    /**
     * Sets the name filter regexp.
     * @param filter The new filter. Can be null.
     */
    public final void setNameFilter( Pattern filter )
    {
        if ( ( filter != null || nameFilter != null ) &&
             ( filter == null || !filter.equals(nameFilter)))
        {
            nameFilter = filter;
            updateNodeTexts();
        }
    }

    /**
     * Gets the name filter regexp.
     * @return The File Filter.
     */
    public final Pattern getNameFilter( )
    {
        return nameFilter;
    }

    protected void updateNodeTexts()
    {
        visitNodes( (node) ->
        {
            if ( updateNodeText( node ) )
            {
                nodeChanged(node);
            }
        });
    }



    protected boolean showFullClassNames = true;
    protected Pattern nameFilter;
}
