package com.bw.jtools.svg;

import com.bw.jtools.shape.ShapeWithStyle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Font;
import java.awt.MultipleGradientPaint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses and converts a SVG document into Java2D-shapes plus bacis style-information (see {@link ShapeWithStyle}).</br>
 * </br>
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
 * <li>linearGradient (without stop-opacity)</li>
 * <li>radialGradient (without stop-opacity)</li>
 * <li>clipPath (only direct use by attribute clip-path, no inheritance)</li>
 * </ul>
 * As the svg elements are simply converted to Shapes, complex stuff that needs offline-rendering (like blur) can't work.
 * Also a lot of complex use-case will not work as specified. </br>
 * The SVG specification contains a lot of such case with a large amounts of hints how agents should render it correctly.
 * But most svg graphics doesn't use such stuff, so the conversion to Java2D shapes is the most efficient way to draw
 * simple scalable graphics (see {@link com.bw.jtools.shape.ShapePainter} and {@link com.bw.jtools.shape.ShapeIcon}).</br>
 * My basic need was to draw icons that will look good also on high-res-screens.</br>
 * If you need a more feature-complete renderer, use batik or (not to forget) SVG Salamander.
 */
public class SVGConverter
{
	public static void warn(String s)
	{
		System.out.println("SVG Warning: " + s);
	}

	public static void error(String s, Throwable t)
	{
		System.err.println("SVG Error: " + (s == null ? "" : s));
		if (t != null)
			t.printStackTrace(System.err);
	}

	private final List<ShapeInfo> shapes_ = new ArrayList<>();
	private final List<ShapeWithStyle> finalShapes_ = new ArrayList<>();
	private Map<String, Gradient> paintServer_ = new HashMap<>();
	private Map<String, PaintWrapper> paints_ = new HashMap<>();
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
	public SVGConverter(final String xml)
	{
		this(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
	}

	public SVGConverter(final InputStream in)
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

			doc_ = db.parse(in);

			// Without automatic processing of "id" attributes will not be detected as key
			// and "getElementById" will not work. So we have to collect the Ids manually.
			elementCache_.scanForIds(doc_);

			// @TODO patterns
			// NodeList patterns = doc_.getElementsByTagName("pattern");

			parseChildren(shapes_, doc_.getElementsByTagName("svg")
									   .item(0));

			for (ShapeInfo s : shapes_)
			{
				finalShapes_.add(finish(s));
			}

		}
		catch (Exception e)
		{
			error("Failed to parse SVG", e);
		}
	}

	/**
	 * Finally create shapes from the elements.
	 */
	private ShapeWithStyle finish(ShapeInfo s)
	{
		ElementWrapper w = elementCache_.getElementWrapperById(s.id_);

		ShapeWithStyle sws = new ShapeWithStyle(
				s.id_,
				s.shape_,
				s.stroke_ == null ? null : s.stroke_.createStroke(w),
				s.paintWrapper_ == null ? null : s.paintWrapper_.createPaint(w),
				s.fillWrapper_ == null ? null : s.fillWrapper_.createPaint(w),
				s.clipping_,
				s.aft_);

		return sws;
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
	public PaintWrapper getPaint(String id)
	{
		PaintWrapper pt = paints_.get(id);
		if (pt == null)
		{
			Gradient g = getPaintServer(id);
			if (g != null)
				pt = g.getPaintWrapper(this);
			if (pt == null)
				pt = new PaintWrapper(java.awt.Color.BLACK);
			paints_.put(id, pt);
		}
		return pt;
	}

	public Shape getClipPath(String id)
	{
		// @TODO: use clip-rule for clipPath elements.

		ElementWrapper w = elementCache_.getElementWrapperById(id);
		if (w != null)
		{
			if (w.getType() != Type.clipPath)
			{
				warn(w.id() + " is not a clipPath");
			}
			else
			{
				ShapeHelper shape = w.getShape();
				if (shape == null)
				{
					List<ShapeInfo> g = new ArrayList<>();
					parseChildren(g, w.getNode());

					Path2D.Double clipPath = new Path2D.Double();
					for (ShapeInfo s : g)
					{
						if (s.clipping_ == null)
						{
							clipPath.append(s.shape_, false);
						}
						else
						{
							// Intersect according to spec.
							Area area = new Area(s.shape_);
							area.intersect(new Area(s.clipping_));
							clipPath.append(area, false);
						}
					}
					g.clear();
					w.setShape(clipPath);
					shape = w.getShape();
				}
				return shape.getShape();
			}
		}
		return null;
	}

	/**
	 * Get all parsed shapes.
	 */
	public List<ShapeWithStyle> getShapes()
	{
		return Collections.unmodifiableList(finalShapes_);
	}

	/**
	 * Gets the internal element cache.
	 *
	 * @return Never null.
	 */
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

			rg.cx = w.toLength("cx");
			rg.cy = w.toLength("cy");
			rg.r = w.toLength("r");

			rg.fx = w.toLength("fx");
			rg.fy = w.toLength("fy");
			rg.fr = w.toLength("fr");

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

			lg.x1 = w.toLength("x1");
			lg.y1 = w.toLength("y1");
			lg.x2 = w.toLength("x2");
			lg.y2 = w.toLength("y2");

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
		Type typ = w.getType();
		List<ShapeInfo> g = new ArrayList<>();

		if (typ == null)
			warn("Unknown command " + e);
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
				if (w.getShape() == null)
					w.setShape(new Path(w.attr("d", false)).getPath());
				shapes.add(createShapeInfo(w));

				// Debugging feature
				if (addPathSegments_)
				{
					Stroke s = new Stroke(new Color(this, "yellow", 1d),
							null, null, null, null, null, null);

					shapes.add(new ShapeInfo(w.getShape()
											  .getSegmentPath(), s, s.getPaintWrapper(),
							null, null));
				}
			}
			break;
			case rect:
			{
				if (w.getShape() == null)
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

					w.setShape(rec);
				}
				shapes.add(createShapeInfo(w));
			}
			break;
			case ellipse:
			{
				float cx = (float) w.toPDouble("cx", false);
				float cy = (float) w.toPDouble("cy", false);
				float rx = (float) w.toPDouble("rx", false);
				float ry = (float) w.toPDouble("ry", false);

				w.setShape(new Ellipse2D.Double(cx - rx, cy - ry, 2d * rx, 2d * ry));
				shapes.add(createShapeInfo(w));
			}
			break;
			case text:
			{
				new Text(this, w, defaultFont_, g);
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

				w.setShape(new Ellipse2D.Double(x1 - r, y1 - r, 2 * r, 2 * r));
				shapes.add(createShapeInfo(w));
			}
			break;
			case line:
			{
				double x1 = w.toPDouble("x1");
				double y1 = w.toPDouble("y1");
				double x2 = w.toPDouble("x2");
				double y2 = w.toPDouble("y2");

				w.setShape(new Line2D.Double(x1, y1, x2, y2));
				shapes.add(createShapeInfo(w));
			}
			break;
			case polyline:
			{
				w.setShape(new Polyline(w.attr("points")).getPath());
				shapes.add(createShapeInfo(w));
			}
			break;
			case polygon:
			{
				w.setShape(new Polyline(w.attr("points")).getPath());
				shapes.add(createShapeInfo(w));
			}
			break;
			case defs:
			case linearGradient:
			case radialGradient:
			case clipPath:
				// Parsed on demand
				break;
		}
	}

	protected void addShapeContainer(ElementWrapper w, List<ShapeInfo> shapes, List<ShapeInfo> global)
	{
		AffineTransform t = w.transform();
		if (t != null)
			for (ShapeInfo s : shapes)
			{
				if (!s.id_.equals(w.id()))
				{
					if (s.aft_ == null)
						s.aft_ = new AffineTransform(t);
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
	protected ShapeInfo createShapeInfo(ElementWrapper w)
	{
		Stroke stroke = stroke(w);
		Color fill = fill(w);
		Shape clipPath = clipPath(w);

		ShapeInfo sinfo = new ShapeInfo(w.getShape()
										 .getShape(),
				stroke.getPaintWrapper() == null ? null : stroke,
				stroke.getPaintWrapper(),
				fill.getPaintWrapper(),
				clipPath
		);
		sinfo.id_ = w.id();
		transform(sinfo, w);
		return sinfo;
	}

	private void parseCommonGradient(Gradient g, ElementWrapper w)
	{
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

		g.gradientUnit_ = GradientUnit.fromString(w.attr("gradientUnits"));

		String gradientTransform = w.attr("gradientTransform", false);
		if (ElementWrapper.isNotEmpty(gradientTransform))
			g.aft_ = new Transform(null, gradientTransform).getTransform();

		NodeList stops = w.getNode()
						  .getElementsByTagName("stop");

		float f;
		int sN = stops.getLength();
		if (sN > 0)
		{
			g.fractions_ = new float[sN];
			g.colors_ = new java.awt.Color[sN];
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

				final Color cp = new Color(this, wrapper.attr("stop-color"),
						wrapper.toPDouble("stop-opacity", 1d, false));
				PaintWrapper pw = cp.getPaintWrapper();
				g.colors_[i] = (pw != null && pw.getColor() != null) ? pw.getColor() : java.awt.Color.WHITE;
			}
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
		return new Color(this, color == null ? "black" : color, w.effectiveOpacity() * w.toPDouble("fill-opacity", 1.0d, true));
	}

	protected Stroke stroke(ElementWrapper w)
	{
		Stroke stroke = new Stroke(
				new Color(this, w.attr("stroke", true),
						w.effectiveOpacity() * w.toPDouble("stroke-opacity", 1.0d, true)),
				w.toLength("stroke-width", true),
				w.toLengthList("stroke-dasharray", true),
				w.toDouble("stroke-dashoffset", true),
				LineCap.fromString(w.attr("stroke-linecap", true)),
				LineJoin.fromString(w.attr("stroke-linejoin", true)),
				w.toDouble("stroke-miterlimit", true)
		);
		return stroke;
	}

	protected Shape clipPath(ElementWrapper w)
	{
		// @TODO intersect inherited clip-paths.
		return getClipPath(w.clipPath());
	}

}
