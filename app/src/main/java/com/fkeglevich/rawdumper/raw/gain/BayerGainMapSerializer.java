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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 21/05/18.
 */
public class BayerGainMapSerializer
{
    public void writeBayerGainMapCollection(Map<ShadingIlluminant, BayerGainMap> map, DataOutputStream dos) throws IOException
    {
        BayerGainMap[] bayerGainMaps = new BayerGainMap[ShadingIlluminant.values().length];
        for (ShadingIlluminant key : map.keySet())
            bayerGainMaps[key.ordinal()] = map.get(key);

        for (int i = 0; i < bayerGainMaps.length; i++)
        {
            BayerGainMap gainMap = bayerGainMaps[i];
            if (gainMap == null)
                dos.writeInt(-1);
            else
            {
                dos.writeInt(i);
                writeBayerGainMap(gainMap, dos);
            }
        }
    }

    public Map<ShadingIlluminant, BayerGainMap> readBayerGainMapCollection(DataInputStream dis) throws IOException
    {
        Map<ShadingIlluminant, BayerGainMap> map = new EnumMap<>(ShadingIlluminant.class);

        int count = ShadingIlluminant.values().length;
        for (int i = 0; i < count; i++)
        {
            int illuminantIndex = dis.readInt();
            if (illuminantIndex != -1)
                map.put(ShadingIlluminant.values()[illuminantIndex], readBayerGainMap(dis));
        }
        return map;
    }

    public void writeBayerGainMap(BayerGainMap bayerGainMap, DataOutputStream dos) throws IOException
    {
        dos.writeInt(bayerGainMap.numColumns);
        dos.writeInt(bayerGainMap.numRows);
        writeGainMapData(bayerGainMap.red, dos);
        writeGainMapData(bayerGainMap.blue, dos);
        writeGainMapData(bayerGainMap.greenRed, dos);
        writeGainMapData(bayerGainMap.greenBlue, dos);
    }

    public BayerGainMap readBayerGainMap(DataInputStream dis) throws IOException
    {
        int numColumns = dis.readInt();
        int numRows = dis.readInt();
        BayerGainMap bayerGainMap = new BayerGainMap(numColumns, numRows);
        readGainMapData(bayerGainMap.red, dis);
        readGainMapData(bayerGainMap.blue, dis);
        readGainMapData(bayerGainMap.greenRed, dis);
        readGainMapData(bayerGainMap.greenBlue, dis);
        return bayerGainMap;
    }

    private void writeGainMapData(GainMap gainMap, DataOutputStream dos) throws IOException
    {
        for (int row = 0; row < gainMap.numRows; row++)
            for (int col = 0; col < gainMap.numColumns; col++)
                dos.writeFloat(gainMap.values[row][col]);
    }

    private void readGainMapData(GainMap gainMap, DataInputStream dis) throws IOException
    {
        for (int row = 0; row < gainMap.numRows; row++)
            for (int col = 0; col < gainMap.numColumns; col++)
                gainMap.values[row][col] = dis.readFloat();
    }
}
