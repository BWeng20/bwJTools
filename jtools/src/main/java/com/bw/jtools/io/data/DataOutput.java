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

import java.io.IOException;

/**
 * Platform and class-version tolerant data-output.<br>
 * Fields are wrapped into type-safe container.<br>
 * Unknown fields/data can safely be skipped.
 * @see DataInput
 */
public interface DataOutput
{
	public void writeBoolean(int fieldId, boolean value) throws IOException;
	public void writeByte(int fieldId, int value) throws IOException;
	public void writeShort(int fieldId, int value) throws IOException;
	public void writeChar(int fieldId, char value ) throws IOException;
	public void writeInt(int fieldId, int value) throws IOException;
	public void writeLong(int fieldId, long value) throws IOException;
	public void writeFloat(int fieldId, float value) throws IOException;
	public void writeDouble(int fieldId, double value) throws IOException;
	public void writeString(int fieldId, CharSequence value) throws IOException;
	public void writeNull(int fieldId) throws IOException;

	public void writeArray(int fieldId, boolean[] value) throws IOException;
	public void writeArray(int fieldId, byte[] value) throws IOException;
	public void writeArray(int fieldId, char[] value) throws IOException;
	public void writeArray(int fieldId, short[] value) throws IOException;
	public void writeArray(int fieldId, int[] data) throws IOException;
	public void writeArray(int fieldId, long[] data) throws IOException;
	public void writeArray(int fieldId, float[] data) throws IOException;
	public void writeArray(int fieldId, double[] data) throws IOException;
	public void writeArray(int fieldId, String[] data) throws IOException;

	/**
	 * Writes a plain object together with meta information.
	 * @param fieldId The for the OBJECT entry.
	 * @param o The Object. If null a "NULL" entry is written, independent of "writeNullValues".
	 * @param writeNullValues If true null values are written for fields with null value.
	 *                        If false, null field-values are not written.
	 */
	public void writeObject( int fieldId, Object o, boolean writeNullValues ) throws IOException;

	/**
	 * Starts a new sub object.
	 * The returned output is automatically closed if one other data is written by this or some parent instance.
	 */
	public DataOutput startObject(int fieldId) throws IOException;

	/**
	 * Closes the current container by adding an end-marker.<br>
	 * Needed if the output should be iterated dynamically.
	 */
	public void finish() throws IOException;
}
