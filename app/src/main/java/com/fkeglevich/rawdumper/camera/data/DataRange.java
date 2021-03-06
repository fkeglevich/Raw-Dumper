/*
 * Copyright 2018, Flávio Keglevich
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

package com.fkeglevich.rawdumper.camera.data;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class DataRange<T extends Comparable<T>>
{
    private final T lower;
    private final T upper;
    private final T disabledValue;

    DataRange(T lower, T upper, T disabledValue)
    {
        this.lower = lower;
        this.upper = upper;
        this.disabledValue = disabledValue;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public boolean contains(T value)
    {
        if (value.equals(disabledValue)) return true;

        return value.compareTo(getLower()) >= 0 && value.compareTo(getUpper()) <= 0;
    }

    public T getLower()
    {
        return lower;
    }

    public T getUpper()
    {
        return upper;
    }
}
