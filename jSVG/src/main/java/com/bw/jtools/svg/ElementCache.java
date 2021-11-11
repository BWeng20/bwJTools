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

package com.bw.jtools.svg;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ElementCache
{
	private final HashMap<String, ElementWrapper> wrapperById_ = new HashMap<>();
	private static AtomicLong idGenerator = new AtomicLong(9999);


	private String generateId()
	{
		return "_#" + idGenerator.incrementAndGet() + "Generated__";

	}

	public void scanForIds(Node node)
	{
		if (node.getNodeType() == Node.ELEMENT_NODE)
		{
			String id = ((Element) node).getAttribute("id");
			if (ElementWrapper.isNotEmpty(id))
			{
				if (wrapperById_.containsKey(id))
				{
					// Duplicate ids are no hard error, as svg seems to allow it.
					// As we handle the element-wrapper via id, we need to remove the id.
					SVGConverter.warn("SVG: Duplicate %s", id);
					((Element) node).removeAttribute("id");
				}
				else
					wrapperById_.put(id, new ElementWrapper(this, (Element) node));
			}
		}
		Node next = node.getNextSibling();
		if (next != null) scanForIds(next);
		next = node.getFirstChild();
		if (next != null) scanForIds(next);
	}

	public ElementWrapper getElementWrapper(Node node)
	{
		if (node instanceof Element)
		{
			Element element = (Element) node;
			String id = element.getAttribute("id");
			if (ElementWrapper.isNotEmpty(id))
				return wrapperById_.get(id);
			else
			{
				element.setAttribute("id", id = generateId());
				ElementWrapper ew = new ElementWrapper(this, element);
				wrapperById_.put(id, ew);
				return ew;
			}
		}
		else
			return null;
	}

	public ElementWrapper getElementWrapperById(String id)
	{
		if (ElementWrapper.isNotEmpty(id))
			return wrapperById_.get(id);
		else
			return null;
	}

}
