package com.bw.jtools.ui.profiling;

import com.bw.jtools.profiling.callgraph.JSONCallGraphParser;

import java.util.List;

/**
 * Interface to notify consumer of new call-graphs.
 */
public interface CallGraphConsumer
{
	/**
	 * New call-graphs were detected.
	 * Called always in UI-Thread.
	 * @param g The graph.
	 */
	public void newCallGraphs(List<JSONCallGraphParser.GraphInfo> g);

	/**
	 * Some error has occurred.
	 * Called always in UI-Thread.
	 * @param message The message.
	 */
	public void error(String message);
}
