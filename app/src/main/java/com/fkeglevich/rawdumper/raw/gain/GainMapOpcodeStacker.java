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

import com.fkeglevich.rawdumper.dng.opcode.GainMapOpcode;
import com.fkeglevich.rawdumper.dng.opcode.OpcodeListWriter;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfo;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.util.Collections;
import java.util.Map;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 21/05/18.
 */
public class GainMapOpcodeStacker
{
    private static final String TAG = "GainMapOpcodeWriter";

    public static void write(ExtraCameraInfo cameraInfo, MakerNoteInfo mknInfo, RawImageSize imageSize, TiffWriter tiffWriter)
    {
        Map<ShadingIlluminant, BayerGainMap> map = cameraInfo.getGainMapCollection();
        double[] illuminantScale = mknInfo.illuminantScale;
        if (map == null || illuminantScale == null) return;

        //PerfInfo.start(TAG);
        BayerGainMap accGainMap = new BayerGainMap(map.get(ShadingIlluminant.A).numColumns, map.get(ShadingIlluminant.A).numRows);
        for (int i = 0; i < illuminantScale.length; i++)
        {
            if (illuminantScale[i] != 0)
            {
                //Log.i(TAG, ShadingIlluminant.values()[i].name());
                BayerGainMap gainMap = map.get(ShadingIlluminant.values()[i]).cloneMap();
                gainMap.multiplyByScalar((float) illuminantScale[i]);
                accGainMap.add(gainMap);
            }
        }
        //Log.i(TAG, accGainMap.red.toString());
        //Log.i(TAG, accGainMap.blue.toString());
        //Log.i(TAG, accGainMap.greenRed.toString());
        //Log.i(TAG, accGainMap.greenBlue.toString());
        //PerfInfo.end("GainMapOpcodeWriter");
        GainMapOpcode opcode = new GainMapOpcode(imageSize, accGainMap);
        OpcodeListWriter.writeOpcodeList3Tag(tiffWriter, Collections.singletonList(opcode));
    }
}
