package com.bw.jtools.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Iterator that filters the elements from some iterator by a predicate.<br>
 * Null object from the base iterator are skipped.
 *
 * @param <T> The element type
 */
public class FilteredIterator<T> implements Iterator<T>
{
	private final Iterator<T> innerIt;
	private final Predicate<T> pred;
	private T next;

	private boolean checkForNext()
	{
		while (next == null && innerIt.hasNext())
		{
			next = innerIt.next();
			if (!pred.test(next)) next = null;
		}
		return next != null;
	}

	public FilteredIterator(Iterator<T> it, Predicate<T> pred)
	{
		this.innerIt = it;
		this.pred = pred;
	}

	@Override
	public boolean hasNext()
	{
		return checkForNext();
	}

	@Override
	public T next()
	{
		if (!checkForNext())
		{
			throw new NoSuchElementException();
		}
		T r = next;
		next = null;
		return r;
	}

	@Override
	public void remove()
	{
		innerIt.remove();
	}

}
