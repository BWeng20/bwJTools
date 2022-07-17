/*
 * (c) copyright Bernd Wengenroth
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
package com.bw.jtools.io;

import com.bw.jtools.Log;
import com.bw.jtools.persistence.StorageBase;

/**
 * Executes a thread as host for some service.<br>
 * Common options for all services:
 * <table><caption></caption>
 * <tr><td><b>delay</b></td><td>delay in milliseconds between execution.</td></tr>
 * <tr><td><b>relativePriority</b></td><td>Priority offset for the thread</td></tr>
 * </table>
 */
public class ServiceRunner
{
	private boolean running_ = false;

	private int delay_ = 60000;
	private int relPriority_ = -1;

	private final Service service_;
	private final String serviceName_;
	private final Executer executer_;
	private Thread thread_;

	/**
	 * Interface for services.
	 */
	public static interface Service
	{
		/**
		 * Executes the service worker once.
		 *
		 * @return false if service shall terminate.
		 */
		public boolean work();

		/**
		 * Gets the name of the service.
		 *
		 * @return The name.
		 */
		public String getName();
	}

	/**
	 * Creates a new Service Runner with service runnable and name.
	 *
	 * @param service The service runnable to host.
	 */
	public ServiceRunner(Service service)
	{
		service_ = service;
		serviceName_ = service_.getName();
		executer_ = new Executer();
	}

	/**
	 * Get the executed service.
	 *
	 * @return The service.
	 */
	public Service getService()
	{
		return service_;
	}

	/**
	 * Executing runnable.
	 */
	private class Executer implements Runnable
	{

		public void run()
		{
			try
			{
				final Thread t = Thread.currentThread();
				int priority = t.getPriority() + relPriority_;
				t.setPriority(priority);
				Log.debug("Set Thread-Priority of " + serviceName_ + " Service to " + priority);
			}
			catch (Exception e)
			{
				Log.warn("Can't modify Thread-Priority of " + serviceName_ + " Service " + e.getMessage());
			}
			while (running_)
			{
				try
				{
					running_ = service_.work();
					if (running_)
					{
						try
						{
							Thread.sleep(delay_);
						}
						catch (InterruptedException e)
						{
						}
					}
				}
				catch (Exception e)
				{
					running_ = false;
					Log.error(serviceName_ + " Service stopped because of exception", e);
				}
			}
		}

	}

	/**
	 * Configures and starts if needed.
	 *
	 * @param config Configuration options. See service class comments.
	 */
	public synchronized void configureAndStart(StorageBase config)
	{
		configureAndStart(config.getInt("relativePriority", relPriority_), config.getInt("delay", delay_));
	}

	/**
	 * Configures and starts if needed.
	 *
	 * @param relativePriority Priority offset for the thread
	 * @param delay            delay in milliseconds between executions.
	 */
	public synchronized void configureAndStart(int relativePriority, int delay)
	{
		relPriority_ = relativePriority;
		delay_ = delay;

		if (!running_)
		{
			running_ = true;
			thread_ = new Thread(executer_);
			thread_.start();
		}
	}


	/**
	 * Stops the service.
	 */
	public synchronized void stop()
	{
		final Thread t = thread_;
		thread_ = null;
		running_ = false;
		if (t != null)
		{
			t.interrupt();
		}
	}

	/**
	 * Sets the delay between executions.
	 * Sets visa configuration option "delay".
	 *
	 * @param delay The delay in milliseconds
	 */
	public void setDelay(int delay)
	{
		delay_ = delay;
	}

}
