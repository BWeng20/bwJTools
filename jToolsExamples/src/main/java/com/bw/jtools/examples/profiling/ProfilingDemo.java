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

import java.text.NumberFormat;
import com.bw.jtools.profiling.ClassProfilingInformation;
import com.bw.jtools.profiling.MethodProfiling;

/**
 * A test for profiling with different examples about manual profiling methods and
 * how to output the information.
 */
public class ProfilingDemo
{

    static int workLoop = 1000;

    /**
     * Main function.
     * @param args the list of command line arguments.
     */
    static public void main(  String args[] )
    {
        ProfilingDemoUtils.parseArguments(args);

        ProfilingDemo demo = new ProfilingDemo();

        int fractions = ProfilingDemoUtils.getArgument("fractionDigits", 5);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(fractions);
        ProfilingDemoUtils.setNumberFormat(nf);

        int repeat = ProfilingDemoUtils.getArgument("repeat", 5);
        if ( repeat<1 ) {
            repeat=1;
        }

        workLoop = ProfilingDemoUtils.getArgument("workLoop", workLoop);

        for ( int i=0 ; i<repeat ; ++i )
        {
            if ( repeat>1 ) {
                System.out.println("############# Run  "+i+"/"+repeat+" ############");
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



            ProfilingDemoUtils.dumpProfilingInformation();
            ProfilingDemoUtils.dumpAllCallGraphs();
        }

        ///////////////////////////////////////////////////
        // Generate files
        ProfilingDemoUtils.writeMindMap( ProfilingDemoUtils.getArgument("mmFile", null) );
        ProfilingDemoUtils.writeJSON( ProfilingDemoUtils.getArgument("jsonFile", null), false );
        ProfilingDemoUtils.writeJSON( ProfilingDemoUtils.getArgument("jsonPrettyFile", null), true );
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
