package com.bw.jtools.ui.profiling;

import com.bw.jtools.Log;
import com.bw.jtools.io.ServiceRunner;
import com.bw.jtools.profiling.callgraph.JSONCallGraphParser;
import com.bw.jtools.ui.UITool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Background status poll service
 */
class StatusSocketService implements ServiceRunner.Service
{
	private char buffer_[] = new char[2048];
	private List<JSONCallGraphParser.GraphInfo> statusGraphs_ = new ArrayList<JSONCallGraphParser.GraphInfo>(10);
	private boolean functional_ = true;
	private SocketAddress socketAddress_;

	private List<CallGraphConsumer> consumer_ = new ArrayList<>();

	public void addCallGraphConsumer( CallGraphConsumer c )
	{
		consumer_.remove(c);
		consumer_.add(c);
	}

	public void removeCallGraphConsumer( CallGraphConsumer c )
	{
		consumer_.remove(c);
	}

	/**
	 * Creates a new service, bound to the given address.
	 * @param socketAddress The address of the statis socket to fetch from.
	 */
	public StatusSocketService(SocketAddress socketAddress ) {
		socketAddress_ = socketAddress;
	}

	@Override
	public String getName()
	{
		return "Status Fetcher";
	}

	/**
	 * Polls status port for data.
	 */
	@Override
	public boolean work()
	{
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(socketAddress_);

			JSONCallGraphParser parser = new JSONCallGraphParser();
			BufferedReader is = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
			parser.parse(is);

			if (parser.getNumberOfCallGraphs() > 0 ) {
				synchronized ( statusGraphs_ ) {
					statusGraphs_.clear();
					statusGraphs_.addAll(Arrays.asList( parser.getCallGraphs() ));
				}
			}

		} catch ( Exception e)
		{
			final String message = "Failed to read from status address " +socketAddress_+".";
			Log.error(message, e);

			List<CallGraphConsumer> c2call = new ArrayList<>();
			c2call.addAll(consumer_);
			for ( CallGraphConsumer	c : c2call )
			{
				c.error( message );
			}

			functional_ = false;
		}
		finally
		{
			try
			{
				socket.close();
			} catch (Exception e)
			{
				if (functional_)
				{
					final String message = "Failed to close socket " + socketAddress_;
					Log.error(message, e);
				}
			}
			socket = null;
		}

		if ( functional_ )
		{
			UITool.executeInUIThread(() ->
			{
				List<JSONCallGraphParser.GraphInfo> newGraphs = new ArrayList<>();
				synchronized (statusGraphs_)
				{
					newGraphs.addAll(statusGraphs_);
					statusGraphs_.clear();
				}
				List<CallGraphConsumer> c2call = new ArrayList<>();
				c2call.addAll(consumer_);
				for ( CallGraphConsumer	c : c2call )
				{
					c.newCallGraphs( newGraphs );
				}
			});
		}
		return functional_;
	}

}
