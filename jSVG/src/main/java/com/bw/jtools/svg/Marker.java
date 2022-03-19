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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static com.bw.jtools.svg.ElementWrapper.isNotEmpty;

public class Marker
{
	Rectangle2D.Double viewBox_;

	/**
	 * Reference point, mapped to marker position.
	 * Using viewport coordinates.
	 */
	public Length refX_;
	public Length refY_;

	/**
	 * Size of markers in
	 */
	public double markerWidth_ = 3;
	public double markerHeight_ = 3;

	/** If auto is active, orientation is reverse for marker-start. */
	public boolean autoReverse_;

	/** If angle is null, auto is active*/
	public Double angle_;

	public MarkerUnit unit_;

	public List<ElementInfo> shapes_ = new ArrayList<>();

	public Marker( ElementWrapper w )
	{
		viewBox_ = w.getViewBox();
		if ( viewBox_ == null ) viewBox_ = new Rectangle2D.Double(0,0,1,1);

		refX_ = toRefLength( w.attr("refX") );
		refY_ = toRefLength( w.attr("refY") );

		Length marker = w.toLength("markerWidth");
		if ( marker != null ) markerWidth_ = marker.toPixel(null);
		marker = w.toLength("markerHeight");
		if ( marker != null ) markerHeight_ = marker.toPixel(null);

		String orient = w.attr("orient");
		if ( "auto".equalsIgnoreCase(orient))
		{
			autoReverse_ = false;
			angle_ = null;
		}
		else if ( "auto-start-reverse".equalsIgnoreCase(orient))
		{
			autoReverse_ = true;
			angle_ = null;
		}
		else
		{
			Length orientL = w.parseLength(orient);
			angle_ = orientL == null ? 0 : orientL.toPixel(null);
		}
		MarkerUnit unit = MarkerUnit.fromString( w.attr("markerUnits") );
		if ( unit != null )
			unit_ = unit;
	}

	private Length toRefLength( String value )
	{
		if ( isNotEmpty(value))
		{
			Length l;
			if ( "left".equalsIgnoreCase(value) || "top".equalsIgnoreCase(value) )
				l = new Length(0, LengthUnit.percent );
			else if ( "center".equalsIgnoreCase(value) )
				l = new Length(50, LengthUnit.percent );
			else if ( "right".equalsIgnoreCase(value) || "bottom".equalsIgnoreCase(value))
				l = new Length(100, LengthUnit.percent );
			else
				l = ElementWrapper.parseLength(value);
			return l;
		}
		return null;
	}



}
