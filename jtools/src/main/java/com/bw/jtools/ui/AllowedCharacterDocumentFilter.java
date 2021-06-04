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
package com.bw.jtools.ui;

import javax.swing.text.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple document filter that filters allowed characters.<br>
 * Useful if a JFormattedTextField can't be used.
 */
public class AllowedCharacterDocumentFilter extends DocumentFilter
{
	private Set<Character> allowedChars_;
	private StringBuilder sb_ = new StringBuilder(10);
	private boolean inverse_;

	/**
	 * Creates a filter from a char array.
	 * @param allowedChars The allowed characters.
	 */
	public AllowedCharacterDocumentFilter(Set<Character> allowedChars)
	{
		this( allowedChars, false);
	}

	/**
	 * Creates a filter from a char array.<br>
	 * <i>Meaning of "inverse":</i>
	 * <ol>
	 * <li><b>true</b>: The given characters are NOT allowed. All other characters are allowed.</li>
	 * <li><b>false</b>: The given characters are allowed. All other characters are NOT allowed.</li>
	 * </ol>
	 * @param chars The characters.
	 * @param inverse If true, the characters are NOT allowed.
	 */
	public AllowedCharacterDocumentFilter(Set<Character> chars, boolean inverse)
	{
		allowedChars_ = chars;
		inverse_ = inverse;
	}

	/**
	 * Creates a filter by an char-array.
	 * @param allowedChars The allowed characters.
	 */
	public AllowedCharacterDocumentFilter(char allowedChars[])
	{
		allowedChars_ = new HashSet();
		for ( char c : allowedChars) allowedChars_.add(c);
		inverse_ = false;
	}


	/**
	 * Installs the filter on a text-component.
	 * @param tf The Text-Component to filter.
	 */
	public void install(JTextComponent tf )
	{
		((AbstractDocument)tf.getDocument()).setDocumentFilter( this );
	}

	/**
	 * Removes the filter from the text-component.
	 * @param tf The Text-Component to filter.
	 */
	public void uninstall(JTextComponent tf )
	{
		AbstractDocument doc = (AbstractDocument)tf.getDocument();
		if ( doc.getDocumentFilter() == this )
		{
			doc.setDocumentFilter(null);
		}
	}

	protected String filter(String input)
	{
		sb_.setLength(0);
		final int n = input.length();
		for (int i = 0; i < n; ++i)
		{
			final char c = input.charAt(i);
			if (allowedChars_.contains(c)  != inverse_)
				sb_.append(c);
		}
		return sb_.toString();
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs) throws BadLocationException
	{
		fb.insertString(offset, filter(text), attrs);
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
	{
		fb.replace(offset, length, filter(text), attrs);
	}
}
