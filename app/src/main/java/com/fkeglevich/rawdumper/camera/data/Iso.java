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

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 21/09/17.
 */

public class Iso implements Displayable
{
    public static final Iso AUTO = new Iso(0);

    public static Iso getFromExifDirectory(Directory directory) throws MetadataException
    {
        return create(directory.getInt(ExifIFD0Directory.TAG_ISO_EQUIVALENT));
    }

    /**
     * Decodes a simple float-encoded ISO value
     *
     * @param value     The encoded ISO
     * @param baseIso   The sensor's base ISO value
     * @return          The ISO value
     */
    public static Iso decodeFloatIso(float value, int baseIso)
    {
        return create(Math.round(baseIso * value));
    }

    public static Iso create(int isoValue)
    {
        if (isoValue <= 0)
            throw new RuntimeException("Invalid ISO value!");

        return new Iso(isoValue);
    }

    private final int numericValue;

    private Iso(int numericValue)
    {
        this.numericValue = numericValue;
    }

    public int getNumericValue()
    {
        if (equals(AUTO))
            throw new RuntimeException("Auto ISO doesn't contain a numeric value!");

        return numericValue;
    }

    @Override
    public String displayValue()
    {
        if (equals(AUTO))
            return "AUTO";
        else
            return "" + getNumericValue();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Iso iso = (Iso) o;
        return numericValue == iso.numericValue;

    }

    @Override
    public int hashCode()
    {
        return numericValue;
    }
}
