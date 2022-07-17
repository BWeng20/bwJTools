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
