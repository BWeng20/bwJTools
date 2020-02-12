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
 * Implementation Log.LoggerFacade to propagate logs to multiple back-ends..
 */
public class MulticastLogger extends Log.LoggerFacade
{
    List<Log.LoggerFacade> logger_ = new ArrayList<>();

    @Override
    public synchronized void setLevel( int level )
    {
        for ( Log.LoggerFacade log : logger_ )
            log.setLevel(level);
        super.setLevel(level);
    }

    public static Log.LoggerFacade addLogger( Log.LoggerFacade loggerVariable, Log.LoggerFacade newLog )
    {
        if ( newLog == null ) return loggerVariable;

        MulticastLogger ml;
        if ( loggerVariable instanceof MulticastLogger )
        {
            ml = (MulticastLogger)loggerVariable;
        }
        else if ( newLog instanceof MulticastLogger )
        {
            ml = (MulticastLogger)newLog;
            newLog = loggerVariable;
        }
        else if (loggerVariable != null)
        {
            ml = new MulticastLogger();
            ml.logger_.add(loggerVariable);
        }
        else
        {
            return newLog;
        }
        synchronized ( ml )
        {
            ml.logger_.remove(newLog);
            ml.logger_.add(newLog);

            ml.calcLevel();
        }
        return ml;
    }

    public static Log.LoggerFacade removeLogger( Log.LoggerFacade loggerVariable, Log.LoggerFacade toRemove )
    {
        MulticastLogger ml;
        if ( loggerVariable instanceof MulticastLogger )
        {
            ml = (MulticastLogger)loggerVariable;
            synchronized ( ml )
            {
                ml.logger_.remove(toRemove);
                if ( ml.logger_.size() == 1)
                {
                    Log.LoggerFacade log = ml.logger_.get(0);
                    ml.logger_.clear();
                    return log;
                }
                else if ( ml.logger_.isEmpty())
                {
                    // Should not happen but possible by missuse.
                    return null;
                }
                else
                {
                    ml.calcLevel();
                }
            }
        }
        if ( loggerVariable == toRemove )
            return null;
        else
            return loggerVariable;
    }

    protected void calcLevel()
    {
        int maxLevel = -1;
        for ( Log.LoggerFacade log : logger_ )
            if ( maxLevel < log.getLevel() )
                maxLevel = log.getLevel();
        if ( maxLevel >=0 )
            setLevel(maxLevel);
    }


    @Override
    public synchronized void error(CharSequence msg)
    {
        for ( Log.LoggerFacade log : logger_ )
            log.error(msg);
    }

    @Override
    public synchronized void warn(CharSequence msg)
    {
        for ( Log.LoggerFacade log : logger_ )
            log.warn(msg);
    }

    @Override
    public synchronized void info(CharSequence msg)
    {
        for ( Log.LoggerFacade log : logger_ )
            log.info(msg);
    }

    @Override
    public synchronized void debug(CharSequence msg)
    {
        for ( Log.LoggerFacade log : logger_ )
            log.debug(msg);
    }
}
