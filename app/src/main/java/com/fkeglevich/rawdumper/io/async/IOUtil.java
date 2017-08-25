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

package com.fkeglevich.rawdumper.io.async;

import android.content.Context;
import android.media.MediaScannerConnection;

import com.fkeglevich.rawdumper.controller.context.ContextManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Flávio Keglevich on 24/08/2017.
 * TODO: Add a class header comment!
 */

public class IOUtil
{
    public static void scanFileWithMediaScanner(String filePath)
    {
        synchronized (ContextManager.getApplicationContext().getLock())
        {
            Context context = ContextManager.getApplicationContext().get();
            MediaScannerConnection.scanFile(context, new String[]{filePath}, null, null);
        }
    }

    public static void saveBytes(byte[] data, String filePath) throws IOException
    {
        BufferedOutputStream bos = null;
        try
        {
            bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            bos.write(data);
            bos.flush();
        }
        finally
        {
            if (bos != null)
                bos.close();
        }
    }
}
