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

package com.fkeglevich.rawdumper.raw.info;

import android.content.Context;
import android.content.res.AssetManager;

import com.fkeglevich.rawdumper.controller.context.ContextManager;
import com.fkeglevich.rawdumper.debug.PerfInfo;
import com.fkeglevich.rawdumper.raw.gain.BayerGainMap;
import com.fkeglevich.rawdumper.raw.gain.BayerGainMapSerializer;
import com.fkeglevich.rawdumper.raw.gain.ShadingIlluminant;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 21/05/18.
 */
class GainMapAssetLoader
{
    private static final String GAIN_MAPS_FOLDER = "gain_maps/";

    static Map<ShadingIlluminant, BayerGainMap> load(String assetName)
    {
        if (assetName == null) return null;
        Context context = ContextManager.getApplicationContext();
        try (DataInputStream dis = getAssetInputStream(assetName, context))
        {
            BayerGainMapSerializer serializer = new BayerGainMapSerializer();
            PerfInfo.start("GainMapAssetLoader");
            Map<ShadingIlluminant, BayerGainMap> map = serializer.readBayerGainMapCollection(dis);
            PerfInfo.end();
            return map;
        }
        catch (IOException ioe)
        {
            return null;
        }
    }

    private static DataInputStream getAssetInputStream(String assetName, Context context) throws IOException
    {
        InputStream assetStream = context.getAssets().open(GAIN_MAPS_FOLDER + assetName, AssetManager.ACCESS_BUFFER);
        return new DataInputStream(new BufferedInputStream(assetStream));
    }
}
