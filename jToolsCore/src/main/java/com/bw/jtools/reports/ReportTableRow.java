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
package com.bw.jtools.reports;

public class ReportTableRow extends ReportElement
{
	public ReportTableRow()
	{
	}

	@Override
	public ReportElement add(ReportElement e) throws IllegalArgumentException
	{
		if (e instanceof ReportTableCell)
		{
			super.add(e);
		}
		else
		{
			super.add(new ReportTableCell().add(e));
		}
		return this;
	}

	@Override
	public ReportElement add(String text)
	{
		return add(new ReportTableCell().add(text));
	}


	@Override
	public void render(ReportRenderer renderer)
	{
		renderer.startRow();
		super.render(renderer);
		renderer.endRow();
	}

}
