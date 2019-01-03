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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 27/06/18.
 */
public class Aperture implements Displayable
{
    private static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US));

    public static final Aperture AUTO = new Aperture(0);

    public static boolean isInvalidAperture(double fNumber)
    {
        return fNumber < 0.0 || Double.isNaN(fNumber) || Double.isInfinite(fNumber);
    }

    public static Aperture create(double fNumber)
    {
        if (isInvalidAperture(fNumber))
            throw new IllegalArgumentException("Invalid aperture value!");

        return new Aperture(fNumber);
    }

    private final double value;

    private Aperture(double value)
    {
        this.value = value;
    }

    public double getValue()
    {
        return value;
    }

    @Override
    public String displayValue()
    {
        if (equals(AUTO))
            return "";

        return "ƒ/" + DEFAULT_FORMAT.format(value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Aperture aperture = (Aperture) o;

        return Double.compare(aperture.value, value) == 0;
    }

    @Override
    public int hashCode()
    {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }
}
