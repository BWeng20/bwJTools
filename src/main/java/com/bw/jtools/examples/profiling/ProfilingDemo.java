/*
 * The MIT License
 *
 * Copyright 2019-2020 Bernd Wengenroth.
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

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import com.bw.jtools.profiling.CalleeProfilingInformation;
import com.bw.jtools.profiling.ClassProfilingInformation;
import com.bw.jtools.profiling.MethodProfiling;
import com.bw.jtools.profiling.MethodProfilingInformation;
import com.bw.jtools.profiling.callgraph.AbstractCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.FreeMindGraphRenderer;
import com.bw.jtools.profiling.callgraph.Options;
import com.bw.jtools.profiling.measurement.AbstractMeasurementSource;
import java.util.Date;

/**
 * A test for profiling with different examples about profiling methods and
 * how to output the information.
 */
public class ProfilingDemo
{

    static HashMap<String,String> arguments = new HashMap<>();
    static NumberFormat nf = NumberFormat.getInstance();
    static int workLoop = 100000;
;

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

    static public void main(  String args[] )
    {

        parseArguments(args);

        ProfilingDemo demo = new ProfilingDemo();

        int fractions = getArgument("fractionDigits", 5);
        nf.setMaximumFractionDigits(fractions);

        int repeat = getArgument("repeat", 1);
        if ( repeat<1 ) {
            repeat=1;
        }

        workLoop = getArgument("workLoop", workLoop);

        for ( int i=0 ; i<repeat ; ++i )
        {
            if ( repeat>1 ) {
                System.out.println("############# Run  "+i);
            }

            ClassProfilingInformation.clearProfilingInformation();

            demo.rec = 0;

            ///////////////////////////////////////////////////
            // Call the top-level method to trigger profiling.
            // The run can be executed in a loop to show performance of
            // profiling. Beside "doSomeWork" all net times are
            // mainly profiling overhead.
            // This overhead will be reduced a bit after system runs hot (~40 runs).
            demo.privateMethodAutomatic();
            demo.otherCallTop();


            System.out.println( "------------------------------------------------------" );

            ///////////////////////////////////////////////////
            // Access profiling information via collected class
            // information.
            List<ClassProfilingInformation> cis = ClassProfilingInformation.getClassInformation();
            for (ClassProfilingInformation ci : cis )
            {
                System.out.println( "Class "+ci.name );
                for (MethodProfilingInformation mi : ci.getMethodInformation() )
                {
                    System.out.println( "  Method "+mi.name+" "+ AbstractMeasurementSource.currentSource.format( nf, mi.sum )+" Calls "+mi.calls+", Recursive "+mi.recursiveCalls );
                    for (CalleeProfilingInformation cli : mi.callees.values() )
                    {
                        System.out.println( "   -> "+cli.calls+" x "+cli.callee.clazz.name+"."+cli.callee.name+" "+
                                AbstractMeasurementSource.currentSource.format( nf, cli.sum ) );
                    }
                }
            }

            ///////////////////////////////////////////////////
            // Access call-relations, beginning at top-level
            // methods.
            List<MethodProfilingInformation> topMethods = AbstractCallGraphRenderer.filterTopLevelCalls(ClassProfilingInformation.getClassInformation());

            // "publicRecursiveMethod" is recursive. Recursive methods are only profiles by
            // the top-most call in the stack.
            System.out.println( "------------------------------------------------------" );
            System.out.println( "Real calls to publicRecursiveMethod: "+demo.rec );
            System.out.println( "------------------------------------------------------" );
            for ( MethodProfilingInformation mi : topMethods )
            {
                System.out.println( "-- Top Method "+mi.name+" ------------" );
                dumpCall( "-->", mi );
            }
            System.out.println( "------------------------------------------------------" );
        }

        ///////////////////////////////////////////////////
        // Generate a mindmap file
        String fileName = getArgument("file", null);
        if ( fileName != null )
        {
            List<MethodProfilingInformation> topMethods = AbstractCallGraphRenderer.filterTopLevelCalls(ClassProfilingInformation.getClassInformation());
            String mindMap = new FreeMindGraphRenderer(nf, Options.HIGHLIGHT_CRITICAL, Options.ADD_MIN_MAX)
                    .render(topMethods, ClassProfilingInformation.getProfilingStartTime(), new Date() );

            Writer w = null;
            try
            {
                w = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_8);
                w.write(mindMap);
            }
            catch (IOException ex)
            {
                System.err.println("Failed to write to '"+fileName+"'. "+ex.getMessage());
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
                        System.err.println("Failed to close '"+fileName+"'. "+ex.getMessage());
                    }
                }
            }

        }

    }

    private static void dumpCall( String prefix, MethodProfilingInformation mi )
    {
        System.out.println( prefix+mi.name+" "+AbstractMeasurementSource.currentSource.format(nf,mi.sum) );
        for ( CalleeProfilingInformation ci : mi.callees.values() ){
            dumpCallEdge("--"+prefix, ci);
        }
    }

    private static void dumpCallEdge( String prefix, CalleeProfilingInformation ci )
    {
        if ( ci.callee != null )
            dumpCall( prefix, ci.callee );
    }

    private void otherCallTop()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            otherCallL1A();
            otherCallL2A();
        }
    }

    private void otherCallL1A()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            otherCallL1B();
            otherCallL2B();
        }
    }

    private void otherCallL2A()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            otherCallL1B();
            otherCallL2B();
        }
    }

    private void otherCallL1B()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            StringBuffer sb = new StringBuffer(1);
            for (long l=0 ; l<workLoop; l++)
            {
                sb.append("test");
            }
        }
    }

    private void otherCallL2B()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            StringBuffer sb = new StringBuffer(1);
            for (long l=0 ; l<workLoop; l++)
            {
                sb.append("test");
            }
        }
    }

    private void privateMethodAutomatic()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            privateMethodManual();
            privateMethod2();
            privateMethod3();
        }
    }

    private void privateMethodManual()
    {
        MethodProfiling np = new MethodProfiling("The ProfileDemo", "privateMethodManual");
        try
        {
            for ( int i=0 ; i<100 ; ++i )
                publicRecursiveMethod(0);
        }
        finally
        {
            np.close();
        }

    }

    private void privateMethod2()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            StringBuffer sb = new StringBuffer(1);
            for (long l=0 ; l<1000; l++)
            {
                sb.append("test");
            }
        }

    }

    private void privateMethod3()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            StringBuffer sb = new StringBuffer(1);
            for (long l=0 ; l<1000; l++)
            {
                sb.append("test");
            }
        }

    }

    int rec = 0;

    public void publicRecursiveMethod(int level)
    {
        rec++;
        try ( MethodProfiling np = new MethodProfiling() )
        {
            if ( level < 100 )
            {
               publicRecursiveMethod( level +1);
            }
            else
                doSomeWork();
        }
    }

    public void doSomeWork()
    {
        try ( MethodProfiling np = new MethodProfiling() )
        {
            String s = "";
            for (long l=0 ; l<workLoop; l++)
            {
              s = s + "+";
            }
        }
    }
}
