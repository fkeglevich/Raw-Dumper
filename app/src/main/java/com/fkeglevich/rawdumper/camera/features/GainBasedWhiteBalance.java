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

package com.fkeglevich.rawdumper.camera.features;

import com.fkeglevich.rawdumper.camera.async.SharedCameraGetter;

/**
 * Created by Flávio Keglevich on 27/08/2017.
 * TODO: Add a class header comment!
 */

public abstract class GainBasedWhiteBalance extends AFeature
{
    GainBasedWhiteBalance(SharedCameraGetter sharedCameraGetter)
    {
        super(sharedCameraGetter);
    }

    public abstract double getBlueGain();

    public abstract void setBlueGain(double value);

    public abstract double getRedGain();

    public abstract void setRedGain(double value);

    @Override
    public boolean isAvailable()
    {
        return false;
    }
}