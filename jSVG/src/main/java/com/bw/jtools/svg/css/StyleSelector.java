package com.bw.jtools.svg.css;


import com.bw.jtools.svg.ElementWrapper;
import com.bw.jtools.svg.Type;

import java.util.ArrayList;
import java.util.List;

public final class StyleSelector
{
	public List<SelectorRule> rules_ = new ArrayList<>();

	public String getStyle(ElementWrapper e, String attributeName)
	{
		int maxPrecedence = -1;
		SelectorRule bestRule = null;
		for ( SelectorRule rule : rules_ )
		{
			int precedence = matchPrecedence( rule, e );
			if ( precedence > maxPrecedence) {
				bestRule = rule;
				maxPrecedence = precedence;
			}
		}
		return  ( bestRule != null ) ? bestRule.styles_.get( attributeName ) : null;
	}

	protected int matchPrecedence( SelectorRule rule, ElementWrapper e)
	{
		for( Selector s : rule.selectors_)
		{
			int precedence = 1;
			while (s != null )
			{
				if ( !match( s, e ))
					break;
				s = s.and;
				// @TODO: make this real! CHeck precedence by type and position
				precedence++;
			}
		}
		return -1;
	}

	protected boolean match( Selector s, ElementWrapper e)
	{
		switch (s.type )
		{
			case CLASS:
				return e.hasClass( s.id );
			case TAG:
				return s.id.equals(e.getTagName());
			case ID:
				return s.id.equals(e.id());
		}
		return false;
	}

}
