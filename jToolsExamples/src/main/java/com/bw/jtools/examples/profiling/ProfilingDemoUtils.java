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
package com.bw.jtools.examples.profiling;

import com.bw.jtools.profiling.CalleeProfilingInformation;
import com.bw.jtools.profiling.ClassProfilingInformation;
import com.bw.jtools.profiling.MethodProfilingInformation;
import com.bw.jtools.profiling.callgraph.AbstractCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.FreeMindGraphRenderer;
import com.bw.jtools.profiling.callgraph.JSONCallGraphParser;
import com.bw.jtools.profiling.callgraph.JSONCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.Options;
import com.bw.jtools.profiling.measurement.AbstractMeasurementSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Some helpful function for the demos in this package.
 */
public final class ProfilingDemoUtils
{
    static HashMap<String,String> arguments = new HashMap<>();
    static NumberFormat nf = NumberFormat.getInstance();

    /**
     * Sets the used number format.
     * @param nf The format.
     */
    public static void setNumberFormat( NumberFormat nf )
    {
        ProfilingDemoUtils.nf = nf;
    }


    protected static void parseArguments( String args[] )
    {
        for ( String p : args )
        {
            while ( p.startsWith("-")) p = p.substring(1);
            int eIdx = p.indexOf('=');
            if ( eIdx > 0) {
                arguments.put( p.substring(0,eIdx).toUpperCase(), p.substring(eIdx+1));
            }
        }
    }

    protected static int getArgument( String name, int defaultVal ) {
        String val = arguments.get(name.toUpperCase());
        if ( val == null )
            return defaultVal;
        try
        {
            return (int)Double.parseDouble(val);
        }
        catch ( NumberFormatException nfe)
        {
            System.err.println("Value '"+val+"' for argument '"+name+"' needs to be an numeric value.");
            return defaultVal;
        }
    }

    protected static String getArgument( String name, String defaultVal ) {
        String val = arguments.get(name.toUpperCase());
        return ( val == null ) ? defaultVal : val;
    }


    /**
     * Manual dump of gathered profiling information.
     */
    static public void dumpProfilingInformation() {
        System.out.println( "-- Dump of gathered profiling information" );

        ///////////////////////////////////////////////////
        // Access profiling information via collected class
        // information.
        List<ClassProfilingInformation> cis = ClassProfilingInformation.getClassInformation();
        for (ClassProfilingInformation ci : cis )
        {
            for (MethodProfilingInformation mi : ci.getMethodInformation() )
            {
                System.out.println( ci.name+"."+mi.name+" "+ AbstractMeasurementSource.format( nf, mi.sum )+" Calls "+mi.calls+", Recursive "+mi.recursiveCalls );
                for (CalleeProfilingInformation cli : mi.callees.values() )
                {
                    System.out.println( "   -> "+cli.calls+" x "+cli.callee.clazz.name+"."+cli.callee.name+" "+
                            AbstractMeasurementSource.currentSource.formatValue( nf, cli.sum ) );
                }
            }
        }
    }

    /**
     * Dumps call graphs of all top-level methods to console.
     */
    static public void dumpAllCallGraphs() {

        // Access call-relations, beginning at top-level methods.
        List<MethodProfilingInformation> topMethods = AbstractCallGraphRenderer.filterTopLevelCalls(ClassProfilingInformation.getClassInformation());
        System.out.println( "-- Call Graphs of all Top Level Methods ----------" );
        for ( MethodProfilingInformation mi : topMethods )
        {
            dumpCall( ">", mi );
            System.out.println( "--------------------------------------------------" );
        }
    }

    /**
     * Dumps a method.
     * @param prefix Dump prefix for each line.
     * @param mi     The Method Information
     */
    static public void dumpCall( String prefix, MethodProfilingInformation mi )
    {
        System.out.println( prefix+mi.name+" "+AbstractMeasurementSource.format(nf,mi.sum) );
        for ( CalleeProfilingInformation ci : mi.callees.values() ){
            dumpCallEdge("--"+prefix, ci);
        }
    }

    /**
     * Dumps a call edge.
     * @param prefix Dump prefix for each line.
     * @param ci     The Callee Information
     */
    static public void dumpCallEdge( String prefix, CalleeProfilingInformation ci )
    {
        if ( ci.callee != null )
            dumpCall( prefix, ci.callee );
    }

    /**
     * Dumps information about a class
     * @param c     The Class of interest.
     */
    static public void dumpClass( Class c )
    {
        System.out.println("== Dumping methods of "+c.getSimpleName()+":");

        Method[] ms = c.getDeclaredMethods();
        for ( Method m : ms )
        {
            Annotation[] as = m.getAnnotations();
            for ( Annotation a : as)
            {
                System.out.println(" @"+a.annotationType().getName() );

            }

            System.out.print(" "+String.format("0x%08X", m.getModifiers())+" "+Modifier.toString(m.getModifiers())+" "+m.getReturnType().getSimpleName()+" "+m.getName()+"(");
            Parameter[] ps = m.getParameters();
            for ( int i = 0 ; i<ps.length; ++i)
            {
                Parameter p = ps[i];
                if (i>0) System.out.print(", ");
                System.out.print( p.getType().getSimpleName()+" "+p.getName() );
            }
            System.out.println(")");
        }
    }

    /**
     * Call JSON renderer with all top-level methods and writes the content to file.
     * @param fileName the JSON-file to write.
     * @param pretty If true output is human readable.
     */
    static public void writeJSON( String fileName, boolean pretty )
    {
        if ( fileName != null && !fileName.isEmpty() )
        {

            List<MethodProfilingInformation> topMethods = AbstractCallGraphRenderer.filterTopLevelCalls(ClassProfilingInformation.getClassInformation());
            String json = new JSONCallGraphRenderer(nf, Options.ADD_CLASSNAMES, Options.HIGHLIGHT_CRITICAL, Options.ADD_MIN_MAX, pretty ? Options.PRETTY : Options.NONE )
                    .render(topMethods, ClassProfilingInformation.getProfilingStartTime(), new Date() );

            Writer w = null;
            try
            {
                w = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_8);
                w.write(json);
            }
            catch (IOException ex)
            {
                System.err.println("ERR: Failed to write to '"+fileName+"'. "+ex.getMessage());
            } finally
            {
                if ( w != null ) {
                    try
                    {
                        w.close();
                        System.out.println("Written JSON to '"+fileName+"'.");

                    }
                    catch (IOException ex)
                    {
                        System.err.println("ERR: Failed to close '"+fileName+"'. "+ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Call FreeMind renderer with all top-level methods and writes the MindMap to file.
     * @param fileName the MM-file to write.
     */
    static public void writeMindMap( String fileName )
    {
        if ( fileName != null && !fileName.isEmpty() )
        {

            List<MethodProfilingInformation> topMethods = AbstractCallGraphRenderer.filterTopLevelCalls(ClassProfilingInformation.getClassInformation());
            String mindMap = new FreeMindGraphRenderer(nf, Options.ADD_CLASSNAMES, Options.HIGHLIGHT_CRITICAL, Options.ADD_MIN_MAX)
                    .render(topMethods, ClassProfilingInformation.getProfilingStartTime(), new Date() );

            Writer w = null;
            try
            {
                w = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_8);
                w.write(mindMap);
            }
            catch (IOException ex)
            {
                System.err.println("ERR: Failed to write to '"+fileName+"'. "+ex.getMessage());
            } finally
            {
                if ( w != null ) {
                    try
                    {
                        w.close();
                        System.out.println("Written mindmap to '"+fileName+"'.");

                    }
                    catch (IOException ex)
                    {
                        System.err.println("ERR: Failed to close '"+fileName+"'. "+ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Tests parsing of a JSON file.
     * @param fileName The file to parse.
     */
    static public void parseJSON(String fileName)
    {
        if ( fileName != null && !fileName.isEmpty() )
        {
            try
            {
                try (Reader r = new FileReader(fileName))
                {
                    JSONCallGraphParser p = new JSONCallGraphParser(r);
                    JSONCallGraphParser.GraphInfo graphs[] = p.getCallGraphs();
                    System.out.println("Parsed '"+fileName+"' with "+graphs.length+" Graphs.");
                }
            } catch (Exception ex)
            {
                System.err.println("ERR: Failed to read from '"+fileName+"'. "+ex.getMessage());
            }
        }
    }



}
