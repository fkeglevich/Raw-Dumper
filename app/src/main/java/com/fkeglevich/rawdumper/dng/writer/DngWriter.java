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

package com.fkeglevich.rawdumper.dng.writer;

import android.util.Log;

import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.debug.PerfInfo;
import com.fkeglevich.rawdumper.raw.capture.RawCaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.RawSettings;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.gain.GainMapOpcodeStacker;
import com.fkeglevich.rawdumper.raw.gain.filter.AnalogPinkGainMapFilter;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.raw.metadata.ExifMetadata;

import java.io.IOException;

public class DngWriter
{
    public void write(RawCaptureInfo captureInfo) throws IOException
    {
        DngNegative negative = new DngNegative();
        try
        {
            writeMetadata(captureInfo, negative);
            captureInfo.getExifMetadata().writeInfoTo(negative);

            RawImageSize imageSize  = captureInfo.getImageSize();
            String destinationPath  = captureInfo.getDestinationFile().getAbsolutePath();
            RawSettings rawSettings = captureInfo.getRawSettings();

            PerfInfo.start("SaveAndCompress");
            negative.writeImageToFile(destinationPath,
                    imageSize.getPaddedWidth(), imageSize.getPaddedHeight(),
                    imageSize.getBytesPerLine(), captureInfo.shouldInvertRows(),
                    captureInfo.getImageData(), !rawSettings.compressRawFiles,
                    rawSettings.calculateDigest);
            PerfInfo.end();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IOException(e);
        }
        finally
        {
            negative.dispose();
            captureInfo.dispose();
        }
    }

    private void writeMetadata(RawCaptureInfo captureInfo, DngNegative negative)
    {
        RawImageSize rawImageSize  = captureInfo.getImageSize();
        RawSettings rawSettings    = captureInfo.getRawSettings();
        ExtraCameraInfo cameraInfo = captureInfo.getCameraInfo();
        ExifMetadata metadata      = captureInfo.getExifMetadata();

        Log.i("RawSettings", captureInfo.getRawSettings().toString());

        negative.setImageSizeAndOrientation(rawImageSize.getPaddedWidth(),
                rawImageSize.getPaddedHeight(),
                rawSettings.getOrientationCode(cameraInfo));

        cameraInfo.getSensor().writeInfoTo(negative, metadata, captureInfo.shouldInvertRows());
        cameraInfo.writeInfoTo(negative);

        captureInfo.writeInfoTo(negative);

        cameraInfo.getColor().writeInfoTo(negative, captureInfo);
        cameraInfo.getNoise().writeInfo(negative);
        captureInfo.getWhiteBalanceInfo().writeInfoTo(negative);

        if (!DebugFlag.dontUseGainMaps())
        {
            if (cameraInfo.getGainMapCollection() != null)
                GainMapOpcodeStacker.write(captureInfo, negative);
            else if (cameraInfo.getOpcodes() != null && cameraInfo.getOpcodes().length >= 1)
                cameraInfo.getOpcodes()[0].writeInfoTo(negative);

            if (rawSettings.addAnalogFilter)
                AnalogPinkGainMapFilter.applyToNegative(negative, rawImageSize);
        }
    }
}
