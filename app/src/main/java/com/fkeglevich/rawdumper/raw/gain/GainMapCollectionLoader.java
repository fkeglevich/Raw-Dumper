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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.EnumMap;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 21/05/18.
 */
public class GainMapCollectionLoader
{
    public Map<ShadingIlluminant, BayerGainMap> load(Reader dataReader) throws IOException
    {
        try (BufferedReader bufferedReader = new BufferedReader(dataReader))
        {
            int width = readWidthOrHeight(bufferedReader.readLine());
            int height = readWidthOrHeight(bufferedReader.readLine());
            Map<ShadingIlluminant, BayerGainMap> map = initMap(width, height);

            String line;
            GainMap gainMap;
            while (true)
            {
                line = bufferedReader.readLine();
                if (line == null) break;
                gainMap = findGainMap(line, map);
                fillGainMap(width, height, gainMap, bufferedReader);
            }

            return map;
        }
        catch (RuntimeException re)
        {
            throw new IOException("Error parsing file");
        }
    }

    @Nullable
    private GainMap findGainMap(String line, Map<ShadingIlluminant, BayerGainMap> map)
    {
        for (ShadingIlluminant illuminant : ShadingIlluminant.values())
            if (line.startsWith(illuminant.token))
            {
                BayerGainMap bayerGainMap = map.get(illuminant);
                return bayerGainMap.getMapFromToken(line.split("_")[1].trim());
            }
        return null;
    }

    private void fillGainMap(int width, int height, @Nullable GainMap gainMap, BufferedReader reader) throws IOException
    {
        String[] values;
        for (int y = 0; y < height; y++)
        {
            values = reader.readLine().split(" ");
            if (gainMap == null) continue;
            for (int x = 0; x < width; x++)
                gainMap.values[y][x] = Float.parseFloat(values[x].trim());
        }
    }

    private int readWidthOrHeight(String line)
    {
        return Integer.parseInt(line.split("=")[1].trim());
    }

    private EnumMap<ShadingIlluminant, BayerGainMap> initMap(int width, int height)
    {
        EnumMap<ShadingIlluminant, BayerGainMap> map = new EnumMap<>(ShadingIlluminant.class);
        for (ShadingIlluminant illuminant : ShadingIlluminant.values())
            map.put(illuminant, new BayerGainMap(width, height));

        return map;
    }
}
