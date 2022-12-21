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

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Convenience wrapper for a property with list content.
 */
public class PropertyListValue<V> extends PropertyValue<List<PropertyValue<V>>>
        implements Collection<V>
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

    @Override
    public boolean add(V value)
    {
        PropertyValue<V> prop = new PropertyValue<>(String.valueOf(getValue().size()), getListValueClass());
        prop.setValue(value);
        getValue().add(prop);
        return true;
    }

    @Override
    public boolean remove(Object o)
    {
        return getValue().removeIf(v -> Objects.equals(v.getValue(), o));
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return c.stream().anyMatch(o -> !contains(c));
    }

    @Override
    public boolean addAll(Collection<? extends V> c)
    {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public void clear()
    {
    }

    public void addProperty(PropertyValue<V> prop)
    {
        getValue().add(prop);
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

    @Override
    public boolean isEmpty()
    {
        return size() > 0;
    }

    @Override
    public boolean contains(Object o)
    {
        return getValue().stream().anyMatch(p -> p != null && Objects.equals(p.getValue(), o));
    }

    @Override
    public Iterator<V> iterator()
    {
        return getValue().stream().map(PropertyValue::getValue).iterator();
    }

    @Override
    public Object[] toArray()
    {
        int N = size();
        Object[] r = new Object[N];
        List<PropertyValue<V>> l = getValue();
        for (int i = 0; i < N; ++i)
            r[i] = l.get(i);
        return r;
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        int N = size();
        if (a.length < N)
            a = Arrays.copyOf(a, N);
        List<PropertyValue<V>> l = getValue();
        for (int i = 0; i < N; ++i)
            a[i] = (T) l.get(i);

        return a;
    }

    @Override
    public void forEach(Consumer<? super V> c)
    {
        getValue().forEach(v -> c.accept(v == null ? null : v.getValue()));
    }

    /**
     * Gets the value as string.
     */
    @Override
    public String toString()
    {
        return getValue().stream()
                .map(v -> v == null ? "null" : String.valueOf(v.getValue()))
                .collect(Collectors.joining(",", "[", "]"));
    }

}
