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

package com.fkeglevich.rawdumper.util;

import java.util.NoSuchElementException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 13/10/17.
 */

public class Nullable<T>
{
    public static <T> Nullable<T> of(T value)
    {
        return new Nullable<>(value);
    }

    private final T value;

    private Nullable(T value)
    {
        this.value = value;
    }

    public boolean isPresent()
    {
        return value != null;
    }

    public T get()
    {
        if (!isPresent())
            throw new NoSuchElementException();

        return value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nullable<?> nullable = (Nullable<?>) o;
        return value != null ? value.equals(nullable.value) : nullable.value == null;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }
}