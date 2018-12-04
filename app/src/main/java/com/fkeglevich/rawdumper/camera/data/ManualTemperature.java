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

import androidx.annotation.NonNull;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class ManualTemperature implements Comparable<ManualTemperature>, ParameterValue, Displayable
{
    private static final int INVALID_NUMERIC_VALUE = 0;

    public static final ManualTemperature DISABLED = new ManualTemperature(INVALID_NUMERIC_VALUE);

    public static ManualTemperature create(int numericValue)
    {
        if (numericValue <= INVALID_NUMERIC_VALUE)
            throw new IllegalArgumentException();

        return new ManualTemperature(numericValue);
    }

    private final int numericValue;

    public static ManualTemperature parse(String value)
    {
        int numeric = value != null ? Integer.parseInt(value) : 0;
        if (numeric == 0)
            return ManualTemperature.DISABLED;

        return ManualTemperature.create(numeric);
    }

    private ManualTemperature(int numericValue)
    {
        this.numericValue = numericValue;
    }

    public int getNumericValue()
    {
        if (equals(DISABLED))
            throw new RuntimeException("Disabled manual temperature doesn't have a numeric value!");

        return numericValue;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManualTemperature that = (ManualTemperature) o;

        return numericValue == that.numericValue;
    }

    @Override
    public int hashCode()
    {
        return numericValue;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(@NonNull ManualTemperature another)
    {
        int myNumeric = equals(DISABLED) ? Integer.MIN_VALUE : getNumericValue();
        int anotherNumeric = another.equals(DISABLED) ? Integer.MIN_VALUE : another.getNumericValue();
        return myNumeric - anotherNumeric;
    }

    @Override
    public String getParameterValue()
    {
        return equals(DISABLED) ? "0" : String.valueOf(getNumericValue());
    }

    @Override
    public String displayValue()
    {
        return equals(DISABLED) ? "AUTO" : (getNumericValue() + "K");
    }
}
