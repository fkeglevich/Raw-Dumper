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

package com.fkeglevich.rawdumper;

import android.os.Bundle;

import com.fkeglevich.rawdumper.activity.ModularActivity;
import com.fkeglevich.rawdumper.controller.feature.CameraLifetimeController;
import com.fkeglevich.rawdumper.controller.orientation.OrientationUIController;

/**
 * Created by Flávio Keglevich on 29/08/2017.
 * TODO: Add a class header comment!
 */

@SuppressWarnings("unused")
public class MainActivity extends ModularActivity
{
    private CameraLifetimeController cameraLifetimeController;
    private OrientationUIController orientationUIController;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackground(null);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);

        cameraLifetimeController = new CameraLifetimeController(reference);
        orientationUIController = new OrientationUIController(reference);
    }
}
