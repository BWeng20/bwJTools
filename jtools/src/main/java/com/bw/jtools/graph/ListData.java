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

package com.bw.jtools.graph;

import com.bw.jtools.io.data.Data;
import com.bw.jtools.io.data.DataInput;
import com.bw.jtools.io.data.DataOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data implementation that hosts a list of data.
 */
public class ListData implements Data
{
	public List<Data> list = new ArrayList<>();

	@Override
	public void write(DataOutput o, int fieldid) throws IOException
	{
		if (list.isEmpty())
			o.writeNull(fieldid);
		else
		{
			DataOutput so = o.startObject(fieldid);
			int i = 1;
			for (Data d : list)
				so.writeObject(i++, d, false);
			so.finish();
		}
	}

	@Override
	public void read(DataInput i) throws IOException
	{
		if (!i.isFieldNull())
		{
			DataInput si = i.startObject();
			while (si.hasNextField())
			{
				list.add((Data) si.readObject());
			}
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj instanceof ListData)
		{
			return Objects.equals(((ListData) obj).list, list);
		}
		return false;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		boolean first = true;
		for (Data d : list)
		{
			if (first) first = false;
			else sb.append(',');
			sb.append(d);
		}
		sb.append('}');
		return sb.toString();
	}
}
