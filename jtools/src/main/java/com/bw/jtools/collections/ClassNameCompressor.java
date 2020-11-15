package com.bw.jtools.collections;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * A simple Class-Name compressor for streaming binary class-related data
 */
public class ClassNameCompressor extends StringPool
{
	private ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(100);
	private StringBuilder outSb = new StringBuilder(100);
	private final char compressSeparatorchar = '.';
	private final int stringTag = 0xFE;
	private final int intTag = 0xFF;
	private static final byte[] emptyData = new byte[0];

	/**
	 * Get the comulative compressed representation of the string.</br>
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
				if (str == null) {
					return null;
				} if ( str.isEmpty() )
				{
					return emptyData;
				} else
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
						byte sbuff[] = str.getBytes(StandardCharsets.UTF_8);
						if ( sbuff[0] == stringTag || sbuff[0] == intTag )
							outBuffer.write(stringTag);
						outBuffer.write( sbuff );
					} else
					{
						if (prefix.string.length() < str.length())
						{
							outBuffer.write(stringTag);
							outBuffer.write(str.substring(prefix.string.length()+1).getBytes(StandardCharsets.UTF_8));
							outBuffer.write(0);
						} else
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
			} catch (IOException e)
			{
			}
			return outBuffer.toByteArray();
		}
	}

	/**
	 * Get the string for a comulative compressed representation.</br>
	 *
	 * @param data Data Paclkage.
	 * @return The re-constructred string.
	 */
	public String getUncompressed(byte[] data ) {
		if (data == null) return null;
		return getUncompressed( data, 0, data.length );
	}


	/**
	 * Get the string for a comulative compressed representation.</br>
	 *
	 * @param data Data Paclkage.
	 * @param offset The offset to start with.
	 * @param len The number of bytes to use.
	 * @return The re-constructred string.
	 */
	public String getUncompressed(byte[] data, int offset, int len )
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

			int t = 0xFF & data[pos];
			if (t != intTag)
			{
				if ( t == stringTag) ++pos;
				int start = pos;
				while (pos < len && data[pos] != 0)
				{
					++pos;
				}
				str = new String(data, start, pos-start, StandardCharsets.UTF_8);
			} else {
				++pos;
			}
			if ( pos<len )
			{
				int idC = 0;
				while (pos < len)
				{
					idC = (idC << 8) | data[pos++];
				}
				outSb.append(stringIds.get(idC).string);
				if ( str != null )
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
