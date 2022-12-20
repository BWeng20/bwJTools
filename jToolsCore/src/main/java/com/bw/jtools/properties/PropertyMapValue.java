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
package com.bw.jtools.properties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convenience wrapper for a property with map content.
 */
public class PropertyMapValue<K, V> extends PropertyValue<Map<K, V>>
{
    public Class<K> getKeyClass()
    {
        return keyClass_;
    }

    public Class<V> getMapValueClass()
    {
        return mapValueClass_;
    }

    private final Class<K> keyClass_;
    private final Class<V> mapValueClass_;

    /**
     * Creates a map value.
     *
     * @param key Key of the property.
     */
    public PropertyMapValue(String key, Class<K> keyClass, Class<V> valueClass)
    {

        super(key, (Class<? extends Map<K, V>>) (Class<?>) LinkedHashMap.class);
        keyClass_ = keyClass;
        mapValueClass_ = valueClass;
        setValue(new LinkedHashMap<>());
    }

    public void put(K key, V value)
    {
        getValue().put(key, value);
    }

    public void putAll(Map<K, V> m)
    {
        getValue().putAll(m);
    }


    public V get(K key)
    {
        return getValue().get(key);
    }

    /**
     * Gets the value as string.
     */
    public String toString()
    {
        return String.valueOf(getValue());
    }

}
