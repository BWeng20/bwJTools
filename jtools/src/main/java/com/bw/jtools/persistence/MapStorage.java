/*
 * (c) copyright Bernd Wengenroth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A StorageBase implementation that stores all values in some Map instance.
 */
public class MapStorage extends StorageBase
{
	/**
	 * The values-
	 */
	private final Map<String, String> values_;
	/**
	 * The prefix.
	 */
	private String prefix_;

	/**
	 * Creates a new Map-based Storage.
	 *
	 * @param values The settings.
	 */
	public MapStorage(Map<String, String> values)
	{
		super(null);
		if (values == null)
		{
			values = new HashMap<String, String>();
		}
		values_ = values;
		prefix_ = null;
	}

	/**
	 * Get the value map.
	 *
	 * @return The values.
	 */
	public Map<String, String> getMap()
	{
		return values_;
	}

	/**
	 * Sets a prefix internally added to all getter calls
	 *
	 * @param prefix Prefix or null.
	 */
	public void setPrefix(String prefix)
	{
		prefix_ = prefix;
	}

	/**
	 * Translate the key to current scope.
	 *
	 * @param key The key to translate.
	 * @return Scoped key
	 */
	protected String translateKey(String key)
	{
		return key == null ? null : (prefix_ == null ? key : (prefix_ + key));
	}

	@Override
	protected String getString_impl(String key)
	{
		return values_.get(translateKey(key));
	}

	@Override
	public void setString(String key, String value)
	{
		values_.put(translateKey(key), value);
	}

	@Override
	public void deleteKey(String key)
	{
		values_.remove(translateKey(key));
	}

	@Override
	public void flush()
	{
	}

	@Override
	public synchronized void clear()
	{
		values_.clear();
	}

	@Override
	protected Collection<String> getAllKeys()
	{
		return values_.keySet();
	}
}
