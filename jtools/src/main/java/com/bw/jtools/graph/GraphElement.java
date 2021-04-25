package com.bw.jtools.graph;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class GraphElement
{
	public final Integer id = idGenetator.incrementAndGet();

	private static final AtomicInteger idGenetator = new AtomicInteger(0);


	private Object[] attributes = new Object[0];

	public Object getAttribute(Attribute a)
	{

		return (a.ordinal < attributes.length) ? attributes[a.ordinal] : null;
	}

	public void setAttribute(Attribute a, Object value)
	{

		if (a.ordinal >= attributes.length)
		{
			final int newLength = a.ordinal + 10;
			Object[] newAttributes = new Object[newLength];
			System.arraycopy(attributes, 0, newAttributes, 0, attributes.length);
			attributes = newAttributes;
		}
		attributes[a.ordinal] = value;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ":" + id;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object obj)
	{
		return this == obj;
	}
}
