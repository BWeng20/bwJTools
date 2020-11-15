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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Call graph renderer to create binary output.<br>
 */
public class RawCallGraphRenderer extends AbstractCallGraphRenderer implements  RawCallGraphTypes
{
    /** The output stream. */
    protected OutputStream os;
    /** The occured exception or null. */
    protected Exception firstException;
    /** The internal buffer. */
    protected byte[] buffer = new byte[50];
    protected ClassNameCompressor idPool = new ClassNameCompressor();

    /**
     * Creates a renderer that creates binary content.
     */
    public RawCallGraphRenderer(OutputStream os)
    {
        super();
        this.os = os == null ? new ByteArrayOutputStream(10240) : os;
    }

    @Override
    protected void start(CallNode root)
    {
        idPool.reset();
    }

    @Override
    protected void startNode(CallNode node)
    {
        writeString( node.name );
        writeInt( node.calls );
        writeValue( node.value );
        writeInt( node.details == null ? 0 : node.details.size() );
        for ( NodeDetail d : node.details)
        {
            writeInt( d.ID );
            writeValue( d.value );
        }
        writeInt( node.edges.size() );
    }

    @Override
    protected void endNode(CallNode node)
    {
    }

    @Override
    protected void startEdge(CallEdge edge)
    {
        writeBoolean( edge.hightlight );
        writeInt( edge.calls );
        writeValue( edge.value );
    }

    @Override
    protected void endEdge(CallEdge edge)
    {
    }

    @Override
    protected void end(CallNode root)
    {
    }

    /**
     * Write a string.
     * @param value The string to write or null.
     */
    protected void writeString( String value ) {
       if ( firstException == null )
       {
           if ( value == null )
           {
                writeNull();
           }
           else
           {
               if ( value.length() > MAX_STRING_LENGTH ) {
                   value = value.substring(0,MAX_STRING_LENGTH);
               }
               byte[] b = idPool.getCompressed(value);
               buffer[0] = STRING;
               write(1);
               int strLen = b.length;
               writeInt(strLen);
               try
               {
                   os.write(b, 0, strLen);
               } catch (IOException i)
               {
                   error(i);
               }
           }
       }
    }

    /**
     * Write a integer value.
     * @param value The value to write.
     */
    protected void writeInt( long value )
    {
        if ( value >= Short.MIN_VALUE && value <= Short.MAX_VALUE )
        {
            buffer[0] = SHORT;
            buffer[1] = (byte) (0xff & (value >> 8));
            buffer[2] = (byte) (0xff & value);
            write(3);
        } else if ( value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE ) {
            buffer[0] = BYTE;
            buffer[1] = (byte) (0xff & value);
            write(2);
        } else if ( value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE ) {
            buffer[0] = INT;
            buffer[1] = (byte) (0xff & (value >> 24));
            buffer[2] = (byte) (0xff & (value >> 16));
            buffer[3] = (byte) (0xff & (value >> 8));
            buffer[4] = (byte) (0xff & value);
            write(5);
        } else {
            buffer[0] = LONG;
            buffer[1] = (byte) (0xff & (value >> 56));
            buffer[2] = (byte) (0xff & (value >> 48));
            buffer[3] = (byte) (0xff & (value >> 40));
            buffer[4] = (byte) (0xff & (value >> 32));
            buffer[5] = (byte) (0xff & (value >> 24));
            buffer[6] = (byte) (0xff & (value >> 16));
            buffer[7] = (byte) (0xff & (value >> 8));
            buffer[8] = (byte) (0xff & value);
            write(9);
        }
    }

    /**
     * Write a boolean value.
     * @param value The value.
     */
    protected void writeBoolean( boolean value )
    {
        buffer[0] = value ? BOOL_TRUE : BOOL_FALSE;
        write( 1);
    }

    /**
     * Write a measurement value.
     * @param value The value or null.
     */
    protected void writeValue( MeasurementValue value ) {
        if ( value == null ) {
            writeNull();
        } else {
            writeInt( value.values.length );
            for (long v : value.values)
            {
                writeInt(v);
            }
        }
    }

    /**
     * Writes a NULL.
     */
    protected void writeNull( ) {
        buffer[0] = NULL;
        write(1);
    }


    /**
     * Writes bytes from internal buffer.
     * @param size Number of bytes.
     */
    protected void write(int size ) {
        if ( firstException == null )
        {
            try
            {
                os.write(buffer, 0, size);
            } catch (IOException i)
            {
                error(i);
            }
        }
    }

    /**
     * Called in case of exception.
     * @param i The exception.
     */
    protected void error(Exception i) {
        firstException = i;
    }

    /**
     * Get the occured exception or null.
     * The renderer stops writing on the first exception.
     * @return The exception or null.
     */
    public Throwable getError() {
        return firstException;
    }
}
