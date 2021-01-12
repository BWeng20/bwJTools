package com.bw.jtools.ui.graph;

import com.bw.jtools.collections.FilteredIterator;
import com.bw.jtools.collections.TransformedIterator;
import com.bw.jtools.collections.Transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Node extends GraphElement
{
	public Data  data;

	public List<Edge> edges = new ArrayList<Edge>();

	public Node( Data data ) {
		this.data = data;
	}

	/**
	 * Iterate across all linked parent (via incoming edges).
	 */
	public Iterator<Node> parents() {
		return new TransformedIterator<Edge,Node>( incoming(),
				(Transformer<Edge, Node>) edge -> edge.source
		) ;
	}

	/**
	 * Iterate across all linked children (via outgoing acyclic edges).
	 */
	public Iterator<Node> children() {
		return new TransformedIterator<Edge,Node>( outgoing(false),
				(Transformer<Edge, Node>) edge -> edge.target
		) ;
	}

	/**
	 * Iterate across all incoming edges.
	 */
	public Iterator<Edge> incoming() {
		return new FilteredIterator<Edge>( edges.iterator(),
				(Predicate<Edge>) edge -> edge.source != Node.this );
	}

	/**
	 * Iterate across all outgoing edges.
	 * @param cyclic if true cyclic edged are also considered.
	 */
	public Iterator<Edge> outgoing(boolean cyclic) {
		return new FilteredIterator<Edge>( edges.iterator(),
				(Predicate<Edge>) edge -> edge.target != Node.this && (cyclic || !edge.cylic));
	}


	/**
	 * Checks if this node is an ancestor of the other node.<br>
	 * Remind that a node is not an ancestor of itself.
	 */
	public boolean isAncestor(Node node)
	{
		if ( this == node ) return false;
		for (Iterator<Node> it = children(); it.hasNext(); )
		{
			Node c = it.next();
			if ( node == c || c.isAncestor(node))
				return true;
		}
		return false;
	}

	public List<Node> getTreeNodes() {
		List<Node> tree = new ArrayList<>();
		getTreeNodes(tree);
		return tree;
	}

	public void getTreeNodes(List<Node> tree) {
		tree.add(this);
		getTreeDescendantNodes(tree);
	}

	public List<Node> getTreeDescendantNodes() {
		List<Node> tree = new ArrayList<>();
		getTreeDescendantNodes(tree);
		return tree;
	}


	public void getTreeDescendantNodes(List<Node> tree) {
		for (Iterator<Node> it = children(); it.hasNext(); )
		{
			it.next().getTreeNodes(tree);
		}
	}

}
