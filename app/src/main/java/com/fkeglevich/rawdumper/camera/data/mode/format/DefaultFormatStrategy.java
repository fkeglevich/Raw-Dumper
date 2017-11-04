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

package com.fkeglevich.rawdumper.camera.data.mode.format;

import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.camera.data.PicFormat;
import com.fkeglevich.rawdumper.camera.data.mode.size.PictureSizeStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class DefaultFormatStrategy extends FormatStrategy
{
    private final List<PicFormat> pictureFormats;

    public DefaultFormatStrategy(PictureSizeStrategy jpegStrategy, PictureSizeStrategy yuvStrategy, PictureSizeStrategy rawStrategy)
    {
        pictureFormats = initFormats(jpegStrategy, yuvStrategy, rawStrategy);
    }

    private List<PicFormat> initFormats(PictureSizeStrategy jpegStrategy, PictureSizeStrategy yuvStrategy, PictureSizeStrategy rawStrategy)
    {
        List<PicFormat> formatList = new ArrayList<>();

        addIfAvailable(jpegStrategy, FileFormat.JPEG, formatList);
        addIfAvailable(rawStrategy, FileFormat.DNG, formatList);
        addIfAvailable(yuvStrategy, FileFormat.PNG, formatList);
        addIfAvailable(yuvStrategy, FileFormat.WEBP, formatList);

        return Collections.unmodifiableList(formatList);
    }

    private void addIfAvailable(PictureSizeStrategy sizeStrategy, FileFormat fileFormat, List<PicFormat> formatList)
    {
        if (sizeStrategy.isAvailable())
            formatList.add(new PicFormat(fileFormat, sizeStrategy.getAvailableSizes()));
    }

    @Override
    public List<PicFormat> getAvailableValues()
    {
        return pictureFormats;
    }
}