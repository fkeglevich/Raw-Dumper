/*
 * Copyright 2019, Flávio Keglevich
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

package com.fkeglevich.rawdumper.raw.gain.filter;

import com.fkeglevich.rawdumper.dng.opcode.GainMapOpcode;
import com.fkeglevich.rawdumper.dng.opcode.Opcode;
import com.fkeglevich.rawdumper.dng.opcode.OpcodeListWriter;
import com.fkeglevich.rawdumper.dng.writer.DngNegative;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.gain.BayerGainMap;
import com.fkeglevich.rawdumper.raw.gain.GainMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AnalogPinkGainMapFilter
{
    private static BayerGainMap createGainMap()
    {
        GainMap filter = new GainMap(2, 2);

        for (float[] row : filter.values)
            Arrays.fill(row, 2);

        BayerGainMap result = new BayerGainMap(filter.numColumns, filter.numRows);
        result.red.add(filter);
        result.blue.add(filter);
        result.greenRed.add(filter);
        result.greenBlue.add(filter);

        return result;
    }

    public static void applyToNegative(DngNegative negative, RawImageSize imageSize)
    {
        Opcode opcode = new GainMapOpcode(imageSize, createGainMap());
        List<Opcode> opcodeList = Collections.singletonList(opcode);
        byte[] opcodeListBytes = OpcodeListWriter.toByteArray(opcodeList);
        negative.setOpcodeList1(opcodeListBytes);
    }
}
