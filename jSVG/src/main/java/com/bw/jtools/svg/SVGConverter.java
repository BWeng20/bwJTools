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

import com.bw.jtools.shape.AbstractShape;
import com.bw.jtools.shape.ShapeGroup;
import com.bw.jtools.shape.StyledShape;
import com.bw.jtools.shape.filter.Composite;
import com.bw.jtools.shape.filter.CompositeOperator;
import com.bw.jtools.shape.filter.FilterBase;
import com.bw.jtools.shape.filter.FilterChain;
import com.bw.jtools.shape.filter.GaussianBlur;
import com.bw.jtools.shape.filter.LightSource;
import com.bw.jtools.shape.filter.LightSourceType;
import com.bw.jtools.shape.filter.Offset;
import com.bw.jtools.shape.filter.SpecularLighting;
import com.bw.jtools.svg.css.CSSParser;
import com.bw.jtools.svg.css.CssStyleSelector;
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
import java.awt.geom.Point2D;
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
import java.util.stream.Collectors;

import static com.bw.jtools.svg.ElementWrapper.isNotEmpty;

/**
 * Parses and converts a SVG document into Java2D-shapes plus bacis style-information (see {@link StyledShape}).<br>
 * <br>
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
 * <li>text (with tspan and textPath, only single x,y,dx,dy length values, no textLength, no lengthAdjust, no rotate, no content area)</li>
 * <li>use</li>
 * <li>linearGradient (without stop-opacity)</li>
 * <li>radialGradient (without stop-opacity)</li>
 * <li>clipPath (only direct use by attribute clip-path, no inheritance)</li>
 * <li>filter (partial and only simple scenarios)</li>
 * </ul>
 * As the svg elements are simply converted to Shapes, complex stuff that needs offline-rendering (like blur) can't work.
 * Some complex use-case will not work as specified. <br>
 * The SVG specification contains a lot of such case with some amounts of hints how agents should render it correctly.<br>
 * Most svg graphics doesn't use such stuff, so the conversion to Java2D shapes is the most efficient way to draw
 * simple scalable graphics (see {@link com.bw.jtools.shape.ShapePainter} and {@link com.bw.jtools.shape.ShapeIcon}).<br>
 * The basic need for thos lib was to draw icons that will look good also on high-res-screens.<br>
 * If you need a feature-complete renderer, use batik or (also not complete) SVG Salamander.
 */
public class SVGConverter
{

	/**
	 * Helper method to dump SVG warnings that may need user-attention.<br>
	 * Can be used
	 */
	public static void warn(String s, Object... params)
	{
		System.out.print("SVG Warning: ");
		System.out.printf(s, params);
		System.out.println();
	}

	/**
	 * Helper method to dump SVG errors that may need user-attention.<br>
	 * Can be used
	 */
	public static void error(Throwable t, String s, Object... params)
	{
		error(s, params);
		if (t != null)
		{
			if (detailedErrorInformation_)
				t.printStackTrace(System.err);
			else
				System.err.println(t.getClass()
									.getSimpleName() + ": " + t.getMessage());
		}
	}

	public static void error(String s, Object... params)
	{
		System.err.print("SVG Error: ");
		System.err.printf(s, params);
		System.err.println();
	}

	private final List<ElementInfo> shapes_ = new ArrayList<>();
	private final List<AbstractShape> finalShapes_ = new ArrayList<>();
	private Map<String, Gradient> paintServer_ = new HashMap<>();
	private Map<String, PaintWrapper> paints_ = new HashMap<>();
	private Font defaultFont_ = Font.decode("Arial-PLAIN-12");
	private Document doc_;
	private final ElementCache elementCache_ = new ElementCache();

	public static final boolean addPathSegments_ = false;

	public static boolean detailedErrorInformation_ = false;

	/**
	 * Parse a SVG document and creates shapes.
	 * After creation call {@link #getShapes()} to retrieve the resulting shapes.<br>
	 *
	 * @param xml The svg document.
	 */
	public SVGConverter(final String xml) throws SVGException
	{
		this(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
	}

	public SVGConverter(final InputStream in) throws SVGException
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

			// Without schema processing (see above), "id" attributes will not be detected as key
			// and "getElementById" will not work. So we have to collect the Ids manually.
			elementCache_.scanForIds(doc_);

			// Parse styles and apply them
			NodeList styles = doc_.getElementsByTagName(Type.style.name());
			if (styles.getLength() > 0)
			{
				CSSParser cssParser = new CSSParser();
				CssStyleSelector cssStyleSelector = new CssStyleSelector();
				for (int i = 0; i < styles.getLength(); ++i)
				{
					cssParser.parse(styles.item(i)
										  .getTextContent(), null, cssStyleSelector);
				}
				cssStyleSelector.apply(doc_.getDocumentElement(), elementCache_);
			}

			// @TODO patterns
			// NodeList patterns = doc_.getElementsByTagName("pattern");

			parseChildren(shapes_, doc_.getElementsByTagName("svg")
									   .item(0));

			for (ElementInfo s : shapes_)
				finalShapes_.add(finish(s));

		}
		catch (Exception e)
		{
			throw new SVGException("Failed to parse SVG", e);
		}
	}

	private void parseChildren(List<ElementInfo> shapes, Node parent)
	{
		Node child = parent.getFirstChild();
		while (child != null)
		{
			while (child != null && child.getNodeType() != Node.ELEMENT_NODE)
				child = child.getNextSibling();

			if (child != null)
			{
				parseElement(shapes, elementCache_.getElementWrapper(child));
				child = child.getNextSibling();
			}
		}
	}

	private void parseElement(List<ElementInfo> shapes, ElementWrapper w)
	{
		final String e = w.getTagName();
		Type typ = w.getType();
		List<ElementInfo> g = new ArrayList<>();

		if (typ == null)
			warn("Unknown command %s", e);
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

					shapes.add(new StyledShapeInfo(w.getShape()
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
				if (isNotEmpty(href))
				{
					ElementWrapper refOrgW = elementCache_.getElementWrapperById(href);
					if (refOrgW != null)
					{
						double x = w.toPDouble("x");
						double y = w.toPDouble("y");
						ElementWrapper uw = refOrgW.createReferenceShadow(w);

						List<ElementInfo> usedElements = new ArrayList<>();
						parseElement(usedElements, uw);
						AffineTransform aft = AffineTransform.getTranslateInstance(x, y);
						for (ElementInfo sinfo : usedElements)
						{
							sinfo.applyTransform( aft );
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
				// @TODO
				w.setShape(new Polyline(w.attr("points")).getPath());
				shapes.add(createShapeInfo(w));
			}
			break;
			case polygon:
			{
				w.setShape(new Polyline(w.attr("points")).toPolygon());
				shapes.add(createShapeInfo(w));
			}
			break;

			// Others are parsed on demand
		}
	}

	/**
	 * Finally create shapes from the elements.
	 */
	private AbstractShape finish(ElementInfo si)
	{
		ElementWrapper w = elementCache_.getElementWrapperById(si.id_);

		if ( si instanceof StyledShapeInfo)
		{
			StyledShapeInfo s = (StyledShapeInfo) si;
			StyledShape sws = new StyledShape(
					s.id_,
					s.shape_,
					s.stroke_ == null ? null : s.stroke_.createStroke(w),
					s.paintWrapper_ == null ? null : s.paintWrapper_.createPaint(w),
					s.fillWrapper_ == null ? null : s.fillWrapper_.createPaint(w),
					s.clipping_,
					s.aft_
			);
			return sws;
		} else {
			GroupInfo g = (GroupInfo)si;
			ShapeGroup gr = new ShapeGroup(g.id_, createFilterChain(g.filter_));
			for ( ElementInfo e : g.shapes_)
				gr.shapes_.add( finish(e) );
			// @TODO
			gr.units_ = new Point2D.Double(1,1);
			return gr;
		}
	}

	protected String mapSvgBufferName(String svgBufferName )
	{
		if ( StandardFilterSource.SourceGraphic.name().equals(svgBufferName))
			return FilterBase.SOURCE;
		else
			return svgBufferName;
	}

	protected FilterChain createFilterChain(Filter filter)
	{
		FilterChain filterChain;
		String src;
		if ( filter != null && !filter.primitives_.isEmpty())
		{
			List<FilterPrimitive> primitives = filter.primitives_;

			// Set-up default in/result-linkage
			Map<String, List<FilterPrimitive>> filterMap = new HashMap<>();
			final String sourceGraphic = StandardFilterSource.SourceGraphic.name();
			int sN;
			for ( int fi = 0 ; fi <  primitives.size(); ++fi)
			{
				FilterPrimitive fp = primitives.get(fi);
				if ( fp.result_ == null ) fp.result_ = "FilterBuffer"+filter.id_+"-"+fi;
				sN = fp.numberOfInputs();
				if ( sN > 0 )
				{
					if ( fp.in_.isEmpty())
						fp.in_.add( fi > 0 ? primitives.get(fi-1).result_ : sourceGraphic );
					if ( sN > 1 && fp.in_.size() < 2 )
						fp.in_.add( fi > 0 ? primitives.get(fi-1).result_ : sourceGraphic );
				}
				filterMap.computeIfAbsent(fp.result_,s -> new ArrayList<>()).add(fp);
			}
			// Build primary filter tree
			List<FilterPrimitive> chain = new ArrayList<>();
			List<String> sourcesNeeded = new ArrayList<>();
			// Get root of primary filter tree
			FilterPrimitive fp = primitives.get(primitives.size()-1);
			// Add sources until needed sources are empty or chain is broken
			while ( fp != null )
			{
				chain.add(0, fp);
				sN = fp.in_.size();
				for (int sI = 0; sI < sN; ++sI)
					sourcesNeeded.add(fp.in_.get(sI));

				if ( sourcesNeeded.isEmpty() ) break;
				do {
					src = sourcesNeeded.remove(0);
					if ( StandardFilterSource.fromString(src) != null)
						src = null;
				} while ( src==null && !sourcesNeeded.isEmpty() );
				if ( src == null )
					fp = null;
				else
				{
					List<FilterPrimitive> srcList = filterMap.get(src);
					if ( srcList == null || srcList.isEmpty())
						fp = null;
					else
						fp = srcList.remove(srcList.size()-1);

				}
			}
			if ( !sourcesNeeded.isEmpty() )
			{
				warn("Filter %s has missing inputs: %s", filter.id_, sourcesNeeded.stream().collect(Collectors.joining(",")) );
			}

			filterChain = new FilterChain(
					chain.stream().map(f -> {

						final String resultBuffer = mapSvgBufferName(f.result_);
						final String inBuffer = f.in_.isEmpty() ? null : mapSvgBufferName(f.in_.get(0));

						switch (f.type_)
						{
							case feGaussianBlur:
								GaussianBlurFilterPrimitive gf = (GaussianBlurFilterPrimitive)f;
								double stdDevX = 0;
								double stdDevY = 0;
								if ( !gf.stdDeviation_.isEmpty() )
								{
									stdDevX = gf.stdDeviation_.get(0);
									stdDevY = ( gf.stdDeviation_.size()>1) ? gf.stdDeviation_.get(1) : stdDevX;
								}
								return new GaussianBlur( inBuffer,resultBuffer,
										stdDevX, stdDevY );
							case feOffset:
								OffsetFilterPrimitive of = (OffsetFilterPrimitive)f;
								return new Offset( inBuffer,resultBuffer,
										of.dx_.toPixel(null), of.dy_.toPixel(null) );
							case feSpecularLighting:
								SpecularLightingFilterPrimitive sl = (SpecularLightingFilterPrimitive)f;
								return new SpecularLighting(inBuffer,resultBuffer,sl.surfaceScale_, sl.specularConstant_,
										sl.specularExponent_, sl.dx_, sl.dy_, sl.light_ );
							case feComposite:
								CompositeFilterPrimitive cp = (CompositeFilterPrimitive)f;
								return new Composite(inBuffer,resultBuffer,cp.operator_, cp.k_);
							case feMerge:
							default:
								return null;
						}
					} ).filter(f -> f != null).collect(Collectors.toList()));
		}
		else
			filterChain = null;
		return filterChain;
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
				warn("%s is not a clipPath", w.nodeName());
			}
			else
			{
				ShapeHelper shape = w.getShape();
				if (shape == null)
				{
					List<ElementInfo> g = new ArrayList<>();
					parseChildren(g, w.getNode());

					Path2D.Double clipPath = new Path2D.Double();
					for (ElementInfo si : g)
					{
						// @TODO: Can Clip paths contain groups?
						if ( si instanceof StyledShapeInfo )
						{
							StyledShapeInfo s = (StyledShapeInfo)si;
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
	 * Gets a marker wrapper.
	 */
	public Marker getMarker(String id)
	{
		Marker m = elementCache_.getMarkerById(id);
		if (m == null)
		{
			ElementWrapper w = elementCache_.getElementWrapperById(id);
			if (w != null && w.getType() != Type.marker)
				warn("%s is not a marker", w.nodeName());
			m = new Marker(w);
			elementCache_.addMarker(id, m);
		}
		return m;
	}

	/**
	 * Gets the filter wrapper for an element.
	 */
	public Filter filter(ElementWrapper w)
	{
		w = w == null ? null : elementCache_.getElementWrapperById(w.filter());
		if (w != null)
		{
			Filter f = new Filter(w.id(), w.getType());
			// @TODO: handle href references for filters (same as for gradients).
			if (f.type_ != Type.filter)
				warn("%s is not a filter", f.id_);

			f.x_ = w.toLength("x");
			f.y_ = w.toLength("y");
			f.width_ = w.toLength("width");
			f.height_ = w.toLength("height");

			// @TODO: filterRes
			f.filterUnits_ = Unit.fromString( w.attr("filterUnits"));
			f.primitiveUnits_ = Unit.fromString( w.attr("primitiveUnits"));

			f.primitives_ = new ArrayList<>();

			elementCache_.forSubTree(w.getNode(), e ->
			{
				FilterPrimitive fp = filterPrimitive(e);
				if ( fp != null )
					f.primitives_.add( fp  );
			});
			return f;
		}
		return null;
	}

	public FilterPrimitive filterPrimitive(ElementWrapper w)
	{
		Type t = w.getType();
		if ( FilterPrimitive.isFilterPrimitive( t ) )
		{
			FilterPrimitive fp;
			switch (t)
			{
				case feGaussianBlur:
					fp = new GaussianBlurFilterPrimitive(w.toPDoubleList("stdDeviation",  false));
					break;
				case feOffset:
					fp = new OffsetFilterPrimitive( w.toLength("dx"), w.toLength("dy"));
					break;
				case feSpecularLighting:
				{
					double surfaceScale = w.toPDouble("surfaceScale", 1, false);
					double specularConstant = w.toPDouble("specularConstant", 1, false);
					double specularExponent = w.toPDouble("specularExponent", 1, false);

					List<Double> kernelUnitLength = w.toPDoubleList("kernelUnitLength", false);
					Double dy = null;
					Double dx = null;
					if (!kernelUnitLength.isEmpty())
					{
						dx = kernelUnitLength.get(0);
						dy = kernelUnitLength.size() > 1 ? kernelUnitLength.get(1) : dx;
					}

					final List<LightSource> lights = new ArrayList<>();

					w.forSubTree(ew -> {
						LightSource source = new LightSource();
						switch ( ew.getType())
						{
							case feDistantLight:
								source.type_ = LightSourceType.distant;
								break;
							case fePointLight:
								source.type_ = LightSourceType.point;
								break;
							case feSpotLight:
								source.type_ = LightSourceType.spot;
								break;
						}
						if ( source.type_ != null )
							lights.add(source);
					});
					if ( lights.isEmpty() )
					{
						warn("Missing light source element for %s", w.nodeName());
						fp = null;
					}
					else
						fp = new SpecularLightingFilterPrimitive(surfaceScale, specularConstant, specularExponent, dx, dy, lights.get(0));
				}
				break;
				case feComposite:
				{
					CompositeOperator operator = CompositeOperator.fromString(w.attr("operator", false));
					List<Double> ks = new ArrayList<>(4);
					for (int k = 1; k < 5; ++k)
					{
						Double kx = w.toDouble("k" + k, false);
						ks.add(kx == null ? 0d : kx);
					}
					fp = new CompositeFilterPrimitive(operator, ks);
				}
				break;
				case feMerge:
				{
					MergeFilterPrimitive merge = new MergeFilterPrimitive();
					elementCache_.forSubTree(w.getNode(), e ->
					{
						if ( e.getType() == Type.feMergeNode)
						{
							MergeFilterNode node = new MergeFilterNode();
							node.id_ = e.id();
							node.in_ = e.attr("in",false);
							merge.nodes_.add(node);
							merge.in_.add(node.in_);
						}
					});
					fp = merge;
				}
				break;
				default:
					fp = null;
					warn("Filter primitive %s not yet supported", t.name());
					break;
			}
			if ( fp != null )
				parseCommonFilterPrimitive(fp, w );
			return fp;
		}
		else
			return null;
	}

	private static Map<String, MultipleGradientPaint.ColorSpaceType> colorInterpolationTypes_ =
			Map.of( // @TODO: Check for correct value for "auto"
					"auto", MultipleGradientPaint.ColorSpaceType.LINEAR_RGB,
					"sRGB ", MultipleGradientPaint.ColorSpaceType.SRGB,
					"linearRGB", MultipleGradientPaint.ColorSpaceType.LINEAR_RGB);

	public void parseCommonFilterPrimitive(FilterPrimitive fp, ElementWrapper w )
	{
		String in = w.attr("in", false);
		if( isNotEmpty(in) )
			fp.in_.add(in);
		in = w.attr("in2", false);
		if( isNotEmpty(in) )
			fp.in_.add(in);

		String colorInterpolationFilters = w.attr( "color-interpolation-filters", true);
		fp.colorInterpolation_ = colorInterpolationFilters == null ? MultipleGradientPaint.ColorSpaceType.LINEAR_RGB :
					colorInterpolationTypes_.getOrDefault(colorInterpolationFilters, MultipleGradientPaint.ColorSpaceType.LINEAR_RGB);

		fp.x_ = w.toLength("x");
		fp.y_ = w.toLength("y");
		fp.width_ = w.toLength("width");
		fp.height_ = w.toLength("height");
		fp.result_ = w.attr("result", false);
	}

	/**
	 * Get all parsed shapes.
	 */
	public List<AbstractShape> getShapes()
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


	protected void addShapeContainer(ElementWrapper w, List<ElementInfo> shapes, List<ElementInfo> global)
	{
		AffineTransform t = w.transform();
		if (t != null)
			for (ElementInfo s : shapes)
				if (!s.id_.equals(w.id()))
					s.applyTransform(t);
		Filter f = filter(w);
		if ( f == null )
			global.addAll(shapes);
		else
		{
			GroupInfo group = new GroupInfo(w.id(), f);
			group.shapes_.addAll(shapes);
			global.add(group);
		}
		shapes.clear();
	}

	/**
	 * Helper to handle common presentation attributes and create a ShapeInfo-instance.
	 */
	protected ElementInfo createShapeInfo(ElementWrapper w)
	{
		Stroke stroke = stroke(w);
		Color fill = fill(w);
		Shape clipPath = clipPath(w);

		StyledShapeInfo styledShapeInfo = new StyledShapeInfo(w.getShape().getShape(),
				stroke.getPaintWrapper() == null ? null : stroke,
				stroke.getPaintWrapper(),
				fill.getPaintWrapper(),
				clipPath
		);

		ElementInfo sinfo = styledShapeInfo;
		sinfo.id_ = w.id();
		transform(styledShapeInfo, w);

		Filter f = filter(w);
		if ( f != null )
		{
			GroupInfo g = new GroupInfo(sinfo.id_, f);
			g.shapes_.add(sinfo);
			return g;
		}
		else
			return sinfo;
	}

	private void parseCommonGradient(Gradient g, ElementWrapper w)
	{
		g.href_ = w.href();

		String spreadMethod = w.attr("spreadMethod");
		if (isNotEmpty(spreadMethod))
		{
			spreadMethod = spreadMethod.trim()
									   .toLowerCase();
			if ("reflect".equals(spreadMethod))
				g.cycleMethod_ = MultipleGradientPaint.CycleMethod.REFLECT;
			else if ("repeat".equals(spreadMethod))
				g.cycleMethod_ = MultipleGradientPaint.CycleMethod.REPEAT;
		}

		g.gradientUnit_ = Unit.fromString(w.attr("gradientUnits"));

		String gradientTransform = w.attr("gradientTransform", false);
		if (isNotEmpty(gradientTransform))
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

				// Java2D enforce strictly increasing fractions.
				// SVG allows it - see https://svgwg.org/svg2-draft/pservers.html#StopNotes
				if (i > 0 && f <= g.fractions_[i - 1])
				{
					f = g.fractions_[i - 1] + .0000000001f;
				}
				g.fractions_[i] = f;

				final Color cp = new Color(this, wrapper.attr("stop-color"),
						wrapper.toPDouble("stop-opacity", 1d, false));
				PaintWrapper pw = cp.getPaintWrapper();
				g.colors_[i] = (pw != null && pw.getColor() != null) ? pw.getColor() : java.awt.Color.BLACK;
			}
		}
	}


	/**
	 * Handles "Transform" attribute.
	 */
	protected final void transform(StyledShapeInfo s, ElementWrapper w)
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
