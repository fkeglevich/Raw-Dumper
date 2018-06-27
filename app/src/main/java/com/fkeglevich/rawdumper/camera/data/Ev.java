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

package com.fkeglevich.rawdumper.camera.data;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/10/17.
 */

public class Ev implements Displayable
{
    public static final Ev DEFAULT = new Ev(0.0f);
    private static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

    public static Ev getFromExifDirectory(Directory directory) throws MetadataException
    {
        return create((float) directory.getDouble(ExifIFD0Directory.TAG_EXPOSURE_BIAS));
    }

    public static boolean isInvalidEv(float evValue)
    {
        return Float.isNaN(evValue) || Float.isInfinite(evValue);
    }

    public static Ev create(float evValue)
    {
        if (isInvalidEv(evValue))
            throw new IllegalArgumentException("Invalid EV value!");

        Ev result = new Ev(evValue);
        if (result.equals(DEFAULT))
            return DEFAULT;

        return result;
    }

    private final float value;

    private Ev(float value)
    {
        this.value = value;
    }

    public float getValue()
    {
        return value;
    }

    @Override
    public String displayValue()
    {
        return (value > 0 ? "+" : "") + DEFAULT_FORMAT.format(value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ev ev = (Ev) o;
        return Float.compare(ev.value, value) == 0;
    }

    @Override
    public int hashCode()
    {
        return (value != +0.0f ? Float.floatToIntBits(value) : 0);
    }
}
