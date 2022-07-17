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
package com.bw.jtools.log;

import com.bw.jtools.Log;
import com.sun.management.UnixOperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

/**
 * Utility class to gather statistics about the running application.<br>
 * Initially this class was created to collect information about a long-running
 * server application that should be reported via a status-service. <br>
 * Most features are only supported by unix operating systems. But for some
 * values a fallback can be used, e.g. we can use the system-time instead of real
 * thread-time.<br>
 * Needs to be extends for a more general use.
 */
public final class OS
{
	/**
	 * Utility class to collect all values at once.<br>
	 * A bit too specific for general usage, but good for demonstration.
	 */
	public static class Info
	{
		public final long open_handles;
		public final long max_handles;
		public final double average_load;

		/**
		 * Create an info instance with the current statistics.
		 */
		public Info()
		{
			final OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
			if (os instanceof UnixOperatingSystemMXBean)
			{
				final UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean) os;
				open_handles = unix.getOpenFileDescriptorCount();
				max_handles = unix.getMaxFileDescriptorCount();
			}
			else
			{
				open_handles = -1;
				max_handles = -1;
			}
			average_load = os.getSystemLoadAverage();
		}
	}

	/**
	 * Gets the number of open files of this process - if possible.<br>
	 * Possible means running inside some "unix".
	 *
	 * @return the amount of open file handles or -1 if value is not available.
	 */
	static public long getOpenFileHandles()
	{
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		if (os instanceof UnixOperatingSystemMXBean)
		{
			return ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount();
		}
		return -1;
	}

	/**
	 * Gets the maximal number of open files of this process - if possible.
	 * <br>
	 * Possible means running, that we are running inside some "unix".
	 *
	 * @return The maximal amount of open file handles or -1 if value is not available.
	 */
	static public long getMaxOpenFileHandles()
	{
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		if (os instanceof UnixOperatingSystemMXBean)
		{
			return ((UnixOperatingSystemMXBean) os).getMaxFileDescriptorCount();
		}
		return -1;
	}

	/**
	 * Gets the current CPU Load average - if possible.<br>
	 * Is this is not possible, -1 is returned.<br>
	 * The value depends on implementation in the OS.
	 *
	 * @return The average system load.
	 */
	static public double getSystemLoadAverage()
	{
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		return os.getSystemLoadAverage();
	}

	static public String getOSName()
	{
		return System.getProperty("os.name")
				+ " v" + System.getProperty("os.version") + "/"
				+ System.getProperty("os.arch");
	}


	/**
	 * Thread time measurement
	 */

	private static ThreadMXBean threadMXBean = null;

	/**
	 * Get execution time of current thread, if possible.<br>
	 * If this is not possible or enabled, system-wide time will be used.
	 *
	 * @return the elapsed time in nanoseconds.
	 */
	public static long getThreadExecutionTimeNS()
	{
		if (threadMXBean != null)
			return threadMXBean.getCurrentThreadCpuTime();
		else
			return System.nanoTime();
	}

	/**
	 * Returns true if getThreadExecutionTimeNS is based on real thread-time.
	 *
	 * @return True if thread-time-measurement is used.
	 */
	public static boolean isThreadTimeMeasurementUsed()
	{
		return threadMXBean != null;
	}


	/**
	 * Initialize this tool class.
	 * Should be called once before usage, because otherwise it will not work ;)
	 */
	public static void init()
	{
		ThreadMXBean mb = ManagementFactory.getThreadMXBean();
		try
		{
			if (mb.isCurrentThreadCpuTimeSupported())
			{
				if (!mb.isThreadCpuTimeEnabled())
				{
					mb = null;
					Log.warn("JVM's Thread-Cpu-Time-Measurement is disabled.");
				}
				else
				{
					Log.info("JVM's Thread-Cpu-Time-Measurement is enabled.");
				}
			}
			else
			{
				Log.warn("JVM does't supports Thread-Cpu-Time-Measurement.");
				mb = null;
			}
		}
		catch (UnsupportedOperationException ignored)
		{
			Log.warn("JVM does't supports Thread-Cpu-Time-Measurement.", ignored);
			mb = null;
		}
		threadMXBean = mb;
	}

}
