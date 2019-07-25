/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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

import java.util.List;

/**
 * Interface for persistence back-ends.
 */
public interface IStorage
{
    /**
     * Get a string-value from storage.
     * @param key The preference-key.
     * @return The stored value or null.
     */
    public String getString(String key);

    /**
     * Set a string-value in storage.
     * @param key The preference-key.
     * @param value The value.
     */
    public void setString(String key, String value);

    /**
     * Deleted a value from storage.
     * @param key The preference-key.
     */
    public void deleteKey(String key);

    /**
     * Makes all changes persistent.
     */
    public void flush();

    /**
     * Removed all keys and values.
     */
    public void clear();

    /**
     * Gets a list with all keys that share some prefix.
     * @param prefix The common prefix.
     * @return The list of matching preferences.
     */
    public List<String> getKeysWithPrefix(String prefix);

}