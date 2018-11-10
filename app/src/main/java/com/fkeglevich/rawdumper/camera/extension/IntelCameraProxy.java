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
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Represents a Intel Camera Extensions library interface.
 *
 * Created by Flávio Keglevich on 30/07/2017.
 */

class IntelCameraProxy implements ICameraExtension
{
    private Object instance;

    private Method releaseMethod;
    private Method getCameraDeviceMethod;
    private Method setWindowlessPreviewFrameCaptureIdMethod;
    private Method pauseWindowlessPreviewFrameUpdateMethod;
    private Method resumeWindowlessPreviewFrameUpdateMethod;
    private Method startSceneDetectionMethod;
    private Method stopSceneDetectionMethod;
    private Method startPanoramaMethod;
    private Method stopPanoramaMethod;
    private Method startSmileShutterMethod;
    private Method stopSmileShutterMethod;
    private Method startBlinkShutterMethod;
    private Method stopBlinkShutterMethod;
    private Method cancelSmartShutterPictureMethod;
    private Method forceSmartShutterPictureMethod;
    private Method startFaceRecognitionMethod;
    private Method stopFaceRecognitionMethod;
    private Method startContinuousShootingMethod;
    private Method stopContinuousShootingMethod;

    static IntelCameraProxy createNew(Class<Object> intelCameraClass, int cameraId) throws Exception
    {
        Object instance;
        instance = intelCameraClass.getConstructor(int.class).newInstance(cameraId);
        return new IntelCameraProxy(intelCameraClass, instance);
    }

    private IntelCameraProxy(Class<Object> intelCameraClass, Object instance)
    {
        this.instance = instance;
        initializeMethods(intelCameraClass);
        Log.i(IntelCameraProxy.class.getSimpleName(), "Intel Camera Extensions loaded!");
    }

    private void initializeMethods(Class<Object> intelCameraClass)
    {
        releaseMethod = getMethod(intelCameraClass, "release");
        getCameraDeviceMethod = getMethod(intelCameraClass, "getCameraDevice");
        setWindowlessPreviewFrameCaptureIdMethod = getMethod(intelCameraClass, "setWindowlessPreviewFrameCaptureId", int.class);
        pauseWindowlessPreviewFrameUpdateMethod = getMethod(intelCameraClass, "pauseWindowlessPreviewFrameUpdate");
        resumeWindowlessPreviewFrameUpdateMethod = getMethod(intelCameraClass, "resumeWindowlessPreviewFrameUpdate");
        startSceneDetectionMethod = getMethod(intelCameraClass, "startSceneDetection");
        stopSceneDetectionMethod = getMethod(intelCameraClass, "stopSceneDetection");
        startPanoramaMethod = getMethod(intelCameraClass, "startPanorama");
        stopPanoramaMethod = getMethod(intelCameraClass, "stopPanorama");
        startSmileShutterMethod = getMethod(intelCameraClass, "startSmileShutter");
        stopSmileShutterMethod = getMethod(intelCameraClass, "stopSmileShutter");
        startBlinkShutterMethod = getMethod(intelCameraClass, "startBlinkShutter");
        stopBlinkShutterMethod = getMethod(intelCameraClass, "stopBlinkShutter");
        cancelSmartShutterPictureMethod = getMethod(intelCameraClass, "cancelSmartShutterPicture");
        forceSmartShutterPictureMethod = getMethod(intelCameraClass, "forceSmartShutterPicture");
        startFaceRecognitionMethod = getMethod(intelCameraClass, "startFaceRecognition");
        stopFaceRecognitionMethod = getMethod(intelCameraClass, "stopFaceRecognition");
        startContinuousShootingMethod = getMethod(intelCameraClass, "startContinuousShooting");
        stopContinuousShootingMethod = getMethod(intelCameraClass, "stopContinuousShooting");
    }

    private Method getMethod(Class<Object> methodClass, String methodName, Class<?>... parameterTypes)
    {
        try
        {   return methodClass.getMethod(methodName, parameterTypes);   }
        catch (NoSuchMethodException ignored)
        {   return null;    }
    }

    private Object callMethod(Method method, Object receiver, Object... args)
    {
        try
        {   return method.invoke(receiver, args);   }
        catch (Exception e)
        {   throw new RuntimeException("Calling the method " + method.getName() + " failed!", e);   }
    }

    private Object callInstanceMethod(Method method, Object... args)
    {
        return callMethod(method, instance, args);
    }

    @Override
    public void release()
    {
        callInstanceMethod(releaseMethod);
    }

    @Override
    public Camera getCameraDevice()
    {
        return (Camera) callInstanceMethod(getCameraDeviceMethod);
    }

    @Override
    public boolean hasIntelFeatures()
    {
        return true;
    }

    @Override
    public void setWindowlessPreviewFrameCaptureId(int id)
    {
        callInstanceMethod(setWindowlessPreviewFrameCaptureIdMethod, id);
    }

    @Override
    public boolean setWindowlessPreviewFrameCaptureIdSupported()
    {
        return setWindowlessPreviewFrameCaptureIdMethod != null;
    }

    @Override
    public void pauseWindowlessPreviewFrameUpdate()
    {
        callInstanceMethod(pauseWindowlessPreviewFrameUpdateMethod);
    }

    @Override
    public boolean pauseWindowlessPreviewFrameUpdateSupported()
    {
        return pauseWindowlessPreviewFrameUpdateMethod != null;
    }

    @Override
    public void resumeWindowlessPreviewFrameUpdate()
    {
        callInstanceMethod(resumeWindowlessPreviewFrameUpdateMethod);
    }

    @Override
    public boolean resumeWindowlessPreviewFrameUpdateSupported()
    {
        return resumeWindowlessPreviewFrameUpdateMethod != null;
    }

    @Override
    public void startSceneDetection()
    {
        callInstanceMethod(startSceneDetectionMethod);
    }

    @Override
    public boolean startSceneDetectionSupported()
    {
        return startSceneDetectionMethod != null;
    }

    @Override
    public void stopSceneDetection()
    {
        callInstanceMethod(stopSceneDetectionMethod);
    }

    @Override
    public boolean stopSceneDetectionSupported()
    {
        return stopSceneDetectionMethod != null;
    }

    @Override
    public void startPanorama()
    {
        callInstanceMethod(startPanoramaMethod);
    }

    @Override
    public boolean startPanoramaSupported()
    {
        return startPanoramaMethod != null;
    }

    @Override
    public void stopPanorama()
    {
        callInstanceMethod(stopPanoramaMethod);
    }

    @Override
    public boolean stopPanoramaSupported()
    {
        return stopPanoramaMethod != null;
    }

    @Override
    public void startSmileShutter()
    {
        callInstanceMethod(startSmileShutterMethod);
    }

    @Override
    public boolean startSmileShutterSupported()
    {
        return startSmileShutterMethod != null;
    }

    @Override
    public void stopSmileShutter()
    {
        callInstanceMethod(stopSmileShutterMethod);
    }

    @Override
    public boolean stopSmileShutterSupported()
    {
        return stopSmileShutterMethod != null;
    }

    @Override
    public void startBlinkShutter()
    {
        callInstanceMethod(startBlinkShutterMethod);
    }

    @Override
    public boolean startBlinkShutterSupported()
    {
        return startBlinkShutterMethod != null;
    }

    @Override
    public void stopBlinkShutter()
    {
        callInstanceMethod(stopBlinkShutterMethod);
    }

    @Override
    public boolean stopBlinkShutterSupported()
    {
        return stopBlinkShutterMethod != null;
    }

    @Override
    public void cancelSmartShutterPicture()
    {
        callInstanceMethod(cancelSmartShutterPictureMethod);
    }

    @Override
    public boolean cancelSmartShutterPictureSupported()
    {
        return cancelSmartShutterPictureMethod != null;
    }

    @Override
    public void forceSmartShutterPicture()
    {
        callInstanceMethod(forceSmartShutterPictureMethod);
    }

    @Override
    public boolean forceSmartShutterPictureSupported()
    {
        return forceSmartShutterPictureMethod != null;
    }

    @Override
    public void startFaceRecognition()
    {
        callInstanceMethod(startFaceRecognitionMethod);
    }

    @Override
    public boolean startFaceRecognitionSupported()
    {
        return startFaceRecognitionMethod != null;
    }

    @Override
    public void stopFaceRecognition()
    {
        callInstanceMethod(stopFaceRecognitionMethod);
    }

    @Override
    public boolean stopFaceRecognitionSupported()
    {
        return stopFaceRecognitionMethod != null;
    }

    @Override
    public void startContinuousShooting()
    {
        callInstanceMethod(startContinuousShootingMethod);
    }

    @Override
    public boolean startContinuousShootingSupported()
    {
        return startContinuousShootingMethod != null;
    }

    @Override
    public void stopContinuousShooting()
    {
        callInstanceMethod(stopContinuousShootingMethod);
    }

    @Override
    public boolean stopContinuousShootingSupported()
    {
        return stopContinuousShootingMethod != null;
    }
}
