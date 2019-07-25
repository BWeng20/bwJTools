/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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
package com.bw.jtools.log;

import com.bw.jtools.Log;
import java.util.ArrayList;
import java.util.List;

/**
* Logger back-end that collects messages in memory.<br>
* Purpose is to collect errors and warnings during some operation
* to enable the software to give hints about the reason of failure.
* E.g by providing an detailed error-report about the operation.<br>
* It is used by {@link Log#startCollectMessages(int, boolean) }.
*/
public class CollectorLogger extends Log.LoggerFacade
{
    Log.LoggerFacade logger;

    public List<String> messages = new ArrayList<>(1000);

    /**
     * Creates a new CollectorLogger.
     * @param logger The real logger back-end.
     */
    public CollectorLogger( Log.LoggerFacade logger )
    {
        this.logger = logger;
    }

    protected void addMessage(int level, CharSequence msg)
    {
        if ( this.level >= level )
        {
            messages.add( getLevelPrefix(level)+" "+msg );
        }
    }


    @Override
    public void error(CharSequence msg)
    {
        logger.error(msg);
        addMessage( Log.ERROR, msg );
    }

    @Override
    public void warn(CharSequence msg)
    {
        logger.warn(msg);
        addMessage( Log.WARN, msg );
    }

    @Override
    public void info(CharSequence msg)
    {
        logger.info(msg);
        addMessage( Log.INFO, msg );
    }

    @Override
    public void debug(CharSequence msg)
    {
        logger.debug(msg);
        addMessage( Log.DEBUG, msg );
    }

    @Override
    public void error(CharSequence msg, Throwable t)
    {
        logger.error(msg,t);
        if (level >= Log.ERROR )
        {
            addMessage( Log.ERROR, msg );
            if (maxStackTraceLines>0) addMessage( Log.ERROR, Log.getRestrictedStackTrace(t, " ", maxStackTraceLines));
        }
    }

    @Override
    public void warn(CharSequence msg, Throwable t)
    {
        logger.warn(msg,t);
        if (level >= Log.WARN )
        {
            addMessage( Log.WARN, msg );
            if (maxStackTraceLines>0) addMessage( Log.WARN, Log.getRestrictedStackTrace(t, " ", maxStackTraceLines));
        }
    }

    @Override
    public void debug(CharSequence msg, Throwable t)
    {
        logger.debug(msg,t);
        if ( level >= Log.DEBUG )
        {
            addMessage( Log.DEBUG, msg );
            if (maxStackTraceLines>0) addMessage( Log.DEBUG,Log.getRestrictedStackTrace(t, " ", maxStackTraceLines));
        }
    }

    @Override
    public void info(CharSequence msg, Throwable t)
    {
        logger.info(msg,t);
        if ( level >= Log.INFO )
        {
            addMessage( Log.INFO, msg );
            if (maxStackTraceLines>0) addMessage( Log.INFO, Log.getRestrictedStackTrace(t, " ", maxStackTraceLines));
        }
    }

    @Override
    public boolean isDebugEnabled(){ return super.isDebugEnabled() || logger.isDebugEnabled(); }
    @Override
    public boolean isInfoEnabled() { return super.isInfoEnabled()  || logger.isInfoEnabled();  }
    @Override
    public boolean isWarnEnabled() { return super.isWarnEnabled()  || logger.isWarnEnabled();  }
    @Override
    public boolean isErrorEnabled(){ return super.isErrorEnabled() || logger.isErrorEnabled(); }
}
