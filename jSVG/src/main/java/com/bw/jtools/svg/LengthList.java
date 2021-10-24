/*
 * (c) copyright 2021 Bernd Wengenroth
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

package com.bw.jtools.svg;

import java.util.ArrayList;
import java.util.List;

public final class LengthList extends Parser
{
	List<Length> lengthList_ = new ArrayList<>(4);

	public LengthList(String list)
	{
		super(list);
		Length l;
		do
		{
			l = nextLengthPercentage();
			if (l != null)
				lengthList_.add(l);
		} while (l != null);
	}

	public List<Length> getLengthList()
	{
		return lengthList_;
	}

	public float[] toFloatPixel(Double absValue)
	{
		final int N = lengthList_.size();
		float fa[] = new float[N];
		for (int i = 0; i < N; ++i)
		{
			fa[i] = (float) lengthList_.get(i)
									   .toPixel(absValue);
		}
		return fa;
	}

	public boolean isEmpty()
	{
		return lengthList_.isEmpty();
	}
}
