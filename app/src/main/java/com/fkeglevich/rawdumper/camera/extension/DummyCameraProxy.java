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

package com.fkeglevich.rawdumper.camera.extension;

import android.hardware.Camera;

/**
 * Represents a "fake" Intel Camera Extensions library interface to be used when the actual
 * library implementation is absent from the system, while the core methods still works.
 *
 * Created by Flávio Keglevich on 30/07/2017.
 */

class DummyCameraProxy implements ICameraExtension
{
    private Camera camera;

    static DummyCameraProxy createNew(int cameraId)
    {
        try
        {   return new DummyCameraProxy(Camera.open(cameraId)); }
        catch (Exception e)
        {   return null;    }
    }

    private DummyCameraProxy(Camera camera)
    {
        this.camera = camera;
    }

    @Override
    public void release()
    {
        camera.release();
    }

    @Override
    public Camera getCameraDevice()
    {
        return camera;
    }

    @Override
    public boolean hasIntelFeatures()
    {
        return false;
    }

    @Override
    public void setWindowlessPreviewFrameCaptureId(int id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setWindowlessPreviewFrameCaptureIdSupported()
    {
        return false;
    }

    @Override
    public void pauseWindowlessPreviewFrameUpdate()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean pauseWindowlessPreviewFrameUpdateSupported()
    {
        return false;
    }

    @Override
    public void resumeWindowlessPreviewFrameUpdate()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean resumeWindowlessPreviewFrameUpdateSupported()
    {
        return false;
    }

    @Override
    public void startSceneDetection()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startSceneDetectionSupported()
    {
        return false;
    }

    @Override
    public void stopSceneDetection()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stopSceneDetectionSupported()
    {
        return false;
    }

    @Override
    public void startPanorama()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startPanoramaSupported()
    {
        return false;
    }

    @Override
    public void stopPanorama()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stopPanoramaSupported()
    {
        return false;
    }

    @Override
    public void startSmileShutter()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startSmileShutterSupported()
    {
        return false;
    }

    @Override
    public void stopSmileShutter()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stopSmileShutterSupported()
    {
        return false;
    }

    @Override
    public void startBlinkShutter()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startBlinkShutterSupported()
    {
        return false;
    }

    @Override
    public void stopBlinkShutter()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stopBlinkShutterSupported()
    {
        return false;
    }

    @Override
    public void cancelSmartShutterPicture()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancelSmartShutterPictureSupported()
    {
        return false;
    }

    @Override
    public void forceSmartShutterPicture()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean forceSmartShutterPictureSupported()
    {
        return false;
    }

    @Override
    public void startFaceRecognition()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startFaceRecognitionSupported()
    {
        return false;
    }

    @Override
    public void stopFaceRecognition()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stopFaceRecognitionSupported()
    {
        return false;
    }

    @Override
    public void startContinuousShooting()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startContinuousShootingSupported()
    {
        return false;
    }

    @Override
    public void stopContinuousShooting()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stopContinuousShootingSupported()
    {
        return false;
    }
}
