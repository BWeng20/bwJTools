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

/**
 * Package contain a serialisation framework for hierarchical data.<br>
 * Main feature of this package is the encapsulation of all parts in the data, so that they
 * can be generically handled. This enables version tolerant storage of data.
 * <br>
 * <ul>
 * <li>All fields owns an id. So order is not important. Unknown fields can be skipped.
 * <li>All fields have type-formation, size and encoding for on field is always known.</li>
 * <li>On top of the base protocol a class-serializer can store class information and can generically
 *     store objects - this functionality is limited to plain objects and will handle only public accessible fields but no
 *     property-like getter/setter./li>
 * <li>The protocol is designed to produce small output, using a platform independent binary representations.</li>
 * </ul>
 */
package com.bw.jtools.io.data;

