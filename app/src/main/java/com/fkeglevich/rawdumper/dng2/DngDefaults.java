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

import com.fkeglevich.rawdumper.tiff.TiffTag;

/**
 * Contains some basic default tiff tag values used by DngWriter.
 *
 * Created by Flávio Keglevich on 16/04/2017.
 */

class DngDefaults
{
    // Default header tag values
    static final int RAW_SUB_FILE_TYPE       = TiffTag.FILETYPE_FULLIMAGE;
    static final int RAW_PHOTOMETRIC         = TiffTag.PHOTOMETRIC_CFA;
    static final int RAW_SAMPLES_PER_PIXEL   = 1;
    static final int RAW_PLANAR_CONFIG       = TiffTag.PLANARCONFIG_CONTIG;

    static final byte[] DNG_VERSION          = new byte[] {1, 4, 0, 0};
    static final byte[] DNG_BACKWARD_VERSION = new byte[] {1, 3, 0, 0};
}
