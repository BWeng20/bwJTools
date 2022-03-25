package com.bw.jtools.graph;

import com.bw.jtools.io.data.Data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GraphElement
{
	public final Integer id = idGenerator.incrementAndGet();

	private static final AtomicInteger idGenerator = new AtomicInteger(0);

	private HashMap<Attribute, Data> attributes_ = new HashMap();

	public Iterator<Attribute> attributes()
	{
		return attributes_.keySet().iterator();
	}

	public Data getAttribute(Attribute a)
	{
		return attributes_.get(a);
	}

	public void setAttribute(Attribute a, Data value)
	{
		attributes_.put(a, value);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		appendTo(sb);
		return sb.toString();
	}

	/**
	 * Adds textual representation to the StringBuilder.
	 */
	public StringBuilder appendTo(StringBuilder sb)
	{
		sb.append(id).append(":[");
		boolean first = true;
		for ( Map.Entry<Attribute,Data> e : attributes_.entrySet() )
		{
			if ( first) first = false;
			else sb.append(',');
			sb.append( e.getKey().name ).append(':').append(e.getValue());
		}
		sb.append(']');
		return sb;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if ( this == obj )
			return true;
		else if ( obj instanceof GraphElement)
		{
			GraphElement ge = (GraphElement)obj;
			if ( ge.attributes_.size() == attributes_.size() )
			{
				for (Map.Entry<Attribute, Data> i : attributes_.entrySet() )
				{
					if ( !Objects.equals( ge.getAttribute(i.getKey()), i.getValue() ))
						return false;
				}
				return true;
			}
		}
		return false;
	}
}
