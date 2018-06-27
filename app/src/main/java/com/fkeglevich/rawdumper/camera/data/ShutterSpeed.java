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

import android.support.annotation.NonNull;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 22/09/17.
 */

public class ShutterSpeed implements Displayable
{
    public static final ShutterSpeed AUTO = new ShutterSpeed(0);

    @NonNull
    public static ShutterSpeed getFromExifDirectory(Directory directory) throws MetadataException
    {
        return create(directory.getDouble(ExifIFD0Directory.TAG_EXPOSURE_TIME));
    }

    /**
     * Decodes integer microsecond exposure time.
     *
     * @param value The microsecond exposure time
     * @return      A ShutterSpeed object containing the exposure time in seconds
     */
    public static ShutterSpeed decodeMicrosecondExposure(int value)
    {
        return create(value / 1.0E06);
    }

    public static ShutterSpeed create(double exposureInSeconds)
    {
        if (exposureInSeconds <= 0)
            throw new RuntimeException("Invalid exposure value!");

        return new ShutterSpeed(exposureInSeconds);
    }

    private final double exposureInSeconds;

    private ShutterSpeed(double exposureInSeconds)
    {
        this.exposureInSeconds = exposureInSeconds;
    }

    public double getExposureInSeconds()
    {
        if (equals(AUTO))
            throw new RuntimeException("Auto ShutterSpeed doesn't have a fixed exposure time!");

        return exposureInSeconds;
    }

    @Override
    public String displayValue()
    {
        if (equals(AUTO))
            return "AUTO";

        if (getExposureInSeconds() > 0.9999)
            return getExposureInSeconds() + "s";
        else
        {
            long roundedValue = Math.round(1.0 / getExposureInSeconds());
            if (roundedValue >= 10000)
                return "1/" + (roundedValue / 1000) + "K";
            else
                return "1/" + roundedValue;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShutterSpeed that = (ShutterSpeed) o;
        return Double.compare(that.exposureInSeconds, exposureInSeconds) == 0;

    }

    @Override
    public int hashCode()
    {
        long temp = Double.doubleToLongBits(exposureInSeconds);
        return (int) (temp ^ (temp >>> 32));
    }
}
