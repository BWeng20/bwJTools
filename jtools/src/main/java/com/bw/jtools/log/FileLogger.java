/*
 * (c) copyright 2020 Bernd Wengenroth
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileLogger extends Log.LoggerFacade
{
	Writer writer;

	public FileLogger( String file) {
		try {
			writer = new BufferedWriter( new FileWriter( file ), 2048 );
		} catch ( IOException e ) {
    		writer = null;
    		System.err.println("Error opening log '"+e.getMessage()+"'. Turning quite" );
		}
	}
	
	
    @Override
    public void error(CharSequence msg)
    {
    	write( Log.ERROR, msg);
    }

    @Override
    public void warn(CharSequence msg)
    {
    	write( Log.WARN, msg);
    }

    @Override
    public void info(CharSequence msg)
    {
    	write( Log.INFO, msg);
    }

    @Override
    public void debug(CharSequence msg)
    {
        write( Log.DEBUG ,msg);
    }
    
    private void write( int level, CharSequence msg) {
    	try {
    		if ( writer != null ) {
	    		writer.append( getLevelPrefix( level ) );
	    		writer.append( msg );
    		}    		
    	} catch ( Exception e ) {
    		System.err.println("Error writing log '"+e.getMessage()+"'. Turning quite" );
    		writer = null;
    	}
    }
}

