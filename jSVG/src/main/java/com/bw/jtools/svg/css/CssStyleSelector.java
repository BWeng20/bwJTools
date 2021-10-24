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


import com.bw.jtools.svg.ElementWrapper;

import java.util.ArrayList;
import java.util.List;

public final class CssStyleSelector
{
	public List<SelectorRule> rules_ = new ArrayList<>();

	public String getStyle(ElementWrapper e, String attributeName)
	{
		int maxPrecedence = -1;
		String bestValue = null;
		for (SelectorRule rule : rules_)
		{
			int precedence = matchPrecedence(rule, e);
			if (precedence > 0)
			{
				String value = rule.styles_.get(attributeName);
				if (value != null && precedence > maxPrecedence)
				{
					bestValue = value;
					maxPrecedence = precedence;
				}
			}
		}
		return bestValue;
	}

	protected int matchPrecedence(SelectorRule rule, ElementWrapper e)
	{
		for (Selector s : rule.selectors_)
		{
			int precedence = 1;
			while (s != null)
			{
				if (!match(s, e))
					break;
				s = s.and_;
				// @TODO: make this real! CHeck precedence by type and position
				precedence++;
			}
		}
		return -1;
	}

	protected boolean match(Selector s, ElementWrapper e)
	{
		switch (s.type_)
		{
			case CLASS:
				return e.hasClass(s.id_);
			case TAG:
				return s.id_.equals(e.getTagName());
			case ID:
				return s.id_.equals(e.id());
		}
		return false;
	}

}
