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
package com.bw.jtools.persistence;

import com.bw.jtools.ui.I18N;

/**
 * Exception used in case some property is missing.
 */
public final class MissingPropertyException extends Exception
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -3988126060036005140L;

	protected String missingKey_;

	/**
	 * Creates a new Exception for the specified key.
	 *
	 * @param keyMissing The missing key.
	 */
	public MissingPropertyException(String keyMissing)
	{
		super(I18N.format("missingpropertyexception.message", keyMissing));
		this.missingKey_ = keyMissing;
	}

	/**
	 * Creates a new Exception for the specified key.
	 *
	 * @param keyMissing The missing key.
	 * @param cause      The cause in case it was some exception.
	 */
	public MissingPropertyException(String keyMissing, Throwable cause)
	{
		super(I18N.format("missingpropertyexception.message", keyMissing), cause);
		this.missingKey_ = keyMissing;
	}

	/**
	 * Gets the property key that was the reason for the exception.
	 *
	 * @return They name of the missing key.
	 */
	public String getKey()
	{
		return missingKey_;
	}


}
