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

package com.bw.jtools.svg.css;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSSParserTest
{
	@Test
	void parse() throws IOException
	{
		CSSParser parser = new CSSParser();

		String css = "svg, div span { color:red;} .a-b+c .xyz { color: blue }";
		CssStyleSelector selector =  parser.parse(css, "text/css");
		assertEquals(2, selector.rules_.size() );
		assertEquals( 2, selector.rules_.get(0).selectors_.size());
		assertEquals( "svg", selector.rules_.get(0).selectors_.get(0).id_);
		assertEquals( "div", selector.rules_.get(0).selectors_.get(1).id_);
		assertEquals( "span", selector.rules_.get(0).selectors_.get(1).combinate_.id_);
		assertEquals( 1, selector.rules_.get(0).styles_.size());
		assertEquals( SelectorType.TAG, selector.rules_.get(0).selectors_.get(0).type_, "Tag selector was not detected");
		assertEquals( "red", selector.rules_.get(0).styles_.get("color"));

		assertEquals( "blue", selector.rules_.get(1).styles_.get("color"));
		assertEquals( 1, selector.rules_.get(1).selectors_.size());
		assertEquals( "a-b+c", selector.rules_.get(1).selectors_.get(0).id_);
		assertEquals( SelectorType.CLASS, selector.rules_.get(1).selectors_.get(0).type_, "Class selector was not detected");

	}
}