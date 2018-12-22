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

package com.fkeglevich.rawdumper.raw.data;

import com.topjohnwu.superuser.io.SuFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DumpFile
{
    public static final String RAW_DUMP_FILE_EXTENSION = ".i3av4";

    public static SuFile selectI3av4Image(String dumpDirectoryPath) throws IOException
    {
        SuFile dumpDirectory = new SuFile(dumpDirectoryPath);

        List<String> i3av4List = getI3av4FilenamesInsideDir(dumpDirectory);

        if (i3av4List.size() != 1)
            throw new IOException("Invalid number of pictures in dump directory!");

        return new SuFile(dumpDirectory, i3av4List.get(0));
    }

    private static List<String> getI3av4FilenamesInsideDir(SuFile dumpDirectory)
    {
        String[] files = dumpDirectory.list();
        List<String> actualImages = new ArrayList<>();
        for (String file : files)
            if (file.endsWith(RAW_DUMP_FILE_EXTENSION))
                actualImages.add(file);

        return actualImages;
    }
}
