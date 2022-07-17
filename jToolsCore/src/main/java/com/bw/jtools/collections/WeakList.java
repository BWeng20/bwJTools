/*
 * (c) copyright Bernd Wengenroth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools.collections;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * List implementation that stored weak references.
 *
 * @param <E> The item type (NOT a weak-reference).
 */
public class WeakList<E> extends AbstractList<E>
{
	private ArrayList<WeakReference<E>> weakList_;

	private E unwrap(WeakReference<E> ref)
	{
		return ref == null ? null : ref.get();
	}

	public WeakList()
	{
		weakList_ = new ArrayList<WeakReference<E>>();
	}

	public WeakList(int size)
	{
		weakList_ = new ArrayList<WeakReference<E>>(size);
	}

	public WeakList(WeakList<E> other)
	{
		weakList_ = new ArrayList<WeakReference<E>>(other.weakList_);
	}

	public WeakList(Collection<E> other)
	{
		weakList_ = new ArrayList<WeakReference<E>>(other.size());
		for (E element : other)
		{
			weakList_.add(new WeakReference<E>(element));
		}
	}


	@Override
	public E get(int index)
	{
		return unwrap(weakList_.get(index));
	}

	@Override
	public int size()
	{
		return weakList_.size();
	}

	@Override
	public E set(int index, E element)
	{
		return unwrap(weakList_.set(index, new WeakReference<E>(element)));
	}

	@Override
	public boolean add(E element)
	{
		return weakList_.add(new WeakReference<E>(element));
	}

	@Override
	public void add(int index, E element)
	{
		weakList_.add(index, new WeakReference<E>(element));
	}

	@Override
	public E remove(int index)
	{
		return unwrap(weakList_.remove(index));
	}

	@Override
	public boolean remove(Object item)
	{
		boolean removed = false;
		Iterator<WeakReference<E>> it = weakList_.iterator();
		while (it.hasNext())
		{
			WeakReference<E> ref = it.next();
			Object i = ref.get();
			if (i == null)
				it.remove();
			else if (i.equals(item))
			{
				removed = true;
				it.remove();
			}
		}
		return removed;
	}

	public Iterator<E> noneNullIterator()
	{
		return noneNullIterator(true);
	}

	public Iterator<E> noneNullIterator(boolean cleanup)
	{
		return new NoneNullIterator<E>(weakList_.iterator(), cleanup);
	}

	private class NoneNullIterator<T> implements Iterator<T>
	{
		private final Iterator<WeakReference<T>> it_;
		private final boolean cleanUp_;
		private T next_;

		public NoneNullIterator(Iterator<WeakReference<T>> it, boolean cleanup)
		{
			it_ = it;
			cleanUp_ = cleanup;
			advance();
		}

		@Override
		public boolean hasNext()
		{
			return next_ != null;
		}

		@Override
		public T next()
		{
			T n = next_;
			advance();
			return n;
		}

		private void advance()
		{
			next_ = null;
			while (it_.hasNext() && next_ == null)
			{
				next_ = it_.next()
						   .get();
				if (cleanUp_ && next_ == null)
					it_.remove();
			}
		}
	}
}
