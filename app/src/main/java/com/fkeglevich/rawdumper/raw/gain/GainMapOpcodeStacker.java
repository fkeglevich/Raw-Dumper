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

import android.support.annotation.Nullable;

import com.fkeglevich.rawdumper.dng.dngsdk.DngNegative;
import com.fkeglevich.rawdumper.dng.opcode.GainMapOpcode;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;

import java.util.Collections;
import java.util.Map;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 21/05/18.
 */
public class GainMapOpcodeStacker
{
    private static final String TAG = "GainMapOpcodeWriter";

    public static void write(CaptureInfo captureInfo, DngNegative negative)
    {
        GainMapOpcode opcode = generateStackedOpcode(captureInfo);
        if (opcode == null) return;

        negative.setOpcodeList3(Collections.singletonList(opcode));
    }

    @Nullable
    private static GainMapOpcode generateStackedOpcode(CaptureInfo captureInfo)
    {
        Map<ShadingIlluminant, BayerGainMap> map = captureInfo.camera.getGainMapCollection();
        double[] illuminantScale = captureInfo.makerNoteInfo.illuminantScale;
        if (map == null || illuminantScale == null) return null;

        BayerGainMap accGainMap = new BayerGainMap(map.get(ShadingIlluminant.A).numColumns, map.get(ShadingIlluminant.A).numRows);
        for (int i = 0; i < illuminantScale.length; i++)
        {
            if (illuminantScale[i] != 0)
            {
                BayerGainMap gainMap = map.get(ShadingIlluminant.values()[i]).cloneMap();
                gainMap.multiplyByScalar((float) illuminantScale[i]);
                accGainMap.add(gainMap);
            }
        }

        if (captureInfo.rawSettings.keepLensVignetting)
            restoreLensVignetting(accGainMap, map.get(ShadingIlluminant.D65));

        if (captureInfo.shouldInvertRows())
            accGainMap.invertRows();

        GainMapOpcode opcode = new GainMapOpcode(captureInfo.imageSize, accGainMap);
        return opcode;
    }

    private static void restoreLensVignetting(BayerGainMap accGainMap, BayerGainMap D65Map)
    {
        accGainMap.red.dividePointWise(D65Map.greenRed);
        accGainMap.blue.dividePointWise(D65Map.greenRed);
        accGainMap.greenRed.dividePointWise(D65Map.greenRed);
        accGainMap.greenBlue.dividePointWise(D65Map.greenRed);
    }
}
