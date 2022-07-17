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
