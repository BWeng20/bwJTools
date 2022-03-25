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

/**
 * The main use case for this library is drawing icons and simple graphics that look great even on high resolution screens.<br>
 * It contains a simple SVG Converter, that creates Java2D-shapes from the SVG source.<br>
 * As the svg elements are simply converted to shapes, complex stuff that needs offline-rendering (like blur) can't work.
 * Also a lot of complex use-case will not work as specified. <br>
 * The SVG specification contains a lot of such case with a large amounts of hints how agents should render it correctly.
 * But most svg graphics doesn't use such stuff, so the conversion to Java2D shapes is a efficient way to draw
 * such simple scalable graphics.<br>
 * If you need a feature-complete renderer, use Batik.
 */
package com.bw.jtools;

