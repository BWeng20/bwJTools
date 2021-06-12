/*
 * Copyright Bernd Wengenroth.
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

import java.text.NumberFormat;

import com.bw.jtools.profiling.ClassProfilingInformation;
import java.beans.BeanProperty;

/**
 * <h2>A test target for profiling via Weaving.</h2>
 * The profiles methods are configured via the ini file below "resources/com/bw/jtools/examples/profiling/Weaver.properties".<br>
 * Check Gradle tasks "runProfilingWeaverDemo" and "runProfilingWeaverDemoVerbose" how ProfileWeaver is used..

 * @see com.bw.jtools.profiling.weaving.ProfilingWeaver
 */
public class ProfilingWeaverDemo
{

    static int workLoop = 10000;

    /**
     * Main function.
     * @param args the list of command line arguments.
     */
    static public void main(  String args[] )
    {
        ProfilingDemoUtils.parseArguments(args);

        ProfilingWeaverDemo demo = new ProfilingWeaverDemo();

        int fractions = ProfilingDemoUtils.getArgument("fractionDigits", 5);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(fractions);
        ProfilingDemoUtils.setNumberFormat(nf);

        int repeat = ProfilingDemoUtils.getArgument("repeat", 1);
        if ( repeat<1 ) {
            repeat=1;
        }

        workLoop = ProfilingDemoUtils.getArgument("workLoop", workLoop);

        System.out.println("fractionDigits= "+fractions);
        System.out.println("repeat        = "+repeat);
        System.out.println("workLoop      = "+workLoop);

        for ( int i=0 ; i<repeat ; ++i )
        {
            if ( repeat>1 ) {
                System.out.println("############# Run  "+i+"/"+repeat+" ############");
            }

            ClassProfilingInformation.clearProfilingInformation();

            demo.rec = 0;

            ///////////////////////////////////////////////////
            // Call the top-level methods to trigger profiling.
            // The run can be executed in a loop to show performance of
            // profiling. Beside "doSomeWork" all net times are
            // mainly profiling overhead.
            // This overhead will be reduced a bit after system runs hot (~40 runs).
            demo.privateTopMethod();
            demo.otherCallTop();

            ProfilingDemoUtils.dumpProfilingInformation();
            ProfilingDemoUtils.dumpAllCallGraphs();
        }

        ///////////////////////////////////////////////////
        // Generate output files
        System.out.println("== Generating Files :");
        ProfilingDemoUtils.writeMindMap( ProfilingDemoUtils.getArgument("mmFile", null) );
        ProfilingDemoUtils.writeJSON( ProfilingDemoUtils.getArgument("jsonFile", null), false );
        ProfilingDemoUtils.parseJSON( ProfilingDemoUtils.getArgument("jsonFile", null) );
        ProfilingDemoUtils.writeJSON( ProfilingDemoUtils.getArgument("jsonPrettyFile", null), true );
        ProfilingDemoUtils.parseJSON( ProfilingDemoUtils.getArgument("jsonPrettyFile", null) );

        ProfilingDemoUtils.dumpClass( ProfilingWeaverDemo.class );

    }


    public void otherCallTop()
    {
        otherCallL1A();
        otherCallL2A();
    }

    public void otherCallL1A()
    {
        otherCallL1B();
        otherCallL2B();
    }

    @BeanProperty
    public void otherCallL2A()
    {
        otherCallL1B();
        otherCallL2B();
    }

    private void otherCallL1B()
    {
        StringBuffer sb = new StringBuffer(1);
        for (long l=0 ; l<workLoop; l++)
        {
            sb.append("test");
        }
    }

    private void otherCallL2B()
    {
        StringBuffer sb = new StringBuffer(1);
        for (long l=0 ; l<workLoop; l++)
        {
            sb.append("test");
        }
    }

    private void privateTopMethod()
    {
        privateMethodManual();
        privateMethod2();
        privateMethod3();
    }

    private void privateMethodManual()
    {
        for ( int i=0 ; i<100 ; ++i )
            publicRecursiveMethod(0);

    }

    private void privateMethod2()
    {
        StringBuffer sb = new StringBuffer(1);
        for (long l=0 ; l<1000; l++)
        {
            sb.append("test");
        }
    }

    private void privateMethod3()
    {
        StringBuffer sb = new StringBuffer(1);
        for (long l=0 ; l<1000; l++)
        {
            sb.append("test");
        }

    }

    int rec = 0;

    public void publicRecursiveMethod(int level)
    {
        rec++;
        if ( level < 25 )
        {
           publicRecursiveMethod( level +1);
        }
        else
            doSomeWork();
    }

    public void doSomeWork()
    {
        String s = "";
        for (long l=0 ; l<workLoop; l++)
        {
          s = s + "+";
        }
    }
}
