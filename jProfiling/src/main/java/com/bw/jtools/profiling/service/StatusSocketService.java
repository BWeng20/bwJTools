package com.bw.jtools.profiling.service;

import com.bw.jtools.Log;
import com.bw.jtools.io.ServiceRunner;
import com.bw.jtools.persistence.MissingPropertyException;
import com.bw.jtools.persistence.StorageBase;
import com.bw.jtools.profiling.ClassProfilingInformation;
import com.bw.jtools.profiling.MethodProfilingInformation;
import com.bw.jtools.profiling.callgraph.AbstractCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.JSONCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.Options;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

public class StatusSocketService
{
	/**
	 * Runnable to executed in the log thread.
	 */
	static final class Service implements ServiceRunner.Service
	{
		int port;
		ServerSocket sock = null;

		// Format options
		boolean pretty = false;
		boolean fullPackageNames = false;

		NumberFormat nf = NumberFormat.getInstance();

		@Override
		public String getName() {
			return "Status";
		}

		@Override
		public boolean work()
		{
			try
			{
				if ( sock == null ) {
					sock = new ServerSocket(port);
				}
			} catch (IOException e)
			{
				Log.error("Status Service failed to bind to port "+port );
				return false;
			}

			try
			{
				Socket s= sock.accept();
				OutputStream os= s.getOutputStream();

				final List<MethodProfilingInformation> topMethods = AbstractCallGraphRenderer.filterTopLevelCalls(ClassProfilingInformation.getClassInformation());
				final String json = new JSONCallGraphRenderer(nf,
						fullPackageNames ? Options.ADD_CLASSNAMES : Options.NONE,
						Options.HIGHLIGHT_CRITICAL, Options.ADD_MIN_MAX,
						pretty ? Options.PRETTY : Options.NONE)
						.render(topMethods, ClassProfilingInformation.getProfilingStartTime(), Calendar.getInstance());

				os.write(json.getBytes());
				os.flush();
				s.close();
			} catch (IOException ex)
			{
				Log.error("Status Service connection error" , ex );
			}
			return true;
		}
	}

	private static ServiceRunner serviceRunner;
	private static Service service_;

	/**
	 * Starts Status service or update current options.
	 *
	 * @param config Map of configuration options. See class comments
	 */
	public static synchronized void start(StorageBase config)
	{
		try
		{
			if (serviceRunner == null)
			{
				service_ = new Service();
				serviceRunner = new ServiceRunner( service_ );
			}
			service_.port = config.getInt("port");

			// Optional arguments
			service_.nf.setMaximumFractionDigits(config.getInt("fractionDigits", service_.nf.getMaximumFractionDigits()));
			service_.pretty =(config.getBoolean("json.pretty", service_.pretty ));
			service_.fullPackageNames =(config.getBoolean("json.packageNames", service_.fullPackageNames ));

			config.setInt("delay", 0);
			serviceRunner.configureAndStart(config);

		} catch ( MissingPropertyException e )
		{
			Log.info("Status Service not started due to missing configuration value for "+e.getKey());
		}
	}

	/**
	 * Stop log service.
	 */
	public static synchronized void stop()
	{
		if ( serviceRunner != null) {
			serviceRunner.stop();
		}
	}

}
