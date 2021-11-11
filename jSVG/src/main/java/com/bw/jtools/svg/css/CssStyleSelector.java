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


import com.bw.jtools.svg.ElementCache;
import com.bw.jtools.svg.ElementWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public final class CssStyleSelector
{
	public List<SelectorRule> rules_ = new ArrayList<>();

	private static class SelectorEntry
	{
		SelectorRule rule_;
		Selector selector_;

		public SelectorEntry(Selector s, SelectorRule rule)
		{
			selector_ = s;
			rule_ = rule;
		}
	}

	public void apply(Node root, ElementCache cache)
	{

		// As css is not used in svg very often, this is not implemented in an optimized way.

		for (SelectorRule rule : rules_)
		{
			for (Selector s : rule.selectors_)
			{
				apply(rule, s, root, cache, new Specificity());
			}
			//....
		}
	}

	protected void apply(SelectorRule rule, Selector s, Node n, ElementCache cache, Specificity spec)
	{
		if (s == null)
		{
			// No further condition.
			setStyles(rule, cache.getElementWrapper(n), spec);
		}
		else
		{
			Specificity specificity = new Specificity(spec);
			switch (s.type_)
			{
				case ID:
					ElementWrapper w = cache.getElementWrapperById(s.id_);
					if (w != null && isAnchestor(n, w.getNode()))
					{
						specificity.addIdMatch();
						apply(rule, s.combinate_, w.getNode(), cache, specificity);
					}
					break;
				case CLASS:
					specificity.addClassMatch();
					Stack<Node> unvisited = new Stack<>();
					unvisited.add(n);
					while (!unvisited.isEmpty())
					{
						n = unvisited.pop();
						w = cache.getElementWrapper(n);
						if (w != null && w.hasClass(s.id_))
							apply(rule, s.combinate_, w.getNode(), cache, specificity);
						n = n.getFirstChild();
						while (n != null)
						{
							unvisited.push(n);
							n = n.getNextSibling();
						}
					}
					break;
				case TAG:
					while (n != null && !(n instanceof Element || n instanceof Document))
					{
						if (n.getNextSibling() == null)
							n = n.getFirstChild();
						else
							n = n.getNextSibling();
					}
					if (n != null)
					{
						specificity.addTagMatch();
						NodeList nodes = (n instanceof Element) ? ((Element) n).getElementsByTagName(s.id_) : ((Document) n).getElementsByTagName(s.id_);
						for (int i = 0; i < nodes.getLength(); ++i)
						{
							Node node = nodes.item(i);
							apply(rule, s.combinate_, node, cache, specificity);
						}
					}
					break;
			}
		}
	}

	private boolean isAnchestor(Node anchestor, Node descendant)
	{
		while (descendant != null && !descendant.isSameNode(anchestor))
		{
			descendant = descendant.getParentNode();
		}
		return descendant != null;
	}

	protected void setStyles(SelectorRule rule, ElementWrapper w, Specificity specificity)
	{
		if (w != null)
		{
			Map<String, StyleValue> styles = w.getStyleAttributes();
			for (Map.Entry<String, String> entry : rule.styles_.entrySet())
			{
				final String style = entry.getKey();
				final String value = entry.getValue();
				StyleValue sv = styles.get(style);
				if (sv == null)
					styles.put(style, sv = new StyleValue(value, new Specificity(specificity)));
				else if (specificity.isMoreSpecificOrEqual(sv.specificity_))
				{
					sv.specificity_.setTo(specificity);
					sv.value_ = value;
				}
			}
		}
	}

}
