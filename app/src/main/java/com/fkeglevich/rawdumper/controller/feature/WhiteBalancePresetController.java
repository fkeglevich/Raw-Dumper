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

package com.fkeglevich.rawdumper.controller.feature;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.data.WhiteBalancePreset;
import com.fkeglevich.rawdumper.controller.feature.preset.PresetController;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class WhiteBalancePresetController extends PresetController<WhiteBalancePreset>
{
    WhiteBalancePresetController(ActivityReference reference, OnClickNotifier clickNotifier)
    {
        super(reference, clickNotifier, WhiteBalancePreset.class);
    }

    @Override
    protected void initializeIconMap(ActivityReference reference)
    {
        putIconMap(reference, WhiteBalancePreset.AUTO,              R.id.autoWbBt);
        putIconMap(reference, WhiteBalancePreset.INCANDESCENT,      R.id.incandescentWbBt);
        putIconMap(reference, WhiteBalancePreset.FLUORESCENT,       R.id.fluorescentWbBt);
        putIconMap(reference, WhiteBalancePreset.WARM_FLUORESCENT,  R.id.fluorescentWbBt);
        putIconMap(reference, WhiteBalancePreset.DAYLIGHT,          R.id.sunnyWbBt);
        putIconMap(reference, WhiteBalancePreset.CLOUDY,            R.id.cloudyWbBt);
        putIconMap(reference, WhiteBalancePreset.TWILIGHT,          R.id.twilightWbBt);
        putIconMap(reference, WhiteBalancePreset.SHADE,             R.id.shadeWbBt);
    }

    @Override
    protected int getManualChooserId()
    {
        return R.id.manualWbChooser;
    }

    @Override
    protected int getMainButtonId()
    {
        return R.id.wbBt;
    }

    @Override
    protected int getMainChooser()
    {
        return R.id.wbChooser;
    }

    @Override
    protected boolean shouldIgnore(WhiteBalancePreset mode)
    {
        return WhiteBalancePreset.WARM_FLUORESCENT.equals(mode);
    }
}
