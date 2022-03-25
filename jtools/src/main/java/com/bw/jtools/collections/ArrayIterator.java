/*
 * (c) copyright 2022 Bernd Wengenroth
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Iterator for arrays.<br>
 * Functional identical to <i>Arrays.asList(...).iterator()</i>
 * but without temporary list-wrapper instance and works with primitive element types.<br>
 * <code>
 *     Iterator&lt;Byte&gt; it;
 *     data byte[] = { 1,2,3,4,5, 6};
 *     // Would not compile:
 *     it = Arrays.asList(data).iterator();
 *     // Will work:
 *     it = ArrayIterator&lt;Byte&gt;(data);
 * </code>
 */
public final class ArrayIterator<E> implements Iterator<E>
{
	/**
	 * Creates a new iterator for an array.
	 */
	public ArrayIterator( Object array )
	{
		array_ = array;
		N_ = Array.getLength(array);
		i_ = 0;
	}

	private final Object array_;
	private int N_;
	private int i_;

	@Override
	public boolean hasNext()
	{
		return i_<N_;
	}

	@Override
	public E next()
	{
		return (E)Array.get(array_,i_++);
	}
}