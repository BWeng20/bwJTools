package com.bw.jtools.ui.graph;

public class Edge extends GraphElement
{
	public final Node source;
	public final Node target;
	public final boolean cylic;

	public Edge( Node source, Node target) {
		this.source = source;
		this.target = target;
		this.cylic = target == source || target.isAncestor( source );
	}
}
