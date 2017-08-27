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

package com.fkeglevich.rawdumper.controller.orientation;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

/**
 * Created by Flávio Keglevich on 27/08/2017.
 * TODO: Add a class header comment!
 */

class OrientationManager
{
    private static final OrientationManager instance = new OrientationManager();

    static OrientationManager getInstance()
    {
        return instance;
    }

    private OrientationEventListener orientationListener = null;
    private int lastDegrees = OrientationEventListener.ORIENTATION_UNKNOWN;

    private OrientationManager()
    {   }

    void setup(Context context)
    {
        disable();
        orientationListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI)
        {
            @Override
            public void onOrientationChanged(int orientation)
            {
                lastDegrees = orientation;
            }
        };
        enable();
    }

    void disable()
    {
        if (orientationListener != null)
            orientationListener.disable();
    }

    void enable()
    {
        if (orientationListener != null)
            orientationListener.enable();
    }

    public int getLastDegrees()
    {
        return lastDegrees;
    }
}
