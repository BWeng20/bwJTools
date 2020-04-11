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

import com.bw.jtools.profiling.measurement.MeasurementValue;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Call graph renderer to create a JSON output.<br>
 * To reduce runtime dependencies and output size no JSON framework is used.<br>
 * If {@link Options#PRETTY} is configured, all element-names are printed out in full, otherwise only the first letter is printed.
 * All line-feeds and indentation are removed if {@link Options#PRETTY} is not set.<br>
 * For reading both formats, {@link JSONCallGraphParser} can be used - which is using JSON API and needs an JSON-implementation in classpath.
 */
public class JSONCallGraphRenderer extends AbstractCallGraphRenderer
{
    protected StringBuilder prefix;
    protected static final String nullString = "null";
    protected static final String trueString = "true";
    protected static final String falseString = "false";

    protected static enum State {
        OBJECT_START,
        OBJECT_ADDITIONAL,
        ARRAY_START,
        ARRAY_ADDITIONAL;
    }

    protected State state;
    protected Deque<State> stack = new ArrayDeque<>(10);

    /**
     * Creates a renderer that creates JSON content.
     * @param nf Number format to use.
     * @param options Options.
     */
    public JSONCallGraphRenderer(NumberFormat nf, Options... options)
    {
        super(nf,options);
        // JSON doesn't allow any grouping or a different separator in numbers.
        if ( nf instanceof DecimalFormat )
        {
            DecimalFormatSymbols ds = ((DecimalFormat )nf).getDecimalFormatSymbols();
            ds.setDecimalSeparator('.') ;
            ((DecimalFormat )nf).setDecimalFormatSymbols( ds );
        }
        nf.setGroupingUsed(false);
        if ( pretty ) prefix = new StringBuilder(20);
    }

    @Override
    protected void start(CallNode root)
    {
        sb.append("{");
        if ( pretty )
        {
            prefix.setLength(0);
            prefix.append('\t');
        }
        stack.clear();
        state = State.OBJECT_START;
        element("title", "CallGraph");
    }

    @Override
    protected void startNode(CallNode node)
    {
        // Only the root node will be printed inside an object and will show this element-name:
        startObject("Call" );
        element("name", node.name );
        if ( node.calls>0) element("calls", node.calls );
        if ( node.details != null && !node.details.isEmpty() )
        {
            startArray("details");
            for ( NodeDetail d : node.details)
            {
                startObject("" );
                element("id", d.ID );
                addValue("value", d.value );
                endObject();
            }
            endArray();
        }
        if ( node.value != null )
        {
            addValue("time", node.value );
            addValue("self", node.getNetMeasurement() );
        }
        startArray("using");
    }

    @Override
    protected void endNode(CallNode node)
    {
        endArray();
        endObject();
    }

    @Override
    protected void startEdge(CallEdge edge)
    {
        startObject("edge" );
        element("highlight", edge.hightlight);
        element("count", edge.calls);
        addValue( "time", edge.value);
    }

    @Override
    protected void endEdge(CallEdge edge)
    {
        endObject();
    }

    @Override
    protected void end(CallNode root)
    {
        element("version", 1.0);
        if ( pretty )
        {
            sb.append("\n");
        }
        sb.append("}");
    }


    /**
     * Appends a JSON element name
     * @param name The name to append.
     */
    protected void appendName( String name )
    {
        if (pretty)
        {
            sb.append('"');
            sb.append( name );
            sb.append("\": ");
        }
        else
        {
            sb.append('"');
            sb.append( name.charAt(0));
            sb.append("\":");
        }
    }

    /**
     * Prepares output for a new element.
     * Cares about separators and new-lines.
     */
    protected void prepareForNextElement()
    {
        switch ( state )
        {
            case ARRAY_START:
                if ( pretty ) sb.append("\n").append( prefix );
                state = State.ARRAY_ADDITIONAL;
                break;
            case OBJECT_START:
                if ( pretty ) sb.append("\n").append( prefix );
                state = State.OBJECT_ADDITIONAL;
                break;
            case OBJECT_ADDITIONAL:
            case ARRAY_ADDITIONAL:
                if ( pretty )
                {
                    sb.append(",\n").append( prefix );
                }
                else
                {
                    sb.append(',');
                }
                break;
        }
    }

    /**
     * Starts a new sub-object.<br>
     * If inside an array, the element name is ignored.
     * @param name The name of the element.
     */
    protected void startObject( String name )
    {
        prepareForNextElement();
        if ( state == State.OBJECT_ADDITIONAL ) appendName(name);
        if ( pretty )
        {
            sb.append("{");
            prefix.append('\t');
        }
        else
        {
            sb.append('{');
        }

        stack.addLast(state );
        state = State.OBJECT_START;

    }

    /**
     * End of an object.
     */
    protected void endObject()
    {
        if ( pretty )
        {
            if ( prefix.length()>0 ) prefix.setLength(prefix.length()-1);
            if ( state == State.OBJECT_ADDITIONAL )
            {
                sb.append("\n");
                sb.append( prefix );
            }
            sb.append('}');
        }
        else
        {
            sb.append('}');
        }
        state = stack.removeLast();
    }

    /**
     * Starts a new array.<br>
     * If inside an array, the element name is ignored.
     * @param name The name of the element.
     */
    protected void startArray(String name)
    {
        prepareForNextElement();
        if ( state == State.OBJECT_ADDITIONAL ) appendName(name);
        if ( pretty )
        {
            sb.append("[");
            prefix.append('\t');

        }
        else
        {
            sb.append('[');
        }

        stack.addLast( state );
        state = State.ARRAY_START;

    }

    /**
     * End of an array.
     */
    protected void endArray()
    {
        if ( pretty )
        {
            if ( prefix.length()>0 ) prefix.setLength(prefix.length()-1);
            if ( state == State.ARRAY_ADDITIONAL )
            {
                sb.append("\n");
                sb.append( prefix );
            }
            sb.append(']');
        }
        else
        {
            sb.append(']');
        }
        state = stack.removeLast();
    }

    /**
     * Appends a text value. Cares about quotes and escaping.
     * @param text The value.
     */
    protected void value(String text)
    {
        if( text == null )
            sb.append( nullString );
        else
        {
            sb.append('"');
            final char data[] = text.toCharArray();
            for (char c : data )
            {
                switch ( c )
                {
                case '"' : sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b" ); break;
                case '\f': sb.append("\\f" ); break;
                case '\n': sb.append("\\n" ); break;
                case '\r': sb.append("\\r" ); break;
                case '\t': sb.append("\\t" ); break;
                default:
                    sb.append( c );
                }
            }
            sb.append('"');
        }
    }

    /**
     * Appends a numeric value.
     * @param value The value.
     */
    protected void value(Number value)
    {
        if( value == null )
            sb.append( nullString );
        else
            sb.append( nf.format(value ));
    }

    /**
     * Appends a boolean value.
     * @param value The value.
     */
    protected void value(boolean value)
    {
        sb.append( value ? trueString : falseString );
    }

    /**
     * Adds a simple text element
     * @param name The name.
     * @param value The value.
     */
    protected void element( String name, String value )
    {
        prepareForNextElement();
        if ( state == State.OBJECT_ADDITIONAL ) appendName(name);
        value(value);
    }

    /**
     * Adds a simple number element
     * @param name The name.
     * @param value The value.
     */
    protected void element( String name, Number value )
    {
        prepareForNextElement();
        if ( state == State.OBJECT_ADDITIONAL ) appendName(name);
        value(value);
    }

    /**
     * Adds a simple Boolean element
     * @param name The name of the element.
     * @param value The value.
     */
    protected void element( String name, boolean value )
    {
        prepareForNextElement();
        if ( state == State.OBJECT_ADDITIONAL ) appendName(name);
        value(value);
    }

    /**
     * Adds a measurement value
     * @param name The name of the element.
     * @param v The value.
     */
    protected void addValue( String name, MeasurementValue v )
    {
        if ( v != null && v.values != null )
        {
            if ( v.values.length==1)
            {
                element(name, v.values[0] );
            }
            else if ( v.values.length>1 )
            {
                startArray(name);
                for( long val : v.values )
                {
                    element("value", val );
                }
                endArray();
            }
        }
    }


}
