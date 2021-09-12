package com.bw.jtools.ui.vector.svg;

import com.bw.jtools.Log;
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
					Log.warn("SVG: Duplicate id " + id);
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

	protected ElementWrapper getElementWrapper(Element node)
	{
		String id = node.getAttribute("id");
		if (ElementWrapper.isNotEmpty(id))
			return wrapperById_.get(id);
		else
		{
			node.setAttribute("id", id = generateId());
			ElementWrapper ew = new ElementWrapper(this, node);
			wrapperById_.put(id, ew);
			return ew;
		}
	}

	protected ElementWrapper getElementWrapperById(String id)
	{
		if (ElementWrapper.isNotEmpty(id))
			return wrapperById_.get(id);
		else
			return null;
	}

}
