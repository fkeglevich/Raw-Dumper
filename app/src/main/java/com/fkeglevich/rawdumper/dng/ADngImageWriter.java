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

package com.fkeglevich.rawdumper.dng;

import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.IOException;

/**
 * Represents an abstract DNG image (full size, thumbnail, compressed) writer, decoupled from the
 * DngWriter class.
 *
 * Created by Flávio Keglevich on 17/04/2017.
 */

public abstract class ADngImageWriter
{
    protected abstract void init(TiffWriter tiffWriter, RawImageSize rawImageSize);

    protected abstract void writeImageData(TiffWriter tiffWriter, RawImageData imageData) throws IOException;
}
