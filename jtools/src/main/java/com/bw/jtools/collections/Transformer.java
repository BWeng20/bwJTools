package com.bw.jtools.collections;

public interface Transformer<S,T>
{
	public T transform( S item);
}
