/*
 * The MIT License
 *
 * Copyright 2020 Bernd Wengenroth.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bw.jtools.profiling.callgraph;

import com.bw.jtools.collections.ClassNameCompressor;
import com.bw.jtools.collections.StringPool;
import com.bw.jtools.profiling.measurement.MeasurementValue;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Call graph parser that consums output from {@link RawCallGraphRenderer}.<br>
 */
public class RawCallGraphParser implements  RawCallGraphTypes
{
    /** The input stream to read from. */
    protected InputStream is;
    /** The current read position. */
    protected int pos;
    /** The internal buffer. */
    protected byte[] buffer = new byte[4*MAX_STRING_LENGTH];
    protected ClassNameCompressor idPool = new ClassNameCompressor();


    /**
     * Creates a parser that parse binary content.
     */
    public RawCallGraphParser(){
    }

    /**
     * Reads a call-graph.
     * @return The root.
     * @throws IOException In case of some i/O- or format-error.
     */
    public CallNode parse(InputStream is ) throws IOException
    {
        pos = 0;
        idPool.reset();
        this.is = is;
        return readNode();
    }

    /**
     * Reads a node, including the sub-tree for which this node is root.
     * @return The node.
     * @throws IOException In case of some i/O- or format-error.
     */
    protected CallNode readNode() throws IOException
    {
        String name = readString();
        int calls = readInt();
        MeasurementValue value = readValue( );
        CallNode node = new CallNode(name,calls,value);

        int details = readInt();
        while ( (--details) >= 0 )
        {
            int id = readInt();
            value = readValue( );
            node.details.add(new NodeDetail(id, value));
        }
        int edges = readInt( );
        while ( (--edges) >= 0 )
        {
            node.edges.add( readEdge() );
        }
        return node;
    }

    /**
     * Reads a edge, including the callee node.
     * @return The edge.
     * @throws IOException In case of some i/O- or format-error.
     */
    protected CallEdge readEdge() throws IOException
    {
        final boolean highlight = readBoolean();
        final int calls = readInt();
        final MeasurementValue value = readValue();

        return new CallEdge(value, calls,readNode() );
    }

    /**
     * Read a string.
     * @return The string or null
     * @throws IOException In case of some i/O- or format-error.
     */
    protected String readString( ) throws IOException
    {
        byte type = readType( NULL, STRING );
        if ( type == NULL ) {
            return null;
        }
        else
        {
            int len = readInt();
            read(len);
            idPool.getUncompressed( buffer, 0, len );
            return new String(buffer, StandardCharsets.UTF_8);
        }
    }

    /**
     * Reads the next type-id and check if the type is inside the allowed group of types.
     * @param types The allowed types.
     * @return The checked type.
     * @throws IOException In case of some i/O-error or if the read type doesn't match.
     */
    protected byte readType( byte... types ) throws IOException
    {
        int r = is.read(buffer, 0, 1);
        if ( r <= 0 )
            throw new IOException("Unexpected end of stream at position "+pos+".");
        pos += r;
        for ( byte t : types )
            if (buffer[0] == t)
                return t;
        throw new IOException("Illegal format at position "+pos+")");
    }

    /**
     * Reads  a integer value.
     * @return The numeric value or null.
     * @throws IOException In case of some i/o- or format-error.
     */
    protected Integer readInt(  ) throws IOException
    {
        final Number n =  readNumber();
        return n == null ? null : n.intValue();
    }

    /**
     * Reads  a long value.
     * @return The numeric value or null.
     * @throws IOException In case of some i/o- or format-error.
     */
    protected Long readLong(  ) throws IOException
    {
        final Number n =  readNumber();
        return n == null ? null : n.longValue();
    }


    /**
     * Reads  a numeric value.
     * @return The numeric value or null.
     * @throws IOException In case of some i/o- or format-error.
     */
    protected Number readNumber(  ) throws IOException
    {
        int orgPos = pos;
        byte type = readType(NULL, SHORT, BYTE, INT, LONG);
        switch ( type ) {
            case NULL:
                return null;

            case SHORT:
                read(2);
                return (((short)buffer[0])<< 8) | ((short)buffer[1]);

            case  BYTE:
                read(1);
                return buffer[0];
            case INT:
                return  (((long)buffer[0])<<24) |
                        (((long)buffer[1])<<16) |
                        (((long)buffer[2])<< 8) |
                        (((long)buffer[3]));

            case LONG:
                return
                        (((long)buffer[0])<<56) |
                        (((long)buffer[1])<<48) |
                        (((long)buffer[2])<<40) |
                        (((long)buffer[3])<<32) |
                        (((long)buffer[4])<<24) |
                        (((long)buffer[5])<<16) |
                        (((long)buffer[6])<< 8) |
                        (((long)buffer[7])    );
        }
        throw new IOException("Illegal format at position "+orgPos+")");
    }

    /**
     * Reads a boolean value.
     * @return The boolean value.
     * @throws IOException In case of some i/o- or format-error.
     */
    protected boolean readBoolean() throws IOException
    {
        return readType( BOOL_TRUE , BOOL_FALSE ) == BOOL_TRUE;
    }

    /**
     * Reads a measurement-value.
     * @return The measurement-value or null.
     * @throws IOException  In case of some i/o- or format-error.
     */
    protected MeasurementValue readValue() throws IOException
    {
        Integer len = readInt();
        if ( len != null ) {
            int l = len;
            long[] values = new long[l];
            for (int idx =0 ; idx < l ; ++idx)
            {
                values[idx] = readLong();
            }
            return new MeasurementValue(values);
        } else {
            return null;
        }
    }

    /**
     * Reads bytes into the internal buffer.
     * @param size The number of bytes to read.
     * @throws IOException In case of some i/o-error or if not enough bytes could be read.
     */
    protected void read(int size ) throws IOException {
        int i = 0;
        while( i< size )
        {
            int r = is.read(buffer, i, size-i);
            if ( r < 0 )
                throw new IOException("Unexpected end of stream at position "+pos+".");
            i += r;
            pos += r;
        }
    }
}
