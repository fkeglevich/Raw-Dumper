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

package com.fkeglevich.rawdumper.camera.feature.restriction.chain;

import com.fkeglevich.rawdumper.camera.action.PreviewActions;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.feature.PictureFormatFeature;
import com.fkeglevich.rawdumper.camera.feature.PictureModeFeature;
import com.fkeglevich.rawdumper.camera.feature.PictureSizeFeature;
import com.fkeglevich.rawdumper.camera.feature.PreviewFeature;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.fixer.PictureFormatFixer;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.fixer.PictureSizeFixer;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.fixer.PreviewSizeFixer;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.validator.DataContainerVF;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public class ModeRestrictionChain extends RestrictionChain
{
    private final PreviewActions previewActions;

    public ModeRestrictionChain(PictureModeFeature modeFeature,
                                PictureFormatFeature formatFeature,
                                PictureSizeFeature pictureSizeFeature,
                                PreviewFeature previewFeature,
                                PreviewActions previewActions)
    {
        this.previewActions = previewActions;

        ChainNode<Mode, PictureFormat, List<PictureFormat>> modeNode
                = new ChainNode<>(modeFeature, new DataContainerVF<>(), new PictureFormatFixer(), formatFeature);
        addChainNode(modeNode);

        ChainNode<PictureFormat, CaptureSize, List<CaptureSize>> formatNode
                = new ChainNode<>(formatFeature, new DataContainerVF<>(), new PictureSizeFixer(), pictureSizeFeature);
        addChainNode(formatNode);

        ChainNode<CaptureSize, CaptureSize, List<CaptureSize>> pictureSizeNode
                = new ChainNode<>(pictureSizeFeature, null, new PreviewSizeFixer(), previewFeature);
        addChainNode(pictureSizeNode);

        setupLatestSlave(previewFeature);
    }

    @Override
    protected void onBeforeChain()
    {
        previewActions.stopPreview();
    }

    @Override
    protected void onAfterChain()
    {
        previewActions.startPreview();
    }
}
