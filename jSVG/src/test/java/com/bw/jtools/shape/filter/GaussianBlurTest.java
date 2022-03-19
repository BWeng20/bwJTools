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

package com.bw.jtools.shape.filter;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class GaussianBlurTest
{
	@Test
	public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{

		Method initKernel = GaussianBlur.class.getDeclaredMethod("initKernel", float[].class, double.class);
		initKernel.setAccessible(true);
		GaussianBlur gb = new GaussianBlur(FilterBase.SOURCE, "Target", 1, 1);

		for ( int s=0 ; s<3; ++s)
		{
			float[] kernelX = new float[s*4+1];
			initKernel.invoke(gb, kernelX, s);
			double sum = 0;
			for (int i = 0; i < kernelX.length; i++)
			{
				System.out.print(kernelX[i] + " ");
				sum += kernelX[i];
			}
		}
	}
}