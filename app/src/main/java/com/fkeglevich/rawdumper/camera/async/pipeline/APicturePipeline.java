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

package com.fkeglevich.rawdumper.camera.async.pipeline;

import android.hardware.Camera;

/**
 * Created by Flávio Keglevich on 25/06/2017.
 * TODO: Add a class header comment!
 */

public abstract class APicturePipeline
{
    protected Camera.ShutterCallback shutterCallback    = null;
    protected Camera.PictureCallback rawCallback        = null;
    protected Camera.PictureCallback postviewCallback   = null;
    protected Camera.PictureCallback pictureCallback    = null;

    abstract void initCallbacks();

    public void takePicture(Camera camera)
    {
        camera.takePicture(shutterCallback, rawCallback, postviewCallback, pictureCallback);
    }
}
