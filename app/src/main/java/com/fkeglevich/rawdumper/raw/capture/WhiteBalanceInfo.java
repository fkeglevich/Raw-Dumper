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

package com.fkeglevich.rawdumper.raw.capture;

import android.util.Log;

import com.fkeglevich.rawdumper.camera.service.available.WhiteBalanceService;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;
import com.fkeglevich.rawdumper.util.ColorUtil;
import com.fkeglevich.rawdumper.util.MathUtil;

import java.util.Arrays;

/**
 * Created by Flávio Keglevich on 14/06/2017.
 * TODO: Add a class header comment!
 */

public class WhiteBalanceInfo
{
    private static final int DAYLIGHT_TEMPERATURE = 5503;

    public static WhiteBalanceInfo create(ExtraCameraInfo cameraInfo, MakerNoteInfo makerNoteInfo)
    {
        float[] neutralValues = WhiteBalanceService.getInstance().isAvailable() ? WhiteBalanceService.getInstance().getValue() : null;

        if (neutralValues != null)
        {
            Log.i(WhiteBalanceInfo.class.getSimpleName(), "Using neutral values from WB Service: " + Arrays.toString(neutralValues));
            return new WhiteBalanceInfo(neutralValues);
        }
        if (cameraInfo.hasKnownMakernote())
            return createFromMakerNote(makerNoteInfo, cameraInfo.getColor());
        else
            return createFromDaylightTemperature(cameraInfo.getColor());
    }

    private static WhiteBalanceInfo createFromXYCoords(double x, double y, ColorInfo colorInfo)
    {
        float[] neutralValues = MathUtil.doubleArrayToFloat(colorInfo.calculateSimpleAsShotNeutral(x, y));
        return new WhiteBalanceInfo(neutralValues);
    }

    private static WhiteBalanceInfo createFromMakerNote(MakerNoteInfo makerNoteInfo, ColorInfo colorInfo)
    {
        if (makerNoteInfo.wbCoordinatesXY != null)
            return createFromXYCoords(makerNoteInfo.wbCoordinatesXY[0], makerNoteInfo.wbCoordinatesXY[1], colorInfo);
        else
            return createFromDaylightTemperature(colorInfo);
    }

    private static WhiteBalanceInfo createFromDaylightTemperature(ColorInfo colorInfo)
    {
        float[] xy = ColorUtil.getXYFromCCT(DAYLIGHT_TEMPERATURE, colorInfo);
        return createFromXYCoords(xy[0], xy[1], colorInfo);
    }

    private final float[] asShotNeutral;

    private WhiteBalanceInfo(float[] asShotNeutral)
    {
        this.asShotNeutral = asShotNeutral;
    }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        /*

        04-15 14:59:08.701 334-24441/? D/Camera_ISP: @setAicParameter
    @setAicParameter: wb integer_bits=1 gr=32768 r=57976 b=51000 gb=32768

         */

        /*

        04-15 15:00:41.500 334-24572/? D/Camera_AtomAIQ: total_gain: 3.480188  digital gain: 1.000000
    @getManualIso - -1

         */

        //logcat "Camera_ISP:D" "Camera_AtomAIQ:D" "*:S"

        //logcat "Camera_ISP:D" "Camera_AtomAIQ:D" "*:S" -v raw

        //logcat "Camera_ISP:D" "*:S" -v raw
        //@setAicParameter: wb integer_bits=1 gr=32768 r=58899 b=50257 gb=32768

        //| grep -i "@setAicParameter: wb integer_bits"

        //tiffWriter.setField(TiffTag.TIFFTAG_ASSHOTNEUTRAL, asShotNeutral, true);
        //32768 / (2^15)
        //wb integer_bit

        //@setAicParameter: wb integer_bits=1 gr=32768 r=51236 b=42524 gb=32768
        //gr / r
        //gb / b

        /*String wb = MainActivity.WB_MATCH.latestMatch;
        if (wb != null)
        {
            //@setAicParameter: wb integer_bits=1 gr=32768 r=60811 b=50613 gb=32768
            String[] split = wb.replace("@setAicParameter: wb ", "").split(" ");

            String gr = split[1].split("=")[1];
            String r = split[2].split("=")[1];
            String b = split[3].split("=")[1];
            String gb = split[4].split("=")[1];

            float rGain = (float) (Double.parseDouble(gr) / Double.parseDouble(r));
            float bGain = (float) (Double.parseDouble(gb) / Double.parseDouble(b));

            tiffWriter.setField(TiffTag.TIFFTAG_ASSHOTNEUTRAL, new float[] {rGain, 1f, bGain}, true);
        }

        else
            tiffWriter.setField(TiffTag.TIFFTAG_ASSHOTNEUTRAL, new float[] {0,639550316f, 1f, 0,770576616f}, true);*/



        tiffWriter.setField(TiffTag.TIFFTAG_ASSHOTNEUTRAL, asShotNeutral, true);
    }
}
