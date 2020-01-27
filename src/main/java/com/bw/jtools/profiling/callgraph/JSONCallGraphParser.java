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

import com.bw.jtools.Log;
import com.bw.jtools.io.JsonTool;
import com.bw.jtools.profiling.measurement.MeasurementValue;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonNumber;

/**
 * Parser that reads output from {@link JSONCallGraphRenderer}.<br>
 * This class is designed to be used by offline analyst software that read some log output.<br>
 * The graphs in the input should not have additional new-lines added inside.
 */
public class JSONCallGraphParser
{
    static final String plong  = "{\t\"title\": \"CallGraph\",";
    static final String pshort = "{\"t\":\"CallGraph\"";

    static final String elong  = "\"version\": 1}";
    static final String eshort = ",\"v\":1}";

    public JSONCallGraphParser( Reader reader )
    {
        parse( reader );
    }

    public JSONCallGraphParser( )
    {
        reset();
    }

    /**
     * Parse the complete content.
     * @param reader
     */
    public void parse( Reader reader )
    {
        reset();
        LineNumberReader lr;

        if ( reader instanceof LineNumberReader )
            lr = (LineNumberReader)reader;
        else
            lr = new LineNumberReader(reader);

        try{
            String line;
            do {
                line = lr.readLine();
                parse(line);
            } while(line != null);
        }
        catch ( Exception e)
        {
            Log.error("Failed to parse call-graph.", e);
        }
    }


    boolean inGraph;
    boolean formatShort;
    StringBuilder graph = new StringBuilder(2048);
    StringBuilder chunkBuffer = new StringBuilder(2048);

    public void reset()
    {
        inGraph = false;
        formatShort = false;
        graph.setLength(0);
        chunkBuffer.setLength(0);
    }

    /**
     * Parse input continuously.
     */
    public void parse( String chunk )
    {
        if ( chunk != null )
        {
            if ( chunkBuffer.length() > 200 )
            {
                chunkBuffer.delete(0, chunkBuffer.length()-plong.length() );
            }

            String endPattern = null;
            int startIndex = 0;
            do {
                if ( inGraph )
                {
                    final int oldLength = graph.length();
                    appendText( graph, chunk, startIndex, chunk.length() );
                    int graphEndIndex = graph.indexOf( endPattern );
                    if ( graphEndIndex >= 0 )
                    {
                        graphEndIndex += endPattern.length();
                        graph.setLength( graphEndIndex );

                        parseGraph( graph, formatShort );
                        graph.setLength(0);

                        startIndex += graphEndIndex-oldLength ;
                        inGraph = false;
                    }
                    else
                    {
                        startIndex = -1;
                    }
                }
                else
                {
                    final int oldLength = chunkBuffer.length();
                    appendText( chunkBuffer, chunk, startIndex, chunk.length() );

                    int bufferStartIndex;
                    if ( (bufferStartIndex=chunkBuffer.indexOf(pshort, startIndex))>=0 )
                    {
                        inGraph = true;
                        formatShort = true;
                        endPattern = eshort;
                        graph.append( pshort );

                        chunk = chunkBuffer.substring(bufferStartIndex+pshort.length());
                        startIndex = 0;
                        chunkBuffer.setLength(0);
                    }
                    else if ( (bufferStartIndex=chunkBuffer.indexOf(plong, startIndex))>=0 )
                    {
                        inGraph = true;
                        formatShort = false;
                        endPattern = elong;
                        graph.append( plong );

                        chunk = chunkBuffer.substring(bufferStartIndex+plong.length());
                        startIndex = 0;
                        chunkBuffer.setLength(0);
                    }
                    else
                    {
                        startIndex = -1;
                    }
                }
            } while(startIndex >= 0);
        }
    }

    protected final void appendText( StringBuilder buffer, final String chunk, int start, int end )
    {
        buffer.ensureCapacity(buffer.length()+(end-start));
        for ( int i = start; i<end; ++i )
        {
            final char c = chunk.charAt(i);
            if ( c == '\r' || c == '\n' )
            {
            }
            else
            {
                buffer.append(c);
            }
        }
    }


    public static class GraphInfo
    {
        private GraphInfo( String source, CallNode root )
        {
            this.source = source;
            this.root = root;
        }

        public final String source;
        public final CallNode root;
    }

    /**
     * Get all found call graphs.
     * @return The call graphs.
     */
    public GraphInfo[] getCallGraphs() {
        return callGraphs.toArray( new GraphInfo[0] );
    }

    public int getNumberOfCallGraphs() {
        return callGraphs.size();
    }

    ArrayList<GraphInfo> callGraphs = new ArrayList<>();

    private void parseGraph(CharSequence graph, boolean formatShort)
    {
        final String graphSource = graph.toString();
        JsonObject json = JsonTool.parseJson(graphSource);
        if ( json == null ) {
            Log.warn("Failed to parse graph.");
            System.err.println(graph);
        }
        else
        {
            callGraphs.add( new GraphInfo(graphSource, parseJsonMethod( JsonTool.getJsonObject(json, formatShort?"C":"Call"), formatShort )));
        }
    }

    private CallNode parseJsonMethod( JsonObject js, boolean formatShort )
    {

        CallNode node = new CallNode(
                JsonTool.getJsonString(js, formatShort? "n":"name"),
                JsonTool.getJsonInt(js, formatShort? "c":"calls", 0),
                parseMeasurementValue( JsonTool.getJsonValue(js, formatShort? "t":"time") )
                );
        node.netValue = parseMeasurementValue( JsonTool.getJsonValue(js, formatShort? "s":"self" ));
        JsonArray details = JsonTool.getJsonArray(js, formatShort? "d":"details" );
        if ( details != null )
        {
            for ( JsonValue v : details )
            {
                node.details.add( JsonTool.getJsonString(v, null) );
            }
        }
        JsonArray calls = JsonTool.getJsonArray(js, formatShort? "u":"using");
        if ( calls != null )
        {
            for ( JsonValue cv : calls)
            {
                CallNode callee = parseJsonMethod( JsonTool.getJsonObject(cv, formatShort?"C":"Call"), formatShort );
                CallEdge edge = new CallEdge(
                        parseMeasurementValue(JsonTool.getJsonValue(cv, formatShort?"t":"time")),
                        JsonTool.getJsonInt(cv, formatShort?"c":"count", 0),
                        callee);
                edge.hightlight = JsonTool.getJsonBoolean(cv, formatShort?"h":"highlight", false);
                node.edges.add( edge );
            }
        }
        return node;
    }

    private MeasurementValue parseMeasurementValue( JsonValue value )
    {
        MeasurementValue v = null;
        if ( value instanceof JsonArray )
        {
            JsonArray ar = (JsonArray)value;
            long[] data = new long[ ar.size() ];
            for ( int i = 0 ; i<data.length ; ++i )
            {
                data[i] = ((JsonNumber)ar.get(i)).intValue();
            }
            v = new MeasurementValue( data );
        } else if ( value != null )
        {
            v = new MeasurementValue( new long[] {JsonTool.getJsonInt(value, "",0)} );
        }
        return v;
    }

}
