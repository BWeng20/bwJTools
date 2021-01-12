package com.bw.jtools.collections;

import java.util.Iterator;

/**
 * Iterator that transforms the elements from some iterator by call to a transform function.
 * @param <S> The source type.
 * @param <T> The target type.
 */
public class TransformedIterator<S,T>  implements Iterator<T>
{

	private final Iterator<S> innerIt;
	private final Transformer<S,T> trans;

	public TransformedIterator( Iterator<S> it, Transformer<S,T> tr ) {
		innerIt = it;
		trans = tr;
	}

	@Override
	public boolean hasNext()
	{
		return innerIt.hasNext();
	}

	@Override
	public T next()
	{
		return trans.transform( innerIt.next() );
	}

	@Override
	public void remove()
	{
		innerIt.remove();
	}
}
