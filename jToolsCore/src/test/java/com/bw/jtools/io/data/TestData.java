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

package com.bw.jtools.io.data;

import java.util.Objects;

public class TestData
{
	public String text;
	public long longValue;
	public Long LongValue;

	public TestData child;


	@Override
	public String toString()
	{
		return new StringBuilder().append('\'')
								  .append(text)
								  .append("',")
								  .append(longValue)
								  .append(',')
								  .append(longValue)
								  .append(child != null ? ",Child" : ",null")
								  .toString();
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
			return true;
		else if (other instanceof TestData)
		{
			TestData o = (TestData) other;
			return Objects.equals(o.text, text)
							&& Objects.equals(o.LongValue, LongValue)
							&& o.longValue == longValue
			&& Objects.equals(o.child, child);

		}
		return false;
	}
}