package com.bw.jtools.collections;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.rmi.server.ExportException;
import java.util.HashMap;

/**
 * A simple String Pool
 */
public class StringPool
{

	protected int idGenerator = 0;
	protected HashMap<String, StringId> stringpool = new HashMap<>(10069);
	protected HashMap<Integer,StringId> stringIds = new HashMap<>(10069);

	protected final static class StringId
	{
		public final String string;
		public final Integer id;

		StringId(final String s, int id)
		{
			string = s;
			this.id = id;
		}
	}

	/**
	 * Get an unique Id for a string.
	 *
	 * @param str The string.
	 * @return The Id.
	 */
	public Integer getStringId(String str)
	{
		synchronized (stringpool)
		{
			StringId id = getStringSId(str);
			return id.id;
		}
	}

	/**
	 * Get pool string.
	 *
	 * @param str The string.
	 * @return The string.
	 */
	public String getString(String str)
	{
		synchronized (stringpool)
		{
			StringId id = stringpool.get(str);
			if (id == null)
			{
				id = addString(str);
			}
			return id.string;
		}
	}

	protected StringId getStringSId(String str)
	{
		StringId id = stringpool.get(str);
		if (id == null)
		{
			id = addString(str);
		}
		return id;
	}

	protected StringId addString(String str)
	{
		StringId sid = new StringId(str, ++idGenerator);
		stringpool.put(str, sid);
		stringIds.put( sid.id, sid);

		return sid;
	}

	public void reset()
	{
		synchronized (stringpool)
		{
			idGenerator = 0;
			stringpool.clear();
			stringIds.clear();
		}
	}
}
