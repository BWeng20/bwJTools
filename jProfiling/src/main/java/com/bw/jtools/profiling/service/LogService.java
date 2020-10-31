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

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Profiling Log Service.
 * Executes a thread that dump the profiling information.<br>
 * Options:
 * <table><caption></caption>
 * <tr><td><b>delay</b></td><td>delay in milliseconds between execution.</td></tr>
 * <tr><td style="vertical-align :top;"><b>filePattern</b></td><td>pattern for the file-output-path. <br>
 *                      "{0}" has to be used to specify the file-number if more than
 *                      one file is specified.</td></tr>
 * <tr><td><b>nbOfFiles</b></td><td>Number of files.</td></tr>
 * </table>
 */
public class LogService
{
    /**
     * Runnable to executed in service runner.
     */
	static class Service implements ServiceRunner.Service
	{
		String filePattern;
		int nbOfFiles;
		int fileIndex = 0;

		// Format options
		boolean pretty = false;
		boolean fullPackageNames = false;

		NumberFormat nf = NumberFormat.getInstance();

		@Override
		public String getName()
		{
			return "Log";
		}

		@Override
		public boolean work()
		{
			final List<MethodProfilingInformation> topMethods = AbstractCallGraphRenderer.filterTopLevelCalls(ClassProfilingInformation.getClassInformation());
			final String json = new JSONCallGraphRenderer(nf,
					fullPackageNames ? Options.ADD_CLASSNAMES : Options.NONE,
					Options.HIGHLIGHT_CRITICAL, Options.ADD_MIN_MAX,
					pretty ? Options.PRETTY : Options.NONE)
					.render(topMethods, ClassProfilingInformation.getProfilingStartTime(), Calendar.getInstance());

			final String file = MessageFormat.format(filePattern, fileIndex );
			++fileIndex;
			if ( fileIndex > nbOfFiles ) {
				fileIndex = 1;
			}

			try
			{
				FileWriter w = new FileWriter(file);
				w.write(json);
				w.flush();
				w.close();

				Log.info( "Written Profiling Status to "+file );

				return true;
			} catch (IOException e)
			{
				Log.error( "Failed to write "+file, e );
				return false;
			}

		}
	}

	private static ServiceRunner serviceRunner;
	private static Service service_;

	/**
	 * Starts log service or update current option.
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
			// Mandatory arguments
			service_.filePattern = config.getString("filePattern");
			service_.nbOfFiles = config.getInt("nbOfFiles");

			// Optional arguments
			service_.nf.setMaximumFractionDigits(config.getInt("fractionDigits", service_.nf.getMaximumFractionDigits()));
			service_.pretty =(config.getBoolean("json.pretty", service_.pretty ));
			service_.fullPackageNames =(config.getBoolean("json.packageNames", service_.fullPackageNames ));

			serviceRunner.configureAndStart(config);

		} catch ( MissingPropertyException e )
		{
			Log.info("Log Service not started due to missing configuration value for "+e.getKey());
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
