package com.bw.jtools.profiling.service;

import com.bw.jtools.profiling.ClassProfilingInformation;
import com.bw.jtools.profiling.callgraph.AbstractCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.JSONCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.Options;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Service to dump profiling information.
 */
public class DumpService {

    private static class Runner implements Runnable {
        
        public boolean      running = true;
        public long         delay   = 1000;
        public NumberFormat nf;

        
        public String filePattern;
        
        public int    nbFiles = 10;
        
        private int    fileIndex = 0;
        
        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            while ( running ) {
                try {
                    List<ClassProfilingInformation> classes = ClassProfilingInformation.getClassInformation();
                    if (!classes.isEmpty()) {
                        // Render top-level call graphs
                        JSONCallGraphRenderer renderer = new JSONCallGraphRenderer(nf, Options.ADD_CLASSNAMES, Options.HIGHLIGHT_CRITICAL, Options.ADD_MIN_MAX);
                        sb.setLength(0);
                        sb.append( renderer.render(AbstractCallGraphRenderer.filterTopLevelCalls(classes), ClassProfilingInformation.getProfilingStartTime(), Calendar.getInstance() ) );
                        sb.append("\n");
                        try {
                            ++fileIndex;
                            if ( nbFiles <= fileIndex) {
                                fileIndex = 1;
                            }
                            OutputStream os = new FileOutputStream( MessageFormat.format(filePattern, fileIndex ),false);
                            try {
                                os.write( sb.toString().getBytes() );
                            } finally {
                                os.close();
                            }
                        } catch (IOException e) {
                            System.err.println("Stopping Profiling Dump due to IO-Error: "+e.getMessage() );
                            running = false;
                            break;
                        }
                    }
                    Thread.sleep(delay);
                } catch ( InterruptedException ie )
                {
                }
                catch ( Exception e )
                {
                    System.err.println("Stopping Profiling Dump due unknown Error: "+e.getMessage() );
                    e.printStackTrace();
                    running = false;
                }
            }
        }
    }

    public static synchronized void start(String config) {
        
        // E.g. "2000:10:/logs/profiling{0}.json"
        String[] args = config.split("(?<!\\\\):");
        if ( args != null && args.length>2 ) {
         
            long delayDuration = Long.parseLong(args[0]);
            int nbFiles = Integer.parseInt(args[1]);
            String filePattern = args[2];
            
            if ( dumpRunner == null || !dumpRunner.running) {
                dumpRunner = new Runner();
                dumpRunner.delay = delayDuration;
                dumpRunner.filePattern = filePattern;
                dumpRunner.nbFiles = nbFiles;
                dumpRunner.nf = NumberFormat.getNumberInstance();
                dumpRunner.nf.setMaximumFractionDigits(5);
                dumpRunner.nf.setRoundingMode(RoundingMode.HALF_UP);
                dumpRunner.nf.setGroupingUsed(false);
    
                new Thread( dumpRunner ).start();
            } else {
                dumpRunner.delay = delayDuration;
                dumpRunner.filePattern = filePattern;
                dumpRunner.nbFiles = nbFiles;
            }
        }
    }

    public static synchronized void stop() {
        if ( dumpRunner != null ) {
            dumpRunner.running = false;
            dumpRunner = null;
        }
    }
    
    private static Runner dumpRunner;
    
    
    
    
}
