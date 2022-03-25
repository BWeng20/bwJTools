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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DataReflectMapTest
{

	@Test
	void testReadWriteMetaData() throws IOException
	{
		DataReflectMap map = new DataReflectMap();
		map.setFieldStartId(10);

		int metaStartId = 7;

		Random rand = new Random(System.currentTimeMillis());

		ByteArrayOutputStream s = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(s);
		map.writeClass(os, TestDataGenerator.generateTestData(rand,1),  metaStartId);
		os.finish();

		ByteArrayInputStream bis = new ByteArrayInputStream(s.toByteArray());
		DataInputStream is = new DataInputStream(bis);
		DataReflectMap map2 = new DataReflectMap();
		DataReflectMap.ClassInfo ci = map2.readClass(is,metaStartId);
		assertFalse( is.hasNextField(), "Stream shall be exhausted" );
		assertEquals( map.classes_, map2.classes_, "Resulting classes shall be equal" );

		DataDump.dumpDataInputWithStats( "Meta Data", s.toByteArray() );

	}
}