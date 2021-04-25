package com.bw.jtools.graph;

import java.util.concurrent.atomic.AtomicInteger;

public class Attribute
{
	private static final AtomicInteger ordinalGenerator = new AtomicInteger(0);

	public final int ordinal = ordinalGenerator.getAndIncrement();
	public final String name;

	public Attribute(String name)
	{
		this.name = name;
	}

}
