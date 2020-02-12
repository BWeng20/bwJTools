/*
 * (c) copyright 2015-2019 Bernd Wengenroth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools;

import com.bw.jtools.log.CollectorLogger;
import com.bw.jtools.log.CollectorThreadLogger;
import com.bw.jtools.log.ConsoleLogger;
import com.bw.jtools.log.MulticastLogger;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Simple log handling.<br>
 * In most projects at some early point you have to decide which logging framework should be used.<br>
 * Often this decision is forced because of libraries you need and that already use some logging framework.<br>
 * I personally don't like to bind my source sp deeply to such stuff.<br>
 * The used logging framework is mostly not determined by functional requirements,
 * so why bind a huge part of the sources to it?<br>
 * <br>
 * This class is a lightweight layer to keep the impact low.<br>
 * <b>All methods are static!</b><br>
 * <b>Pro:</b> There is no need to store a "logger"-Instance in your class or to call singleton-getter each time.<br>
 * <b>Contra:</b> No way to specify different settings for different classes by configuration.<br>
 * I personally never needed to configure the logger differently for different parts of my software,
 * so I can live with this restriction. And noise from other libraries can turned off by the used
 * framework.<br>
 * If you use the other parts of this library that use this log, you can simply delegate the output to
 * what-ever you like or turn it off.<br>
 * <br>
 * Current implementation tries to initialize Log4J2 as back-end. If Log4J2 is not
 * found in class-path, a simple console-logger is used.
 */
public final class Log
{
    /**
     * Get a stack-trace with restricted length.
     * @param t The Throwable for the stack-trace.
     * @param prefix Prefix for each lines, e.g. to add some indentation. Can be null.
     * @param max_lines Maximum number of lines to print.
     * @return The stack-trace.
     */
    public static String getRestrictedStackTrace(Throwable t, String prefix, int max_lines)
    {
        StringWriter sw = new StringWriter(1000);
        t.printStackTrace(new PrintWriter(sw));

        String lines[] = sw.toString().split("[\\r\\n]+");


        StringBuilder sb = new StringBuilder(2048);
        int l=0;
        while ( l<max_lines && l<lines.length)
        {
            if ( prefix != null ) sb.append(prefix);
            sb.append(lines[l++]).append("\n");
        }
        if ( l<lines.length )
        {
            if ( prefix != null ) sb.append(prefix);
            sb.append("...\n");
        }
        return sb.toString();
    }

    /** This logging level turns all output off. */
    public final static int NONE  = 0;
    /** This logging level enables only errors. */
    public final static int ERROR = 1;
    /** This logging level enables errors and warnings. */
    public final static int WARN  = 2;
    /** This logging level enables errors,warnings and information. */
    public final static int INFO  = 3;
    /** This logging level enables all output. */
    public final static int DEBUG = 4;

    /**
     * Adapter for logging back-ends.
     */
    public static abstract class LoggerFacade
    {
        protected int maxStackTraceLines = 10;
        protected int level = ERROR;

        protected final static String DEBUG_PREFIX = "[DBG]";
        protected final static String INFO_PREFIX  = "[INF]";
        protected final static String WARN_PREFIX  = "[WRN]";
        protected final static String ERROR_PREFIX = "[ERR]";
        protected final static String UNKNW_PREFIX = "";

        public static String getLevelPrefix( int level )
        {
            switch ( level )
            {
                case ERROR: return ERROR_PREFIX;
                case WARN : return WARN_PREFIX;
                case INFO : return INFO_PREFIX;
                case DEBUG: return DEBUG_PREFIX;
                default:    return UNKNW_PREFIX;
            }
        }

        /**
         * Sets the logging level.
         * @param level The level.
         */
        public void setLevel(int level )
        {
            this.level = level;
        }

        /**
         * gets the logging level.
         * @return The level.
         */
        public int getLevel()
        {
            return this.level;
        }

        /**
         * Sets the maximum length of stack-trace.<br>
         * 0 deactivate any stack-traces.
         * @param maxLines The maximum number of lines.
         */
        public void setMaximumLinesOfStackTrace(int maxLines )
        {
            this.maxStackTraceLines = maxLines;
        }

        /**
         * Logs an error message.
         * @param msg The text to log.
         */
        public abstract void error(CharSequence msg);

        /**
         * Logs a warning message.
         * @param msg The text to log.
         */
        public abstract void warn(CharSequence msg);

        /**
         * Logs an informational message.
         * @param msg The text to log.
         */
        public abstract void info(CharSequence msg);

        /**
         * Logs a debugging message.
         * @param msg The text to log.
         */
        public abstract void debug(CharSequence msg);

        /**
         * Logs an error message plus message and stack-trace of the Throwable.
         * @param msg The text to log.
         * @param t The Throwable to trace.
         */
        public void error(CharSequence msg, Throwable t)
        {
            if ( level >= ERROR )
            {
                error( msg );
                if (t != null && maxStackTraceLines>0)
                    error( getRestrictedStackTrace(t," ", maxStackTraceLines) );
            }
        }

        /**
         * Logs a warning message error plus message and stack-trace of the Throwable.
         * @param msg The text to log.
         * @param t The Throwable to trace.
         */
        public void warn(CharSequence msg, Throwable t)
        {
            if ( level >= WARN )
            {
                warn( msg );
                if (maxStackTraceLines>0) warn( getRestrictedStackTrace(t," ", maxStackTraceLines) );
            }
        }

        /**
         * Logs an informational message error plus message and stack-trace of the Throwable.
         * @param msg The text to log.
         * @param t The Throwable to trace.
         */
        public void info(CharSequence msg, Throwable t)
        {
            if ( level >= INFO )
            {
                info( msg );
                if (maxStackTraceLines>0) info( getRestrictedStackTrace(t," ", maxStackTraceLines) );
            }
        }

        /**
         * Logs a warning message error plus message and stack-trace of the Throwable.
         * @param msg The text to log.
         * @param t The Throwable to trace.
         */
        public void debug(CharSequence msg, Throwable t)
        {
            if ( level >= DEBUG )
            {
                debug( msg );
                if (maxStackTraceLines>0) debug( getRestrictedStackTrace(t," ", maxStackTraceLines) );
            }
        }

        /**
         * Checks if debug log-output is enabled.
         * @return true if current log-level is equal to or higher than DEBUG.
         */
        public boolean isDebugEnabled(){ return level >= DEBUG; }

        /**
         * Checks if informational log-output is enabled.
         * @return true if current log-level is equal to or higher than INFO.
         */
        public boolean isInfoEnabled() { return level >= INFO; }

        /**
         * Checks if warning log-output is enabled.
         * @return true if current log-level is equal to or higher than WARN.
         */
        public boolean isWarnEnabled() { return level >= WARN; }

        /**
         * Checks if error log-output is enabled.
         * @return true if current log-level is equal to or higher than WARN.
         */
        public boolean isErrorEnabled(){ return level >= ERROR; }
    }

    public static LoggerFacade log;

    protected static final CollectorLogger collectorLog;

    static
    {
        try{
            log = MulticastLogger.addLogger( null, new com.bw.jtools.log.Log4JLogger() );
        }
        catch ( NoClassDefFoundError ignored )
        {
            log = MulticastLogger.addLogger( null, new ConsoleLogger() );
            log.debug("Log4J2 is not available. Logging to console.", ignored );
        }
        collectorLog = new CollectorLogger();
    }

    /**
     * Starts collecting messages for the current thread.<br>
     * From this point until {@link #stopCollectMessages(java.util.List) stopCollectMessages}
     * is called, all output in this thread that match the log.level is collected.<br>
     * This function was designed to provide "internal detail" data for some short-term
     * operation.<br>
     * @param level The level to collect for.
     * @param includeStackTraces If Stack-Traces should be included.
     */
    static public void startCollectMessages(int level, boolean includeStackTraces)
    {
        collectorLog.setThreadLog( new CollectorThreadLogger(level, includeStackTraces?10:0 ) );
        log = MulticastLogger.addLogger(log, collectorLog );
    }

    /**
     * Stops collecting messages.<br>
     * All messages that were collected from the last call to {@link #startCollectMessages(int, boolean) startCollectMessages}
     * are returned.
     * @param messages List to add the messages to.
     */
    static public void stopCollectMessages(List<String> messages)
    {
        CollectorThreadLogger tlog = collectorLog.getThreadLog();
        collectorLog.setThreadLog( null );
        if (!collectorLog.hasThreadLog())
        {
            log = MulticastLogger.removeLogger(log, collectorLog );
        }

        if (messages != null && tlog != null)
        {
            messages.addAll(tlog.messages);
            tlog.messages.clear();
        }
    }

    /**
     * Adds a application specific logger.
     * @param logger The logger to add.
     */
    static public void addLogger(LoggerFacade logger)
    {
        log = MulticastLogger.addLogger(log, logger );
    }

    /**
     * Removes a application specific logger.
     * @param logger The logger to remove.
     */
    static public void removeLogger(LoggerFacade logger)
    {
        log = MulticastLogger.removeLogger(log, logger);
    }

    /**
     * Sets system log level.<br>
     * The value can be one of the constants
     * <ul>
     * <li>NONE
     * <li>ERROR
     * <li>WARN
     * <li>INFO
     * <li>DEBUG
     * </ul>
     * @param level The new system log level.
     */
    static public void setLevel(int level)
    {
        log.setLevel(level);
    }


    /**
     * Logs an error.
     * @param msg  The message to log.
     */
    static public void error(CharSequence msg)
    {
        log.error(msg);
    }

    /**
     * Logs an error plus stack-trace.
     * @param msg  The message to log.
     * @param t    The Throwable to trace.
     */
    static public void error(CharSequence msg, Throwable t)
    {
        log.error(msg, t);
    }

    /**
     * Logs a warning.
     * @param msg  The message to log.
     */
    static public void warn(CharSequence msg)
    {
        log.warn(msg);
    }

    /**
     * Logs an warning plus stack-trace.
     * @param msg  The message to log.
     * @param t    The Throwable to trace.
     */
    static public void warn(CharSequence msg, Throwable t)
    {
        log.warn(msg, t);
    }

    /**
     * Logs an informational message.
     * @param msg  The message to log.
     */
    static public void info(CharSequence msg)
    {
        log.info(msg);
    }

    /**
     * Logs a debug message.
     * @param msg  The message to log.
     */
    static public void debug(CharSequence msg)
    {
        log.debug(msg);
    }

    /**
     * Logs a debug message plus stack-trace.
     * @param msg  The message to log.
     * @param t    The Throwable to trace.
     */
    static public void debug(CharSequence msg, Throwable t)
    {
        log.debug(msg, t);
    }

    /**
     * Checks if debug log-output is enabled.
     * @return true if current log-level is equal to or higher than DEBUG.
     */
    static public boolean isDebugEnabled(){ return log.isDebugEnabled(); }

    /**
     * Checks if informational log-output is enabled.
     * @return true if current log-level is equal to or higher than INFO.
     */
    static public boolean isInfoEnabled() { return log.isInfoEnabled(); }

    /**
     * Checks if warning log-output is enabled.
     * @return true if current log-level is equal to or higher than WARN.
     */
    static public boolean isWarnEnabled() { return log.isWarnEnabled(); }

    /**
     * Checks if error log-output is enabled.
     * @return true if current log-level is equal to or higher than WARN.
     */
    static public boolean isErrorEnabled(){ return log.isErrorEnabled(); }


}
