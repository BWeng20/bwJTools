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
import com.bw.jtools.io.data.DataDump;
import com.bw.jtools.io.data.DataInputStream;
import com.bw.jtools.io.data.DataOutputStream;
import com.bw.jtools.io.data.TestDataGenerator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GraphSerializerTest
{

	static final Attribute a1 = Attribute.getAttribute ("A1" );
	static final Attribute a2 = Attribute.getAttribute ("A2" );
	static final Attribute a3 = Attribute.getAttribute ("A3" );

	public static Data generateData(Random r)
	{
		return new TextData( TestDataGenerator.generateString(r) );
	}

	public static Data generateDataList(Random r, int nr)
	{
		ListData d = new ListData();
		while ( nr > 0 )
		{
			d.list.add(generateData(r));
			--nr;
		}
		return d;
	}


	public static Node generateNode(Graph g, int deep )
	{
		Random rand = new Random(System.currentTimeMillis());

		Node n = new Node();
		if ( deep > 0)
		{
			g.addEdge( n, generateNode(g, deep -1 ));
			g.addEdge( n, generateNode(g, deep -1 ));
		}

		n.setAttribute(a1, generateDataList(rand, 2) );
		n.setAttribute(a2, generateData(rand) );
		n.setAttribute(a3, generateData(rand) );

		return n;
	}

	@Test
	public void testWriteRead() throws IOException
	{
		Graph gw = new Graph();

		gw.setRoot( generateNode(gw,2) );
		System.out.println("Write: "+gw );

		GraphSerializer serializer = new GraphSerializer();

		ByteArrayOutputStream s = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(s);
		serializer.write(os, gw, 1);
		os.finish();

		DataDump.dumpDataInputWithStats(  "Graph Data", s.toByteArray() );

		ByteArrayInputStream bis = new ByteArrayInputStream(s.toByteArray());
		DataInputStream is = new DataInputStream(bis);
		Graph gr = serializer.read(is);
		System.out.println("Read: "+gr );

		assertFalse( is.hasNextField() );

		assertEquals( gw.getRoot().getTreeDescendantNodes(), gr.getRoot().getTreeDescendantNodes() );

	}

}