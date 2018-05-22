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

package com.fkeglevich.rawdumper.raw.gain;

import com.fkeglevich.rawdumper.raw.data.CalibrationIlluminant;

/**
 * Created by flavio on 24/03/18.
 */

public enum ShadingIlluminant
{
    A   ( 0, "A",    CalibrationIlluminant.STANDARD_LIGHT_A),
    CW  ( 1, "CW",   CalibrationIlluminant.COOL_WHITE_FLUORESCENT),
    TL84( 2, "TL84", CalibrationIlluminant.FLUORESCENT),
    D50 ( 3, "D50",  CalibrationIlluminant.D50),
    D65 ( 4, "D65",  CalibrationIlluminant.D65),
    H   ( 5, "H",    CalibrationIlluminant.UNKNOWN);
    //U30 (-1, "U30",  CalibrationIlluminant.OTHER_LIGHT_SOURCE),
    //Cali(-1, "Cali", CalibrationIlluminant.UNKNOWN);

    public final int mknIndex;
    public final String token;
    public final CalibrationIlluminant illuminant;

    ShadingIlluminant(int mknIndex, String token, CalibrationIlluminant illuminant)
    {
        this.mknIndex = mknIndex;
        this.token = token;
        this.illuminant = illuminant;
    }

}
