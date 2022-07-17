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

/**
 * Data 
 */
public class TextData implements Data
{
	private static final String EMPTY = "";

	public String text;

	public TextData()
	{
		text = EMPTY;
	}

	public TextData(String text)
	{
		this.text = text;
	}

	@Override
	public void write(DataOutput o, int fieldid) throws IOException
	{
		o.writeString(fieldid, text);
	}

	@Override
	public void read(DataInput i) throws IOException
	{
		text = i.readString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if ( obj == this )
			return true;
		if ( obj instanceof TextData)
		{
			TextData ts = (TextData)obj;
			return ts.text.compareTo( text ) == 0;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return text == null ? "" : text;
	}
}
