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

import org.junit.Assert;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GainMapCollectionFactory
{

    @Test
    public void load() throws IOException
    {
        /*BayerGainMapSerializer serializer = new BayerGainMapSerializer();
        try(DataInputStream dis = new DataInputStream(new FileInputStream("/sdcard/shades/t4k37_shading.txt")))
        {
            serializer.
        }*/

        generateGainMapBinary("/sdcard/shades/t4k37_shading.txt");
        generateGainMapBinary("/sdcard/shades/ov5670_shading.txt");
    }

    private void generateGainMapBinary(String fileName) throws IOException
    {
        Map<ShadingIlluminant, BayerGainMap> map;
        try(FileReader reader = new FileReader(fileName))
        {
            GainMapCollectionLoader loader = new GainMapCollectionLoader();
            map = loader.load(reader);
        }
        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName + ".bin")))
        {
            BayerGainMapSerializer serializer = new BayerGainMapSerializer();
            serializer.writeBayerGainMapCollection(map, dos);
        }
        Map<ShadingIlluminant, BayerGainMap> map2;
        try(DataInputStream dis = new DataInputStream(new FileInputStream(fileName + ".bin")))
        {
            BayerGainMapSerializer serializer = new BayerGainMapSerializer();
            map2 = serializer.readBayerGainMapCollection(dis);
        }
        for (ShadingIlluminant key : map.keySet())
        {
            Assert.assertTrue(map.get(key).toString().equals(map2.get(key).toString()));
        }
    }
}