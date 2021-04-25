/*
 * (c) copyright Bernd Wengenroth
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
public class CollectorThreadLogger
{
	public List<String> messages = new ArrayList<>(1000);

	public final int level;
	public final int maxTraceLines;

	/**
	 * Creates a new CollectorLogger.
	 *
	 * @param level         Level to restrict the collected messages to.
	 * @param maxTraceLines Maximum number of lines in stack-traces.
	 */
	public CollectorThreadLogger(int level, int maxTraceLines)
	{
		this.level = level;
		this.maxTraceLines = maxTraceLines;
	}

}
