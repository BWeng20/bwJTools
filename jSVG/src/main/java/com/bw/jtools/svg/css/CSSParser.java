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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Simple CSS Parser.<br>
 */
public class CSSParser
{
	public static CssStyleSelector parse(Reader reader, String type) throws IOException
	{
		// if ( type == null ) type = "text/css";
		//@TODO: any other then "text/css"?

		CssStyleSelector styleSelector = new CssStyleSelector();
		new CSSParser().parse(new Lexer(reader, true), styleSelector);
		return styleSelector;
	}

	private StringBuilder declaration;
	private StringBuilder attribute;
	private static final Pattern styleSplitRegExp_ = Pattern.compile(";");

	private static class RuleStub
	{
		Selector selector;
		boolean chain;
	}

	private Stack<RuleStub> ruleStack = new Stack<>();

	private Selector selector_;


	/**
	 * Parse a style sheet and put all style in the selector.<br>
	 * Method is not thread-safe, use separate instances on each thread!
	 */
	public void parse(Lexer lexer, CssStyleSelector styleSelector)
	{
		LexerSymbol symbol;
		LexerSymbolType lastLexType = LexerSymbolType.EOF;
		boolean chain = true;
		loop:
		while (true)
		{
			symbol = lexer.nextSymbol();
			switch (symbol.type_)
			{
				case SEPARATOR:
					char c = symbol.value_.charAt(0);
					if (attribute != null)
					{
						if (c == ']')
							//@TODO: How to handle attributes in svg? Ignored for now.
							attribute = null;
						else
							attribute.append(c);
					}
					else if (declaration != null)
					{
						if (c == '}')
							styleSelector.rules_.add(generateRule());
						else
							declaration.append(c);
					}
					else
					{
						switch (c)
						{
							case '[':
								attribute = new StringBuilder();
								break;
							case '{':
								declaration = new StringBuilder();
								break;
							case ',':
								chain = false;
								break;
							case '#':
								startRule(SelectorType.ID, chain);
								chain = true;
								break;
							case '.':
								startRule(SelectorType.CLASS, chain);
								chain = true;
								break;
						}
					}
					break;
				case IDENTIFIER:
					if (attribute != null)
					{
						if (lastLexType != LexerSymbolType.SEPARATOR)
							attribute.append(' ');
						attribute.append(symbol.value_);

					}
					else if (declaration != null)
					{
						if (lastLexType != LexerSymbolType.SEPARATOR)
							declaration.append(' ');
						declaration.append(symbol.value_);
					}
					else
					{
						Selector s = getCurrentSelector();
						if (s.id_ != null)
							startRule(SelectorType.TAG, chain);
						getCurrentSelector().id_ = symbol.value_;
					}
					chain = true;
					break;
				case EOF:
					break loop;
			}
			lastLexType = symbol.type_;
		}

	}

	/**
	 * Finalize a rule with current definition.
	 */
	private SelectorRule generateRule()
	{
		SelectorRule rule = new SelectorRule();

		rule.styles_ = parseStyle(declaration.toString());
		declaration = null;

		Selector selectorToChain = null;
		while (!ruleStack.isEmpty())
		{
			RuleStub stub = ruleStack.pop();
			if (selectorToChain != null)
				stub.selector.and_ = selectorToChain;

			if (stub.chain)
			{
				selectorToChain = stub.selector;
			}
			else
			{
				selectorToChain = null;
				rule.selectors_.add(stub.selector);
			}
		}
		if (selectorToChain != null)
			rule.selectors_.add(selectorToChain);
		return rule;
	}

	private void startRule(SelectorType type, boolean chain)
	{
		RuleStub stub = new RuleStub();
		stub.selector = selector_ = new Selector();
		;
		selector_.type_ = type;
		stub.chain = chain;
		ruleStack.push(stub);
	}

	private Selector getCurrentSelector()
	{
		if (selector_ == null)
			startRule(SelectorType.TAG, false);
		return selector_;
	}

	/**
	 * Parses a style definition, e.g. "width:10px;height30px" and returns all entries as map.
	 */
	public static Map<String, String> parseStyle(String style)
	{
		Map<String, String> attrs = new HashMap<>();
		if (style != null && !style.isEmpty())
		{
			String[] stylesAr = styleSplitRegExp_.split(style);
			for (String s : stylesAr)
			{
				final int i = s.indexOf(':');
				if (i > 0)
					attrs.put(s.substring(0, i)
							   .trim(), s.substring(i + 1));
			}
		}
		return attrs;
	}


}
