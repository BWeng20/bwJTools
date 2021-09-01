package com.bw.jtools.ui.vector.svg;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ElementWrapper
{
	private static final Pattern styleSplitRegExp_ = Pattern.compile(";");

	private HashMap<String, String> attributes_;
	private final Element node_;

	protected final static boolean isEmpty(String v)
	{
		return v == null || v.isEmpty();
	}

	protected final static boolean isNotEmpty(String v)
	{
		return v != null && !v.isEmpty();
	}

	protected final static Float convFloat(String val)
	{
		if (val != null)
			try
			{
				return Float.parseFloat(val);
			}
			catch (Exception e)
			{
			}
		return null;
	}

	protected final static Double convDouble(String val)
	{
		if (val != null)
			try
			{
				return Double.parseDouble(val);
			}
			catch (Exception e)
			{
			}
		return null;
	}

	public ElementWrapper(Element node)
	{
		node_ = node;
	}

	public String getTagName()
	{
		return node_.getTagName();
	}

	public String id()
	{
		return node_.getAttribute("id");
	}

	public String href()
	{
		String href = node_.getAttribute("href")
						   .trim();
		if (isEmpty(href))
			href = node_.getAttribute("xlink:href")
						.trim();
		if (isNotEmpty(href))
		{
			if (href.startsWith("#"))
				href = href.substring(1);
			return href;
		}
		return null;
	}

	public Double toDouble(String attributeName)
	{
		return convDouble(attr(attributeName));
	}

	public Float toFloat(String attributeName)
	{
		return convFloat(attr(attributeName));
	}

	public String attr(String attributeName)
	{
		return attr(attributeName, true);
	}

	public String attr(String attributeName, boolean inherited)
	{
		String v = node_.getAttribute(attributeName);
		if (isEmpty(v))
		{
			if (attributes_ == null)
			{
				attributes_ = new HashMap<>();

				v = node_.getAttribute("style");
				if (isNotEmpty(v))
				{
					String[] stylesAr = styleSplitRegExp_.split(v);
					for (String s : stylesAr)
					{
						final int i = s.indexOf(':');
						if (i > 0)
						{
							attributes_.put(s.substring(0, i)
											 .trim(), s.substring(i + 1));
						}
					}
				}
			}
			v = attributes_.get(attributeName);
		}
		if (isEmpty(v) && inherited)
		{
			v = inherited(node_, attributeName);
			if (isNotEmpty(v))
				attributes_.put(attributeName, v);
		}
		return v;
	}

	protected String inherited(Element node, String attributeName)
	{
		String v = null;
		while (v == null && node != null)
		{
			Node p = node.getParentNode();
			while (p != null && !"g".equals(p.getNodeName()))
				p = p.getParentNode();
			node = (Element) p;
			if (node != null)
				v = new ElementWrapper(node).attr(attributeName);
		}
		return v;
	}
}
