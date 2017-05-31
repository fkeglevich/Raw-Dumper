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

package com.fkeglevich.rawdumper.dng2;

import com.fkeglevich.rawdumper.raw.ImageOrientation;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.util.GregorianCalendar;

/**
 * Created by Flávio Keglevich on 16/04/2017.
 * TODO: Add a class header comment!
 */

public class ImageInfo
{
    public String make;
    public String model;
    public String software;
    public String uniqueCameraModel;
    public String originalRawFilename;
    public ImageOrientation orientation;
    public GregorianCalendar dateTime;

    public ImageInfo()
    {   }

    public void setTiffFields(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_MAKE,                   make);
        tiffWriter.setField(TiffTag.TIFFTAG_MODEL,                  model);
        tiffWriter.setField(TiffTag.TIFFTAG_SOFTWARE,               software);
        tiffWriter.setField(TiffTag.TIFFTAG_UNIQUECAMERAMODEL,      uniqueCameraModel);
        tiffWriter.setField(TiffTag.TIFFTAG_ORIGINALRAWFILENAME,    originalRawFilename);
        tiffWriter.setField(TiffTag.TIFFTAG_ORIENTATION,            orientation.getExifCode());
        tiffWriter.setField(TiffTag.TIFFTAG_DATETIME,               DataFormatter.formatCalendar(dateTime));
    }
}
