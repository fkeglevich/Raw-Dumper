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

package com.fkeglevich.rawdumper.io.async.function;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunction;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.dng.DngWriter;
import com.fkeglevich.rawdumper.dng.dngsdk.DngWriter2;
import com.fkeglevich.rawdumper.dng.tiffwriter.TiffDngWriter;
import com.fkeglevich.rawdumper.dng.tiffwriter.writer.StripImageWriter;
import com.fkeglevich.rawdumper.io.async.IOUtil;
import com.fkeglevich.rawdumper.io.async.exception.SaveFileException;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.data.buffer.ArrayRawImageData;
import com.fkeglevich.rawdumper.raw.data.buffer.FileRawImageData;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.IOException;

/**
 * Created by Flávio Keglevich on 24/08/2017.
 * TODO: Add a class header comment!
 */

public class SaveDngFunction extends ThrowingAsyncFunction<CaptureInfo, Void, MessageException>
{
    @Override
    protected Void call(CaptureInfo captureInfo) throws MessageException
    {
        if (DebugFlag.dontSavePictures()) return null;
        if (!captureInfo.isValid()) throw new IllegalArgumentException("Invalid capture info!");

        /*DngWriter writer = TiffDngWriter.open(captureInfo.destinationRawFilename);
        if (writer != null)
        {
            RawImageData rawImageData = null;
            try
            {
                rawImageData = buildRawImageData(captureInfo);
                writer.write(captureInfo, new StripImageWriter(), rawImageData);
                IOUtil.scanFileWithMediaScanner(captureInfo.destinationRawFilename);
                if (captureInfo.relatedI3av4File != null)
                    captureInfo.relatedI3av4File.delete();
            }
            catch (IOException ioe)
            {
                throw new SaveFileException();
            }
            finally
            {
                if (rawImageData != null)
                    closeRawImageData(rawImageData);
            }
        }
        else
        {
            throw new SaveFileException();
        }*/

        DngWriter2 writer = new DngWriter2();
        RawImageData rawImageData = null;
        try
        {
            rawImageData = buildRawImageData(captureInfo);
            writer.write(captureInfo, rawImageData);
            IOUtil.scanFileWithMediaScanner(captureInfo.destinationRawFilename);
            if (captureInfo.relatedI3av4File != null)
                captureInfo.relatedI3av4File.delete();
        }
        catch (IOException ioe)
        {
            throw new SaveFileException();
        }
        finally
        {
            if (rawImageData != null)
                closeRawImageData(rawImageData);
        }

        return null;
    }

    private void closeRawImageData(RawImageData rawImageData)
    {
        try
        {
            rawImageData.close();
        }
        catch (IOException ignored)
        {   }
    }

    private RawImageData buildRawImageData(CaptureInfo captureInfo) throws IOException
    {
        if (captureInfo.rawDataBytes != null)
            return new ArrayRawImageData(captureInfo.imageSize, captureInfo.rawDataBytes);
        else
            return new FileRawImageData(captureInfo.imageSize, captureInfo.relatedI3av4File.getAbsolutePath());
    }
}