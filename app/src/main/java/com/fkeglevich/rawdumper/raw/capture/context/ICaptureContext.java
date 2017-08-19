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

package com.fkeglevich.rawdumper.raw.capture.context;

import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;

/**
 * A capture context represents an entity that can fill the gaps in the information
 * contained by a capture info object. The concept of capture context is important because it
 * is what determines which information is already available in the capture info object.
 *
 * Example of capture contexts:
 *  - The raw image is represented by a single i3av4 file
 *  - The raw image is represented by a byte array and a jpeg picture with valid Exif
 *  - The raw image is represented by just a bunch of seemly random bytes without a header
 *
 * Since many devices have different raw capabilities and different workarounds are needed,
 * the software has to be as generic as possible.
 *
 * Created by Flávio Keglevich on 24/07/2017.
 */

public interface ICaptureContext
{
    /**
     * Fills the missing required fields in the capture info object, so it can be
     * used to generate valid and complete raw files.
     *
     * @param captureInfo   A CaptureInfo object
     */
    void fillCaptureInfo(CaptureInfo captureInfo);

    /**
     * Gets whether this capture context can fill the required capture info.
     *
     * @param captureInfo   A CaptureInfo object
     * @return  True if it can, false otherwise.
     */
    boolean canFillCaptureInfo(CaptureInfo captureInfo);
}
