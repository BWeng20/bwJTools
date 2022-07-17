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

import java.util.ArrayList;
import java.util.List;

public class Graph
{

    private Node root;
    private List<GraphListener> listener = new ArrayList<>();

    public Graph()
    {
    }

    public void setRoot(Node node)
    {
        if ( node != root )
        {
            root = node;

            GraphEvent ev = new GraphEvent(GraphEvent.Type.ROOT_CHANGED);
            fireEvent(ev);
        }
    }

    public Node getRoot()
    {
        return root;
    }

    public Edge addEdge(Node parent, Node child)
    {
        Edge e = new Edge(parent, child);
        parent.edges.add(e);
        child.edges.add(e);

        GraphEvent ev = new GraphEvent(GraphEvent.Type.EDGE_ADDED);
        ev.edge = e;
        fireEvent(ev);
        return e;
    }

    public void removeEdge(Edge e)
    {
        if ( e != null )
        {
            Node parent = e.getSource();
            Node child = e.getTarget();
            
            if ( parent != null )
                parent.edges.remove(e);
            if ( child != null )
                child.edges.remove(e);
            
            GraphEvent ev = new GraphEvent(GraphEvent.Type.EDGE_REMOVED);
            ev.edge = e;
            fireEvent(ev);
        }
    }
    
    
    public void addGraphListener(GraphListener l)
    {
        synchronized (listener)
        {
            listener.add(l);
        }
    }

    protected void fireEvent(GraphEvent ev)
    {
        ev.source = this;
        GraphListener[] la;
        synchronized (listener)
        {
            la = listener.toArray(new GraphListener[listener.size()]);
        }
        for (GraphListener l : la)
        {
            l.graphChanged(ev);
        }
    }

    @Override
    public String toString()
    {
        if (root == null)
        {
            return "null";
        } else
        {
            return root.toString();
        }
    }
}
