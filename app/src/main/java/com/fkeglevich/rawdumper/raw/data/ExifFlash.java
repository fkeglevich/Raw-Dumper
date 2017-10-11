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

package com.fkeglevich.rawdumper.raw.data;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * Enumerates the implemented values of the ExifFlash Exif tag
 *
 * Created by Flávio Keglevich on 27/05/2017.
 */

public enum ExifFlash
{
    DID_NOT_FIRE((short)0),
    FIRED((short)1),
    UNKNOWN((short)-1);

    private final short exifValue;

    public static ExifFlash getFlashFromValue(short exifValue)
    {
        for (ExifFlash exifFlash : ExifFlash.values())
            if (exifFlash.exifValue == exifValue)
                return exifFlash;

        return UNKNOWN;
    }

    public static ExifFlash getFromExifDirectory(Directory directory) throws MetadataException
    {
        return getFlashFromValue((short)directory.getInt(ExifIFD0Directory.TAG_FLASH));
    }

    ExifFlash(short exifValue)
    {
        this.exifValue = exifValue;
    }

    public short getExifValue()
    {
        return exifValue;
    }
}
