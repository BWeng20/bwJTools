package com.bw.jtools.svg;

import java.awt.Paint;
import java.util.HashMap;

public class Color
{
	private static final HashMap<String, java.awt.Color> name2color_ = new HashMap<>();

	static
	{
		name2color_.put("black", new java.awt.Color(0x00, 0x00, 0x00));
		name2color_.put("navy", new java.awt.Color(0x00, 0x00, 0x80));
		name2color_.put("darkblue", new java.awt.Color(0x00, 0x00, 0x8B));
		name2color_.put("mediumblue", new java.awt.Color(0x00, 0x00, 0xCD));
		name2color_.put("blue", new java.awt.Color(0x00, 0x00, 0xFF));
		name2color_.put("darkgreen", new java.awt.Color(0x00, 0x64, 0x00));
		name2color_.put("green", new java.awt.Color(0x00, 0x80, 0x00));
		name2color_.put("teal", new java.awt.Color(0x00, 0x80, 0x80));
		name2color_.put("darkcyan", new java.awt.Color(0x00, 0x8B, 0x8B));
		name2color_.put("deepskyblue", new java.awt.Color(0x00, 0xBF, 0xFF));
		name2color_.put("darkturquoise", new java.awt.Color(0x00, 0xCE, 0xD1));
		name2color_.put("mediumspringgreen", new java.awt.Color(0x00, 0xFA, 0x9A));
		name2color_.put("lime", new java.awt.Color(0x00, 0xFF, 0x00));
		name2color_.put("springgreen", new java.awt.Color(0x00, 0xFF, 0x7F));
		name2color_.put("cyan(Safe 16=aqua Hex3)", new java.awt.Color(0x00, 0xFF, 0xFF));
		name2color_.put("aqua", new java.awt.Color(0x00, 0xFF, 0xFF));
		name2color_.put("midnightblue", new java.awt.Color(0x19, 0x19, 0x70));
		name2color_.put("dodgerblue", new java.awt.Color(0x1E, 0x90, 0xFF));
		name2color_.put("lightseagreen", new java.awt.Color(0x20, 0xB2, 0xAA));
		name2color_.put("forestgreen", new java.awt.Color(0x22, 0x8B, 0x22));
		name2color_.put("seagreen", new java.awt.Color(0x2E, 0x8B, 0x57));
		name2color_.put("darkslategray", new java.awt.Color(0x2F, 0x4F, 0x4F));
		name2color_.put("darkslategrey", new java.awt.Color(0x2F, 0x4F, 0x4F));
		name2color_.put("limegreen", new java.awt.Color(0x32, 0xCD, 0x32));
		name2color_.put("mediumseagreen", new java.awt.Color(0x3C, 0xB3, 0x71));
		name2color_.put("turquoise", new java.awt.Color(0x40, 0xE0, 0xD0));
		name2color_.put("royalblue", new java.awt.Color(0x41, 0x69, 0xE1));
		name2color_.put("steelblue", new java.awt.Color(0x46, 0x82, 0xB4));
		name2color_.put("darkslateblue", new java.awt.Color(0x48, 0x3D, 0x8B));
		name2color_.put("mediumturquoise", new java.awt.Color(0x48, 0xD1, 0xCC));
		name2color_.put("indigo", new java.awt.Color(0x4B, 0x00, 0x82));
		name2color_.put("darkolivegreen", new java.awt.Color(0x55, 0x6B, 0x2F));
		name2color_.put("cadetblue", new java.awt.Color(0x5F, 0x9E, 0xA0));
		name2color_.put("cornflowerblue", new java.awt.Color(0x64, 0x95, 0xED));
		name2color_.put("mediumaquamarine", new java.awt.Color(0x66, 0xCD, 0xAA));
		name2color_.put("dimgrey", new java.awt.Color(0x69, 0x69, 0x69));
		name2color_.put("dimgray", new java.awt.Color(0x69, 0x69, 0x69));
		name2color_.put("slateblue", new java.awt.Color(0x6A, 0x5A, 0xCD));
		name2color_.put("olivedrab", new java.awt.Color(0x6B, 0x8E, 0x23));
		name2color_.put("slategrey", new java.awt.Color(0x70, 0x80, 0x90));
		name2color_.put("slategray", new java.awt.Color(0x70, 0x80, 0x90));
		name2color_.put("lightslategray", new java.awt.Color(0x77, 0x88, 0x99));
		name2color_.put("lightslategrey", new java.awt.Color(0x77, 0x88, 0x99));
		name2color_.put("mediumslateblue", new java.awt.Color(0x7B, 0x68, 0xEE));
		name2color_.put("lawngreen", new java.awt.Color(0x7C, 0xFC, 0x00));
		name2color_.put("chartreuse", new java.awt.Color(0x7F, 0xFF, 0x00));
		name2color_.put("aquamarine", new java.awt.Color(0x7F, 0xFF, 0xD4));
		name2color_.put("maroon", new java.awt.Color(0x80, 0x00, 0x00));
		name2color_.put("purple", new java.awt.Color(0x80, 0x00, 0x80));
		name2color_.put("olive", new java.awt.Color(0x80, 0x80, 0x00));
		name2color_.put("gray", new java.awt.Color(0x80, 0x80, 0x80));
		name2color_.put("grey", new java.awt.Color(0x80, 0x80, 0x80));
		name2color_.put("skyblue", new java.awt.Color(0x87, 0xCE, 0xEB));
		name2color_.put("lightskyblue", new java.awt.Color(0x87, 0xCE, 0xFA));
		name2color_.put("blueviolet", new java.awt.Color(0x8A, 0x2B, 0xE2));
		name2color_.put("darkred", new java.awt.Color(0x8B, 0x00, 0x00));
		name2color_.put("darkmagenta", new java.awt.Color(0x8B, 0x00, 0x8B));
		name2color_.put("saddlebrown", new java.awt.Color(0x8B, 0x45, 0x13));
		name2color_.put("darkseagreen", new java.awt.Color(0x8F, 0xBC, 0x8F));
		name2color_.put("lightgreen", new java.awt.Color(0x90, 0xEE, 0x90));
		name2color_.put("mediumpurple", new java.awt.Color(0x93, 0x70, 0xDB));
		name2color_.put("darkviolet", new java.awt.Color(0x94, 0x00, 0xD3));
		name2color_.put("palegreen", new java.awt.Color(0x98, 0xFB, 0x98));
		name2color_.put("darkorchid", new java.awt.Color(0x99, 0x32, 0xCC));
		name2color_.put("yellowgreen", new java.awt.Color(0x9A, 0xCD, 0x32));
		name2color_.put("sienna", new java.awt.Color(0xA0, 0x52, 0x2D));
		name2color_.put("brown", new java.awt.Color(0xA5, 0x2A, 0x2A));
		name2color_.put("darkgray", new java.awt.Color(0xA9, 0xA9, 0xA9));
		name2color_.put("darkgrey", new java.awt.Color(0xA9, 0xA9, 0xA9));
		name2color_.put("lightblue", new java.awt.Color(0xAD, 0xD8, 0xE6));
		name2color_.put("greenyellow", new java.awt.Color(0xAD, 0xFF, 0x2F));
		name2color_.put("paleturquoise", new java.awt.Color(0xAF, 0xEE, 0xEE));
		name2color_.put("lightsteelblue", new java.awt.Color(0xB0, 0xC4, 0xDE));
		name2color_.put("powderblue", new java.awt.Color(0xB0, 0xE0, 0xE6));
		name2color_.put("firebrick", new java.awt.Color(0xB2, 0x22, 0x22));
		name2color_.put("darkgoldenrod", new java.awt.Color(0xB8, 0x86, 0x0B));
		name2color_.put("mediumorchid", new java.awt.Color(0xBA, 0x55, 0xD3));
		name2color_.put("rosybrown", new java.awt.Color(0xBC, 0x8F, 0x8F));
		name2color_.put("darkkhaki", new java.awt.Color(0xBD, 0xB7, 0x6B));
		name2color_.put("silver", new java.awt.Color(0xC0, 0xC0, 0xC0));
		name2color_.put("mediumvioletred", new java.awt.Color(0xC7, 0x15, 0x85));
		name2color_.put("indianred", new java.awt.Color(0xCD, 0x5C, 0x5C));
		name2color_.put("peru", new java.awt.Color(0xCD, 0x85, 0x3F));
		name2color_.put("chocolate", new java.awt.Color(0xD2, 0x69, 0x1E));
		name2color_.put("tan", new java.awt.Color(0xD2, 0xB4, 0x8C));
		name2color_.put("lightgray", new java.awt.Color(0xD3, 0xD3, 0xD3));
		name2color_.put("lightgrey", new java.awt.Color(0xD3, 0xD3, 0xD3));
		name2color_.put("thistle", new java.awt.Color(0xD8, 0xBF, 0xD8));
		name2color_.put("orchid", new java.awt.Color(0xDA, 0x70, 0xD6));
		name2color_.put("goldenrod", new java.awt.Color(0xDA, 0xA5, 0x20));
		name2color_.put("palevioletred", new java.awt.Color(0xDB, 0x70, 0x93));
		name2color_.put("crimson", new java.awt.Color(0xDC, 0x14, 0x3C));
		name2color_.put("gainsboro", new java.awt.Color(0xDC, 0xDC, 0xDC));
		name2color_.put("plum", new java.awt.Color(0xDD, 0xA0, 0xDD));
		name2color_.put("burlywood", new java.awt.Color(0xDE, 0xB8, 0x87));
		name2color_.put("lightcyan", new java.awt.Color(0xE0, 0xFF, 0xFF));
		name2color_.put("lavender", new java.awt.Color(0xE6, 0xE6, 0xFA));
		name2color_.put("darksalmon", new java.awt.Color(0xE9, 0x96, 0x7A));
		name2color_.put("violet", new java.awt.Color(0xEE, 0x82, 0xEE));
		name2color_.put("palegoldenrod", new java.awt.Color(0xEE, 0xE8, 0xAA));
		name2color_.put("lightcoral", new java.awt.Color(0xF0, 0x80, 0x80));
		name2color_.put("khaki", new java.awt.Color(0xF0, 0xE6, 0x8C));
		name2color_.put("aliceblue", new java.awt.Color(0xF0, 0xF8, 0xFF));
		name2color_.put("honeydew", new java.awt.Color(0xF0, 0xFF, 0xF0));
		name2color_.put("azure", new java.awt.Color(0xF0, 0xFF, 0xFF));
		name2color_.put("sandybrown", new java.awt.Color(0xF4, 0xA4, 0x60));
		name2color_.put("wheat", new java.awt.Color(0xF5, 0xDE, 0xB3));
		name2color_.put("beige", new java.awt.Color(0xF5, 0xF5, 0xDC));
		name2color_.put("whitesmoke", new java.awt.Color(0xF5, 0xF5, 0xF5));
		name2color_.put("mintcream", new java.awt.Color(0xF5, 0xFF, 0xFA));
		name2color_.put("ghostwhite", new java.awt.Color(0xF8, 0xF8, 0xFF));
		name2color_.put("salmon", new java.awt.Color(0xFA, 0x80, 0x72));
		name2color_.put("antiquewhite", new java.awt.Color(0xFA, 0xEB, 0xD7));
		name2color_.put("linen", new java.awt.Color(0xFA, 0xF0, 0xE6));
		name2color_.put("lightgoldenrodyellow", new java.awt.Color(0xFA, 0xFA, 0xD2));
		name2color_.put("oldlace", new java.awt.Color(0xFD, 0xF5, 0xE6));
		name2color_.put("red", new java.awt.Color(0xFF, 0x00, 0x00));
		name2color_.put("fuchsia", new java.awt.Color(0xFF, 0x00, 0xFF));
		name2color_.put("magenta(Safe 16=fuchsia Hex3)", new java.awt.Color(0xFF, 0x00, 0xFF));
		name2color_.put("deeppink", new java.awt.Color(0xFF, 0x14, 0x93));
		name2color_.put("orangered", new java.awt.Color(0xFF, 0x45, 0x00));
		name2color_.put("tomato", new java.awt.Color(0xFF, 0x63, 0x47));
		name2color_.put("hotpink", new java.awt.Color(0xFF, 0x69, 0xB4));
		name2color_.put("coral", new java.awt.Color(0xFF, 0x7F, 0x50));
		name2color_.put("darkorange", new java.awt.Color(0xFF, 0x8C, 0x00));
		name2color_.put("lightsalmon", new java.awt.Color(0xFF, 0xA0, 0x7A));
		name2color_.put("orange", new java.awt.Color(0xFF, 0xA5, 0x00));
		name2color_.put("lightpink", new java.awt.Color(0xFF, 0xB6, 0xC1));
		name2color_.put("pink", new java.awt.Color(0xFF, 0xC0, 0xCB));
		name2color_.put("gold", new java.awt.Color(0xFF, 0xD7, 0x00));
		name2color_.put("peachpuff", new java.awt.Color(0xFF, 0xDA, 0xB9));
		name2color_.put("navajowhite", new java.awt.Color(0xFF, 0xDE, 0xAD));
		name2color_.put("moccasin", new java.awt.Color(0xFF, 0xE4, 0xB5));
		name2color_.put("bisque", new java.awt.Color(0xFF, 0xE4, 0xC4));
		name2color_.put("mistyrose", new java.awt.Color(0xFF, 0xE4, 0xE1));
		name2color_.put("blanchedalmond", new java.awt.Color(0xFF, 0xEB, 0xCD));
		name2color_.put("papayawhip", new java.awt.Color(0xFF, 0xEF, 0xD5));
		name2color_.put("lavenderblush", new java.awt.Color(0xFF, 0xF0, 0xF5));
		name2color_.put("seashell", new java.awt.Color(0xFF, 0xF5, 0xEE));
		name2color_.put("cornsilk", new java.awt.Color(0xFF, 0xF8, 0xDC));
		name2color_.put("lemonchiffon", new java.awt.Color(0xFF, 0xFA, 0xCD));
		name2color_.put("floralwhite", new java.awt.Color(0xFF, 0xFA, 0xF0));
		name2color_.put("snow", new java.awt.Color(0xFF, 0xFA, 0xFA));
		name2color_.put("yellow", new java.awt.Color(0xFF, 0xFF, 0x00));
		name2color_.put("lightyellow", new java.awt.Color(0xFF, 0xFF, 0xE0));
		name2color_.put("ivory", new java.awt.Color(0xFF, 0xFF, 0xF0));
		name2color_.put("white", java.awt.Color.WHITE);
		name2color_.put("none", null);
		name2color_.put("currentColor", java.awt.Color.BLACK);
	}

	private Paint color_;
	private float opacity_;

	public Color(SVGConverter svg, String color, Double opacity)
	{
		if (color != null)
		{
			color = color.trim();
			if (color.startsWith("#"))
			{
				if (color.length() < 5)
				{
					char r = color.charAt(1);
					char g = color.charAt(2);
					char b = color.charAt(3);
					color = new StringBuilder(7).append('#')
												.append(r)
												.append(r)
												.append(g)
												.append(g)
												.append(b)
												.append(b)
												.toString();
				}

				try
				{
					color_ = java.awt.Color.decode(color);
				}
				catch (NumberFormatException ne)
				{
					color_ = java.awt.Color.BLACK;
				}
			}
			else
			{
				String[] ref = ElementWrapper.urlRef(color);
				if (ref != null)
				{
					color_ = svg.getPaint(ref[0]);
					if (color_ == null)
					{
						// Use fallback if reference doesn't exists.
						color_ = new Color(svg, ref[1], opacity).color_;
						return;
					}
				}
				else
				{
					String colorLC = color.toLowerCase();
					color_ = name2color_.getOrDefault(colorLC, null);
				}
			}
		}
		else
		{
			color_ = null;
		}
		opacity_ = 1f;
		if (opacity != null)
		{
			double o = opacity;
			if (o < 0)
				o = 0;
			else if (o > 1f)
				o = 1f;
			if (o != 1f)
			{
				if (color_ instanceof java.awt.Color)
				{
					// Opacity can be coded inside the color, no need to set it explicitly.
					java.awt.Color c = (java.awt.Color) color_;
					color_ = new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (o * 255 + 0.5));
				}
				else
				{
					opacity_ = (float) o;
				}
			}
		}
	}

	public Paint getColor()
	{
		return color_;
	}

	public float getOpacity()
	{
		return opacity_;
	}
}
