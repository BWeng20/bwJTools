package com.bw.jtools.ui.vector.svg;

import com.bw.jtools.Application;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.ui.JExceptionDialog;
import com.bw.jtools.ui.icon.ShapeIcon;
import com.bw.jtools.ui.vector.ShapeInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
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
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SVG
{
	private final List<ShapeInfo> shapes_ = new ArrayList<>();
	private Map<String, Gradient> paintServer_ = new HashMap<>();
	private Map<String, Paint> paints_ = new HashMap<>();
	private Font defaultFont_ = Font.decode("Arial-PLAIN-12");
	private FontRenderContext frc = new FontRenderContext(null, false, false);


	public SVG(final String xml) throws XMLParseException
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xml)));

			// Collect all gradient definitions.
			NodeList linearGradient = doc.getElementsByTagName("linearGradient");
			final int lgN = linearGradient.getLength();
			for (int i = 0; i < lgN; ++i)
				parseLinearGradient((Element) linearGradient.item(i));

			NodeList radialGradient = doc.getElementsByTagName("radialGradient");
			final int rgN = radialGradient.getLength();
			for (int i = 0; i < rgN; ++i)
				parseRadialGradient((Element) radialGradient.item(i));

			NodeList patterns = doc.getElementsByTagName("pattern");
			// @TODO

			parseChildren(shapes_, doc.getElementsByTagName("svg")
									  .item(0));
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	private void parseRadialGradient(Element node)
	{
		ElementWrapper w = new ElementWrapper(node);
		String id = w.id();
		if (id != null && !id.isEmpty())
		{
			RadialGradient rg = new RadialGradient(id);
			// @TODO gradientUnits
			// @TODO spreadMethod

			rg.cx = w.toDouble("cx");
			rg.cy = w.toDouble("cy");
			rg.r = w.toDouble("r");
			rg.fx = w.toDouble("fx");
			rg.fy = w.toDouble("fy");
			rg.fr = w.toDouble("fr");

			String gradientTransform = w.attr("gradientTransform");
			if (ElementWrapper.isNotEmpty(gradientTransform))
				rg.aft_ = new Transform(null, gradientTransform).getTransform();
			else
				rg.aft_ = new AffineTransform();
			rg.href_ = w.href();

			addPaintServer(rg);
		}
	}

	private void parseLinearGradient(Element node)
	{
		ElementWrapper w = new ElementWrapper(node);
		String id = w.id();
		if (id != null && !id.isEmpty())
		{
			LinearGradient lg = new LinearGradient(id);

			// @TODO gradientUnits
			lg.href_ = w.href();
			lg.x1 = w.toDouble("x1");
			lg.y1 = w.toDouble("y1");
			lg.x2 = w.toDouble("x2");
			lg.y2 = w.toDouble("y2");

			String spreadMethod = w.attr("spreadMethod");
			if (ElementWrapper.isNotEmpty(spreadMethod))
			{
				spreadMethod = spreadMethod.trim()
										   .toLowerCase();
				if ("reflect".equals(spreadMethod))
					lg.cycleMethod_ = MultipleGradientPaint.CycleMethod.REFLECT;
				else if ("repeat".equals(spreadMethod))
					lg.cycleMethod_ = MultipleGradientPaint.CycleMethod.REPEAT;
			}

			String gradientTransform = w.attr("gradientTransform", false);
			if (ElementWrapper.isNotEmpty(gradientTransform))
				lg.aft_ = new Transform(null, gradientTransform).getTransform();

			NodeList stops = node.getElementsByTagName("stop");

			lg.fractions_ = new float[stops.getLength()];
			float f;
			lg.colors_ = new java.awt.Color[stops.getLength()];

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
						f = toPFloat(offset.substring(0, offset.length() - 1)) / 100f;
					else
						f = toPFloat(offset);
					if (f < 0)
						f = 0;
					else if (f > 1.0f)
						f = 1.0f;
				}
				else
					f = i > 0 ? lg.fractions_[i - 1] : 0;

				lg.fractions_[i] = f;

				Paint p = new Color(this, wrapper.attr("stop-color"),
						wrapper.toFloat("stop-opacity")).getColor();
				if (p instanceof java.awt.Color)
					lg.colors_[i] = (java.awt.Color) p;
				else
					lg.colors_[i] = java.awt.Color.WHITE;
			}
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
				parse(shapes, (Element) child);
				child = child.getNextSibling();
			}
		}
	}

	private void parse(List<ShapeInfo> shapes, Element n)
	{
		final ElementWrapper w = new ElementWrapper(n);
		final String e = w.getTagName();

		if ("g".equalsIgnoreCase(e))
		{
			List<ShapeInfo> g = new ArrayList<>();
			parseChildren(g, n);

			String transform = n.getAttribute("transform");
			if (ElementWrapper.isNotEmpty(transform))
			{
				AffineTransform t = new Transform(null, transform).getTransform();
				if (t != null)
					for (ShapeInfo s : g)
					{
						if (s.aft_ == null)
							s.aft_ = t;
						else
							s.aft_.concatenate(t);
					}
			}
			for (ShapeInfo s : g)
				shapes.add(s);
			g.clear();
		}
		else if ("path".equalsIgnoreCase(e))
		{
			Path path = new Path(n.getAttribute("d"));

			Stroke stroke = stroke(w);
			Color fill = fill(w);

			ShapeInfo s = new ShapeInfo(path.getPath(),
					stroke.getColor() == null ? null : stroke.getStroke(),
					stroke.getColor(),
					opacity(w),
					fill(w).getColor(),
					fill.getOpacity());
			s.id_ = w.id();
			transform(s, n);
			shapes.add(s);
		}
		else if ("rect".equalsIgnoreCase(e))
		{
			float x = toPFloat(w.attr("x"));
			float y = toPFloat(w.attr("y"));
			float width = toPFloat(w.attr("width"));
			float height = toPFloat(w.attr("height"));

			Stroke stroke = stroke(w);
			Color fill = fill(w);

			Rectangle2D.Double rec = new Rectangle2D.Double(x, y, width, height);

			ShapeInfo s = new ShapeInfo(rec,
					stroke.getColor() == null ? null : stroke.getStroke(),
					stroke.getColor(),
					opacity(w),
					fill.getColor(),
					fill.getOpacity()
			);
			s.id_ = w.id();
			transform(s, n);
			shapes.add(s);
		}
		else if ("text".equalsIgnoreCase(e))
		{
			float x = toPFloat(w.attr("x"));
			float y = toPFloat(w.attr("y"));
			Stroke stroke = stroke(w);
			Color fill = fill(w);

			String text = n.getTextContent();

			String ts = w.attr("font-size");
			if (ts == null) ts = "12";
			if (ts.endsWith("px"))
				// @TODO
				ts = ts.substring(0, ts.length() - 2);


			Font dcf = defaultFont_.deriveFont(toPFloat(ts));

			Shape textshape = new TextLayout(text, dcf, frc).getOutline(AffineTransform.getTranslateInstance(x, y));

			ShapeInfo s = new ShapeInfo(textshape,
					stroke.getColor() == null ? null : stroke.getStroke(),
					stroke.getColor(),
					opacity(w),
					fill.getColor(),
					fill.getOpacity());
			s.id_ = w.id();
			transform(s, n);
			shapes.add(s);
		}
		else
		{
			System.out.println("Unknown command " + e);
		}
	}

	public Gradient getPaintServer(String id)
	{
		return paintServer_.get(id);
	}

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

	public void addPaintServer(Gradient grad)
	{
		paintServer_.put(grad.id_, grad);
	}

	public List<ShapeInfo> getShapes()
	{
		return Collections.unmodifiableList(shapes_);
	}

	protected final void transform(ShapeInfo s, Element n)
	{
		String transform = n.getAttribute("transform");
		if (ElementWrapper.isNotEmpty(transform))
		{
			AffineTransform t = new Transform(null, transform).getTransform();
			if (t != null)
				s.aft_ = t;
		}

	}

	protected final float opacity(ElementWrapper w)
	{
		Float opacityO = w.toFloat("opacity");
		float opacity = opacityO == null ? 1.0f : opacityO;
		return opacity;
	}

	protected Color fill(ElementWrapper w)
	{
		return new Color(this, w.attr("fill"), w.toFloat("fill-opacity"));
	}

	protected Stroke stroke(ElementWrapper w)
	{
		Stroke stroke = new Stroke(
				new Color(this, w.attr("stroke"), w.toFloat("stroke-opacity")),
				w.toFloat("stroke-width"),
				toFloatArray(w.attr("stroke-dasharray")),
				w.toFloat("stroke-dashoffset"),
				LineCap.fromString(w.attr("stroke-linecap")),
				LineJoin.fromString(w.attr("stroke-linejoin")),
				w.toFloat("stroke-miterlimit")
		);
		return stroke;
	}

	protected final static float toPFloat(String val)
	{
		Float f = ElementWrapper.convFloat(val);
		return f == null ? 0f : f;
	}

	protected final static float[] toFloatArray(String val)
	{
		if (val != null)
			try
			{
				val = val.replace(',', ' ');
				String values[] = val.split("[ ,]+");
				float farr[] = new float[values.length];
				for (int i = 0; i < values.length; ++i)
					farr[i] = toPFloat(values[i]);
			}
			catch (Exception e)
			{
			}
		return null;
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
		try (InputStream is = SVG.class.getResourceAsStream("address-book-new.svg"))
		{
			data.setText(new String(is.readAllBytes(), StandardCharsets.UTF_8));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

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
