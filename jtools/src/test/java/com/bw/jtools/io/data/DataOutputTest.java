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

import com.bw.jtools.collections.GenericIterator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DataOutputTest
{
	/** Writes data that can be checked by "readData" below. */
	int writeData(DataOutput os, int firstId) throws IOException
	{
		int fieldId = firstId;
		os.writeNull(fieldId++);
		os.writeBoolean(fieldId++, false);
		os.writeBoolean(fieldId++, true);
		os.writeString(fieldId++, "Hallo");
		os.writeString(fieldId++, "");
		os.writeString(fieldId++, null);

		os.writeByte(fieldId++, Byte.MIN_VALUE);
		os.writeByte(fieldId++, Byte.MAX_VALUE);
		os.writeShort(fieldId++, Short.MIN_VALUE);
		os.writeShort(fieldId++, Short.MAX_VALUE);
		os.writeInt(fieldId++, Integer.MIN_VALUE);
		os.writeInt(fieldId++, Integer.MAX_VALUE);
		os.writeChar(fieldId++, Character.MIN_VALUE);
		os.writeChar(fieldId++, Character.MAX_VALUE);
		os.writeLong(fieldId++, Long.MIN_VALUE);
		os.writeLong(fieldId++, Long.MAX_VALUE);
		os.writeFloat(fieldId++, Float.MIN_VALUE);
		os.writeFloat(fieldId++, Float.MAX_VALUE);
		os.writeDouble(fieldId++, Double.MIN_VALUE);
		os.writeDouble(fieldId++, Double.MAX_VALUE);

		os.writeString(fieldId, "The End");
		return fieldId;
	}

	/** Read and verify data written by writeData above. */
	int readData(DataInput is, int firstId) throws IOException
	{
		int fieldId = firstId;

		assertEquals( fieldId++, is.getFieldId()  );
		assertNull( is.readBoolean() );

		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( false, is.readBoolean());
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( true, is.readBoolean());

		// Test all string variants
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( "Hallo", is.readString());
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( "", is.readString());
		assertEquals( fieldId++, is.getFieldId()  );
		assertNull( is.readString()) ;

		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Byte.MIN_VALUE, is.readNumber().byteValue());
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Byte.MAX_VALUE, is.readNumber().byteValue());

		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Short.MIN_VALUE, is.readNumber().intValue());
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Short.MAX_VALUE, is.readNumber().shortValue());

		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Integer.MIN_VALUE, is.readNumber().intValue() );
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Integer.MAX_VALUE, is.readNumber().intValue() );

		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Character.MIN_VALUE, is.readCharacter());
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Character.MAX_VALUE, is.readCharacter() );

		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Long.MIN_VALUE, is.readNumber().longValue());
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Long.MAX_VALUE, is.readNumber().longValue());

		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Float.MIN_VALUE, is.readNumber().floatValue() );
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Float.MAX_VALUE, is.readNumber().floatValue());

		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Double.MIN_VALUE, is.readNumber().doubleValue());
		assertEquals( fieldId++, is.getFieldId()  );
		assertEquals( Double.MAX_VALUE, is.readNumber().doubleValue());

		assertEquals( fieldId, is.getFieldId()  );
		assertEquals(  "The End", is.readString());

		return fieldId;
	}

	@Test
	void testDatatypes() throws IOException
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();

		DataOutputStream os = new DataOutputStream(s);
		writeData(os,2);
		os.finish();

		ByteArrayInputStream bis = new ByteArrayInputStream(s.toByteArray());
		DataInputStream is = new DataInputStream(bis);
		readData(is,2);
		assertFalse( is.hasNextField() );
	}

	@Test
	void testSubStructures() throws IOException
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(s);

		int fieldId = 1;
		writeData(os.startObject(fieldId++),1);
		os.finish();

		ByteArrayInputStream bis = new ByteArrayInputStream(s.toByteArray());
		DataInputStream is = new DataInputStream(bis);

		fieldId = 1;
		assertEquals( fieldId++, is.getFieldId() );
		readData( is.startObject(), 1);
		assertFalse( is.hasNextField() );
	}

	final int N = 101;
	boolean dataBoolean[] = new boolean[N];
	byte dataByte[] = new byte[N];
	char dataChar[] = new char[N];
	short dataShort[] = new short[N];
	int dataInt[] = new int[N];
	long dataLong[] = new long[N];
	float dataFloat[] = new float[N];
	double dataDouble[] = new double[N];
	String dataString[] = new String[N];

	int writeArrays(DataOutput os, int firstId) throws IOException
	{
		Random rand = new Random(System.currentTimeMillis());

		rand.nextBytes(dataByte);
		for ( int i=0 ; i<N; ++i)
		{
			dataBoolean[i] = rand.nextBoolean();
			dataShort[i] = (short)rand.nextInt();
			dataChar[i] = (char)rand.nextInt();
			dataInt[i] = rand.nextInt();
			dataLong[i] = rand.nextLong();
			dataFloat[i] = rand.nextFloat();
			dataDouble[i] = rand.nextDouble();
			dataString[i] = TestDataGenerator.generateString(rand);
		}

		int fieldId = firstId;
		os.writeArray( fieldId, dataByte );
		os.writeArray( ++fieldId, new byte[0] );
		os.writeArray( ++fieldId, dataBoolean );
		os.writeArray( ++fieldId, new boolean[0] );
		os.writeArray( ++fieldId, dataShort );
		os.writeArray( ++fieldId, new short[0] );
		os.writeArray( ++fieldId, dataChar );
		os.writeArray( ++fieldId, new char[0] );
		os.writeArray( ++fieldId, dataInt );
		os.writeArray( ++fieldId, new int[0] );
		os.writeArray( ++fieldId, dataLong );
		os.writeArray( ++fieldId, new long[0] );
		os.writeArray( ++fieldId, dataFloat );
		os.writeArray( ++fieldId, new float[0] );
		os.writeArray( ++fieldId, dataDouble );
		os.writeArray( ++fieldId, new double[0] );
		os.writeArray( ++fieldId, dataString );
		os.writeArray( ++fieldId, new String[0] );

		return fieldId;
	}

	int readArrays(DataInput is, int firstId) throws IOException
	{
		int fieldId = firstId;
		assertEquals( fieldId, is.getFieldId() );
		assertArrayEquals(  dataByte, is.readByteArray() );
		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new byte[0], is.readByteArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals(  dataBoolean, is.readBooleanArray() );
		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new boolean[0], is.readBooleanArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals(  dataShort, is.readShortArray() );
		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new short[0], is.readShortArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals(  dataChar, is.readCharArray() );
		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new char[0], is.readCharArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals(  dataInt, is.readIntArray() );
		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new int[0], is.readIntArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals(  dataLong, is.readLongArray() );
		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new long[0], is.readLongArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals(  dataFloat, is.readFloatArray() );
		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new float[0], is.readFloatArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals(  dataDouble, is.readDoubleArray() );
		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new double[0], is.readDoubleArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals(  dataString, is.readStringArray() );

		assertEquals( ++fieldId, is.getFieldId() );
		assertArrayEquals( new String[0], is.readStringArray() );
		return fieldId;
	}


	@Test
	void testArrays() throws IOException
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(s);

		int fieldId = writeData(os, 1);
		writeArrays( os, fieldId+1 );
		os.finish();

		ByteArrayInputStream bis = new ByteArrayInputStream(s.toByteArray());
		DataInputStream is = new DataInputStream(bis);

		fieldId = readData( is, 1 );
		readArrays( is, fieldId+1 );
	}


	Object writeObject( DataOutput os, int fieldId, boolean writeNullValues) throws IOException
	{
		Random rand = new Random(System.currentTimeMillis());

		TestData td = TestDataGenerator.generateTestData(rand,1);
		os.writeObject(fieldId, td, writeNullValues);
		return td;
	}

	@Test
	void testObjectsWithWriteNull() throws IOException
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(s);

		// Write some data simply to increase complexity
		int fieldId = writeData(os, 1);
		Object obw = writeObject( os, fieldId+1, true );
		os.finish();

		ByteArrayInputStream bis = new ByteArrayInputStream(s.toByteArray());
		DataInputStream is = new DataInputStream(bis);

		fieldId = readData( is, 1 );
		Object obr = is.readObject();

		assertEquals(obw,obr);

		DataDump.dumpDataInputWithStats(  "Test Data", s.toByteArray() );

	}

	@Test
	void testObjectsWithNotWriteNull() throws IOException
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(s);

		// Write some data simply to increase complexity
		int fieldId = writeData(os, 1);
		Object obw = writeObject( os, fieldId+1, false );
		os.finish();
		System.out.println("Write: "+obw );

		ByteArrayInputStream bis = new ByteArrayInputStream(s.toByteArray());
		DataInputStream is = new DataInputStream(bis);

		fieldId = readData( is, 1 );
		Object obr = is.readObject();

		System.out.println("Read: "+obr );
		assertEquals(obw,obr);

		DataDump.dumpDataInputWithStats(  "Test Data", s.toByteArray() );

	}


}