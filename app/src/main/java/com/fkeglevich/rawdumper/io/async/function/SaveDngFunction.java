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

import com.fkeglevich.rawdumper.async.Locked;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * Created by Flávio Keglevich on 24/08/2017.
 * TODO: Add a class header comment!
 */

public class SaveDngFunction extends FileFunction<Locked<CaptureInfo>>
{
    public SaveDngFunction(String destinationFilePath)
    {
        super(destinationFilePath);
    }

    @Override
    protected Void call(Locked<CaptureInfo> argument) throws MessageException
    {
        /*DngWriter writer = DngWriter.open(getDestinationFilePath());
        if (writer != null)
        {
            synchronized (argument.getLock())
            {
                CaptureInfo captureInfo = argument.get();




                writer.writeMetadata(captureInfo);

                writer.writeImageData(new ScanlineImageWriter(), i3av4RAFile);
                writer.writeExifInfo(captureInfo);
                writer.close();
            }

        }
        else
        {
            i3av4RAFile.close();
        }

        return null;*/
        return null;
    }
}

/*DngWriter writer = DngWriter.open(destFilePath);

        if (writer != null)
        {
            writer.writeMetadata(ioLock.getApplicationContext(), captureInfo);
            writer.writeImageData(new ScanlineImageWriter(), i3av4RAFile);
            writer.writeExifInfo(captureInfo);
            writer.close();
            resultCallback.onResult(true);
        }
        else
        {
            i3av4RAFile.close();
            resultCallback.onResult(false);
        }*/
