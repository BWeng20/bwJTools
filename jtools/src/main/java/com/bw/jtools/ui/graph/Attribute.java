package com.bw.jtools.ui.graph;

import java.util.concurrent.atomic.AtomicInteger;

public class Attribute
{
	private static final AtomicInteger ordinalGenetator = new AtomicInteger(0);

	public final int ordinal = ordinalGenetator.getAndIncrement();
	public final String name;

	public Attribute(String name) {
		this.name = name;
	}

}
