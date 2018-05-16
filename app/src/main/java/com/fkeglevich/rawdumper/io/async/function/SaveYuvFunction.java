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

package com.fkeglevich.rawdumper.io.async.function;

import android.graphics.Bitmap;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunction;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.debug.PerfInfo;
import com.fkeglevich.rawdumper.io.async.exception.SaveFileException;
import com.fkeglevich.rawdumper.raw.capture.YuvCaptureInfo;
import com.fkeglevich.rawdumper.util.YuvUtil;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.FileOutputStream;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImageNativeLibrary;

import static android.graphics.Bitmap.Config.ARGB_4444;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 16/05/18.
 */
public class SaveYuvFunction extends ThrowingAsyncFunction<YuvCaptureInfo, Void, MessageException>
{
    @Override
    protected Void call(YuvCaptureInfo captureInfo) throws MessageException
    {
        if (DebugFlag.dontSavePictures()) return null;
        if (!captureInfo.isValid()) throw new IllegalArgumentException("Invalid yuv capture info!");

        PerfInfo.start("SaveYuvFunction Total");
        PerfInfo.start("switchUVPlanes");
        YuvUtil.switchUVPlanes(captureInfo.yuvBuffer, captureInfo.width, captureInfo.height);
        PerfInfo.end("switchUVPlanes");
        PerfInfo.start("YUVtoARBG");
        GPUImageNativeLibrary.YUVtoARBG(captureInfo.yuvBuffer, captureInfo.width, captureInfo.height, captureInfo.bitmapBuffer);
        PerfInfo.end("YUVtoARBG");
        PerfInfo.start("createBitmap");
        Bitmap bitmap = Bitmap.createBitmap(captureInfo.bitmapBuffer, captureInfo.width, captureInfo.height, ARGB_4444);
        PerfInfo.end("createBitmap");
        //PerfInfo.start("setPixels");
        //bitmap.setPixels(captureInfo.bitmapBuffer, 0, captureInfo.width, 0, 0, captureInfo.width, captureInfo.height);
        //PerfInfo.end("setPixels");
        PerfInfo.start("compress");
        try (FileOutputStream fos = new FileOutputStream(captureInfo.filename))
        {
            if (!bitmap.compress(captureInfo.fileFormat.getCompressFormat(), 100, fos))
                throw new IOException("Error compressing bitmap!");
        }
        catch (IOException e)
        {
            throw new SaveFileException();
        }
        finally
        {
            bitmap.recycle();
        }
        PerfInfo.end("compress");
        PerfInfo.end("SaveYuvFunction Total");
        return null;
    }
}
