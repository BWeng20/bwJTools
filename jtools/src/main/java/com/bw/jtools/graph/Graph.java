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

	public void setRoot(Node note)
	{
		root = note;
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
		return e;
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
		if ( root == null)
			return "null";
		else
			return root.toString();
	}
}
