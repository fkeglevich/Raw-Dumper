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

package com.fkeglevich.rawdumper.extension;

import android.hardware.Camera;

import java.lang.reflect.Method;

/**
 * Created by Flávio Keglevich on 30/07/2017.
 * TODO: Add a class header comment!
 */

public class IntelCameraProxy implements ICameraExtension
{
    private static final String SUPPORTED_VALUES_SUFFIX = "-values";
    private static final String KEY_FOCUS_WINDOW = "focus-window";
    private static final String KEY_XNR = "xnr";
    private static final String KEY_ANR = "anr";
    private static final String KEY_GDC = "gdc";
    private static final String KEY_TEMPORAL_NOISE_REDUCTION = "temporal-noise-reduction";
    private static final String KEY_NOISE_REDUCTION_AND_EDGE_ENHANCEMENT = "noise-reduction-and-edge-enhancement";
    private static final String KEY_MULTI_ACCESS_COLOR_CORRECTION = "multi-access-color-correction";
    private static final String KEY_AE_MODE = "ae-mode";
    private static final String KEY_AE_METERING_MODE = "ae-metering-mode";
    private static final String KEY_SHUTTER = "shutter";
    private static final String KEY_APERTURE = "aperture";
    private static final String KEY_ISO = "iso";
    private static final String KEY_AF_METERING_MODE = "af-metering-mode";
    private static final String KEY_AWB_MAPPING_MODE = "awb-mapping-mode";
    private static final String KEY_COLOR_TEMPERATURE = "color-temperature";
    private static final String KEY_RAW_DATA_FORMAT = "raw-data-format";
    private static final String KEY_CAPTURE_BRACKET = "capture-bracket";
    private static final String KEY_ROTATION_MODE = "rotation-mode";

    private static final String KEY_CONTRAST_MODE = "contrast-mode";
    private static final String KEY_SATURATION_MODE = "saturation-mode";
    private static final String KEY_SHARPNESS_MODE = "sharpness-mode";

    // HDR
    private static final String KEY_HDR_IMAGING = "hdr-imaging";
    private static final String KEY_HDR_SAVE_ORIGINAL = "hdr-save-original";

    // Ultra low light
    private static final String KEY_ULL = "ull";

    // panorama
    private static final String KEY_PANORAMA = "panorama";

    // face detection and recognition
    private static final String KEY_FACE_RECOGNITION = "face-recognition";

    // scene detection
    private static final String KEY_SCENE_DETECTION = "scene-detection";

    // smart shutter
    private static final String KEY_SMILE_SHUTTER = "smile-shutter";
    private static final String KEY_SMILE_SHUTTER_THRESHOLD = "smile-shutter-threshold";
    private static final String KEY_BLINK_SHUTTER = "blink-shutter";
    private static final String KEY_BLINK_SHUTTER_THRESHOLD = "blink-shutter-threshold";

    // GPS extension
    private static final String KEY_GPS_IMG_DIRECTION = "gps-img-direction";
    private static final String KEY_GPS_IMG_DIRECTION_REF = "gps-img-direction-ref";
    // possible value for the KEY_GPS_IMG_DIRECTION_REF
    private static final String GPS_IMG_DIRECTION_REF_TRUE = "true-direction";
    private static final String GPS_IMG_DIRECTION_REF_MAGNETIC = "magnetic-direction";

    // intelligent mode
    public static final String KEY_INTELLIGENT_MODE = "intelligent-mode";

    // hw overlay rendering
    private static final String KEY_HW_OVERLAY_RENDERING = "overlay-render";

    // burst capture
    private static final String KEY_BURST_LENGTH = "burst-length";
    private static final String KEY_BURST_FPS = "burst-fps"; // TODO: old API, remove it in the future
    private static final String KEY_BURST_SPEED = "burst-speed";
    public static final String KEY_BURST_START_INDEX = "burst-start-index";
    private static final String KEY_MAX_BURST_LENGTH_WITH_NEGATIVE_START_INDEX = "burst-max-length-negative";

    // values for af metering mode
    private static final String AF_METERING_MODE_AUTO = "auto";
    private static final String AF_METERING_MODE_SPOT = "spot";

    private static final String KEY_FOCUS_DISTANCES = "focus-distances";
    private static final String KEY_ANTIBANDING = "antibanding";

    private static final String KEY_PANORAMA_LIVE_PREVIEW_SIZE = "panorama-live-preview-size";
    private static final String KEY_SUPPORTED_PANORAMA_LIVE_PREVIEW_SIZES = "panorama-live-preview-sizes";
    private static final String KEY_PANORAMA_MAX_SNAPSHOT_COUNT = "panorama-max-snapshot-count";

    // preview update
    public static final String KEY_PREVIEW_UPDATE_MODE = "preview-update-mode";
    public static final String PREVIEW_UPDATE_MODE_STANDARD = "standard";
    public static final String PREVIEW_UPDATE_MODE_DURING_CAPTURE = "during-capture";
    public static final String PREVIEW_UPDATE_MODE_CONTINUOUS = "continuous";
    public static final String PREVIEW_UPDATE_MODE_WINDOWLESS = "windowless";

    // preview keep alive
    private static final String KEY_PREVIEW_KEEP_ALIVE = "preview-keep-alive";


    // high speed recording, slow motion playback
    private static final String KEY_SLOW_MOTION_RATE = "slow-motion-rate";
    private static final String KEY_HIGH_SPEED_RESOLUTION_FPS = "high-speed-resolution-fps";
    private static final String KEY_RECORDING_FRAME_RATE = "recording-fps";

    // dual mode
    private static final String KEY_DUAL_MODE = "dual-mode";
    private static final String KEY_DUAL_MODE_SUPPORTED = "dual-mode-supported";

    // dual video
    private static final String KEY_DUAL_VIDEO = "dual-video";
    private static final String KEY_DUAL_VIDEO_SUPPORTED = "dual-video-supported";

    /* dual camera mode */
    /** @hide */
    public static final String KEY_DUAL_CAMERA_MODE = "dual-camera-mode";
    /** @hide */
    public static final String DUAL_CAMERA_MODE_NORMAL = "normal";
    /** @hide */
    public static final String DUAL_CAMERA_MODE_DEPTH = "depth";

    // Exif data
    public static final String KEY_EXIF_MAKER = "exif-maker-name";
    public static final String KEY_EXIF_MODEL = "exif-model-name";
    public static final String KEY_EXIF_SOFTWARE = "exif-software-name";

    // Save still image and recorded video as mirrored (for front camera only)
    public static final String KEY_SAVE_MIRRORED = "save-mirrored";

    // continuous shooting.
    private static final String KEY_CONTINUOUS_SHOOTING = "continuous-shooting";
    private static final String KEY_CONTINUOUS_SHOOTING_SUPPORTED = "continuous-shooting-supported";

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    // values for ae mode setting.
    /** @hide */
    public static final String AE_MODE_AUTO = "auto";
    /** @hide */
    public static final String AE_MODE_MANUAL = "manual";
    /** @hide */
    public static final String AE_MODE_SHUTTER_PRIORITY = "shutter-priority";
    /** @hide */
    public static final String AE_MODE_APERTURE_PRIORITY = "aperture-priority";

    // Values for ae metering setting.
    /** @hide */
    public static final String AE_METERING_AUTO = "auto";
    /** @hide */
    public static final String AE_METERING_SPOT = "spot";
    /** @hide */
    public static final String AE_METERING_CENTER = "center";
    /** @hide */
    public static final String AE_METERING_CUSTOMIZED = "customized";

    // Values for awb mapping mode.
    /** @hide */
    public static final String AWB_MAPPING_AUTO = "auto";
    /** @hide */
    public static final String AWB_MAPPING_INDOOR = "indoor";
    /** @hide */
    public static final String AWB_MAPPING_OUTDOOR = "outdoor";

    /** @hide */
    public static final String FLASH_MODE_DAY_SYNC = "day-sync";
    /** @hide */
    public static final String FLASH_MODE_SLOW_SYNC = "slow-sync";

    /** @hide */
    public static final String FOCUS_MODE_MANUAL = "manual";

    /** @hide */
    public static final String FOCUS_MODE_TOUCH = "touch";

    /** @hide */
    public static final String SLOW_MOTION_RATE_1x = "1x";
    public static final String SLOW_MOTION_RATE_2x = "2x";
    public static final String SLOW_MOTION_RATE_3x = "3x";
    public static final String SLOW_MOTION_RATE_4x = "4x";

    // value of contrast mode
    /** @hide */
    public static final String CONTRAST_MODE_NORMAL = "normal";
    /** @hide */
    public static final String CONTRAST_MODE_SOFT = "soft";
    /** @hide */
    public static final String CONTRAST_MODE_HARD = "hard";

    // value of saturation mode
    /** @hide */
    public static final String SATURATION_MODE_NORMAL = "normal";
    /** @hide */
    public static final String SATURATION_MODE_LOW = "low";
    /** @hide */
    public static final String SATURATION_MODE_HIGH = "high";

    // value of sharpness mode
    /** @hide */
    public static final String SHARPNESS_MODE_NORMAL = "normal";
    /** @hide */
    public static final String SHARPNESS_MODE_SOFT = "soft";
    /** @hide */
    public static final String SHARPNESS_MODE_HARD = "hard";

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

    static IntelCameraProxy createNew(Class<Object> intelCameraClass, int cameraId)
    {
        Object instance;
        try
        {
            instance = intelCameraClass.getConstructor(int.class).newInstance(cameraId);
            return new IntelCameraProxy(intelCameraClass, instance);
        }
        catch (Exception e)
        {   return null;    }
    }

    private IntelCameraProxy(Class<Object> intelCameraClass, Object instance)
    {
        this.instance = instance;
        initializeMethods(intelCameraClass);
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
