package com.bw.jtools.collections;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * A simple Class-Name compressor for streaming binary class-related data.
 * Basic idea is to reference already written package-prefixes instead of writing
 * common prefixes again and again.<br>
 * For this the output of the compressor is a binary format that mix ids as reference
 * to previous written prefixes and the byte (utf8) format of the strings.<br>
 * Main usage is streaming/storage of serialized java data which need to include class-names.
 * E.g. see {@link com.bw.jtools.io.data.DataInput}/{@link com.bw.jtools.io.data.DataOutput}
 */
public class ClassNameCompressor extends StringPool
{
	private ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(100);
	private StringBuilder outSb = new StringBuilder(100);
	private final char compressSeparatorchar = '.';
	private final byte stringTag = (byte) 0xFE;
	private final byte intTag = (byte) 0xFF;
	private static final byte[] emptyData = new byte[0];

	public ClassNameCompressor()
	{
		reset();
	}

	@Override
	public void reset()
	{
		synchronized (stringpool)
		{
			super.reset();
			// Predefined prefixes.
			// If changed, previous written data may no longer be valid
			addString("com.bw.jtools");
			addString("java.lang");
			addString("java.util");
		}
	}


	/**
	 * Get the cumulative compressed representation of the string.<br>
	 *
	 * @param str The string.
	 * @return The Id.
	 */
	public byte[] getCompressed(String str)
	{
		synchronized (stringpool)
		{
			try
			{
				outBuffer.reset();
				if (str == null)
				{
					return null;
				}
				if (str.isEmpty())
				{
					return emptyData;
				}
				else
				{
					StringId prefix = stringpool.get(str);
					if (prefix == null)
					{
						addString(str);
					}
					int idx;
					String prefixStr = str;
					while (prefix == null && prefixStr.length() > 3 &&
							((idx = prefixStr.lastIndexOf(compressSeparatorchar)) > 2))
					{
						prefixStr = prefixStr.substring(0, idx);
						prefix = stringpool.get(prefixStr);
						if (prefix == null)
						{
							addString(prefixStr);
						}
					}
					if (prefix == null)
					{
						byte[] sbuff = str.getBytes(StandardCharsets.UTF_8);
						if (sbuff[0] == stringTag || sbuff[0] == intTag)
							outBuffer.write(stringTag);
						outBuffer.write(sbuff);
					}
					else
					{
						if (prefix.string.length() < str.length())
						{
							outBuffer.write(stringTag);
							outBuffer.write(str.substring(prefix.string.length() + 1)
											   .getBytes(StandardCharsets.UTF_8));
							outBuffer.write(0);
						}
						else
						{
							outBuffer.write(intTag);
						}
						int idC = prefix.id;
						while (idC != 0)
						{
							outBuffer.write(0xFF & idC);
							idC = idC >>> 8;
						}
					}
				}
			}
			catch (IOException e)
			{
			}
			return outBuffer.toByteArray();
		}
	}

	/**
	 * Get the string for a cumulative compressed representation.<br>
	 *
	 * @param data Data Package.
	 * @return The re-constructed string.
	 */
	public String getUncompressed(byte[] data)
	{
		if (data == null) return null;
		return getUncompressed(data, 0, data.length);
	}


	/**
	 * Get the string for a cumulative compressed representation.<br>
	 *
	 * @param data   Data Package.
	 * @param offset The offset to start with.
	 * @param len    The number of bytes to use.
	 * @return The re-constructed string.
	 */
	public String getUncompressed(byte[] data, int offset, int len)
	{
		synchronized (stringpool)
		{
			if (data == null) return null;
			if (data.length == 0 || offset >= data.length || len <= 0) return "";

			outBuffer.reset();
			outSb.setLength(0);

			int pos = offset;
			len += offset;
			String str = null;

			byte t = data[pos];
			if (t != intTag)
			{
				if (t == stringTag) ++pos;
				int start = pos;
				while (pos < len && data[pos] != 0)
				{
					++pos;
				}
				str = new String(data, start, pos - start, StandardCharsets.UTF_8);
			}
			else
			{
				++pos;
			}
			if (pos < len)
			{
				int idC = 0;
				while (pos < len)
				{
					idC = (idC << 8) | data[pos++];
				}
				outSb.append(stringIds.get(idC).string);
				if (str != null)
				{
					outSb.append(compressSeparatorchar);
					outSb.append(str);
				}
				str = outSb.toString();

			}
			if (t != intTag)
			{
				StringId id = getStringSId(str);
				str = id.string;
				int idx;
				String prefix = str;
				id = null;
				while (id == null && prefix.length() > 3 && ((idx = prefix.lastIndexOf(compressSeparatorchar)) > 2))
				{
					prefix = prefix.substring(0, idx);
					id = stringpool.get(prefix);
					if (id == null)
					{
						addString(prefix);
					}
				}
			}
			return str;
		}
	}

}
