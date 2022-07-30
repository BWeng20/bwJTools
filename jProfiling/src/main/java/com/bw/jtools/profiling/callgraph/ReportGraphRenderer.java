/*
 * The MIT License
 *
 * Copyright 2020 Bernd Wengenroth.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bw.jtools.profiling.callgraph;

import com.bw.jtools.reports.ReportElement;
import com.bw.jtools.reports.ReportList;
import com.bw.jtools.reports.ReportListElement;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Call graph renderer to create a html output.<br>
 */
public class ReportGraphRenderer  extends AbstractCallGraphRenderer
{
	private ReportElement doc;
	private List<ReportList> nodeStack = new ArrayList<ReportList>();
	
	
	public ReportGraphRenderer( ReportElement doc, NumberFormat nf, Options... options)
	{
		super(nf, options);
		this.doc = doc;
	}

	@Override
	protected void start(CallNode root)
	{
		ReportList li = new ReportList( root.name );
		nodeStack.add( li );
		doc.add( li );
	}

	@Override
	protected void startNode(CallNode node)
	{
		ReportList parent = nodeStack.get(nodeStack.size() - 1);
		ReportListElement li = new ReportListElement();
		parent.add( li );

		li.add( node.name );
        if ( node.calls>0) li.add( "Calls: "+ nf.format( node.calls ) );
        if ( node.details != null && !node.details.isEmpty() )
        {
    		ReportList detailList = new ReportList( "Details" );
            for ( NodeDetail d : node.details)
            {
            	detailList.add( new ReportListElement().add( renderValue( d.value ) ) );
            }
            li.add( detailList );
        }
        if ( node.value != null )
        {
            li.add( "Time: " + renderValue( node.value ) );
            li.add( "Self: " + renderValue( node.getNetMeasurement() ) );
        }

        if ( node.edges != null && !node.edges.isEmpty() ) {
			ReportList nodeList = new ReportList( "Calls ("+node.edges.size()+")" );
			li.add( nodeList );
			nodeStack.add( nodeList );
        }        
	}

	@Override
	protected void endNode(CallNode node)
	{
		nodeStack.remove( nodeStack.size()-1 );
	}

	@Override
	protected void startEdge(CallEdge edge)
	{
		
	}

	@Override
	protected void endEdge(CallEdge edge)
	{
	}

	@Override
	protected void end(CallNode root)
	{
		nodeStack.clear();
	}

}
