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
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.ExifInfo;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.raw.gain.GainMapOpcodeStacker;

import java.io.IOException;

public class DngWriter
{
    public void write(CaptureInfo captureInfo, RawImageData imageData) throws IOException
    {
        DngNegative negative = new DngNegative();
        try
        {
            ExifInfo exifInfo = new ExifInfo();
            exifInfo.getExifDataFromCapture(captureInfo);

            writeMetadata(captureInfo, exifInfo, negative);

            exifInfo.writeInfoTo(negative);

            // Start writing image
            RawImageSize imageSize = imageData.getSize();
            PerfInfo.start("BufferTime");
            byte[] buffer = new byte[imageSize.getPaddedWidth() * imageSize.getPaddedHeight() * imageSize.getBytesPerPixel()];


            int paddedHeight = imageSize.getPaddedHeight();
            int widthBytes = imageSize.getPaddedWidth() * imageSize.getBytesPerPixel();

            for (int row = 0; row < paddedHeight; row++)
                imageData.copyValidRowToBuffer(captureInfo.shouldInvertRows() ? (paddedHeight - 1 - row): row, buffer, widthBytes * row);

            PerfInfo.end("BufferTime");

            PerfInfo.start("SaveAndCompress");
            negative.writeImageToFile(captureInfo.destinationRawFilename, imageSize.getPaddedWidth(), imageSize.getPaddedHeight(), buffer, !captureInfo.rawSettings.compressRawFiles);
            PerfInfo.end("SaveAndCompress");
            // End writing image
        }
        finally
        {
            negative.dispose();
        }
    }

    private void writeMetadata(CaptureInfo captureInfo, ExifInfo exifInfo, DngNegative negative)
    {
        Log.i("RawSettings", captureInfo.rawSettings.toString());

        negative.setImageSizeAndOrientation(captureInfo.imageSize.getPaddedWidth(), captureInfo.imageSize.getPaddedHeight(), captureInfo.rawSettings.getOrientationCode(captureInfo));

        captureInfo.camera.getSensor().writeInfoTo(negative, exifInfo, captureInfo.shouldInvertRows());
        captureInfo.camera.writeInfoTo(negative);
        captureInfo.writeInfoTo(negative);

        captureInfo.camera.getColor().writeInfoTo(negative, captureInfo);
        captureInfo.camera.getNoise().writeInfo(negative);
        captureInfo.whiteBalanceInfo.writeInfoTo(negative);

        if (!DebugFlag.dontUseGainMaps())
        {
            if (captureInfo.camera.getGainMapCollection() != null)
                GainMapOpcodeStacker.write(captureInfo, negative);
            else if (captureInfo.camera.getOpcodes() != null && captureInfo.camera.getOpcodes().length >= 1)
                captureInfo.camera.getOpcodes()[0].writeInfoTo(negative);
        }
    }
}
