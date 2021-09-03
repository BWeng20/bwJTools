package com.bw.jtools.ui.vector.svg;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ElementWrapper
{
	private static final Pattern styleSplitRegExp_ = Pattern.compile(";");

	private HashMap<String, String> attributes_;
	private HashMap<String, String> overrides_;
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

	private static final HashMap<String, Integer> fontWeights_ = new HashMap<>();

	static
	{
		double ppi = 72;
		try
		{
			ppi = Toolkit.getDefaultToolkit()
						 .getScreenResolution();
		}
		catch (Exception ex)
		{
		}
		pixelPerInch_ = ppi;
		pixelPerPoint_ = ppi / 72d;
		pixelPerCM_ = 0.3937d * ppi;
		pixelPerMM_ = 0.03937d * ppi;
		pixelPerPica_ = ppi / 6d;

		fontWeights_.put("normal", 400);
		fontWeights_.put("bold", 700);
		fontWeights_.put("lighter", 400);
		fontWeights_.put("bolder", 700);
	}

	protected static boolean isEmpty(String v)
	{
		return v == null || v.isEmpty();
	}

	protected static boolean isNotEmpty(String v)
	{
		return v != null && !v.isEmpty();
	}

	protected static Double convDouble(String val)
	{
		if (val != null)
			try
			{
				Matcher m = unitRegExp_.matcher(val);
				if (m.matches())
					return convUnitToPixel(Double.parseDouble(m.group(1)), Unit.valueFrom(m.group(2)));
				else
					return Double.parseDouble(val);
			}
			catch (Exception e)
			{
			}
		return null;
	}

	protected enum Unit
	{
		pt, px, em, percent, in, cm, mm, ex, pc;

		public static Unit valueFrom(String unit)
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

	public static double convUnitToPixel(double val, Unit unit)
	{
		if (unit != null)
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

	/**
	 * Gets the java font-weight-value from "font-weight"-attribute.
	 */
	public double fontWeight()
	{
		String w = attr("font-weight");
		if (isEmpty(w)) return 400; // Normal

		Integer pref = fontWeights_.get(w.trim()
										 .toLowerCase());
		if (pref != null) return pref / 400d;
		Double d = convDouble(w);
		return (d == null) ? 1d : d / 400d;
	}

	/**
	 * Get the double value of a none-inherited xml- or style-attribute as Double.
	 *
	 * @return The double or null if the attribute doesn't exists.
	 */
	public Double toDouble(String attributeName)
	{
		return convDouble(attr(attributeName, false));
	}

	/**
	 * Get the primitive double value of a none-inherited xml- or style-attribute.
	 *
	 * @return The double or 0 if the attribute doesn't exists.
	 */
	public double toPDouble(String attributeName)
	{
		return toPDouble(attributeName, false);
	}

	/**
	 * Get the double value of a xml- or style-attribute.
	 *
	 * @param inherited If true and the attribute doesn't exists also the parent nodes are scanned.
	 * @return The double or null if the attribute doesn't exists.
	 */
	public Double toDouble(String attributeName, boolean inherited)
	{
		return convDouble(attr(attributeName, inherited));
	}

	/**
	 * Get the primitive double value of a xml- or style-attribute.
	 *
	 * @param inherited If true and the attribute doesn't exists also the parent nodes are scanned.
	 * @return The double or 0 if the attribute doesn't exists.
	 */
	public double toPDouble(String attributeName, boolean inherited)
	{
		Double d = convDouble(attr(attributeName, inherited));
		return d == null ? 0d : d;
	}

	public float[] toFloatArray(String attributeName)
	{
		return convFloatArray(attr(attributeName));
	}

	public static double convPDouble(String value)
	{
		Double d = convDouble(value);
		return d == null ? 0d : d;
	}

	public static float[] convFloatArray(String val)
	{
		if (val != null)
			try
			{
				val = val.replace(',', ' ');
				String values[] = val.split("[ ,]+");
				float farr[] = new float[values.length];
				for (int i = 0; i < values.length; ++i)
					farr[i] = (float) convPDouble(values[i]);
			}
			catch (Exception e)
			{
			}
		return null;
	}

	/**
	 * Get the text content - to be used for text-elements.
	 */
	public String text(String attibuteName)
	{
		String text = node_.getTextContent();
		// @TODO: Support all modes of "white-space" correctly.
		if (text != null && !preserveSpace())
			text = text.trim();
		return text;
	}

	/**
	 * Checks if the node has white-space-preservation on.
	 */
	public boolean preserveSpace()
	{
		final String ws = attr("white-space");
		return "preserve".equals(attr("xml:space")) || (ws != null && ws.startsWith("pre"));
	}

	public String attr(String attributeName)
	{
		return attr(attributeName, true);
	}

	public String attr(String attributeName, boolean inherited)
	{
		if (overrides_ != null)
		{
			String ov = overrides_.get(attributeName);
			if (isNotEmpty(ov))
				return ov;
		}
		String v = node_.getAttribute(attributeName);
		if (isEmpty(v))
		{
			v = getStyleAttributes().get(attributeName);
		}
		if (isEmpty(v) && inherited)
		{
			v = inherited(node_, attributeName);
			if (isNotEmpty(v))
				attributes_.put(attributeName, v);
		}
		return v;
	}

	public HashMap<String, String> getStyleAttributes()
	{
		if (attributes_ == null)
		{
			attributes_ = new HashMap<>();

			String v = node_.getAttribute("style");
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
		return attributes_;
	}

	public void override(String attributeName, String value)
	{
		if (value != null && isEmpty(attr(attributeName)))
		{
			if (overrides_ == null)
				overrides_ = new HashMap<>();
			overrides_.put(attributeName, value);
		}
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
