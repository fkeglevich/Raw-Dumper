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

package com.fkeglevich.rawdumper.raw.capture;

import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

/**
 * Created by Flávio Keglevich on 14/06/2017.
 * TODO: Add a class header comment!
 */

public class WhiteBalanceInfo
{
    public float[] asShotNeutral = null;

    public WhiteBalanceInfo()
    {   }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_ASSHOTNEUTRAL, asShotNeutral, true);
    }
}
