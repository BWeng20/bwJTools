package com.bw.jtools.graph;

import java.util.HashMap;

/**
 * Dynamic Enum to handle attribute of graph-elements.<br>
 * Attributes should be defined in a static ways with fixed ordinals.
 * Especially if the graph is use with {@link com.bw.jtools.io.data.DataOutput}/{@link com.bw.jtools.io.data.DataInput}
 * the ordinals of the attributes needs to be fixed, otherwise {@link GraphSerializer} needs to be configured to write
 * the names instead of ordinals which will increase data size.
 */
public final class Attribute
{
	public final int ordinal;
	public final String name;

	@Override
	public int hashCode()
	{
		return ordinal;
	}

	@Override
	public boolean equals(Object obj)
	{
		if ( this == obj )
			return true;
		else if ( obj instanceof Attribute)
		{
			return ((Attribute)obj).ordinal == ordinal;
		}
		else
			return false;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append(name).append(" (").append(ordinal).append(')').toString();
	}

	private Attribute(String name, int ordinal)
	{
		this.name = name;
		this.ordinal = ordinal;
	}

	private static final HashMap<String, Attribute> attributes_ = new HashMap<>();
	private static final HashMap<Integer, Attribute> attributeByOrdinal_ = new HashMap<>();


	public static Attribute getAttribute( int ordinal )
	{
		synchronized (attributes_)
		{
			return attributeByOrdinal_.get(ordinal);
		}
	}

	public static Attribute getAttribute( String name )
	{
		synchronized (attributes_)
		{
			Attribute a = attributes_.get(name);
			if ( a == null )
			{
				a = new Attribute(name, attributes_.size());
				attributes_.put(name, a);
				attributeByOrdinal_.put(Integer.valueOf(a.ordinal), a);
			}
			return a;
		}
	}

}
