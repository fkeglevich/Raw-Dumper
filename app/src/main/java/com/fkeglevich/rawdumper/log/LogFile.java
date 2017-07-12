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

package com.fkeglevich.rawdumper.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Simple class for store a simple file log.
 *
 * Created by Flávio Keglevich on 20/06/2017.
 */

public class LogFile
{
    private static final String LOG_FILENAME = "RawDumperLog.txt";

    private static File logFile;

    public static void initLogFile(File logDir)
    {
        logFile = new File(logDir, LOG_FILENAME);
        try
        {
            if (logFile.exists())
                logFile.delete();
            logFile.createNewFile();
        } catch (IOException e)
        {   e.printStackTrace();    }
    }

    public static void writeLine(String data)
    {
        FileOutputStream stream = null;
        try
        {
            stream = new FileOutputStream(logFile, true);
            stream.write((data + "\n").getBytes());
            stream.close();
        }
        catch (IOException e)
        {   e.printStackTrace();    }
    }
}
