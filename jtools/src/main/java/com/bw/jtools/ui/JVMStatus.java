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

package com.bw.jtools.ui;

import com.bw.jtools.HumanNumbers;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.LinkedList;

/**
 * JComponent to show the current JVM status.
 */
public class JVMStatus extends JComponent
{
	private int updateIntervalMS_;
	private int graphWidth_ = 100;
	private int graphGap_ = 5;
	private int historySize_ = 30;

	private History.Value value2Show_ = History.Value.USED;
	private History.Value backValue2Show_ = History.Value.TOTAL;

	protected LinkedList<History> history_ = new LinkedList<History>();

	private Color valueColor_ = new Color(100,155,255);
	private Color backValueColor_ = new Color(200,200,200);
	private Color graphBackground_ = new Color(200,230,180);

	private Timer updateTimer_;

	protected static class History
	{
		public enum Value
		{
			FREE,
			USED,
			TOTAL
		}

		public long getValue( Value v )
		{
			switch (v)
			{
				case FREE:
					return freeMem_;
				case TOTAL:
					return totalMem_;
				default:
					return usedMem_;
			}
		}

		public long freeMem_;
		public long usedMem_;
		public long totalMem_;

		public History()
		{
			Runtime rt = Runtime.getRuntime();
			freeMem_ = rt.freeMemory();
			totalMem_ = rt.totalMemory();
			usedMem_ = totalMem_ -freeMem_;
		}

	}

	/**
	 * Create a new JVM-status component that will update the shown values
	 * each "updateIntervalMS" milliseconds.<br>
	 * The update is triggered if the component is "showing". See {@link #isShowing()}.
	 * @param updateIntervalMS The update interval in milliseconds
	 */
	public JVMStatus( int updateIntervalMS )
	{
		updateIntervalMS_ = updateIntervalMS;
		updateUI();

		addHierarchyListener(e -> {
			if ( isShowing() != (updateTimer_ != null)) {
				if ( updateTimer_ == null )
				{
					updateTimer_ = new Timer(updateIntervalMS_, ae -> updateInformation() );
					updateTimer_.start();
				}
				else
				{
					updateTimer_.stop();
					clearHistory();
					updateTimer_ = null;
				}
			}
		});
	}

	/**
	 * Resets the history.
	 */
	public void clearHistory()
	{
		history_.clear();
	}

	protected void updateInformation()
	{
		while ( history_.size() >= historySize_ )
			history_.remove(0);

		history_.add(new History());
		repaint();
	}


	@Override
	public Dimension getPreferredSize()
	{
		Font font = getFont();
		FontMetrics fm = getFontMetrics(font);
		return new Dimension(graphWidth_+graphGap_+fm.charWidth('X')*8,fm.getHeight());
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		int h = getHeight()-1;
		int x0 = 0;
		int y0 = 0;

		Graphics2D g2d = (Graphics2D)g.create();
		try
		{
			Border b = getBorder();
			if (b != null)
			{
				Insets i = b.getBorderInsets(this);
				x0 = i.left;
				y0 = i.top;
				h -= i.top + i.bottom;
			}

			g2d.setBackground(graphBackground_);
			g2d.clearRect(x0, y0, graphWidth_, h);

			if (!history_.isEmpty())
			{
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Font font = getFont();
				FontMetrics fm = getFontMetrics(font);
				int baseY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

				History current = history_.get(history_.size() - 1);

				// Get max value
				long max = 0;
				for (History hi : history_)
				{
					long v = hi.getValue(backValue2Show_);
					if (v > max) max = v;
					v = hi.getValue(value2Show_);
					if (v > max) max = v;
				}
				final double factor = ((double) h) / max;

				double xInc = graphWidth_ / ((float) historySize_-1);
				double x = x0;
				double y;
				double ymax = y0 + max * factor;

				Path2D.Double backp = new Path2D.Double();
				backp.moveTo(x, ymax);

				Path2D.Double p = new Path2D.Double();
				p.moveTo(x, ymax);
				for (History hi : history_)
				{
					y = y0 + (max - hi.getValue(backValue2Show_)) * factor;
					backp.lineTo(x, y);
					y = y0 + (max - hi.getValue(value2Show_)) * factor;
					p.lineTo(x, y);
					x += xInc;
				}
				x -= xInc;
				backp.lineTo(x, ymax);
				backp.closePath();

				p.lineTo(x, ymax);
				p.closePath();

				g2d.setColor(backValueColor_);
				g2d.fill(backp);
				g2d.setColor(valueColor_);
				g2d.fill(p);
				g2d.drawString(HumanNumbers.getShortSIFormat4Bytes(current.getValue(value2Show_), getLocale()), graphWidth_ + graphGap_, baseY);
			}
		}
		finally
		{
			g2d.dispose();
		}
	}
}
