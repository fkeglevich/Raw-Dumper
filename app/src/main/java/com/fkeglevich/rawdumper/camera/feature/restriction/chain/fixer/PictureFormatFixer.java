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

package com.fkeglevich.rawdumper.camera.feature.restriction.chain.fixer;

import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;

import org.junit.Assert;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public class PictureFormatFixer implements FeatureValueFixer<Mode, PictureFormat, List<PictureFormat>>
{
    @Override
    public PictureFormat fixValue(ValueValidator<PictureFormat, List<PictureFormat>> newValidator, PictureFormat toBeFixed, Mode ignored)
    {
        Assert.assertTrue(!newValidator.getAvailableValues().isEmpty());
        List<PictureFormat> availableValues = newValidator.getAvailableValues();

        if (toBeFixed != null)
            for (PictureFormat format : availableValues)
            {
                if (format.getFileFormat().equals(toBeFixed.getFileFormat()))
                    return format;
            }

        return newValidator.getAvailableValues().get(0);
    }
}
