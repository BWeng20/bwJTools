/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bw.jtools.log;

import com.bw.jtools.Log;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Logger back-end for log4j2.
 */
public class Log4JLogger extends Log.LoggerFacade
{
	static public Logger log4j = org.apache.logging.log4j.LogManager.getLogger(Log.class);

	protected int getCurrentLog4JLevel()
	{
		final Level lv = log4j.getLevel();
		if (lv == Level.FATAL)
			return Log.ERROR;
		if (lv == Level.INFO)
			return Log.INFO;
		if (lv == Level.ALL || lv == Level.DEBUG || lv == Level.TRACE)
			return Log.DEBUG;
		if (lv == Level.WARN)
			return Log.WARN;
		return Log.NONE;
	}

	public Log4JLogger()
	{
		super.setLevel(getCurrentLog4JLevel());
	}

	@Override
	public void setLevel(int level)
	{
		super.setLevel(level);

		Level lv;
		switch (level)
		{
			case Log.NONE:
				lv = Level.OFF;
				break;
			case Log.ERROR:
				lv = Level.ERROR;
				break;
			case Log.WARN:
				lv = Level.WARN;
				break;
			case Log.INFO:
				lv = Level.INFO;
				break;
			case Log.DEBUG:
				lv = Level.DEBUG;
				break;
			default:
				lv = Level.WARN;
				break;
		}
		Configurator.setRootLevel(lv);
	}

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
