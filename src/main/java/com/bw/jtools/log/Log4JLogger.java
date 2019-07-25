/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bw.jtools.log;

import com.bw.jtools.Log;
import org.apache.logging.log4j.Logger;

/**
 * Logger back-end for log4j2.
 */
public class Log4JLogger extends Log.LoggerFacade
{

    static public Logger log4j = org.apache.logging.log4j.LogManager.getLogger(Log.class);

    @Override
    public void error(CharSequence msg)
    {
        log4j.error(msg);
    }

    @Override
    public void warn(CharSequence msg)
    {
        log4j.warn(msg);
    }

    @Override
    public void info(CharSequence msg)
    {
        log4j.info(msg);
    }

    @Override
    public void debug(CharSequence msg)
    {
        log4j.debug(msg);
    }

    @Override
    public void error(CharSequence msg, Throwable t)
    {
        log4j.error(msg, t);
    }

    @Override
    public void warn(CharSequence msg, Throwable t)
    {
        log4j.warn(msg, t);
    }

    @Override
    public void info(CharSequence msg, Throwable t)
    {
        log4j.info(msg, t);
    }

    @Override
    public boolean isDebugEnabled()
    {
        return log4j.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled()
    {
        return log4j.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled()
    {
        return log4j.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled()
    {
        return log4j.isErrorEnabled();
    }
}
