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

import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public class PreviewSizeFixer implements FeatureValueFixer<CaptureSize, CaptureSize, List<CaptureSize>>
{
    @Override
    public CaptureSize fixValue(ValueValidator<CaptureSize, List<CaptureSize>> newValidator,
                                CaptureSize ignored, CaptureSize pictureSize)
    {
        Assert.assertTrue(!newValidator.getAvailableValues().isEmpty());

        List<CaptureSize> previewCandidates = new ArrayList<>();
        for (CaptureSize preview : newValidator.getAvailableValues())
            if (preview.hasSameAspectRatio(pictureSize))
                previewCandidates.add(preview);

        Collections.sort(previewCandidates);
        return previewCandidates.get(previewCandidates.size() - 1);
    }
}
