package com.bw.jtools.profiling.servlet;

import com.bw.jtools.profiling.ClassProfilingInformation;
import com.bw.jtools.profiling.MethodProfilingInformation;
import com.bw.jtools.profiling.callgraph.AbstractCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.Options;
import com.bw.jtools.profiling.callgraph.ReportGraphRenderer;
import com.bw.jtools.reports.ReportDocument;
import com.bw.jtools.reports.ReportParagraph;
import com.bw.jtools.reports.ReportText;
import com.bw.jtools.reports.html.HtmlRenderer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Servlet for access to profiling information.
 */
@WebServlet("/jprofiling")
public class ProfilingServlet extends javax.servlet.http.HttpServlet
{

	/**
	 * Generated Serialisation Id.
	 */
	private static final long serialVersionUID = -5541599162640512947L;

	/**
	 * NumberFormat used for time output.
	 */
	protected static NumberFormat nf;

	public ProfilingServlet()
	{
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(5);
		nf.setRoundingMode(RoundingMode.HALF_UP);
		nf.setGroupingUsed(false);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		final String cmd = request.getParameter("cmd");

		// TODO Get Arguments
		ReportDocument doc = new ReportDocument("bw jProfiling");

		try
		{
			
			if ("graph".equalsIgnoreCase(cmd))
			{
				graph(doc);
			} else
			{
				status(doc);
			}
			

		} catch (Exception e)
		{
			doc.add(new ReportText("Exception: " + e.getMessage()));
		}

		try
		{
			HtmlRenderer html = new HtmlRenderer();
			html.enableCollapsiblesLists( true );
			doc.render(html);

			response.setContentType("text/html;charset=UTF-8");
			PrintWriter printWriter = response.getWriter();
			printWriter.write(html.toString());
			printWriter.flush();
		} catch (IOException e)
		{// NOPMD
		}

	}

	/** Appends list of profiling info. */
	private void graph(ReportDocument doc)
	{
		// Render top-level call graphs

		doc.add( new ReportText("Call Graph").setBold() );
		ReportParagraph p = new ReportParagraph();
		
		List<MethodProfilingInformation> topMethods = AbstractCallGraphRenderer
		        .filterTopLevelCalls(ClassProfilingInformation.getClassInformation());
		
		ReportGraphRenderer html = new ReportGraphRenderer(p, nf,  Options.ADD_CLASSNAMES, Options.HIGHLIGHT_CRITICAL, Options.ADD_MIN_MAX );
		html.render(topMethods, ClassProfilingInformation.getProfilingStartTime(), Calendar.getInstance());

		doc.add( p );

	}

	/**
	 * Appends info about methods from one class.
	 * 
	 * @param doc
	 */
	protected void methods(ReportDocument doc, ClassProfilingInformation pi)
	{
		doc.add( "methods" );

	}

	/** Appends status info. */
	protected void status(ReportDocument doc)
	{
		doc.add( "Status" );
	}
}
