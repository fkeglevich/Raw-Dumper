/*
 * Copyright 2017, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fkeglevich.rawdumper.util.collection;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Created by Flávio Keglevich on 04/09/2017.
 * TODO: Add a class header comment!
 */

public class ImmutableList<E> implements List<E>, RandomAccess, Serializable
{
    private final List<E> delegate;

    public ImmutableList(Collection<E> collection)
    {
        delegate = new ArrayList<>(Collections.unmodifiableCollection(collection));
    }

    public void add(int location, E object)
    {
        delegate.add(location, object);
    }

    public boolean add(E object)
    {
        return delegate.add(object);
    }

    public boolean addAll(int location, @NonNull Collection<? extends E> collection)
    {
        return delegate.addAll(location, collection);
    }

    public boolean addAll(@NonNull Collection<? extends E> collection)
    {
        return delegate.addAll(collection);
    }

    public void clear()
    {
        delegate.clear();
    }

    public boolean contains(Object object)
    {
        return delegate.contains(object);
    }

    public boolean containsAll(@NonNull Collection<?> collection)
    {
        return delegate.containsAll(collection);
    }

    public E get(int location)
    {
        return delegate.get(location);
    }

    public int indexOf(Object object)
    {
        return delegate.indexOf(object);
    }

    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    public Iterator<E> iterator()
    {
        return delegate.iterator();
    }

    public int lastIndexOf(Object object)
    {
        return delegate.lastIndexOf(object);
    }

    public ListIterator<E> listIterator()
    {
        return delegate.listIterator();
    }

    public ListIterator<E> listIterator(int location)
    {
        return delegate.listIterator(location);
    }

    public E remove(int location)
    {
        return delegate.remove(location);
    }

    public boolean remove(Object object)
    {
        return delegate.remove(object);
    }

    public boolean removeAll(@NonNull Collection<?> collection)
    {
        return delegate.removeAll(collection);
    }

    public boolean retainAll(@NonNull Collection<?> collection)
    {
        return delegate.retainAll(collection);
    }

    public E set(int location, E object)
    {
        return delegate.set(location, object);
    }

    public int size()
    {
        return delegate.size();
    }

    public List<E> subList(int start, int end)
    {
        return delegate.subList(start, end);
    }

    public Object[] toArray()
    {
        return delegate.toArray();
    }

    public <T> T[] toArray(@NonNull T[] array)
    {
        return delegate.toArray(array);
    }

    @SuppressWarnings("unchecked")
    public E[] toArray(Class<E> elementClass)
    {
        return delegate.toArray((E[]) Array.newInstance(elementClass, delegate.size()));
    }
}
