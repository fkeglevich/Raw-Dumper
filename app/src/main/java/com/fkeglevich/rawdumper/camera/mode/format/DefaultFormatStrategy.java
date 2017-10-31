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

package com.fkeglevich.rawdumper.camera.mode.format;

import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.mode.size.PictureSizeStrategy;

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
    private final List<CompoundFormat> compoundFormats;

    public DefaultFormatStrategy(PictureSizeStrategy jpegStrategy, PictureSizeStrategy yuvStrategy, PictureSizeStrategy rawStrategy)
    {
        compoundFormats = initCompoundFormats(jpegStrategy, yuvStrategy, rawStrategy);
    }

    private List<CompoundFormat> initCompoundFormats(PictureSizeStrategy jpegStrategy, PictureSizeStrategy yuvStrategy, PictureSizeStrategy rawStrategy)
    {
        List<CompoundFormat> formatList = new ArrayList<>();

        addIfAvailable(jpegStrategy, PictureFormat.JPEG, formatList);
        addIfAvailable(yuvStrategy, PictureFormat.YUV, formatList);
        addIfAvailable(rawStrategy, PictureFormat.RAW, formatList);

        return Collections.unmodifiableList(formatList);
    }

    private void addIfAvailable(PictureSizeStrategy sizeStrategy, PictureFormat pictureFormat, List<CompoundFormat> formatList)
    {
        if (sizeStrategy.isAvailable())
            formatList.add(new CompoundFormat(pictureFormat, sizeStrategy.getAvailableSizes()));
    }

    @Override
    public List<CompoundFormat> getAvailableFormats()
    {
        return compoundFormats;
    }
}