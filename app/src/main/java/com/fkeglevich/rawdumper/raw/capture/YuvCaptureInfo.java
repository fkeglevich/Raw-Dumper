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

package com.fkeglevich.rawdumper.raw.capture;

import com.fkeglevich.rawdumper.camera.data.FileFormat;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 16/05/18.
 */
public class YuvCaptureInfo
{
    public byte[] yuvBuffer         = null;
    public int[] bitmapBuffer       = null;
    public int width                = 0;
    public int height               = 0;
    public FileFormat fileFormat    = null;
    public String filename          = null;

    public boolean isValid()
    {
        return yuvBuffer        != null
                && bitmapBuffer != null
                && width        > 0
                && height       > 0
                && fileFormat   != null
                && filename     != null;
    }
}
