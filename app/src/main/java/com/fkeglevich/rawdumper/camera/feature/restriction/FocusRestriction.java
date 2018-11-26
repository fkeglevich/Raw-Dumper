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

package com.fkeglevich.rawdumper.camera.feature.restriction;

import android.util.Log;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.feature.FocusFeature;
import com.fkeglevich.rawdumper.camera.feature.ListFeature;
import com.fkeglevich.rawdumper.camera.feature.RangeFeature;
import com.fkeglevich.rawdumper.util.Nullable;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public class FocusRestriction
{
    private Flash oldFlashValue     = null;
    private Iso oldIsoValue         = null;
    private ShutterSpeed oldSSValue = null;

    private boolean flashFocusFlag       = false;
    private boolean flashFocusTriggered  = false;

    public FocusRestriction(TurboCamera turboCamera)
    {
        FocusFeature focusFeature = (FocusFeature) turboCamera.getListFeature(FocusMode.class);
        RangeFeature<ManualFocus> manualFocusFeature = turboCamera.getRangeFeature(ManualFocus.class);
        ListFeature<Flash> flashFeature = turboCamera.getListFeature(Flash.class);
        ListFeature<Iso> isoFeature = turboCamera.getListFeature(Iso.class);
        ListFeature<ShutterSpeed> ssFeature = turboCamera.getListFeature(ShutterSpeed.class);
        Feature<Nullable<Iso>> isoMeteringFeature = turboCamera.getMeteringFeature(Iso.class);
        Feature<Nullable<ShutterSpeed>> ssMeteringFeature = turboCamera.getMeteringFeature(ShutterSpeed.class);

        focusFeature.getOnChanging().addListener(eventData ->
        {
            if (manualFocusFeature.isAvailable())
                manualFocusFeature.setValue(ManualFocus.DISABLED);

            flashFocusFlag = eventData.parameterValue.equals(FocusMode.FLASH);

            if (!flashFocusFlag)
                resetValues(flashFeature, isoFeature, ssFeature);
        });

        focusFeature.onStartAutoFocus.addListener(eventData ->
        {
            Log.i("ASD", "FLASH: " + flashFocusFlag);
            if (flashFocusFlag)
            {
                if(isHighIso(isoMeteringFeature) || isSlowSS(ssMeteringFeature))
                {
                    flashFocusTriggered = true;
                    if (flashFeature.isAvailable())
                    {
                        oldFlashValue = flashFeature.getValue();
                        flashFeature.setValue(Flash.ON);
                    }

                    if (isoFeature.isAvailable())
                    {
                        oldIsoValue = isoFeature.getValue();
                        isoFeature.setValue(Iso.AUTO);
                    }

                    if (ssFeature.isAvailable())
                    {
                        oldSSValue = ssFeature.getValue();
                        ssFeature.setValue(ShutterSpeed.AUTO);
                    }
                }
            }
        });

        focusFeature.onAutoFocusResult.addListener(eventData ->
        {
            if (flashFocusFlag && flashFocusTriggered)
                resetValues(flashFeature, isoFeature, ssFeature);
        });

        turboCamera.getOnTakePicture().addListener(eventData ->
        {
            if (flashFocusFlag && flashFocusTriggered)
                resetValues(flashFeature, isoFeature, ssFeature);
        });

        manualFocusFeature.getOnChanging().addListener(eventData ->
        {
            boolean manualFocusEnabled = !eventData.parameterValue.equals(ManualFocus.DISABLED);

            if (manualFocusEnabled)
                focusFeature.cancelAutoFocus();
        });
    }

    private void resetValues(ListFeature<Flash> flashFeature, ListFeature<Iso> isoFeature, ListFeature<ShutterSpeed> ssFeature)
    {
        flashFocusTriggered = false;

        if (oldFlashValue != null)
        {
            flashFeature.setValue(oldFlashValue);
            oldFlashValue = null;
        }

        if (oldIsoValue != null)
        {
            isoFeature.setValue(oldIsoValue);
            oldIsoValue = null;
        }

        if (oldSSValue != null)
        {
            ssFeature.setValue(oldSSValue);
            oldSSValue = null;
        }
    }

    /*
            TODO: remove these hardcoded values
            TODO: remove the appearing of notification toasts
            TODO: make the focus feature virtual
     */

    private boolean isHighIso(Feature<Nullable<Iso>> isoMeteringFeature)
    {
        if (!isoMeteringFeature.isAvailable())
            return false;

        Nullable<Iso> nullable = isoMeteringFeature.getValue();
        if (nullable.isPresent())
        {
            Iso iso = nullable.get();
            if (!Iso.AUTO.equals(iso))
                return iso.getNumericValue() >= 600;
        }
        return false;
    }

    private boolean isSlowSS(Feature<Nullable<ShutterSpeed>> ssMeteringFeature)
    {
        if (!ssMeteringFeature.isAvailable())
            return false;

        Nullable<ShutterSpeed> nullable = ssMeteringFeature.getValue();
        if (nullable.isPresent())
        {
            ShutterSpeed shutterSpeed = nullable.get();
            if (!ShutterSpeed.AUTO.equals(shutterSpeed))
                return shutterSpeed.getExposureInSeconds() >= 1.0/30.0;
        }
        return false;
    }
}
