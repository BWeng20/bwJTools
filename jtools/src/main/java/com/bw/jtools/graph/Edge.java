package com.bw.jtools.graph;

/**
 * An edge is a directed connection between two nodes.
 */
public class Edge extends GraphElement
{
	private Node source;
	private Node target;

	private boolean cyclic;

	public static boolean isCyclic( Node source, Node target)
	{
		return (target != null) && (target == source || target.isAncestor(source));
	}

	/**
	 * An edge is cyclic if the target is identical to source or an ancestor ior source.
	 */
	public boolean isCyclic()
	{
		return cyclic;
	}

	public Node getSource()
	{
		return source;
	}

	public void setSource(Node node)
	{
		source = node;
		this.cyclic = isCyclic(source,target);
	}

	public Node getTarget()
	{
		return target;
	}

	public void setTarget(Node node)
	{
		target = node;
		cyclic = isCyclic(source,target);
	}

	public void setSourceAndTarget(Node source, Node target)
	{
		this.source = source;
		this.target = target;
		cyclic = isCyclic(source,target);
	}

	public Edge()
	{
		cyclic = false;
	}

	public Edge(Node source, Node target)
	{
		this.source = source;
		this.target = target;
		cyclic = isCyclic(source,target);
	}

	@Override
	public String toString()
	{
		return
		super.appendTo(new StringBuilder().append( "Edge " )).append('{')
			 .append( source == null ? "null" : Integer.toString(source.id) )
			 .append("->")
			 .append( target == null ? "null" : Integer.toString(target.id) )
			 .append('}')
			 .toString();
	}
}
