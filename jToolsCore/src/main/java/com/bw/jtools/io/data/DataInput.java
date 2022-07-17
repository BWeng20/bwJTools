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
 * Platform and class-version tolerant data handling.<br>
 * Fields are wrapped into type-safe container.<br>
 * Unknown fields/data can safely be skipped.
 */
public interface DataInput
{

    /**
     * Checks if the input has fields left.
     */
    public boolean hasNextField() throws IOException;

    /**
     * Get field-id of the current field. If called after one of the "readXXX"
     * methods the next field is automatically fetched.
     */
    public int getFieldId() throws IOException;

    /**
     * Checks if current field is null.
     */
    public boolean isFieldNull() throws IOException;

    /**
     * Checks if current field is an object.
     */
    public boolean isFieldObject() throws IOException;

    /**
     * Skips the current field.<br>
     * Works also on fields of type object.
     */
    public void skip() throws IOException;

    /**
     * Reads a boolean value.
     */
    public Boolean readBoolean() throws IOException;

    /**
     * Reads a character value.
     */
    public Character readCharacter() throws IOException;

    /**
     * Checks if the current field is a numeric type.
     */
    public boolean isFieldNumeric() throws IOException;

    /**
     * Reads byte, short, integer, long, float or double fields.<br>
     * Can also be used to read char fields. In this case an integer value is
     * returned.
     */
    public Number readNumber() throws IOException;

    /**
     * Checks if the current field is a string.
     */
    public boolean isFieldString() throws IOException;

    /**
     * Reads a string value.
     */
    public String readString() throws IOException;

    /**
     * Checks if the current field is an array.
     */
    public boolean isFieldArray() throws IOException;

    /**
     * Reads a boolean array.
     */
    public boolean[] readBooleanArray() throws IOException;

    /**
     * Reads a byte array.
     */
    public byte[] readByteArray() throws IOException;

    /**
     * Reads a short array.
     */
    public short[] readShortArray() throws IOException;

    /**
     * Reads a character array.
     */
    public char[] readCharArray() throws IOException;

    /**
     * Reads an integer array.
     */
    public int[] readIntArray() throws IOException;

    /**
     * Reads a long integer array.
     */
    public long[] readLongArray() throws IOException;

    /**
     * Reads a float array.
     */
    public float[] readFloatArray() throws IOException;

    /**
     * Reads a double array.
     */
    public double[] readDoubleArray() throws IOException;

    /**
     * Reads an array of string values.
     */
    public String[] readStringArray() throws IOException;

    /**
     * Generic read of arrays.<br>
     * The returned object is a native array of the element type.<br>
     * The method is slower than the specific versions.
     */
    public Object readArray() throws IOException;

    /**
     * Creates a sub-data-input to read an object.<br>
     * The returned input can't read across the end of the object.<br>
     * The returned input will be closed automatically (and the data will be
     * skipped) if some other method on this instance is called.
     */
    public DataInput startObject() throws IOException;

    /**
     * Reads an object that was written by
     * {@link DataOutput#writeObject(int, Object, boolean)}.<br>
     * If the stored object implement the {@link Data} interface, the
     * {@link Data#read(DataInput)} method is called.
     */
    public Object readObject() throws IOException;

    /**
     * Close this and any underlying stream.
     */
    void close() throws IOException;
}
