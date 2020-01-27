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
package com.bw.jtools.profiling.weaving;

import com.bw.jtools.Log;
import java.io.InputStreamReader;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

/**
 * Profiling by weaving instrumentation during run-time into the byte-code.<br/>
 * This class implements a "Java Agent".<br/>
 * <p>
 * <i>JavaAgents</i> is a JVM feature for attaching handlers to the classloaders
 * that can manipulate classes before they are loaded.<br/>
 * For details please check the documentation about package
 * <i>java.lang.instrument</i>.
 * </p><p>
 * The library '<i>Byte Buddy</i>' is used here to inject instrumentation code
 * that enable us to gather profiling information without any need to modify the
 * original sources. '<i>Byte Buddy</i>' is a very capable high-level API around
 * the byte-code manipulation library ASM.<br/>
 * </p>
 * <h2>Usage:</h2>
 * <p>
 * To select the classes and methods to profile you need to specify regular-expressions.<br/>
 * The matcher-expression can contain multiple sub-expressions, separated by blank or ';' characters.<br/>
 * </p>
 * <pre>
 *
 *     com\.myorg\..*:get.* com\.myorg\..*:print.* com\.otherlib\..*:set.*
 * </pre>
 * <p>
 * You can use system properties or a property-file to specify arguments.<br/>
 * </p>
 * <p><u>As system property:</u>
 * <pre>
 *
 *   java -classpath myClassPath "-D<b><i>profiling.weaver.regex=com\.myorg\..*:get.*;com\.myorg\..*:print.*;com\.otherlib\..*:set.*"</i></b>
 *        -D<b><i>profiling.weaver.verbose=true</i></b>
 *        -javaagent:path/bwJProfilingWeaverAgent-1.0.jar MyMainClass ...
 * </pre>
 * </p><p><u>As property-file:</u></p>
 * <p>This is useful if e.g. you command processor has issues with the regular expressions.<br/>
 * The path to the property-file needs to be expressed as URL and can be specified by the agent-argument or as system property:
 * </p>
 * <pre>
 *
 *   java -classpath myClassPath -javaagent:path/bwJProfilingWeaverAgent-1.0.jar=<b><i>file:/path/profilingWeaver.ini</i></b> MyMainClass ...
 *
 *   java -classpath myClassPath <b><i>-Dprofiling.weaver.ini=file:/path/profilingWeaver.ini</i></b>
 *        -javaagent:path/bwJProfilingWeaverAgent-1.0.jar MyMainClass ...
 * </pre>
 * The property-file has two settings, identical to the system-properties, but without prefix:
 * <pre>
 *
 *    # Profile all getter and print-methods:
 *    regexp = com\.myorg\..*:get.* com\.myorg\..*:print.*
 *    verbose= true
 * </pre>
 *
 * You can use any legal url that is supported by your JVM as path to the file.<br/>
 * E.g. if the file is located inside the META-INF-folder of a jar file you can try to use a JAR-url:
 * <pre>
 *
 *   java -classpath myClassPath -javaagent:path/bwJProfilingWeaverAgent-1.0.jar=<b><i>jar:file:/path/myJar.jar!/META-INF/profiling.ini</i></b>
 *        MyMainClass ...
 * </pre>
 * URLs always use absolute paths. As configuration via absolute paths is pain, you can use the key-word '$CODESOURCE' in you url.<br/>
 * It is replaced with the path to the folder of the codebase of the agent. From there you can use relative paths, e.g.
 * if you main-class is inside a jar that is stored in a parallel folder to the agent-jar:
 * <pre>
 *
 *   java -classpath myClassPath
 *        -javaagent:path/bwJProfilingWeaverAgent-1.0.jar=jar:file:<b><i>$CODESOURCE</i></b>../apps/myJar.jar!/META-INF/profiling.ini
 *        MyMainClass ...
 * </pre>
 *
 * <p>
 * If you are using a newer Java version with activated module-security, you may
 * need to 'open' packages to the weaver.<br/>
 * You can try to add such options to the java command-line, here for the system
 * package java.lang:<br/>
 * <pre>
 *   java --add-opens java.base/java.lang=ALL-UNNAMED --illegal-access=deny ...
 * </pre>
 But use this only as hint, as such issues may be related to specific configurations or JVM implementations.
 * </p>
 */
public class ProfilingWeaver
{
    /**
     * Argument for class and method matching.
     */
    public static final String ARG_REGEX = "regex";

    /**
     * Argument for verbosity.
     */
    public static final String ARG_VERBOSE = "verbose";

    /**
     * Symbol to replace in property-file URLs by code-source-location.
     */
    public static final String CODE_SOURCE = "$CODESOURCE";

    /**
     * System-Properties prefix.
     */
    public static final String PROP_PREFIX = "profiling.weaver.";

    /**
     * System-Properties for property file in case the agent-argument is not set.
     */
    public static final String PROP_PROPERTY_FILE = "profiling.weaver.ini";

    /**
     * Java-Agent entry point.
     * @param agentArgument   The agent-argument from command line.
     * @param instrumentation The provided instrumentation from JVM.
     */
    public static void premain(String agentArgument, Instrumentation instrumentation)
    {
        java.util.HashMap<String, String> args = new java.util.HashMap<>();

        args.put(ARG_VERBOSE, "false");

        /////////////////////////////////////////
        // Setting arguments from system properties

        String val = System.getProperty(PROP_PREFIX+ARG_REGEX);
        if (val != null)
        {
            args.put(ARG_REGEX, val);
        }

        String argPropertyFile = System.getProperty(PROP_PROPERTY_FILE);

        val = System.getProperty(PROP_PREFIX+ARG_VERBOSE);
        if (val != null)
        {
            args.put(ARG_VERBOSE, val);
        }

        /////////////////////////////////////////
        // Setting file from agent-argument

        if (agentArgument != null)
        {
            argPropertyFile = agentArgument;
        }

        /////////////////////////////////////////
        // Setting arguments from ini file

        if (argPropertyFile != null)
        {
            try
            {
                final int csIndex = argPropertyFile.indexOf(CODE_SOURCE);
                if (csIndex >= 0)
                {
                    URL codeURL;
                    try
                    {
                        codeURL = ProfilingWeaver.class.getProtectionDomain().getCodeSource().getLocation();
                        Path appPath = Paths.get(codeURL.toURI());
                        while (Files.isRegularFile(appPath))
                        {
                            appPath = appPath.getParent();
                        }
                        codeURL = appPath.toUri().toURL();

                    } catch (Exception se)
                    {
                        // Possibly no permissions...
                        Log.error("Failed to access code source location.", se);
                        codeURL = new URL(".");
                    }
                    argPropertyFile = argPropertyFile.substring(0, csIndex) + codeURL.toURI().getPath() + argPropertyFile.substring(csIndex + CODE_SOURCE.length());
                }

                URL uri = new URL(argPropertyFile);

                Properties o = new Properties();
                o.load(new InputStreamReader(uri.openStream()));

                for (Map.Entry<Object, Object> v : o.entrySet())
                {
                    args.put(((String) v.getKey()), ((String) v.getValue()));
                }

            } catch (Exception e)
            {
                Log.error("Failed to access property file '" + argPropertyFile + "'", e);
            }
        }

        final boolean verbose = Boolean.valueOf(args.get(ARG_VERBOSE));

        if ( verbose && !args.isEmpty())
        {
            Log.info("Arguments:");
            for( Map.Entry<String, String> arg : args.entrySet())
            {
                Log.info("\t"+arg.getKey()+ " = " + arg.getValue());
            }
        }

        final String regExp = args.get(ARG_REGEX);

        if (regExp != null && !regExp.isEmpty())
        {
            AgentBuilder agent = new AgentBuilder.Default(); // .with(AgentBuilder.TypeStrategy.Default.REBASE);
            if (verbose)
            {
                agent = agent.with(AgentBuilder.Listener.StreamWriting.toSystemOut());
            }

            final String matchRegExp[] = regExp.split("[\\s;]+");
            for ( String reg : matchRegExp)
            {
                if ( !reg.isEmpty() ) {

                    int colIndex = reg.indexOf(':');
                    if (colIndex<=0 || colIndex==(reg.length()-1))
                    {
                        Log.error( "Illegal matcher expression: "+reg);
                    }
                    else
                    {
                        final String classRegExp = reg.substring(0,colIndex);
                        final String methodRegExp= reg.substring(colIndex+1);

                        agent = agent.type(ElementMatchers.nameMatches(classRegExp))
                                .transform((builder, typeDescription, classLoader, module)
                                        ->
                                {
                                    if (verbose)
                                    {
                                        Log.info("Weaving methods of " + typeDescription.getName() + " matching " + methodRegExp);
                                    }
                                    return builder
                                            .method(ElementMatchers
                                                    .nameMatches(methodRegExp)
                                                    .and(isDeclaredBy(typeDescription))
                                                    .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                                            ).intercept(MethodDelegation.to(ByteBuddyProfilingInterceptor.class));
                                });
                    }
                }
            }
            agent.installOn(instrumentation);
        }
    }
}
