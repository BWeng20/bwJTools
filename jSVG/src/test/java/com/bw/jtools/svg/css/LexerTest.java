package com.bw.jtools.svg.css;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest
{

	@Test
	void nextToken() throws IOException
	{
		Lexer lx = new Lexer(new StringReader(
				"{#q500-500 --+3.00e-22symb:::{\n" +
				"  color: \"red \";\n" +
				"  width: 500px;border: 1px solid black;\n" +
						"background:url('a-()[]sdf');"+
				"}"), true);

		LexerSymbol t;
		int c =0;
		while ( (t = lx.nextSymbol()).type_ != LexerSymbolType.EOF )
		{
			c++;
			System.out.println(" '"+t+"'");
		}
		assertEquals( 36, c);
		assertEquals( LexerSymbolType.EOF, t.type_);
		assertEquals( "", t.value_);


	}
}