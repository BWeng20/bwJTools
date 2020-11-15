package com.bw.jtools.collections;
import jdk.vm.ci.meta.JavaKind;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ClassNameCompressorTest
{
	@Test
	public void getComulativeCompressed()
	{
		String tests[] = {
				"com.bw.jtools",
				"com.bw.jtools.collections",
				"jtools.collections.com.bw.jtools",
				"com.bw",
				"....",
				"co.m.bw",
				"com.bw.jtools.collections",
				"com.bw",
				"",
				"-"
		};

		byte[][] testData = new byte[tests.length][];

		ClassNameCompressor pool = new ClassNameCompressor();
		StringBuilder sb = new StringBuilder();

		int orgCount = 0;
		int compCount= 0;

		for (int i = 0; i < tests.length; ++i)
		{
			orgCount += tests[i].getBytes().length;
			testData[i] = pool.getCompressed(tests[i]);
			System.out.print("" + i + ": <"+tests[i]+"> ");
			sb.setLength(0);
			for (int x = 0; x < testData[i].length; ++x)
			{
				char c = (char)( 0xff & testData[i][x] );
				sb.append( Character.isAlphabetic(c) || c == '.' ? c : '_' );
				System.out.print(String.format("%02X", 0xff & testData[i][x]));

			}
			System.out.print( " "+sb.toString()+ " " );
			System.out.println(" ("+tests[i].getBytes(StandardCharsets.UTF_8).length+" -> "+testData[i].length+" bytes)");
			compCount += testData[i].length;

		}

		System.out.println("Bytes "+orgCount+" -> "+compCount);

		boolean error = false;
		pool.reset();
		for (int i = 0; i < tests.length; ++i)
		{
			String r = pool.getUncompressed(testData[i]);
			if (tests[i].compareTo(r) == 0)
			{
				System.out.println("" + i + ": ok");
			} else
			{
				error = true;
				System.out.println("" + i + ": error \n"
						+ "<"+tests[i]+"> len="+ tests[i].length() + " <>  <" + r+"> len="+ r.length() );
			}
		}

		assertEquals(error, false);
	}
}