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
 * Provides a simple interface for accessing Intel Camera Extension features.
 *
 * The functions here are analogous to the function in the IntelCamera class.
 *
 * Created by Flávio Keglevich on 30/07/2017.
 */

public interface ICameraExtension
{
    void release();

    Camera getCameraDevice();

    boolean hasIntelFeatures();

    void setWindowlessPreviewFrameCaptureId(int id);

    boolean setWindowlessPreviewFrameCaptureIdSupported();

    void pauseWindowlessPreviewFrameUpdate();

    boolean pauseWindowlessPreviewFrameUpdateSupported();

    void resumeWindowlessPreviewFrameUpdate();

    boolean resumeWindowlessPreviewFrameUpdateSupported();

    void startSceneDetection();

    boolean startSceneDetectionSupported();

    void stopSceneDetection();

    boolean stopSceneDetectionSupported();

    void startPanorama();

    boolean startPanoramaSupported();

    void stopPanorama();

    boolean stopPanoramaSupported();

    void startSmileShutter();

    boolean startSmileShutterSupported();

    void stopSmileShutter();

    boolean stopSmileShutterSupported();

    void startBlinkShutter();

    boolean startBlinkShutterSupported();

    void stopBlinkShutter();

    boolean stopBlinkShutterSupported();

    void cancelSmartShutterPicture();

    boolean cancelSmartShutterPictureSupported();

    void forceSmartShutterPicture();

    boolean forceSmartShutterPictureSupported();

    void startFaceRecognition();

    boolean startFaceRecognitionSupported();

    void stopFaceRecognition();

    boolean stopFaceRecognitionSupported();

    void startContinuousShooting();

    boolean startContinuousShootingSupported();

    void stopContinuousShooting();

    boolean stopContinuousShootingSupported();
}
