package com.bw.jtools.ui.vector.svg;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Toolkit;

public class ElementWrapper
{
	private static final Pattern styleSplitRegExp_ = Pattern.compile(";");

	private HashMap<String, String> attributes_;
	private final Element node_;

	private static final double pixelPerInch_;
	private static final double pixelPerPoint_;
	private static final double pixelPerCM_;
	private static final double pixelPerMM_;
	private static final double pixelPerPica_;

	// @TODO: font height
	private static final double pixelPerEM_ = 12;
	// @TODO: font x-height (height of small letters)
	private static final double pixelPerEX_ = 8;

	private static final Pattern unitRegExp_ = Pattern.compile("([\\d+-\\.e]+)(pt|px|em|%|in|cm|mm|ex|pc)", Pattern.CASE_INSENSITIVE);

	static
	{
		double ppi = 72;
		try
		{
			ppi = Toolkit.getDefaultToolkit().getScreenResolution();
		}
		catch (Exception ex)
		{}
		pixelPerInch_ = ppi;
		pixelPerPoint_ = ppi / 72d;
		pixelPerCM_ = 0.3937d * ppi;
		pixelPerMM_ = 0.03937d * ppi;
		pixelPerPica_ = ppi / 6d;
	}

	protected final static boolean isEmpty(String v)
	{
		return v == null || v.isEmpty();
	}

	protected final static boolean isNotEmpty(String v)
	{
		return v != null && !v.isEmpty();
	}

	protected final static Double convDouble(String val)
	{
		if (val != null)
			try
			{
				Matcher m = unitRegExp_.matcher(val);
				if ( m.matches() )
					return convUnit( Double.parseDouble(m.group(1)), Unit.valueFrom( m.group(2) ));
				else
					return Double.parseDouble(val);
			}
			catch (Exception e)
			{
			}
		return null;
	}

	protected final static Double convAngle(String val)
	{
		// Units like "deg" etc. are not supported but if we need to, do it here!
		Double d = convDouble(val);
		if ( d != null )
			d = Math.toRadians(d);
		return d;
	}

	protected enum Unit
	{
		pt, px, em, percent, in, cm, mm, ex,  pc;

		public static Unit valueFrom( String unit )
		{
			unit = unit.toLowerCase();
			if (unit.equals("pt")) return pt;
			if (unit.equals("px")) return px;
			if (unit.equals("em")) return em;
			if (unit.equals("%")) return percent;
			if (unit.equals("in")) return in;
			if (unit.equals("mm")) return mm;
			if (unit.equals("cm")) return cm;
			if (unit.equals("ex")) return ex;
			if (unit.equals("pc")) return pc;
			return null;
		}
	}

	protected final static Double convUnit(double val, Unit unit)
	{
		if ( unit != null )
		{
			switch (unit)
			{
				case pt:
					return val * pixelPerPoint_;
				case px:
					return val;
				case in:
					return val * pixelPerInch_;
				case cm:
					return val * pixelPerCM_;
				case mm:
					return val * pixelPerMM_;
				case pc:
					return val * pixelPerPica_;
				case em:
					return val * pixelPerEM_;
				case ex:
					return val * pixelPerEX_;
				case percent:
					// @TODO
			}
		}
		return val;
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
		return convDouble(attr(attributeName, false));
	}

	public Double toDouble(String attributeName, boolean inherited)
	{
		return convDouble(attr(attributeName, inherited));
	}

	public Double toAngle( String attributeName, boolean inherited)
	{
		return convAngle(attr(attributeName, inherited));

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

	public Element getNode()
	{
		return node_;
	}
}
