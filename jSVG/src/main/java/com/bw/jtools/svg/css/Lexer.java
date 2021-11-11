/*
 * (c) copyright 2021 Bernd Wengenroth
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

package com.bw.jtools.svg.css;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexer: reads characters and returns token,
 * The lexer doesn't know about datatypes like numbers. It simply tokenize the input-stream by stop-characters.
 * E.g. a floating-point-mumber "-123.3e22 will be split into a sequence of "-", "123", ".", "3", "e", "22".<br>
 * Identifiers bound by a " or ' quote are retuend as one token (also including stop characters and white-spaces).<br>
 * E.g. 'abc,123.2 ' will be returned as one token "abc,123.2 " (without the quotes).
 */
public class Lexer
{
	private Reader reader_;

	private static final int[] separators_;

	static
	{
		// +,- are no separators in css.
		separators_ = new int[]{'\t', ' ', ':', '\n', ';', '.', ',', '(', ')', '{', '}', '[', ']', '#', '*', '?', '=', '%', '@', '!', -1};
		Arrays.sort(separators_);
	}

	private LexerSymbol reused_;
	private int stringDelimiter = -100;
	private boolean number = false;
	private final static Pattern numberpattern = Pattern.compile("[+-]?\\d+(\\.\\d*)?([eE]?[+-]?\\d+)");

	public Lexer(Reader reader, boolean reUseSymbol)
	{
		reader_ = reader;
		if (reUseSymbol)
			reused_ = new LexerSymbol();
	}

	StringBuilder buffer = new StringBuilder();

	public LexerSymbol nextSymbol()
	{
		LexerSymbol result = reused_ == null ? new LexerSymbol() : reused_;
		number = false;

		// at start of new symbol, eat all spaces
		eatSpace();
		int c;
		while (true)
		{
			if (isStop(c = nextChar()))
			{
				if (c > 0)
				{
					// If not an EOF
					if (buffer.length() == 0)
						// if empty, return the current stop as symbol
						buffer.append((char) c);
					else if (!isStringDelimiter(c))
						// otherwise handle it next call
						pushBack(c);
				}
				break;
			}
			// append until stop is found.
			buffer.append((char) c);
		}

		// Return current buffer
		result.value_ = buffer.toString();
		buffer.setLength(0);

		if (result.value_.isEmpty())
			result.type_ = LexerSymbolType.EOF;
		else if (numberpattern.matcher(result.value_)
							  .matches())
			result.type_ = LexerSymbolType.NUMBER;
		else if (result.value_.length() > 1)
			result.type_ = LexerSymbolType.IDENTIFIER;
		else
			result.type_ = isStopChar(result.value_.charAt(0)) ? LexerSymbolType.SEPARATOR : LexerSymbolType.IDENTIFIER;
		return result;
	}

	/**
	 * Check if the character is a stop in current state.
	 */
	protected boolean isStop(int c)
	{
		if (c < 0)
			return true;
		else if (buffer.length() == 0)
		{
			buffer.append((char) c);
			number = checkForNumber();
			buffer.setLength(0);
			if (number)
				return false;
		}
		else if (number)
		{
			int oldLength = buffer.length();
			buffer.append((char) c);
			boolean numberStop = !checkForNumber();
			buffer.setLength(oldLength);
			return numberStop;
		}

		if (stringDelimiter > 0)
		{
			if (c == stringDelimiter)
			{
				stringDelimiter = -100;
				return true;
			}
			else
				// EOF is in any case a stop
				return c < 0;
		}
		else if (isStopChar(c))
		{
			if (isStringDelimiter(c))
				stringDelimiter = c;
			return true;
		}
		else
			return false;
	}

	/**
	 * Check if the character is in list of stop characters
	 */
	protected boolean isStopChar(int c)
	{
		// Binary search
		int startIndex = 0;
		int endIndex = separators_.length - 1;
		int midIndex;

		while (startIndex <= endIndex)
		{
			midIndex = (startIndex + endIndex) >>> 1;
			int midVal = separators_[midIndex];
			if (midVal > c)
				endIndex = midIndex - 1;
			else if (midVal < c)
				startIndex = midIndex + 1;
			else
				return true;
		}
		return false;
	}

	protected boolean isStringDelimiter(int c)
	{
		return (c == '\'' || c == '"');
	}

	protected void eatSpace()
	{
		if (stringDelimiter < 0)
		{
			int c;
			while (Character.isWhitespace(c = nextChar())) ;

			if (isStringDelimiter(c))
				stringDelimiter = c;
			else if (c > 0)
				pushBack(c);
		}
	}

	/**
	 * Check if the current buffer is possibly start of a number
	 */
	private boolean checkForNumber()
	{
		Matcher m = numberpattern.matcher(buffer);
		m.matches();
		return m.hitEnd();
	}

	/**
	 * Push back stack.
	 * Currently only 1 char is needed.
	 */
	private int[] pushback_ = new int[10];
	private int pushbackPos_ = -1;

	private void pushBack(int c)
	{
		pushback_[++pushbackPos_] = c;
	}

	private int nextChar()
	{
		if (pushbackPos_ < 0)
		{
			if (reader_ == null)
				return -1;
			try
			{
				return reader_.read();
			}
			catch (IOException e)
			{
				exception_ = e;
				reader_ = null;
				return -1;
			}
		}
		else
			return pushback_[pushbackPos_--];
	}

	private Exception exception_;

}
