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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An HashMap with a primitive integer type as key.<br>
 * This table is designed to work for fast id to object mappings where the Id is sequential without any factors.<br>
 * There is no hash-pimping as in java.util.HashMap or prim-number tricks as in other generic implementations.
 * As the key is a primitive type, the common interfaces of maps are not implemented (as this would involve auto-boxing).
 * <p>
 * "AbstractCollection.add" is not supported, the base class will throw exceptions if you try.
 */
public final class IdMap<E> extends AbstractCollection<E>
{
	int size;
	int threshold;
	final float loadFactor;
	IdEntry<E>[] table;

	public IdMap()
	{
		this(16, 0.75f);
	}

	public IdMap(int initialCapacity)
	{
		this(initialCapacity, 0.75f);
	}

	@SuppressWarnings("unchecked")
	public IdMap(int initialCapacity, float loadFactor)
	{
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;

		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new IdEntry[capacity];
		size = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<E> iterator()
	{
		return new IdMapIterator();
	}

	public E put(int id, E value)
	{
		final int index = indexForId(id);

		IdEntry<E> e = table[index];
		for (; e != null; e = e.next)
		{
			if (e.id == id)
			{
				E oldValue = e.value;
				e.value = value;
				return oldValue;
			}
		}

		table[index] = new IdEntry<E>(id, value, table[index]);
		++size;
		if (size > threshold)
			resize(2 * table.length);

		return null;
	}

	public E removeKey(int id)
	{
		final int index = indexForId(id);

		IdEntry<E> e = table[index];
		if (e != null)
		{
			if (e.id == id)
			{
				table[index] = e.next;
				--size;
				return e.value;
			}
			else
			{
				IdEntry<E> prev = e;
				for (e = e.next; e != null; e = e.next)
				{
					if (e.id == id)
					{
						prev.next = e.next;
						--size;
						return e.value;
					}
					prev = e;
				}
			}
		}
		return null;
	}

	public E get(int id)
	{
		final int index = indexForId(id);
		for (IdEntry<E> e = table[index]; e != null; e = e.next)
		{
			if (e.id == id)
			{
				return e.value;
			}
		}
		return null;
	}

	public boolean containsId(int id)
	{
		return get(id) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size()
	{
		return size;
	}

	public void clear()
	{
		final int N = table.length;
		size = 0;
		for (int i = 0; i < N; ++i)
		{
			table[i] = null;
		}
	}

	protected static final class IdEntry<E>
	{
		final int id;
		E value;
		IdEntry<E> next;

		IdEntry(int id_, E value_, IdEntry<E> next_)
		{
			id = id_;
			value = value_;
			next = next_;
		}

		public final String toString()
		{
			return id + "=" + value;
		}
	}

	private final class IdMapIterator implements Iterator<E>
	{
		IdEntry<E> next;
		int bucket;
		IdEntry<E> current;

		IdMapIterator()
		{
			current = null;
			bucket = 0;
			while (bucket < table.length && table[bucket] == null) ++bucket;
			next = (bucket < table.length) ? table[bucket] : null;
		}

		public final boolean hasNext()
		{
			return next != null;
		}

		final IdEntry<E> nextEntry()
		{
			current = next;
			if (current == null)
				throw new java.util.NoSuchElementException();

			if ((next = current.next) == null)
			{
				++bucket;
				while (bucket < table.length && table[bucket] == null) ++bucket;
				next = (bucket < table.length) ? table[bucket] : null;
			}
			return current;
		}

		@Override
		public void remove()
		{
			if (current == null)
				throw new IllegalStateException();
			final int k = current.id;
			current = null;
			IdMap.this.removeKey(k);
		}

		@Override
		public E next()
		{
			return nextEntry().value;
		}
	}

	/**
	 * Return the table next_bucket_index for the Id.
	 *
	 * @param key The Key to get the index for.
	 * @return The bucket-index of the key.
	 */
	protected final int indexForId(int key)
	{
		// table.length is always some 2^N, so
		// table.length-1 will give a bit-mask.
		return key & (table.length - 1);
	}

	@SuppressWarnings("unchecked")
	protected final void resize(int newCapacity)
	{
		final int oldLength = table.length;
		IdEntry<E>[] oldTable = table;
		table = new IdEntry[newCapacity];
		threshold = (int) (newCapacity * loadFactor);

		for (int i = 0; i < oldLength; ++i)
		{
			IdEntry<E> e = oldTable[i];
			while (e != null)
			{
				IdEntry<E> next = e.next;
				int newIndex = indexForId(e.id);
				e.next = table[newIndex];
				table[newIndex] = e;
				e = next;
			}
		}
	}

	/**
	 * Converts this set to a list instance.
	 *
	 * @return List of all curently contained values.
	 */
	public final List<E> toList()
	{
		ArrayList<E> list = new ArrayList<>(size);
		for (E v : this)
			list.add(v);
		return list;
	}
}
