package com.bw.jtools.ui.vector.svg;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.ui.JExceptionDialog;
import com.bw.jtools.ui.icon.ShapeIcon;
import com.bw.jtools.ui.vector.ShapeInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.modelmbean.XMLParseException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Currently supported features:
 * path
 * rect
 * ellipse
 * text (but not tspan)
 */
public class SVG
{
	private final List<ShapeInfo> shapes_ = new ArrayList<>();
	private Map<String, Gradient> paintServer_ = new HashMap<>();
	private Map<String, Paint> paints_ = new HashMap<>();
	private Font defaultFont_ = Font.decode("Arial-PLAIN-12");
	private FontRenderContext frc = new FontRenderContext(null, false, false);
	private Document doc_;
	private final HashMap<String, ElementWrapper> wrapperById_ = new HashMap<>();

	/**
	 * Parse a SVG document and creates shapes.
	 *
	 * @param xml The svg document.
	 * @throws XMLParseException If the document is not valid xml.
	 */
	public SVG(final String xml) throws XMLParseException
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			DocumentBuilder db = dbf.newDocumentBuilder();

			doc_ = db.parse(new ByteArrayInputStream(xml.getBytes()));

			// Loading the DTD/Schema will slow us down (if specified). But without validating/loading of schema the "id" attributes will not be detected as key
			// and "getElementById" will not work. So we have to collect Ids manually.
			scanForIds(doc_);

			// Collect all gradient definitions.
			NodeList linearGradient = doc_.getElementsByTagName("linearGradient");
			final int lgN = linearGradient.getLength();
			for (int i = 0; i < lgN; ++i)
				parseLinearGradient((Element) linearGradient.item(i));

			NodeList radialGradient = doc_.getElementsByTagName("radialGradient");
			final int rgN = radialGradient.getLength();
			for (int i = 0; i < rgN; ++i)
				parseRadialGradient((Element) radialGradient.item(i));

			NodeList patterns = doc_.getElementsByTagName("pattern");
			// @TODO

			parseChildren(shapes_, doc_.getElementsByTagName("svg")
									   .item(0));

		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			e.printStackTrace();
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
		return paintServer_.get(id);
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
			Gradient g = paintServer_.get(id);
			if (g != null)
				pt = g.getPaint(this);
			if (pt == null)
				pt = java.awt.Color.BLACK;
			paints_.put(id, pt);
		}
		return pt;
	}

	/**
	 * Adds a gradient definition.
	 *
	 * @param grad The gradient. "id_" has to be set.
	 */
	public void addPaintServer(Gradient grad)
	{
		paintServer_.put(grad.id_, grad);
	}

	/**
	 * Get all parsed shapes.
	 */
	public List<ShapeInfo> getShapes()
	{
		return Collections.unmodifiableList(shapes_);
	}

	private void scanForIds(Node node)
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
					wrapperById_.put(id, new ElementWrapper((Element) node));
			}
		}
		Node next = node.getNextSibling();
		if (next != null) scanForIds(next);
		next = node.getFirstChild();
		if (next != null) scanForIds(next);
	}

	private ElementWrapper getElementWrapper(Element node)
	{
		String id = node.getAttribute("id");
		if (ElementWrapper.isNotEmpty(id))
			return wrapperById_.get(id);
		else
			return new ElementWrapper(node);
	}

	private ElementWrapper getElementWrapperById(String id)
	{
		if (ElementWrapper.isNotEmpty(id))
			return wrapperById_.get(id);
		else
			return null;
	}

	private void parseRadialGradient(Element node)
	{
		ElementWrapper w = getElementWrapper(node);
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
			addPaintServer(rg);
		}
	}

	private void parseLinearGradient(Element node)
	{
		ElementWrapper w = getElementWrapper(node);
		String id = w.id();
		if (id != null && !id.isEmpty())
		{
			LinearGradient lg = new LinearGradient(id);

			lg.x1 = w.toDouble("x1");
			lg.y1 = w.toDouble("y1");
			lg.x2 = w.toDouble("x2");
			lg.y2 = w.toDouble("y2");

			parseCommonGradient(lg, w);
			addPaintServer(lg);

		}
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
				parse(shapes, getElementWrapper((Element) child));
				child = child.getNextSibling();
			}
		}
	}

	private void parse(List<ShapeInfo> shapes, ElementWrapper w)
	{
		final String e = w.getTagName();

		if ("g".equalsIgnoreCase(e))
		{
			List<ShapeInfo> g = new ArrayList<>();
			parseChildren(g, w.getNode());

			String transform = w.attr("transform", false);
			if (ElementWrapper.isNotEmpty(transform))
			{
				AffineTransform t = new Transform(null, transform).getTransform();
				if (t != null)
					for (ShapeInfo s : g)
					{
						if (s.aft_ == null)
							s.aft_ = t;
						else
							s.aft_.preConcatenate(t);
					}
			}
			for (ShapeInfo s : g)
				shapes.add(s);
			g.clear();
		}
		else if ("path".equalsIgnoreCase(e))
		{
			Path path = new Path(w.attr("d", false));
			shapes.add(createShape(w, path.getPath()));
		}
		else if ("rect".equalsIgnoreCase(e))
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
		else if ("ellipse".equalsIgnoreCase(e))
		{
			float cx = (float) w.toPDouble("cx", false);
			float cy = (float) w.toPDouble("cy", false);
			float rx = (float) w.toPDouble("rx", false);
			float ry = (float) w.toPDouble("ry", false);

			Ellipse2D.Double ellipse = new Ellipse2D.Double(cx - rx, cy - ry, 2d * rx, 2d * ry);
			shapes.add(createShape(w, ellipse));
		}
		else if ("text".equalsIgnoreCase(e))
		{
			float x = (float) w.toPDouble("x");
			float y = (float) w.toPDouble("y");
			String text = w.getNode()
						   .getTextContent();

			if (text != null && !w.preserveSpace())
				text = text.trim();

			Font dcf = font(w);

			Shape textshape = new TextLayout(text, dcf, frc).getOutline(AffineTransform.getTranslateInstance(x, y));
			shapes.add(createShape(w, textshape));
		}
		else if ("use".equalsIgnoreCase(e))
		{
			String href = w.href();
			if (ElementWrapper.isNotEmpty(href))
			{
				ElementWrapper refOrgW = getElementWrapperById(href);
				if (refOrgW != null)
				{
					ElementWrapper uw = new ElementWrapper(refOrgW.getNode());
					String tag = uw.getTagName();
					if (tag.equals("svg") || tag.equals("symbol"))
					{
						// @TODO: set width and height of viewbox
					}

					double x = w.toPDouble("x");
					double y = w.toPDouble("y");

					NamedNodeMap attributes = w.getNode()
											   .getAttributes();
					int nAttr = attributes.getLength();
					for (int iAttr = 0; iAttr < nAttr; ++iAttr)
					{
						Node attrNode = attributes.item(iAttr);
						String attrName = attrNode.getNodeName();
						if (attrName != null)
							uw.override(attrName, attrNode.getNodeValue());
					}
					HashMap<String, String> styleAttributes = uw.getStyleAttributes();
					for (Map.Entry<String, String> styleAttr : styleAttributes.entrySet())
					{
						String attrName = styleAttr.getKey();
						uw.override(attrName, styleAttr.getValue());
					}

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
		else if ("defs".equalsIgnoreCase(e))
		{
		}
		else if ("linearGradient".equalsIgnoreCase(e))
		{
			// Already processed. See C'tor.
		}
		else if ("radialGradient".equalsIgnoreCase(e))
		{
			// Already processed. See C'tor.
		}
		else
		{
			Log.warn("Unknown command " + e);
		}
	}

	/**
	 * Helper to handle common presentation attributes and create a ShapeInfo-instance.
	 */
	protected ShapeInfo createShape(ElementWrapper w, Shape s)
	{
		Stroke stroke = stroke(w);
		Color fill = fill(w);

		ShapeInfo sinfo = new ShapeInfo(s,
				stroke.getColor() == null ? null : stroke.getStroke(),
				stroke.getColor(),
				opacity(w),
				fill.getColor(),
				fill.getOpacity()
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

		NodeList stops = w.getNode().getElementsByTagName("stop");

		g.fractions_ = new float[stops.getLength()];
		float f;
		g.colors_ = new java.awt.Color[stops.getLength()];

		int sN = stops.getLength();
		for (int i = 0; i < sN; ++i)
		{
			Element stop = (Element) stops.item(i);
			ElementWrapper wrapper = new ElementWrapper(stop);

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
		String transform = w.attr("transform", false);
		if (ElementWrapper.isNotEmpty(transform))
		{
			AffineTransform t = new Transform(null, transform).getTransform();
			if (t != null)
				s.aft_ = t;
		}
	}

	/**
	 * Handles  font repated attributes and returns the calculated font.
	 */
	protected Font font(ElementWrapper w)
	{
		Double fontSize = w.toDouble("font-size");
		String fontFamily = w.attr("font-family");
		double fontWeight = w.fontWeight();

		if (fontSize == null) fontSize = ElementWrapper.convUnitToPixel(12, ElementWrapper.Unit.pt);
		if (fontFamily == null) fontFamily = defaultFont_.getFamily();

		Map<TextAttribute, Object> attributes = new HashMap<>();

		// @TODO: I don't think it is this simple... check more deeply how this css-property shall be proceeded.
		//        At least we don't habe all families that a browser have...
		attributes.put(TextAttribute.FAMILY, fontFamily);

		attributes.put(TextAttribute.WEIGHT, fontWeight);

		// font-size seems to use also pixel as unit. But I found no documentation about that.
		attributes.put(TextAttribute.SIZE, fontSize);

		return Font.getFont(attributes);
	}

	protected final float opacity(ElementWrapper w)
	{
		Double opacityO = w.toDouble("opacity");
		float opacity = opacityO == null ? 1.0f : opacityO.floatValue();
		return opacity;
	}

	protected Color fill(ElementWrapper w)
	{
		String color = w.attr("fill");
		return new Color(this, color == null ? "black" : color, w.toDouble("fill-opacity"));
	}

	protected Stroke stroke(ElementWrapper w)
	{
		Stroke stroke = new Stroke(
				new Color(this, w.attr("stroke"), w.toDouble("stroke-opacity")),
				w.toDouble("stroke-width"),
				w.toFloatArray("stroke-dasharray"),
				w.toDouble("stroke-dashoffset"),
				LineCap.fromString(w.attr("stroke-linecap")),
				LineJoin.fromString(w.attr("stroke-linejoin")),
				w.toDouble("stroke-miterlimit")
		);
		return stroke;
	}

	public static void main(String[] args)
	{
		Application.initialize(SVG.class);

		JPanel input = new JPanel(new BorderLayout());
		JButton render = new JButton("Show");
		JButton load = new JButton("Load");
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		final List<ShapeIcon> icons = new ArrayList<>();

		JTextArea data = new JTextArea(5, 100);

		input.add(BorderLayout.CENTER, new JScrollPane(data));

		JPanel drawPanel = new JPanel(new BorderLayout(10, 10));
		JPanel controls = new JPanel(new GridLayout(0, 1));

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.add(load);
		buttons.add(render);
		controls.add(buttons);

		double sliderFactor = 100;

		Hashtable<Integer, String> labels = new Hashtable(
				Map.of((int) (0 * sliderFactor), new JLabel("0"),
						(int) (1 * sliderFactor), new JLabel("1"),
						(int) (2 * sliderFactor), new JLabel("2"),
						(int) (4 * sliderFactor), new JLabel("4"),
						(int) (8 * sliderFactor), new JLabel("8"),
						(int) (10 * sliderFactor), new JLabel("10"),
						(int) (15 * sliderFactor), new JLabel("15"),
						(int) (20 * sliderFactor), new JLabel("20")
				));

		final JSlider scaleX = new JSlider(JSlider.HORIZONTAL, 0, (int) (20 * sliderFactor), (int) sliderFactor);
		scaleX.setPaintLabels(false);
		scaleX.setPaintTicks(false);
		scaleX.setExtent((int) (sliderFactor / 10));

		final JSlider scaleY = new JSlider(JSlider.HORIZONTAL, 0, (int) (20 * sliderFactor), (int) sliderFactor);
		scaleY.setLabelTable(labels);
		scaleY.setPaintLabels(true);
		scaleY.setMajorTickSpacing((int) (sliderFactor / 2));
		scaleY.setMinorTickSpacing((int) (sliderFactor / 10));
		scaleY.setPaintTicks(true);
		scaleY.setExtent((int) (sliderFactor / 10));

		controls.add(scaleX);
		controls.add(scaleY);
		input.add(BorderLayout.LINE_END, controls);

		JLabel draw = new JLabel();
		draw.setHorizontalAlignment(JLabel.LEFT);
		draw.setVerticalAlignment(JLabel.TOP);

		drawPanel.add(BorderLayout.CENTER, draw);
		drawPanel.setPreferredSize(new Dimension(200, 600));

		JTextField status = new JTextField();
		status.setEditable(false);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(BorderLayout.NORTH, input);
		panel.add(BorderLayout.CENTER, drawPanel);
		panel.add(BorderLayout.SOUTH, status);

		Runnable updateStatus = () ->
		{
			status.setText(draw.getIcon()
							   .getIconWidth() + "x" + draw.getIcon()
														   .getIconHeight() + ", scale " +
					nf.format(scaleX.getValue() / sliderFactor) + " x " + nf.format(scaleY.getValue() / sliderFactor)
			);
		};

		ChangeListener sliderCl = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				SwingUtilities.invokeLater(() ->
				{
					double x = scaleX.getValue() / sliderFactor;
					double y = scaleY.getValue() / sliderFactor;

					if (x < 0.1d)
					{
						scaleX.setValue((int) (0.1d * sliderFactor));
						x = 0.1;
					}
					if (y < 0.1d)
					{
						scaleY.setValue((int) (0.1d * sliderFactor));
						x = 0.1;
					}

					boolean repaint = false;
					for (ShapeIcon i : icons)
					{
						if (x != i.getXScale() || y != i.getYScale())
						{
							repaint = true;
							i.setScale(x, y);
						}
					}
					if (repaint)
					{
						updateStatus.run();
						drawPanel.repaint();
					}
				});
			}
		};

		scaleX.addChangeListener(sliderCl);
		scaleY.addChangeListener(sliderCl);

		final Runnable doRender = () ->
		{
			try
			{
				SVG svg = new SVG(data.getText());

				ShapeIcon icon = new ShapeIcon(svg.getShapes());
				icon.setInlineBorder(true);

				Dimension d = drawPanel.getSize();

				double scale = Math.min(d.width / (double) icon.getIconWidth(), d.height / (double) icon.getIconHeight());
				icon.setScale(scale, scale);
				scaleX.setValue((int) (0.5 + scale * sliderFactor));
				scaleY.setValue((int) (0.5 + scale * sliderFactor));

				icons.clear();
				icons.add(icon);
				draw.setIcon(icon);
				updateStatus.run();

				drawPanel.invalidate();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				status.setText(ex.getMessage());
			}
		};

		render.addActionListener(e ->
		{
			doRender.run();
		});

		JFrame f = new JFrame("Path  Test");

		load.addActionListener(e ->
		{
			File file = IOTool.selectFile(f, "svg_file", "Select SVG", IOTool.OPEN, new FileNameExtensionFilter("SVG Files", "svg"));
			if (file != null)
			{
				try
				{
					byte d[] = Files.readAllBytes(file.toPath());
					data.setText(new String(d, StandardCharsets.UTF_8));
					SwingUtilities.invokeLater(() ->
					{
						doRender.run();
					});
				}
				catch (Exception ex)
				{
					JExceptionDialog d = new JExceptionDialog(f, ex);
					d.setLocationByPlatform(true);
					d.setVisible(true);
				}
			}
		});

		f.setContentPane(panel);
		f.pack();
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}

}
