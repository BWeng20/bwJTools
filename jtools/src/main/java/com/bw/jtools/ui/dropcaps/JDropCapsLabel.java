/*
 * (c) copyright Bernd Wengenroth
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
package com.bw.jtools.ui.dropcaps;

import com.bw.jtools.Log;
import com.bw.jtools.ui.icon.IconTool;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Objects;

/**
 * A label that shows the first letter of a paragraph as drop cap, also called initial.
 */
public class JDropCapsLabel extends JComponent
{
	private Font dropCapFont_ = new Font("Castellar", Font.PLAIN, 	12);
	private Paint dropCapPaint_ = null;
	private Composite dropCapComposite_= null;
	private Color dropCapColor_ = new Color( 120, 25, 25);
	private Paint textPaint_= Color.BLACK;
	private int preferredWith_ = 400;
	private Insets capInsets_ = new Insets(4,0,4,5);
	private String text_;

	private String initialImage_Set_;
	private String initialImage_Key_;
	private int initialImage_Width_ = 32;
	private int initialImage_Height_ = 32;
	private boolean initialImage_colorize_ = false;
	private boolean initialImage_CacheGlobal_ = true;
	private boolean justified_ = true;

	/** Global initial cache for initials that can be addressed by unique parameters. */
	private static final HashMap<String, HashMap<Character, BufferedImage>> initialImage_GlobalCache =
			new HashMap<String, HashMap<Character, BufferedImage>>();

	/**
	 * Global initial cache for initials that can not be addressed via key.
	 * This is the case if the initial images are colorized with a complex paint. */
	private final HashMap<Character, BufferedImage> initialImage_LocalCache_ =
			new HashMap<Character, BufferedImage>();

	private String initialImage_LocalCache_Set = null;
	private int    initialImage_LocalCache_Width = -1;
	private int    initialImage_LocalCache_Height = -1;
	private Paint  initialImage_LocalCache_Paint = null;
	private Composite  initialImage_LocalCache_Composite = null;

	protected void calculateInitialImageCaching() {

		if ( initialImage_Set_ == null )
		{
			initialImage_Key_ = null;
			initialImage_CacheGlobal_ = true;
		}
		else
		{
			Color c = getDropCapColor();
			Paint p = getDropCapPaint();
			if ( initialImage_colorize_ && (c != null || p != null ) )
			{
				if ( p == null || p instanceof Color)
				{
					Color kc = (p == null) ? c : (Color) p;
					// Image depends on set, size and colorize rgb, but can be cached globally.
					initialImage_Key_ = initialImage_Set_ + ":" + initialImage_Width_ + ":" + initialImage_Height_ + ":" + kc.getRGB();
					initialImage_CacheGlobal_ = true;
				}
				else
				{
					// Image can only be cached locally. No key needed
					initialImage_Key_ = null;
					initialImage_CacheGlobal_ = false;

					if (!(Objects.equals( initialImage_Set_, initialImage_LocalCache_Set)
							&& initialImage_LocalCache_Width == initialImage_Width_
							&& initialImage_LocalCache_Height == initialImage_Height_
						 	&& p.equals(initialImage_LocalCache_Paint)
							&& Objects.equals( initialImage_LocalCache_Composite, dropCapComposite_) ))
					{
						initialImage_LocalCache_Set = initialImage_Set_;
						initialImage_LocalCache_Width = initialImage_Width_;
						initialImage_LocalCache_Height =initialImage_Height_;
						initialImage_LocalCache_Paint = p;
						initialImage_LocalCache_Composite = dropCapComposite_;
						if ( Log.isDebugEnabled() )
						{
							if ( !initialImage_LocalCache_.isEmpty())
								Log.debug("Clearing local initial cache. ");
						}
						initialImage_LocalCache_.clear();
					}
				}
			}
			else
			{
				// Image only depends on set and size
				initialImage_Key_ = initialImage_Set_ +":"+ initialImage_Width_ +":"+ initialImage_Height_;
				initialImage_CacheGlobal_ = true;
			}
		}
	}

	protected BufferedImage getLetterImage(char letter)
	{
		final Character lc = letter = Character.toUpperCase(letter);
		HashMap<Character, BufferedImage> images;
		if (initialImage_CacheGlobal_)
		{
			synchronized (initialImage_GlobalCache)
			{
				images = initialImage_GlobalCache.computeIfAbsent(initialImage_Key_, k -> new HashMap<Character, BufferedImage>());
			}
		}
		else
			images = initialImage_LocalCache_;

		BufferedImage image;
		synchronized (images) {
			image = images.get(lc);
			if ( image == null && !images.containsKey(lc))
			{
				try
				{
					Log.debug("Loading Letter Image "+letter );

					image = IconTool.getImage(JDropCapsLabel.class, initialImage_Set_ + letter+".png", initialImage_Width_, initialImage_Height_);

					if (initialImage_colorize_)
					{
						Color c = getDropCapColor();
						if ( c != null )
						{
							image = IconTool.colorizeImage(image, c );
						}
					}
					Paint dcp = getDropCapPaint();
					if ( dcp != null && image != null)
					{
						BufferedImage copy = IconTool.createImage(image.getWidth(),image.getHeight(),image.getTransparency());
						Graphics2D g2 = copy.createGraphics();
						g2.drawImage(image, 0,0, null);
						if ( dropCapComposite_ != null )
							g2.setComposite(dropCapComposite_);
						g2.setPaint(dcp);
						g2.fillRect(0,0, image.getWidth(),image.getHeight());
						g2.dispose();
						image = copy;
					}

				} catch (IOException e)
				{
					images.put(lc, null);
				}
				images.put(lc, image);
			}
		}
		return image;
	}


	/**
	 * Creates a new Drop-Cap-Label.
	 * @param text The text to show.
	 */
	public JDropCapsLabel( String text ) {
		text_ = text;
	}

	/**
	 * Sets the initial image set to use for drop caps.
	 * @param initialSet The set of initials to use. The string has to be the name of a folder in the app resource, following the rules for resource-paths.<br>
	 *                   If 'name' has no leading /, the resulting folder will be com/bw/jtools/ui/dropcaps/NAME in the classpath.<br>
	 *                   If 'name' has leading /, the path is a absolute resource-path.
	 * @param width The width of the drop chars. The images will be scaled to this width. -1 for original size.
	 * @param height The height of the drop chars. The images will be scaled to this height.  -1 for original size.
	 * @param colorize If true the image is colorized with the drop cap paint.
	 */
	public void setInitialSet( String initialSet, int width, int height, boolean colorize )
	{
		if ( initialSet != null )
		{
			initialImage_colorize_ = colorize;
			initialImage_Set_ = initialSet +"/";
		}
		else
		{
			initialImage_Set_ = null;
		}
		initialImage_Width_ = width;
		initialImage_Height_ = height;

		calculateInitialImageCaching();
		revalidate();
		repaint();
	}

	/**
	 * Sets the text justified (german: Blocksatz).
	 * Default is true.
	 */
	public void setJustified( boolean justified)
	{
		if ( justified != justified_)
		{
			justified_ = justified;
			repaint();
		}
	}

	/**
	 * Sets the color of the drop-caps.<br>
	 * If null the foreground-paint is used.
	 * @param cp The paint or null
	 */
	public void setDropCapPaint( Paint cp, Composite comp )
	{
		dropCapPaint_ = cp;
		dropCapComposite_ = comp;
		if ( dropCapPaint_ != null && dropCapComposite_ == null )
			dropCapComposite_ = AlphaComposite.Dst;
		calculateInitialImageCaching();
		repaint();
	}

	/**
	 * Gets the current drop cap paint that is in effect.
	 * @return the drop cap paint.
	 */
	public Paint getDropCapPaint() {
		return dropCapPaint_ == null ? getForegroundPaint() : dropCapPaint_;
	}

	/**
	 * Sets the drop cap color.
	 */
	public void setDropCapColor(Color color) {
		if (! Objects.equals(color, dropCapColor_))
		{
			dropCapColor_ = color;
			calculateInitialImageCaching();
			repaint();
		}
	}

	/**
	 * Gets the current drop cap color that is in effect.
	 * @return the drop cap color.
	 */
	public Color getDropCapColor() {
		return dropCapColor_ == null ? getForeground() : dropCapColor_;
	}


	/**
	 * Sets the foreground paint.<br>
	 * If null, the foreground color is used.
	 * @param paint The paint or null.
	 */
	public void setForegroundPaint(Paint paint)
	{
		if ( !Objects.equals( getForegroundPaint(), paint) )
		{
			textPaint_ = paint;
			// For case the the foreground is used to colorize the initial-image:
			calculateInitialImageCaching();
			repaint();
		}
	}

	/**
	 * Gets the foreground paint.
	 * If no paint is set, the foreground color is returned.
	 */
	public Paint getForegroundPaint()
	{
		return textPaint_ == null ? getForeground() : textPaint_;
	}


	@Override
	protected void paintComponent(Graphics g)
	{
		long start;
		if ( Log.isDebugEnabled() )
			start = System.currentTimeMillis();
		else
			start = 0;

		Graphics2D g2 = (Graphics2D)g.create();
		try
		{
			if (isOpaque())
			{
				g2.setPaint(getBackground());
				g2.fillRect(0, 0, getWidth(), getHeight());
			}

			final Insets i = getInsets();
			float x0 = i.left;
			float y0 = i.top;

			Font font = getFont();

			AffineTransform at1 = AffineTransform.getScaleInstance(5d, 5d);

			FontRenderContext frc = g2.getFontRenderContext();

			int rw;
			int rh;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			BufferedImage img;
			if ( initialImage_Set_ != null )
				img = getLetterImage(text_.charAt(0));
			else
				img = null;

			if ( img != null )
			{
				Paint dcp = getDropCapPaint();

				int ix0 = (int)(x0 + capInsets_.left);
				int iy0 = (int) y0 + capInsets_.top;

				g2.drawImage(img, ix0, iy0, null);

				rw = initialImage_Width_ + capInsets_.left + capInsets_.right;
				rh = initialImage_Height_ + capInsets_.top;

			}
			else
			{
				Font dcf = dropCapFont_ == null ? font : dropCapFont_;
				Shape shape = new TextLayout(text_.substring(0, 1), dcf, frc).getOutline(at1);
				Rectangle r = shape.getBounds();
				rw = r.width + capInsets_.left + capInsets_.right;
				rh = r.height + capInsets_.top;

				AffineTransform at2 = AffineTransform.getTranslateInstance(x0 + capInsets_.left, y0 + rh);
				Shape s2 = at2.createTransformedShape(shape);

				g2.setPaint(getDropCapPaint());
				if ( dropCapComposite_ != null ) {
					Composite c = g2.getComposite();
					g2.setComposite(dropCapComposite_);
					g2.fill(s2);
					g2.setComposite( c );
				}
				else
				{
					g2.fill(s2);
				}
			}


			rh += capInsets_.bottom;

			float x = x0 + rw;
			float y = y0;
			int w0 = getWidth() - i.left - i.right;
			int w = w0 - rw;

			AttributedString as = new AttributedString(text_.substring(1));
			as.addAttribute(TextAttribute.FONT, font);
			AttributedCharacterIterator aci = as.getIterator();

			LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

			g2.setPaint( getForegroundPaint() );

			int lineEndIdx;
			boolean justify;
			final int endIdx = aci.getEndIndex();

			while (lbm.getPosition() < endIdx)
			{
				lineEndIdx = lbm.nextOffset(w);
				int crIdx = text_.indexOf('\n', lbm.getPosition()+1);
				if ( crIdx != -1 && crIdx < lineEndIdx )
				{
					justify = false;
					lineEndIdx = crIdx;
				}
				else
				{
					justify = justified_;
				}

				TextLayout tl = lbm.nextLayout(w, lineEndIdx, false);

				if ( justify && lbm.getPosition() < endIdx )
					tl = tl.getJustifiedLayout(w);

				tl.draw(g2, x, y + tl.getAscent());
				y += tl.getDescent() + tl.getLeading() + tl.getAscent();
				if (y0 + rh < y)
				{
					x = x0;
					w = w0;
				}
			}
		}
		finally
		{
			g2.dispose();
			if ( Log.isDebugEnabled() )
			{
				long end = System.currentTimeMillis();
				Log.debug("paint took " + (end - start) + "ms");
			}
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		Font font = getFont();

		Font dcf = dropCapFont_ == null ? font : dropCapFont_;

		FontRenderContext frc = new FontRenderContext( dcf.getTransform(), false, false);
		int rw;
		int rh;

		if ( initialImage_Set_ != null )
		{
			rw = initialImage_Width_ + capInsets_.left + capInsets_.right;
			rh = initialImage_Height_ + capInsets_.top;
		}
		else
		{
			Shape shape = new TextLayout(text_.substring(0, 1), dcf, frc).getOutline(null);
			Rectangle r = shape.getBounds();
			rw = r.width+capInsets_.left+capInsets_.right;
			rh = r.height+capInsets_.top;
		}

		float y = 0;

		int fw = preferredWith_;
		int w = fw  - rw;

		AttributedString as = new AttributedString(text_.substring(1));
		as.addAttribute(TextAttribute.FONT, font);
		AttributedCharacterIterator aci = as.getIterator();

		int lineEndIdx;
		final int endIdx = aci.getEndIndex();

		LineBreakMeasurer lbm = new LineBreakMeasurer(aci, BreakIterator.getLineInstance(), frc);
		while(lbm.getPosition() < endIdx) {

			lineEndIdx = lbm.nextOffset(w);
			int crIdx = text_.indexOf('\n', lbm.getPosition()+1);
			if ( crIdx != -1 && crIdx < lineEndIdx )
			{
				lineEndIdx = crIdx;
			}
			TextLayout tl = lbm.nextLayout(w, lineEndIdx, false);
			y += tl.getDescent() + tl.getLeading() + tl.getAscent();
			if(rh < y) {
				w = fw;
			}
		}
		Insets i = getInsets();
		return new Dimension(fw + i.left + i.right, (int)Math.ceil(y+i.top+i.bottom));
	}


}
