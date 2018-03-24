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

package com.fkeglevich.rawdumper.raw.mkn;

import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import java.io.UnsupportedEncodingException;

/**
 * Created by flavio on 24/03/18.
 */

public class MakerNoteInfoFactory
{
    private static final String CCM_MKN_FINGERPRINT     = "factory_golden_at_lab";
    private static final String HOYA_MKN_FINGERPRINT    = "HOYA";

    public static MakerNoteInfo create(byte[] mknBytes, ExtraCameraInfo cameraInfo)
    {
        try
        {
            String mknStringBytes = new String(mknBytes, "ISO-8859-1");

            if (mknStringBytes.contains(CCM_MKN_FINGERPRINT))
                return new CcmMakerNoteBuilder().build(mknStringBytes, cameraInfo);
            else if (mknStringBytes.contains(HOYA_MKN_FINGERPRINT))
                return new HoyaMakerNoteBuilder().build(mknStringBytes, cameraInfo);
        }
        catch (UnsupportedEncodingException ignored)
        {   }

        return new DummyMakerNoteBuilder().build(mknBytes, cameraInfo);
    }

}
