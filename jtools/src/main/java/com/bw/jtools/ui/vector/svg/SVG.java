package com.bw.jtools.ui.vector.svg;

import com.bw.jtools.Log;
import com.bw.jtools.ui.vector.ShapeInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Font;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses and converts a SVG document into a liost of "ShapeInfo"-objects.</br>
 * Currently supported features:
 * <ul>
 * <li>g</li>
 * <li>path</li>
 * <li>rect</li>
 * <li>circle</li>
 * <li>ellipse</li>
 * <li>line</li>
 * <li>polyline</li>
 * <li>polygon</li>
 * <li>text (with tspan and testPath)</li>
 * <li>use</li>
 * <li>linearGradient</li>
 * <li>radialGradient</li>
 * </ul>
 */
public class SVG
{
	private final List<ShapeInfo> shapes_ = new ArrayList<>();
	private Map<String, Gradient> paintServer_ = new HashMap<>();
	private Map<String, Paint> paints_ = new HashMap<>();
	private Font defaultFont_ = Font.decode("Arial-PLAIN-12");
	private Document doc_;
	private final ElementCache elementCache_ = new ElementCache();

	public static final boolean addPathSegments_ = false;

	/**
	 * Parse a SVG document and creates shapes.
	 * After creation call {@link #getShapes()} to retrieve the resulting shapes.</br>
	 *
	 * @param xml The svg document.
	 */
	public SVG(final String xml)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			// Loading a DTD/Schema will slow down processing by several seconds (if schema is specified).
			// Suppress loading of references dtd/schema. This will also deactivate validation and
			// id processing (see scanForIDs call below).
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			DocumentBuilder db = dbf.newDocumentBuilder();

			doc_ = db.parse(new ByteArrayInputStream(xml.getBytes()));

			// Without automatic processing of "id" attributes will not be detected as key
			// and "getElementById" will not work. So we have to collect the Ids manually.
			elementCache_.scanForIds(doc_);

			NodeList patterns = doc_.getElementsByTagName("pattern");
			// @TODO

			parseChildren(shapes_, doc_.getElementsByTagName("svg")
									   .item(0));

		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			Log.error(e);
		}
	}

	/**
	 * Get a pre-defined gradient.
	 *
	 * @param id The Id of the definition.
	 * @return The gradient of null.
	 */
	public Gradient getPaintServer(String id)
	{
		Gradient g = paintServer_.get(id);
		if (g == null)
		{
			ElementWrapper w = elementCache_.getElementWrapperById(id);
			if (w != null)
			{
				if ("linearGradient".equals(w.getTagName()))
					g = parseLinearGradient(w);
				else if ("radialGradient".equals(w.getTagName()))
					g = parseRadialGradient(w);
			}
			if (g != null)
				paintServer_.put(g.id_, g);
		}
		return g;
	}

	/**
	 * Get a pre-defined paint.
	 *
	 * @param id The Id of the definition.
	 * @return The paint of null.
	 */
	public Paint getPaint(String id)
	{
		Paint pt = paints_.get(id);
		if (pt == null)
		{
			Gradient g = getPaintServer(id);
			if (g != null)
				pt = g.getPaint(this);
			if (pt == null)
				pt = java.awt.Color.BLACK;
			paints_.put(id, pt);
		}
		return pt;
	}

	/**
	 * Get all parsed shapes.
	 */
	public List<ShapeInfo> getShapes()
	{
		return Collections.unmodifiableList(shapes_);
	}


	public ElementCache getCache()
	{
		return elementCache_;
	}

	private RadialGradient parseRadialGradient(ElementWrapper w)
	{
		String id = w.id();
		if (id != null && !id.isEmpty())
		{
			RadialGradient rg = new RadialGradient(id);

			rg.cx = w.toDouble("cx");
			rg.cy = w.toDouble("cy");
			rg.r = w.toDouble("r");
			rg.fx = w.toDouble("fx");
			rg.fy = w.toDouble("fy");
			rg.fr = w.toDouble("fr");

			parseCommonGradient(rg, w);

			return rg;
		}
		else
			return null;
	}

	private LinearGradient parseLinearGradient(ElementWrapper w)
	{
		String id = w.id();
		if (id != null && !id.isEmpty())
		{
			LinearGradient lg = new LinearGradient(id);

			lg.x1 = w.toDouble("x1");
			lg.y1 = w.toDouble("y1");
			lg.x2 = w.toDouble("x2");
			lg.y2 = w.toDouble("y2");

			parseCommonGradient(lg, w);

			return lg;
		}
		else
			return null;
	}

	private void parseChildren(List<ShapeInfo> shapes, Node parent)
	{
		Node child = parent.getFirstChild();
		while (child != null)
		{
			while (child != null && child.getNodeType() != Node.ELEMENT_NODE)
				child = child.getNextSibling();

			if (child != null)
			{
				parse(shapes, elementCache_.getElementWrapper((Element) child));
				child = child.getNextSibling();
			}
		}
	}

	private void parse(List<ShapeInfo> shapes, ElementWrapper w)
	{
		final String e = w.getTagName();
		ElementWrapper.Type typ = w.getType();
		List<ShapeInfo> g = new ArrayList<>();

		if (typ == null)
			Log.warn("Unknown command " + e);
		else switch (typ)
		{
			case g:
			{
				parseChildren(g, w.getNode());
				addShapeContainer(w, g, shapes);
			}
			break;
			case path:
			{
				ShapeWrapper shape = w.getShape();
				if (shape == null)
					w.setShape(shape = new ShapeWrapper(new Path(w.attr("d", false)).getPath()));
				shapes.add(createShape(w, shape.getShape()));

				if (addPathSegments_)
				{
					Stroke s = new Stroke(new Color(this, "yellow", null),
							0.1d, null, null, null, null, null);

					shapes.add(new ShapeInfo(shape.getSegmentPath(), s.getStroke(), s.getColor(), 1, null, 1));
				}
			}
			break;
			case rect:
			{
				float x = (float) w.toPDouble("x");
				float y = (float) w.toPDouble("y");
				float width = (float) w.toPDouble("width");
				float height = (float) w.toPDouble("height");
				Double rx = w.toDouble("rx", false);
				Double ry = w.toDouble("ry", false);

				RectangularShape rec;

				if (rx != null || ry != null)
					rec = new RoundRectangle2D.Double(x, y, width, height, 2d * (rx == null ? ry : rx), 2d * (ry == null ? rx : ry));
				else
					rec = new Rectangle2D.Double(x, y, width, height);

				shapes.add(createShape(w, rec));
			}
			break;
			case ellipse:
			{
				float cx = (float) w.toPDouble("cx", false);
				float cy = (float) w.toPDouble("cy", false);
				float rx = (float) w.toPDouble("rx", false);
				float ry = (float) w.toPDouble("ry", false);

				Ellipse2D.Double ellipse = new Ellipse2D.Double(cx - rx, cy - ry, 2d * rx, 2d * ry);
				shapes.add(createShape(w, ellipse));
			}
			break;
			case text:
			{
				Text text = new Text(this, w, defaultFont_, g);
				addShapeContainer(w, g, shapes);
			}
			break;
			case use:
			{
				String href = w.href();
				if (ElementWrapper.isNotEmpty(href))
				{
					ElementWrapper refOrgW = elementCache_.getElementWrapperById(href);
					if (refOrgW != null)
					{
						double x = w.toPDouble("x");
						double y = w.toPDouble("y");
						ElementWrapper uw = refOrgW.createReferenceCopy(w);

						List<ShapeInfo> useShapes = new ArrayList<>();
						parse(useShapes, uw);
						AffineTransform aft = AffineTransform.getTranslateInstance(x, y);
						for (ShapeInfo sinfo : useShapes)
						{
							// Remind: Ids are duplicates!
							if (sinfo.aft_ == null)
								sinfo.aft_ = aft;
							else
								sinfo.aft_.preConcatenate(aft);
							shapes.add(sinfo);
						}
					}
				}
			}
			break;
			case circle:
			{
				double x1 = w.toPDouble("cx");
				double y1 = w.toPDouble("cy");
				double r = w.toPDouble("r");

				Ellipse2D.Double circle = new Ellipse2D.Double(x1 - r, y1 - r, 2 * r, 2 * r);
				shapes.add(createShape(w, circle));
			}
			break;
			case line:
			{
				double x1 = w.toPDouble("x1");
				double y1 = w.toPDouble("y1");
				double x2 = w.toPDouble("x2");
				double y2 = w.toPDouble("y2");

				Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
				shapes.add(createShape(w, line));
			}
			break;
			case polyline:
			{
				Polyline poly = new Polyline(w.attr("points"));
				shapes.add(createShape(w, poly.getPath()));
			}
			break;
			case polygon:
			{
				Polyline poly = new Polyline(w.attr("points"));
				shapes.add(createShape(w, poly.toPolygon()));
			}
			break;
			case defs:
			case linearGradient:
			case radialGradient:
				// Already processed. See C'tor.
				// Already processed. See C'tor.
				break;
		}
	}

	protected void addShapeContainer(ElementWrapper w, List<ShapeInfo> shapes, List<ShapeInfo> global)
	{
		String transform = w.attr("transform", false);
		if (ElementWrapper.isNotEmpty(transform))
		{
			AffineTransform t = new Transform(null, transform).getTransform();
			if (t != null)
				for (ShapeInfo s : shapes)
				{
					if (s.aft_ == null)
						s.aft_ = t;
					else
						s.aft_.preConcatenate(t);
				}
		}
		global.addAll(shapes);
		shapes.clear();
	}

	/**
	 * Helper to handle common presentation attributes and create a ShapeInfo-instance.
	 */
	protected ShapeInfo createShape(ElementWrapper w, Shape s)
	{
		Stroke stroke = stroke(w);
		Color fill = fill(w);

		float generalOpacity = w.opacity();

		ShapeInfo sinfo = new ShapeInfo(s,
				stroke.getColor() == null ? null : stroke.getStroke(),
				stroke.getColor(),
				generalOpacity * stroke.getOpacity(),
				fill.getColor(),
				generalOpacity * fill.getOpacity()
		);
		sinfo.id_ = w.id();
		transform(sinfo, w);
		return sinfo;
	}

	private void parseCommonGradient(Gradient g, ElementWrapper w)
	{
		// @TODO gradientUnits
		g.href_ = w.href();

		String spreadMethod = w.attr("spreadMethod");
		if (ElementWrapper.isNotEmpty(spreadMethod))
		{
			spreadMethod = spreadMethod.trim()
									   .toLowerCase();
			if ("reflect".equals(spreadMethod))
				g.cycleMethod_ = MultipleGradientPaint.CycleMethod.REFLECT;
			else if ("repeat".equals(spreadMethod))
				g.cycleMethod_ = MultipleGradientPaint.CycleMethod.REPEAT;
		}

		String gradientTransform = w.attr("gradientTransform", false);
		if (ElementWrapper.isNotEmpty(gradientTransform))
			g.aft_ = new Transform(null, gradientTransform).getTransform();

		NodeList stops = w.getNode()
						  .getElementsByTagName("stop");

		g.fractions_ = new float[stops.getLength()];
		float f;
		g.colors_ = new java.awt.Color[stops.getLength()];

		int sN = stops.getLength();
		for (int i = 0; i < sN; ++i)
		{
			Element stop = (Element) stops.item(i);
			ElementWrapper wrapper = new ElementWrapper(elementCache_, stop);

			String offset = wrapper.attr("offset");
			if (offset != null)
			{
				offset = offset.trim();
				if (offset.endsWith("%"))
					f = (float) ElementWrapper.convPDouble(offset.substring(0, offset.length() - 1)) / 100f;
				else
					f = (float) ElementWrapper.convPDouble(offset);
				if (f < 0)
					f = 0;
				else if (f > 1.0f)
					f = 1.0f;
			}
			else
				f = i > 0 ? g.fractions_[i - 1] : 0;

			g.fractions_[i] = f;

			Paint p = new Color(this, wrapper.attr("stop-color"),
					wrapper.toDouble("stop-opacity")).getColor();
			if (p instanceof java.awt.Color)
				g.colors_[i] = (java.awt.Color) p;
			else
				g.colors_[i] = java.awt.Color.WHITE;
		}
	}


	/**
	 * Handles "Transform" attribute.
	 */
	protected final void transform(ShapeInfo s, ElementWrapper w)
	{
		AffineTransform t = w.transform();
		if (t != null)
			s.aft_ = t;
	}

	protected Color fill(ElementWrapper w)
	{
		String color = w.attr("fill", true);
		return new Color(this, color == null ? "black" : color, w.toDouble("fill-opacity", true));
	}

	protected Stroke stroke(ElementWrapper w)
	{
		Stroke stroke = new Stroke(
				new Color(this, w.attr("stroke", true), w.toDouble("stroke-opacity", true)),
				w.toDouble("stroke-width", true),
				w.toFloatArray("stroke-dasharray", true),
				w.toDouble("stroke-dashoffset", true),
				LineCap.fromString(w.attr("stroke-linecap", true)),
				LineJoin.fromString(w.attr("stroke-linejoin", true)),
				w.toDouble("stroke-miterlimit", true)
		);
		return stroke;
	}

}
