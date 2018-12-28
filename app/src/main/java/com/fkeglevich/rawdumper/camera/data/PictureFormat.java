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

import java.util.List;

import androidx.annotation.NonNull;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public class PictureFormat implements DataContainer<CaptureSize>
{
    private final FileFormat fileFormat;
    private final List<CaptureSize> availableSizes;

    public PictureFormat(FileFormat fileFormat, List<CaptureSize> availableSizes)
    {
        this.fileFormat = fileFormat;
        this.availableSizes = availableSizes;
    }

    public DataFormat getDataFormat()
    {
        return fileFormat.getDataFormat();
    }

    public FileFormat getFileFormat()
    {
        return fileFormat;
    }

    @Override
    public List<CaptureSize> getAvailableValues()
    {
        return availableSizes;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PictureFormat that = (PictureFormat) o;

        if (fileFormat != that.fileFormat) return false;
        return availableSizes.equals(that.availableSizes);
    }

    @Override
    public int hashCode()
    {
        int result = fileFormat.hashCode();
        result = 31 * result + availableSizes.hashCode();
        return result;
    }

    @Override
    @NonNull
    public String toString()
    {
        return fileFormat.name();
    }
}
