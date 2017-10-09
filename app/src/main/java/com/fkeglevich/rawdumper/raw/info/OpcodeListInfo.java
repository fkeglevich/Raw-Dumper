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

package com.fkeglevich.rawdumper.raw.info;

import android.support.annotation.Keep;

import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;
import com.fkeglevich.rawdumper.util.AssetUtil;

import java.io.IOException;

/**
 * Represents a set of opcode lists used to apply basic operations
 * on the raw images (like basic lens correction, for example).
 *
 * Created by Flávio Keglevich on 14/06/2017.
 */

@Keep
@SuppressWarnings("unused")
public class OpcodeListInfo
{
    private String opcodeList1File;
    private String opcodeList2File;
    private String opcodeList3File;

    private transient byte[] opcodeList1Cache;
    private transient byte[] opcodeList2Cache;
    private transient byte[] opcodeList3Cache;

    private Double temperature;

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        opcodeList1Cache = updateOpcodeListCache(opcodeList1Cache, opcodeList1File);
        opcodeList2Cache = updateOpcodeListCache(opcodeList2Cache, opcodeList2File);
        opcodeList3Cache = updateOpcodeListCache(opcodeList3Cache, opcodeList3File);

        safeWriteField(tiffWriter, TiffTag.TIFFTAG_OPCODELIST1, opcodeList1Cache, true);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_OPCODELIST2, opcodeList2Cache, true);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_OPCODELIST3, opcodeList3Cache, true);
    }

    private void safeWriteField(TiffWriter writer, int tag, byte[] data, boolean writeLength)
    {
        if (data != null)
            writer.setField(tag, data, writeLength);
    }

    private byte[] updateOpcodeListCache(byte[] cache, String resourceNam)
    {
        if (cache != null)
            return cache;
        else
            return loadOpcodeListFromRawResource(resourceNam);
    }

    private byte[] loadOpcodeListFromRawResource(String resourceName)
    {
        if (resourceName == null)
            return null;

        byte[] result;
        try
        {
            result = AssetUtil.getAssetBytes(resourceName + ".bin");
        }
        catch (IOException ioe)
        {   return null;    }

        return result;
    }
}
