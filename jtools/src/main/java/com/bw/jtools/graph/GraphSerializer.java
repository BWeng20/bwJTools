package com.bw.jtools.graph;

import com.bw.jtools.io.data.Data;
import com.bw.jtools.io.data.DataInput;
import com.bw.jtools.io.data.DataOutput;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * DataI/O serializer for Graphs.<br>
 * One instance is not thread-safe. A Serializer can be re-used, but not be used in parallel for
 * different graphs.
 */
public class GraphSerializer
{
	/**
	 * Resets the serializer.<br>
	 * Written elements are forgotten.<br>
	 * Is automatically called if a new graph is started.
	 */
	public void reset()
	{
		elementsWritten_.clear();
		elementsRead_.clear();
	}

	/**
	 * Controls if attributes are written by ordinals or by names.<br>
	 * Ordinals can be used if the ordinals are fixed and not dynamic assigned.<br>
	 * Default is usage of ordinals.
	 * @see Attribute
	 */
	public void setUseAttributeOrdinals( boolean useOrdinals )
	{
		useAttributeOrdinals = useOrdinals;
	}

	/**
	 * Writes edge data with the specified fieldId.<br>
	 * Can be used directly if called implements some other storage format, but normally this method would be called 
	 * internally from {@link #write(DataOutput, Graph, int)}.<br>
	 * If one edge was written already by this instance it
	 * will be referenced via the element id.
	 * @param e The edge to write. Can be null.
	 */
	public void writeEdge( DataOutput o, Edge e, int fieldId ) throws IOException
	{
		if ( e == null )
		{
			o.writeNull(fieldId);
		}
		else
		{
			DataOutput eo = writeElement( o, e,  fieldId++);
			if ( eo != null )
			{
				writeNode(eo, e.getSource(), fieldId++);
				writeNode(eo, e.getTarget(), fieldId++);
				eo.finish();
			}
		}
	}

	/**
	 * Writes node data with the specified fieldId.<br>
	 * Can be used directly if called implements some other storage format, but normally this method would be called
	 * internally from {@link #write(DataOutput, Graph, int)}.<br>
	 * Connected nodes are written recursively. If one node was written already by this instance it
	 * will be referenced via the id. So also cyclic graphs can be written.
	 * @param n The node to write. Can be null.
	 */
	public void writeNode( DataOutput o, Node n, int fieldId ) throws IOException
	{
		if ( n == null )
		{
			o.writeNull(fieldId);
		}
		else
		{
			DataOutput eo = writeElement( o, n,  fieldId++);
			if ( eo != null )
			{
				for (Edge e : n.edges)
					writeEdge(eo, e, fieldId++);
				eo.finish();
			}
		}
	}

	/**
	 * Writes a graph with the specified fieldId.<br>
	 */
	public void write(DataOutput o, Graph g, int fieldId ) throws IOException
	{
		reset();
		if ( o == null )
		{
			o.writeNull(fieldId);
		}
		else
		{
			writeNode( o, g.getRoot(), 1 );
		}
	}

	/**
	 * Read edge data.<br>
	 * Can be used directly if called implements some other storage format, but normally this method would be called 
	 * internally from {@link #read(DataInput)}.<br>
	 */
	public Edge readEdge( DataInput i ) throws IOException
	{
		if ( i.isFieldNull() )
		{
			i.skip();
			return null;
		}
		else if ( i.isFieldNumeric())
		{
			return (Edge)elementsRead_.get(i.readNumber().intValue());
		}
		else
		{
			Edge e = new Edge();
			DataInput ei = readElement(i, e);
			if ( ei != null )
			{
				Node source = readNode(ei);
				Node target = readNode(ei);
				e.setSourceAndTarget( source, target );
			}
			return e;
		}
	}

	/**
	 * Reads a node.<br>
	 * Can be used directly if called implements some other storage format, but normally this method would be called
	 * internally from {@link #read(DataInput)}.<br>
	 */
	public Node readNode(DataInput i ) throws IOException
	{
		if ( i.isFieldNull() )
		{
			i.skip();
		}
		else
		{
			if (i.isFieldNumeric())
			{
				return (Node) elementsRead_.get(i.readNumber()
												 .intValue());
			}
			else if (i.isFieldObject())
			{
				Node node = new Node();
				DataInput oi = readElement(i, node);
				if (oi != null)
				{
					while (oi.hasNextField())
						node.edges.add(readEdge(oi));
				}
				return node;
			}
		}
		return null;
	}


	/**
	 * Reads a graph from the current input field.<br>
	 */
	public Graph read(DataInput i ) throws IOException
	{
		reset();
		if ( i.isFieldNull() )
		{
			i.skip();
			return null;
		}
		else
		{
			Graph g = new Graph();
			g.setRoot( readNode( i ) );
			return g;
		}
	}

	/**
	 * Write common data for graph elements.
	 */
	private DataOutput writeElement( DataOutput o, GraphElement e, int fieldId ) throws IOException
	{
		if ( e == null )
		{
			o.writeNull(fieldId);
			return null;
		}
		else if (elementsWritten_.contains(e.id))
		{
			o.writeInt( fieldId, e.id );
			return null;
		}
		else
		{
			elementsWritten_.add(Integer.valueOf(e.id));

			DataOutput so = o.startObject( fieldId );
			int oId = 1;
			so.writeInt( oId++, e.id );
			for (Iterator<Attribute> it = e.attributes(); it.hasNext(); )
			{
				final Attribute a = it.next();
				Data val = e.getAttribute(a);
				if ( val != null )
				{
					if ( useAttributeOrdinals )
						so.writeInt( oId++, a.ordinal );
					else
						so.writeString( oId++, a.name );
					so.writeObject( oId++, val, false );
				}
			}
			so.writeNull( oId++ );
			return so;
		}
	}

	/**
	 * Write common data for graph elements.
	 */
	private DataInput readElement( DataInput i, GraphElement e ) throws IOException
	{
		Attribute a;
		DataInput si = i.startObject();
		int id = si.readNumber().intValue();
		elementsRead_.put(Integer.valueOf(id), e);

		while ( si.hasNextField() )
		{
			if( si.isFieldNull() )
			{
				si.skip();
				break;
			}
			if ( si.isFieldNumeric() )
			{
				a = Attribute.getAttribute( si.readNumber().intValue() );
			}
			else
			{
				a = Attribute.getAttribute(si.readString());
			}
			e.setAttribute( a, (Data)si.readObject());
		}
		return si;
	}

	/** Remember ids of written elements. */
	private Set<Integer> elementsWritten_ = new HashSet();

	/**
	 * Remembers read elements for later references.<br>
	 * Key is the read id, the element id is different - assigned as normal from id-generator.
	 */
	private Map<Integer,GraphElement> elementsRead_ = new HashMap<>();

	/**
	 * Controls usage of attributes.
	 * @see #setUseAttributeOrdinals(boolean)
	 */
	private boolean useAttributeOrdinals = true;

}
