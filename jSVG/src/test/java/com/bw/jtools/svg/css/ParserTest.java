package com.bw.jtools.svg.css;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest
{

	@Test
	void parse()
	{
		CSSParser cssParser = new CSSParser();
		Lexer lx = new Lexer( new StringReader("[xxx=123], .class dev , #someid span { color: red; background: red blue; }"), true);
		StyleSelector ssel = new StyleSelector();
		cssParser.parse(lx,ssel);

		for (SelectorRule r : ssel.rules_)
		{
			for ( Selector s : r.selectors_ )
			{
				System.out.print("Selector");
				while (s != null)
				{
					System.out.print( " "+s.type.name() + "-" + s.id);
					s = s.and;
				}
				System.out.println("");

			}
			System.out.print("-> "+r.styles_);
		}
		// [] (attributes) are currently ignored.
		assertEquals(1, ssel.rules_.size());
		assertEquals(2, ssel.rules_.get(0).selectors_.size());
	}
}