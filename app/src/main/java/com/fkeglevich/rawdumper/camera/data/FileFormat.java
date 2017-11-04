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

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/11/17.
 */

public enum FileFormat
{
    JPEG(DataFormat.JPEG, ".jpeg"),
    DNG(DataFormat.RAW, ".dng"),
    PNG(DataFormat.YUV, ".png"),
    WEBP(DataFormat.YUV, ".webp");

    private final DataFormat dataFormat;
    private final String extension;

    FileFormat(DataFormat dataFormat, String extension)
    {
        this.dataFormat = dataFormat;
        this.extension = extension;
    }

    public DataFormat getDataFormat()
    {
        return dataFormat;
    }

    public String getExtension()
    {
        return extension;
    }
}
