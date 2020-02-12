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

/**
 * Implementation Log.LoggerFacade used as fallback in case no logger framework
 * is available.
 */
public class ConsoleLogger extends Log.LoggerFacade
{

    @Override
    public void error(CharSequence msg)
    {
        System.err.print(getLevelPrefix(Log.ERROR));
        System.err.println(msg);
    }

    @Override
    public void warn(CharSequence msg)
    {
        System.err.print(getLevelPrefix(Log.WARN));
        System.err.println(msg);
    }

    @Override
    public void info(CharSequence msg)
    {
        System.out.print(getLevelPrefix(Log.INFO));
        System.out.println(msg);
    }

    @Override
    public void debug(CharSequence msg)
    {
        System.out.print(getLevelPrefix(Log.DEBUG));
        System.out.println(msg);
    }
}
