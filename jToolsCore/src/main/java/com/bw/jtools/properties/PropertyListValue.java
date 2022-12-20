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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Convenience wrapper for a property with list content.
 */
public class PropertyListValue<V> extends PropertyValue<List<PropertyValue<V>>>
{
    public Class<V> getListValueClass()
    {
        return listValueClass_;
    }

    private final Class<V> listValueClass_;

    /**
     * Creates a map value.
     *
     * @param key Key of the property.
     */
    public PropertyListValue(String key, Class<V> valueClass)
    {

        super(key, (Class<? extends List<PropertyValue<V>>>) (Class<?>) List.class);
        listValueClass_ = valueClass;
        setValue(new ArrayList<>());
    }

    public void add(V value)
    {
        PropertyValue<V> prop = new PropertyValue<>(String.valueOf(getValue().size()), getListValueClass());
        prop.setValue(value);
        getValue().add(prop);
    }

    public void add(PropertyValue<V> prop)
    {
        getValue().add(prop);
    }

    public void addAll(Collection<V> m)
    {
        m.forEach(this::add);
    }


    public V get(int index)
    {
        PropertyValue<V> prop = getValue().get(index);
        return (prop == null) ? null : prop.getValue();
    }

    public int size()
    {
        return getValue().size();
    }

    public void forEach(Consumer<V> c)
    {
        getValue().forEach(v -> c.accept(v == null ? null : v.getValue()));
    }

    /**
     * Gets the value as string.
     */
    public String toString()
    {
        return getValue().stream()
                .map(v -> v == null ? "null" : String.valueOf(v.getValue()))
                .collect(Collectors.joining(",", "[", "]"));
    }

}
