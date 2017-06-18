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

import android.os.Build;

import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

/**
 * Created by Flávio Keglevich on 11/06/2017.
 * TODO: Add a class header comment!
 */

public class CameraInfo
{
    private static final String FRONT_CAMERA_SUFIX = " (front camera)";
    private static final String REAR_CAMERA_SUFIX = " (rear camera)";

    private int id;
    private String name;

    private SensorInfo sensor;
    private LensInfo lens;
    private ColorInfo color;
    private OpcodeListInfo[] opcodes;

    private boolean isFrontCamera;
    private boolean hasKnownMakernote;
    private boolean retryOnError;

    private CameraInfo()
    {   }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_MODEL, Build.MODEL + (isFrontCamera ? FRONT_CAMERA_SUFIX : REAR_CAMERA_SUFIX));
        tiffWriter.setField(TiffTag.TIFFTAG_UNIQUECAMERAMODEL, name);
    }

    public SensorInfo getSensor()
    {
        return sensor;
    }

    public LensInfo getLens()
    {
        return lens;
    }

    public ColorInfo getColor()
    {
        return color;
    }

    public OpcodeListInfo[] getOpcodes()
    {
        return opcodes;
    }

    public static CameraInfo createT4k37CInfo()
    {
        CameraInfo result = new CameraInfo();
        result.id = 0;
        result.sensor = SensorInfo.createT4K37();
        result.lens = LensInfo.create2AptertureLens();
        result.color = ColorInfo.getToshibaColorInfo();
        result.opcodes = OpcodeListInfo.getOpcodeInfoT4K37();
        result.isFrontCamera = false;
        result.hasKnownMakernote = true;
        result.retryOnError = false;
        result.name = "Asus ZenFone 2 ZE551ML (back camera)";

        return result;
    }

    public static CameraInfo createOV5670CInfo()
    {
        CameraInfo result = new CameraInfo();
        result.id = 1;
        result.sensor = SensorInfo.createOV5670();
        result.lens = LensInfo.create2AptertureLens();
        result.color = ColorInfo.getOVColorInfo();
        result.opcodes = OpcodeListInfo.getOpcodeInfoOV5670();
        result.isFrontCamera = true;
        result.hasKnownMakernote = true;
        result.retryOnError = true;
        result.name = "Asus ZenFone 2 ZE551ML (front camera)";

        return result;
    }
}
