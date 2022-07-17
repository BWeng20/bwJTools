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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class DataDump
{
	final static int SIZE_STRING = 0;
	final static int SIZE_FIXED = 1;
	final static int SIZE_ARRAYS = 2;
	final static int COUNT_OBJECTS = 3;
	final static int COUNT_NUMERIC = 4;
	final static int COUNT_STRING = 5;
	final static int COUNT_NULL = 6;
	final static int COUNT_OTHER = 7;
	final static int COUNT_ARRAY = 8;
	final static int COUNT_BOOLEAN = 9;

	public static void dumpDataInputWithStats(String name, byte[] data ) throws IOException
	{
		DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
		System.out.println("=========== "+name+" ===================");
		int sizes[] = dumpDataInputIntern( "", is );
		System.out.println("========================================");
		System.out.println("#Objects:        "+sizes[COUNT_OBJECTS] );
		System.out.println("#Arrays:         "+sizes[COUNT_ARRAY] );
		System.out.println("#Ar-Elements:    "+sizes[SIZE_ARRAYS] );
		System.out.println("#Numbers:        "+sizes[COUNT_NUMERIC] );
		System.out.println("#Strings:        "+sizes[COUNT_STRING] );
		System.out.println("#Nulls:          "+sizes[COUNT_NULL]);
		System.out.println("#Booleans:       "+sizes[COUNT_BOOLEAN]);
		System.out.println("#Other:          "+sizes[COUNT_OTHER]+" (should be 0)");
		System.out.println("Length:          "+data.length+" bytes");
		System.out.println("For strings:     "+sizes[SIZE_STRING]+" bytes");
		System.out.println("For fixed types: "+sizes[SIZE_FIXED]+" bytes");
		System.out.println("----------------------------------------");
		System.out.println("Overhead:        "+(data.length-sizes[SIZE_STRING]-sizes[SIZE_FIXED])+" bytes");
		System.out.println("========================================");
	}

	public static void dumpDataInput(String name, byte[] data ) throws IOException
	{
		DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
		int sizes[] = dumpDataInputIntern( "", is );
	}

	private static int[] dumpDataInputIntern(String prefix, DataInputStream is ) throws IOException
	{
		int sizes[] = new int[10];
		while  ( is.hasNextField() )
		{
			System.out.print(prefix+is.getFieldId()+": "+is.currentFieldType_ );
			int sz = is.getSize(is.currentFieldType_);
			if ( sz > 0)
				sizes[SIZE_FIXED] += sz;
			if ( is.currentFieldType_ == DataType.OBJECT)
			{
				sizes[COUNT_OBJECTS]++;
				System.out.println();
				int ns[] = dumpDataInputIntern( prefix+" ", (DataInputStream)is.startObject() );
				for ( int i=0 ; i<ns.length; ++i)
					sizes[i] += ns[i];
			}
			else if ( is.currentFieldType_ == DataType.ARRAY)
			{
				sizes[COUNT_ARRAY]++;
				Object ar = is.readArray();

				int len = Array.getLength(ar);

				sizes[SIZE_ARRAYS] += len;

				DataType ct = DataReflectMap.detectType( ar.getClass().getComponentType() );
				int csize = is.getSize(ct);
				if(  csize > 0)
					sizes[SIZE_FIXED] += csize * len;
				else if ( ct == DataType.STRING)
				{
					for (int i=0 ; i<len ; ++i)
					{
						String s = ((String[])ar)[i];
						if ( s != null )
						{
							sizes[COUNT_STRING]++;
							sizes[SIZE_STRING] += s.getBytes(StandardCharsets.UTF_8).length;
						}
					}
				}
				System.out.println( " "+ct.name()+"["+len+"]" );
			}
			else if ( is.currentFieldType_ == DataType.STRING)
			{
				sizes[COUNT_STRING]++;
				String s = is.readString();
				sizes[SIZE_STRING] += s.getBytes(StandardCharsets.UTF_8).length;
				System.out.println( " "+s );
			}
			else if ( is.currentFieldType_ != null &&
					is.currentFieldType_.isNumeric())
			{
				sizes[COUNT_NUMERIC]++;
				System.out.println( " "+ is.readNumber());
			}
			else
			{
				if ( is.currentFieldType_ == DataType.NULL)
					sizes[COUNT_NULL]++;
				else if ( is.currentFieldType_ == DataType.BOOLEAN)
					sizes[COUNT_BOOLEAN]++;
				else
					sizes[COUNT_OTHER]++;
				System.out.println();
				is.skip();
			}
		}
		return sizes;
	}
}