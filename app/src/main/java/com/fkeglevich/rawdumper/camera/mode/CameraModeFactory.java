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

package com.fkeglevich.rawdumper.camera.mode;

import com.fkeglevich.rawdumper.camera.ModeInfo;

/**
 * Created by Flávio Keglevich on 24/04/2017.
 * TODO: Add a class header comment!
 */

public class CameraModeFactory
{
    public static ACameraMode createMode(ModeInfo modeInfo)
    {
        Class<? extends ACameraMode> modeClass = modeInfo.getModeClass();
        ACameraMode cameraMode = null;
        try
        {
            cameraMode = (ACameraMode)modeClass.getConstructors()[0].newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception occurred during CameraMode creation! Exception: " + e.getMessage());
        }
        return cameraMode;
    }
}
