/*
 * Copyright 2012, Intel Corporation
 * Modifications Copyright 2016, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.camera.extensions;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Iterator;

/**
 * The IntelCamera class is used for accessing Intel's camera extensions.
 *
 * This class allows user access to Intel camera features, such as Intel Parameters setting, smart scene detection,
 * panorama, and so on.
 * <p>
 * IntelCamera is an extension of android.hardware.Camera. When user create an IntelCamera instance, a Camera instance
 * will be opened, user can get it by calling IntelCamera.{@link #getCameraDevice()}. So in this case, don't calling Camera.open()
 * to obtain a Camera instance.
 * <p>
 * Please release Intel camera by IntelCamera.{@link #release()} after using.
 * IntelCamera interface provides access to Intel parameter extensions. The standard parameter handling is done
 * by using the standard Android API: Camera.Parameters, Camera.getParameters() and Camera.setParameters(). The Intel extensions
 * can be accessed via IntelCamera API calls, which affect only the Intel extension part of the parameters.
 * <p>
 * To access the Intel camera device, user must declare the Intel camera extension library in Android project:
 * <p>
 * In Android.mk:
 * <pre>
 *    LOCAL_JAVA_LIBRARIES:=com.intel.camera.extensions
 * </pre>
 * In AndroidManifest.xml:
 * <pre>
 *     <CODE>uses-library android:name="com.intel.camera.extensions"</CODE>
 * </pre>
 * <p>
 */
public class IntelCamera {
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

    private static Boolean intelCameraAvailable;

    private Camera mCameraDevice = null;
    private Parameters mParameters;
    private EventHandler mEventHandler;
    private SceneDetectionListener mSceneDetectionListener;
    private PanoramaListener mPanoramaListener;
    private UllListener mUllListener;
    private LowBatteryListener mLowBatteryListener;
    private CaptureFrameIdCallback mCaptureFrameIdCallback;
    private boolean mSceneDetectionRunning = false;
    private boolean mPanoramaRunning = false;
    private boolean mSmileShutterRunning = false;
    private boolean mBlinkShutterRunning = false;

    //accessed by native methods. In Android Marshmallow, this field has to be long instead of int.
    private long mNativeContext;

    //it was "com.intel.cameraext.Camera" but tag length is now restricted to 23 characters
    private static final String TAG = "IntelCamera";

    private native final void native_setup(Object camera_this, Camera cameraDevice);
    private native final void native_release();
    private native final void native_setPriority(int cameraId, boolean lowPriority);
    private native final boolean native_enableIntelCamera();
    private native final void native_startSceneDetection();
    private native final void native_stopSceneDetection();
    private native final void native_startPanorama();
    private native final void native_stopPanorama();
    private native final void native_startSmileShutter();
    private native final void native_stopSmileShutter();
    private native final void native_startBlinkShutter();
    private native final void native_stopBlinkShutter();
    private native final void native_cancelSmartShutterPicture();
    private native final void native_forceSmartShutterPicture();
    private native final void native_startFaceRecognition();
    private native final void native_stopFaceRecognition();
    private native final void native_startContinuousShooting();
    private native final void native_stopContinuousShooting();
    private native final void native_setPreviewFrameCaptureId(int id);
    private native final void native_pausePreviewFrameUpdate();
    private native final void native_resumePreviewFrameUpdate();

    // here need keep pace with native msgType
    private static final int CAMERA_MSG_SCENE_DETECT = 0x2001;
    private static final int CAMERA_MSG_PANORAMA_SNAPSHOT = 0x2003;
    private static final int CAMERA_MSG_PANORAMA_METADATA = 0x2005;
    private static final int CAMERA_MSG_ULL_SNAPSHOT = 0x2007;
    private static final int CAMERA_MSG_ULL_TRIGGERED = 0x2009;
    private static final int CAMERA_MSG_LOW_BATTERY = 0x200B;
    private static final int CAMERA_MSG_FRAME_ID = 0x2017;

    static {
        /*
        We try to load the jni library.
        If anything bad occurs, the Intel camera features are disabled.

        This is useful because we don't want our app to break when running
        on devices that don't have these features.
        */
        intelCameraAvailable = true;
        try
        {
            System.loadLibrary("intelcamera_jni");
        }
        catch (UnsatisfiedLinkError error)
        {
            intelCameraAvailable = false;
        }
    }

    /**
     * Gets whether the Intel camera features are available.
     *
     * Use this function to check the features before using them.
     * If they aren't, only use the normal Android Camera API features.
     *
     * @return A boolean value
     */
    public static boolean isIntelCameraAvailable()
    {
        return intelCameraAvailable;
    }

    /* When setting lowPriority to true:
         - No existing open cameras: A low priority camera instance is created
         - One or more open cameras: The CameraServicelayer will reject the
                                     request to open a new camera instance
     A running low priority camera instance can be killed by the
     CameraServiceLayer if a normal priority camera instance is being opened.
     The low priority request is (for each cameraId) associated with the
     process ID of the process calling IntelCamera(Int, boolean) and stored
     inside the CameraServiceLayer. A stored request can be cleared again by
     setting lowPriority to false.

     Note: low priority only works when isIntelCameraAvailable() returns true.
     */
    public IntelCamera(int cameraId, boolean lowPriority) {
        native_setPriority(cameraId, lowPriority);
        mCameraDevice = android.hardware.Camera.open(cameraId);
        init();
    }

    public IntelCamera(int cameraId) {
        mCameraDevice = android.hardware.Camera.open(cameraId);
        init();
    }

    public IntelCamera() {
        mCameraDevice = android.hardware.Camera.open();
        init();
    }

    public final void release() {
        if (intelCameraAvailable)
            native_release();

        if (mCameraDevice != null) {
            mCameraDevice.release();
            mCameraDevice = null;
        }
    }

    private void init() {
        if (mCameraDevice == null)
            throw new RuntimeException();

        if (intelCameraAvailable)
        {
            native_setup(new WeakReference<IntelCamera>(this), mCameraDevice);

            Looper looper;
            if ((looper = Looper.myLooper()) != null) {
                mEventHandler = new EventHandler(this, looper);
            } else if ((looper = Looper.getMainLooper()) != null) {
                mEventHandler = new EventHandler(this, looper);
            } else {
                mEventHandler = null;
            }

            native_enableIntelCamera();
            Log.v(TAG, "Intel camera features available");
        }
        else
        {
            Log.v(TAG, "Intel camera features unavailable");
        }
    }

    public Camera getCameraDevice() {
        return mCameraDevice;
    }

    private static void postEventFromNative(Object camera_ref,
                                            int what, int arg1, int arg2, Object obj)
    {
        IntelCamera c = (IntelCamera)((WeakReference)camera_ref).get();
        if (c == null)
            return;

        if (c.mEventHandler != null) {
            Message m = c.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            c.mEventHandler.sendMessage(m);
        }
    }
    private class EventHandler extends Handler
    {
        private IntelCamera mCamera;

        public EventHandler(IntelCamera c, Looper looper) {
            super(looper);
            mCamera = c;
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case CAMERA_MSG_SCENE_DETECT:
                    Log.d(TAG, "Scene Detection Listener Data");
                    SceneDetectionMetadata sceneDetected = (SceneDetectionMetadata) msg.obj;
                    if (mSceneDetectionListener != null) {
                        mSceneDetectionListener.onSceneChange(sceneDetected);
                    }
                    break;
                case CAMERA_MSG_PANORAMA_METADATA:
                    PanoramaMetadata metadata = (PanoramaMetadata) msg.obj;
                    if (mPanoramaListener != null)
                        mPanoramaListener.onDisplacementChange(metadata);
                    break;
                case CAMERA_MSG_PANORAMA_SNAPSHOT:
                    PanoramaSnapshot snapshot = (PanoramaSnapshot) msg.obj;
                    if (mPanoramaListener != null)
                        mPanoramaListener.onSnapshotTaken(snapshot);
                    break;
                case CAMERA_MSG_ULL_SNAPSHOT:
                    Log.d(TAG, "ULL snapshot data");
                    UllSnapshot ullSnapshot = (UllSnapshot) msg.obj;
                    if (mUllListener != null) {
                        mUllListener.onSnapshotTaken(ullSnapshot);
                    }
                case CAMERA_MSG_ULL_TRIGGERED:
                    if (mUllListener != null) {
                        mUllListener.onUllTriggered(msg.arg1);
                    }
                    break;
                case CAMERA_MSG_LOW_BATTERY:
                    Log.v(TAG, "LowBatteryListener");
                    if (mLowBatteryListener != null) {
                        mLowBatteryListener.lowBattery();
                    }
                    break;
                case CAMERA_MSG_FRAME_ID:
                    Log.v(TAG, "CaptureFrameIdCallback");
                    if (mCaptureFrameIdCallback != null) {
                        mCaptureFrameIdCallback.onCaptureFrameIdAvailable(msg.arg1);
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown intel message type " + msg.what);
                    return;
            }
        }
    }

    /**
     * The PanoramaSnapshot class is used to carry information in the PanoramaListener
     * callbacks.
     * @see IntelCamera.PanoramaListener
     * @see #setPanoramaListener(PanoramaListener listener)
     * @hide
     */
    public static class PanoramaSnapshot
    {
        public PanoramaSnapshot()
        {
        }

        /**
         * Metadata during the panorama snapshot. This includes the displacement of the live
         * preview image compared to the previous live preview image.
         */
        public PanoramaMetadata metadataDuringSnap;

        /**
         * Snapshot holds the live preview image which is NV12 format. Size can be set with
         * {@link #setPanoramaLivePreviewSize(int width, int height)}.
         * <p>
         * Note, that the live preview image size is typically small compared to the actual
         * preview size.
         */
        public byte[] snapshot;
    }

    /**
     * The PanoramaMetadata class carries metadata information during panorama mode via the
     * callbacks of the PanoramaListener.
     * @see #setPanoramaListener(PanoramaListener listener)
     * @see IntelCamera.PanoramaListener
     * @see IntelCamera.PanoramaSnapshot
     * @hide
     */
    public static class PanoramaMetadata
    {
        public PanoramaMetadata() {
        }
        /**
         * Direction tells what direction the panorama engine has selected for stitching
         * based on initial movement after first snapshot. Values are:
         * 1 - right
         * 2 - left
         * 3 - down
         * 4 - up
         */
        public int direction = 0;
        /**
         * Horizontal displacement as preview pixels compared to the preview location during
         * previous panorama snapshot. Positive values are to the right, meaning the viewfinder
         * is moved to right of the previous snapshot. Negative values similarly to the left.
         */
        public int horizontalDisplacement = 0;
        /**
         * Vertical displacement as preview pixels compared to the preview location during
         * previous panorama snapshot. Negative values are to up, meaning the viewfinder is
         * moved upwards of the previous snapshot. Positive values similarly to down.
         */
        public int verticalDisplacement = 0;
        /**
         * Motion blur indicates if the camera panning during panorama capturing is done too fast
         * and the end result of taking a panorama snapshot would be blurred.
         */
        public boolean motionBlur = false;
        /**
         * finalizationStarted signals the application whether finalization has begun automatically
         * due to reaching the maximum number of panorama snapshots. Only valid when metadata is
         * carried with a onSnapshotTaken callback.
         */
        public boolean finalizationStarted = false;
    }


    /**
     * The SceneDetectionMetadata class carries metadata information during scene detection mode via the
     * callbacks of the SceneDetectionListener.
     * @see #setSceneDetectionListener(SceneDetectionListener listener)
     * @see IntelCamera.SceneDetectionListener
     * @hide
     */
    public static class SceneDetectionMetadata
    {
        public SceneDetectionMetadata() {
        }

        public String sceneDetected;
        public boolean hdr = false;

    }

    /**
     * Sets the panorama listener for receiving panorama displacement callbacks
     * and live preview images.
     * @param listener the new PanoramaListener
     * @hide
     */
    public void setPanoramaListener(PanoramaListener listener)
    {
        mPanoramaListener = listener;
    }

    /**
     * The PanoramaListener interface is for receiving panorama callbacks.
     * @hide
     */
    public interface PanoramaListener
    {
        /**
         * The onDisplacementChangeNotify callback notifies of the viewfinder moving
         * during panorama capturing. The displacement pixel information is given in actual
         * viewfinder preview pixels.
         * @param metadata about the panorama capturing
         * @hide
         */
        void onDisplacementChange(PanoramaMetadata metadata);

        /**
         * The onSnapshotTaken notifies of a snapshot during panorama capturing. It contains
         * metadata of panorama displacement during time of snapshot and the NV12
         * format live preview image.
         * <p>
         * Note, that the associated displacement pixel information
         * is given in live preview image pixels to make it easier for the application to lay
         * the live preview images on top of each other, if needed.
         * @param snapshot live preview image of the snapshot and associated metadata
         * @hide
         */
        void onSnapshotTaken(PanoramaSnapshot snapshot);
    }

    /**
     * The UllSnapshot class is used to encapsulate the information carried
     * to the application via UllListener callbacks
     * @see IntelCamera.UllListener
     * @see #setUllListener(UllListener listener)
     * @hide
     */
    public static class UllSnapshot
    {
        public UllSnapshot()
        {
        }

        /**
         * ID to identify to Ultra Low Light snapshot taken
         */
        public int id;

        /**
         * JPEG encoded Ultra Low Light snapshot data
         */
        public byte[] snapshot;

        // TODO: Additional ULL metadata needed?
    }

    /**
     * Sets the ultra-low light listener for receiving ULL snapshots.
     * @param listener the new UllListener
     * @hide
     */
    public void setUllListener(UllListener listener)
    {
        mUllListener = listener;
    }

    /**
     * The UllListener interface is used for receiving Ultra-low light callbacks.
     * @hide
     */
    public interface UllListener
    {
        /**
         * Triggered when the ULL activation is detected by the hardware
         * @param id Identifier for the ULL snapshot for which the detection has
         * been made. The snapshot will be later provided with onSnapshotTaken() callback
         * with the same identifier.
         */
        void onUllTriggered(int id);
        /**
         * Triggered after the ULL image post-processing is done
         * and the snapshot will be delivered to the application
         */
        void onSnapshotTaken(UllSnapshot snapshot);
    }

    public interface SceneDetectionListener
    {
        /**
         * Notify the listener of the detected scene mode.
         *
         * @param scene The string constant of scene mode detected.
         * The string provided is one of those being returned by getSceneMode()
         * @param hdrHint The detected HDR state in current scene. True, if HDR
         * is detected
         * @see #getSceneMode()
         */
        void onSceneChange(SceneDetectionMetadata metadata);
    };

    /**
     * @hide
     * Registers a listener to be notified about the scene detected in the
     * preview frames.
     *
     * @param listener the listener to notify
     * @see #startSceneDetection()
     */
    public final void setSceneDetectionListener(SceneDetectionListener listener)
    {
        mSceneDetectionListener = listener;
    }

    public interface LowBatteryListener
    {
        /**
         * Notify the listener of the low battery.
         *
         */
        void lowBattery();
    };

    /**
     * @hide
     * Registers a listener to be notified about the low battery.
     * If the flash is supported and flash mode is not off, when the setParameters is called,
     * camera HAL will check the battery status.
     * Flash will be disabled and one callback message will be sent to the application
     * if low battery.
     *
     * @param listener the listener to notify
     */
    public final void setLowBatteryListener(LowBatteryListener listener)
    {
        mLowBatteryListener = listener;
    }

    /* Capture frame id listener, return frame id */
    public interface CaptureFrameIdCallback {
        /**
         * Notify the listener of the frame id.
         *
         */
        void onCaptureFrameIdAvailable(int frameId);
    }

    /**
     * @hide
     * Registers a listener to be notified about frame id.
     * When take picture, the frame id of captured frame will be sent to the application
     * which gotten from sensor meta data.
     *
     * @param listener the listener to notify
     */
    public final void setFrameIdListener(CaptureFrameIdCallback listener)
    {
        mCaptureFrameIdCallback = listener;
    }

    /**
     * Set capture id of preview frame.
     *
     * @hide
     */
    public final void setWindowlessPreviewFrameCaptureId(int id) {
        native_setPreviewFrameCaptureId(id);
    }

    /**
     * @hide
     * Pause preview frame update.
     * After calling, preview frame queue won't be updated, unless
     * {@link #resumeWindowlessPreviewFrameUpdate()} has been called by the application.
     */
    public final void pauseWindowlessPreviewFrameUpdate()
    {
        native_pausePreviewFrameUpdate();
    }

    /**
     * @hide
     * Resume preview frame update.
     */
    public final void resumeWindowlessPreviewFrameUpdate()
    {
        native_resumePreviewFrameUpdate();
    }

    /**
     * @hide
     * Starts the smart scene detection.
     * After calling, the camera will notify {@link SceneModeListener} of the detected
     * scene modes.
     * Note that some scene modes (like "portrait") are not detected, unless
     * {@link #startFaceDetection()} has been called by the application.
     * If the scene detection has started, apps should not call this again
     * before calling {@link #stopSceneDetection()}.
     * @see #setSceneModeListener()
     */
    public final void startSceneDetection()
    {
        if (mSceneDetectionRunning) {
            throw new RuntimeException("Scene detection is already running");
        }
        native_startSceneDetection();
        mSceneDetectionRunning = true;
    }

    /**
     * @hide
     * Stops the smart scene detection.
     * @see #startSceneDetection()
     */
    public final void stopSceneDetection()
    {
        native_stopSceneDetection();
        mSceneDetectionRunning = false;
    }

    /**
     * Starts the panorama mode. Preview must be started before you can call this function.
     * <p>
     * In panorama mode the takePicture API will behave differently. The first takePicture call
     * will start the panorama capture sequence. During the capture sequence, images are captured
     * automatically while the camera is turned. At the same time, PanoramaListener
     * callbacks will be called for both giving displacement feedback during panning, and for the
     * taken snapshots.
     * <p>
     * The capturing will either end automatically after reaching the maximum count
     * of panorama snapshots, or by calling the takePicture API another time. At that point the
     * final JPEG is returned through the normal PictureCallback given to takePicture.
     * <p>
     * Raw and postview callback types are not supported.
     * <p>
     * Face detection will be automatically stopped as soon as the first image for the panorama has
     * been captured.
     * <p>
     * Flash will not fire during panorama regardless of the flash setting.
     * <p>
     * Exposure, focus and white balance should be locked by using setParameters before takePicture
     * is called first time, to get best image quality.
     * <p>
     * Smart scene detection and smart shutter functionality should be stopped with
     * {@link #stopSceneDetection()}, {@link #stopSmileShutter()} and {@link #stopBlinkShutter()}
     * before calling takePicture for the first time.
     * <p>
     * If the panorama mode has been started, apps should not call this again
     * before calling {@link #stopPanorama()}.
     *
     * @see #stopPanorama()
     * @see #setPanoramaListener(PanoramaListener listener)
     * @see #stopSceneDetection()
     * @hide
     */
    public final void startPanorama() {
        if(mPanoramaRunning) {
            throw new RuntimeException("Panorama is already running");
        }
        native_startPanorama();
        mPanoramaRunning = true;
    }

    /**
     * Stops the panorama mode.
     * @see #startPanorama()
     * @hide
     */
    public final void stopPanorama() {
        native_stopPanorama();
        mPanoramaRunning = false;
    }

    /**
     * @hide
     * Starts the smile detection Smart Shutter.
     * After calling, the camera will trigger on smile when user presses shutter
     * Note that smile detection doesn't work unless
     * {@link #startFaceDetection()} has been called by the application.
     * If the smile shutter has started, apps should not call this again
     * before calling {@link #stopSmileShutter()}.
     */
    public final void startSmileShutter()
    {
        if (mSmileShutterRunning) {
            throw new RuntimeException("Smile Shutter is already running");
        }
        native_startSmileShutter();
        mSmileShutterRunning = true;
    }

    /**
     * @hide
     * Stops the smile shutter trigger.
     * @see #startSmileShutter()
     */
    public final void stopSmileShutter()
    {
        native_stopSmileShutter();
        mSmileShutterRunning = false;
    }

    /**
     * @hide
     * Starts the blink shutter.
     * After calling, the camera will capture on eye blinking events
     * when the user press and hold the camera shutter key.
     * Note that this feature is not working, unless
     * {@link #startFaceDetection()} has been called by the application.
     * If the blink shutter has started, apps should not call this again
     * before calling {@link #stopBlinkShutter()}.
     */
    public final void startBlinkShutter()
    {
        if (mBlinkShutterRunning) {
            throw new RuntimeException("Blink Shutter is already running");
        }
        native_startBlinkShutter();
        mBlinkShutterRunning = true;
    }

    /**
     * @hide
     * Stops the blink shutter.
     * @see #startBlinkShutter()
     */
    public final void stopBlinkShutter()
    {
        native_stopBlinkShutter();
        mBlinkShutterRunning = false;
    }

    /**
     * @hide
     * Cancel capture on smart shutter even when no smile are detected.
     * !!! This must be used only when takePicture() has been initiated
     * !!! during either smile or blink shutter is started.
     */
    public final void cancelSmartShutterPicture()
    {
        native_cancelSmartShutterPicture();
    }

    /**
     * @hide
     * Force capture on smart shutter even when no smile are detected.
     * !!! This must be used only when takePicture() has been initiated
     * !!! during either smile or blink shutter is started.
     */
    public final void forceSmartShutterPicture()
    {
        native_forceSmartShutterPicture();
    }

    /**
     * @hide
     * Starts face recognition.
     * Before starting face recognition the application must start
     * face detection by calling {@link #startFaceDetection()}.
     * If face recognition has been started, application should not call
     * this again before calling {@link #stopFaceRecognition()}.
     */
    public final void startFaceRecognition()
    {
        native_startFaceRecognition();
    }

    /**
     * @hide
     * Stops face recognition.
     * @see #startFaceRecognition()
     */
    public final void stopFaceRecognition()
    {
        native_stopFaceRecognition();
    }

    /**
     * @hide
     * Starts continuous shooting.
     * App needs to call this to enable continuous capture mode.
     */
    public final void startContinuousShooting()
    {
        native_startContinuousShooting();
    }

    /**
     * @hide
     * Starts continuous shooting.
     * @see #startContinuousShooting()
     */
    public final void stopContinuousShooting()
    {
        native_stopContinuousShooting();
    }


    /**
     * Enable or disable XNR (eXtra Noise Reduction)
     *
     * XNR is a hardware accelerated process to reduce high-iso color noise.
     * XNR is a function with heavy computational requirements and has an
     * impact in image post-processing latencies.
     *
     * Availability of XNR is controlled by the scene mode and normally it is
     * enabled by default for night-scene mode(s).
     *
     * Changing scene mode overrules this control so the value must be set with
     * separate call to setParameters with below sequence.
     *
     * 1. change scene mode with separate call to setParameters() as
     *    scene will override the controls given at the same time.
     * 2. call getParameters() to update parameters changed by the scene
     * 3. check the support and state of XNR using getXNR() and getSupportedXNR()
     * 4. set changes to XNR if needed (setXNR())
     * 5. call setParameters()
     * @hide
     */
    public void setXNR(String value, Parameters params) {
        params.set(KEY_XNR, value);
    }

    /**
     * @hide
     */
    public String getXNR(Parameters params) {
        return params.get(KEY_XNR);
    }

    /**
     * @hide
     */
    public List<String> getSupportedXNR(Parameters params) {
        String str = params.get(KEY_XNR + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Enable or disable ANR (Advanced Noise Reduction)
     *
     * ANR is a hardware accelerated process for efficient reducing of Gaussian
     * noise from images taken in low light conditions. ANR allows images to be
     * taken with shorter exposure times and higher ISO and hereby helps to reduce
     * motion blur.
     *
     * Availability of ANR is controlled by the scene mode and normally it is
     * enabled by default for night-scene mode(s).
     *
     * Changing scene mode overrules this control so the value must be set with
     * separate call to setParameters with below sequence.
     *
     * 1. change scene mode with separate call to setParameters() as
     *    scene will override the controls given at the same time.
     * 2. call getParameters() to update parameters changed by the scene
     * 3. check the support and state of ANR using getANR() and getSupportedANR()
     * 4. set changes to ANR if needed (setANR())
     * 5. call setParameters()
     * @hide
     */
    public void setANR(String value, Parameters params) {
        params.set(KEY_ANR, value);
    }

    /**
     * @hide
     */
    public String getANR(Parameters params) {
        return params.get(KEY_ANR);
    }


    /**
     * @hide
     */
    public List<String> getSupportedANR(Parameters params) {
        String str = params.get(KEY_ANR + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Enable or Disable GDC (Geometric Distortion Correction)
     * Deprecated (TODO: remove)
     * @hide
     */
    public void setGDC(String value, Parameters params) {
        params.set(KEY_GDC, value);
    }

    /**
     * Deprecated (TODO: remove)
     * @hide
     */
    public String getGDC(Parameters params) {
        return params.get(KEY_GDC);
    }

    /**
     * Deprecated (TODO: remove)
     * @hide
     */
    public List<String> getSupportedGDC(Parameters params) {
        String str = params.get(KEY_GDC + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Enable or Disable Temporal Noise Reduction (TNR) for video
     * @hide
     */
    public void setTemporalNoiseReduction(String value, Parameters params) {
        params.set(KEY_TEMPORAL_NOISE_REDUCTION, value);
    }

    /**
     * @hide
     */
    public String getTemporalNoiseReduction(Parameters params) {
        return params.get(KEY_TEMPORAL_NOISE_REDUCTION);
    }

    /**
     * @hide
     */
    public List<String> getSupportedTemporalNoiseReduction(Parameters params) {
        String str = params.get(KEY_TEMPORAL_NOISE_REDUCTION + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public String getNoiseReductionAndEdgeEnhancement(Parameters params) {
        return params.get(KEY_NOISE_REDUCTION_AND_EDGE_ENHANCEMENT);
    }

    /**
     * @hide
     */
    public void setNoiseReductionAndEdgeEnhancement(String value, Parameters params) {
        params.set(KEY_NOISE_REDUCTION_AND_EDGE_ENHANCEMENT, value);
    }

    /**
     * @hide
     */
    public List<String> getSupportedNoiseReductionAndEdgeEnhancement(Parameters params) {
        String str = params.get(KEY_NOISE_REDUCTION_AND_EDGE_ENHANCEMENT + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public String getColorCorrection(Parameters params) {
        return params.get(KEY_MULTI_ACCESS_COLOR_CORRECTION);
    }

    /**
     * @hide
     */
    public void setColorCorrection(String value, Parameters params) {
        params.set(KEY_MULTI_ACCESS_COLOR_CORRECTION, value);
    }

    /**
     * @hide
     */
    public List<String> getSupportedColorCorrections(Parameters params) {
        String str = params.get(KEY_MULTI_ACCESS_COLOR_CORRECTION + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Set metering mode of AE (Auto Exposure) algorithm
     *
     * AE Metering mode is normally controlled automatically per scene mode and
     * use cases e.g pointed focusing. This control can be used to select AE
     * metering mode specifically from the given support list
     * (@see #getSupportedAEMeteringMode()).
     *
     * Changing scene mode overrules this control so the value must be set with
     * separate call to setParameters with below sequence.
     *
     * 1. change scene mode with separate call to setParameters() as
     *    scene will override the controls given at the same time.
     * 2. call getParameters() to update parameters changed by the scene
     * 3. check the support and state of XNR using getXNR() and getSupportedXNR()
     * 4. set changes to XNR if needed (setXNR())
     * 5. call setParameters()
     * @hide
     */
    public void setAEMeteringMode(String value, Parameters params) {
        params.set(KEY_AE_METERING_MODE, value);
    }

    /**
     * @hide
     */
    public String getAEMeteringMode(Parameters params) {
        return params.get(KEY_AE_METERING_MODE);
    }

    /**
     * @hide
     */
    public List<String> getSupportedAEMeteringModes(Parameters params) {
        String str = params.get(KEY_AE_METERING_MODE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Set operation mode for AE (Auto Exposure) algorithm
     *
     * AE mode is normally controlled automatically. This control can be used to
     * enforce AE mode to one of the give modes in supported list
     * (@see #getSupportedAEMode()).
     * @hide
     */
    public void setAEMode(String value, Parameters params) {
        params.set(KEY_AE_MODE, value);
    }

    /**
     * @hide
     */
    public String getAEMode(Parameters params) {
        return params.get(KEY_AE_MODE);
    }

    /**
     * @hide
     */
    public List<String> getSupportedAEModes(Parameters params) {
        String str = params.get(KEY_AE_MODE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public String getContrastMode(Parameters params) {
        return params.get(KEY_CONTRAST_MODE);
    }

    /**
     * @hide
     */
    public void setContrastMode(String value, Parameters params) {
        params.set(KEY_CONTRAST_MODE, value);
    }

    /**
     * @hide
     */
    public List<String> getSupportedContrastModes(Parameters params) {
        String str = params.get(KEY_CONTRAST_MODE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public String getSaturationMode(Parameters params) {
        return params.get(KEY_SATURATION_MODE);
    }

    /**
     * @hide
     */
    public void setSaturationMode(String value, Parameters params) {
        params.set(KEY_SATURATION_MODE, value);
    }

    /**
     * @hide
     */
    public List<String> getSupportedSaturationModes(Parameters params) {
        String str = params.get(KEY_SATURATION_MODE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public String getSharpnessMode(Parameters params) {
        return params.get(KEY_SHARPNESS_MODE);
    }

    /**
     * @hide
     */
    public void setSharpnessMode(String value, Parameters params) {
        params.set(KEY_SHARPNESS_MODE, value);
    }

    /**
     * @hide
     */
    public List<String> getSupportedSharpnessModes(Parameters params) {
        String str = params.get(KEY_SHARPNESS_MODE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public String getShutter(Parameters params) {
        return params.get(KEY_SHUTTER);
    }

    /**
     * @hide
     */
    public void setShutter(String value, Parameters params) {
        params.set(KEY_SHUTTER, value);
    }

    /**
     * @hide
     */
    public List<String> getSupportedShutter(Parameters params) {
        String str = params.get(KEY_SHUTTER + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public String getAperture(Parameters params) {
        return params.get(KEY_APERTURE);
    }

    /**
     * @hide
     */
    public void setAperture(String value, Parameters params) {
        params.set(KEY_APERTURE, value);
    }

    /**
     * @hide
     */
    public List<String> getSupportedAperture(Parameters params) {
        String str = params.get(KEY_APERTURE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public String getISO(Parameters params) {
        return params.get(KEY_ISO);
    }

    /**
     * @hide
     */
    public void setISO(String value, Parameters params) {
        params.set(KEY_ISO, value);
    }

    /**
     * @hide
     */
    public List<String> getSupportedISO(Parameters params) {
        String str = params.get(KEY_ISO + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * @hide
     */
    public void setExifMaker(String value, Parameters params) {
        params.set(KEY_EXIF_MAKER, value);
    }

    /**
     * @hide
     */
    public void setExifModel(String value, Parameters params) {
        params.set(KEY_EXIF_MODEL, value);
    }

    /**
     * @hide
     */
    public void setExifSoftware(String value, Parameters params) {
        params.set(KEY_EXIF_SOFTWARE, value);
    }

    /**
     * Set the mapping mode of AWB (Auto White Balance) algorithm
     *
     * AWB Mapping mode is normally controlled automatically and per scene mode.
     * This control can be used to select AWB mapping mode specifically from the
     * given support list (@see #getSupportedAWBMappingMode()).
     *
     * Changing scene mode overrules this control so the value must be set with
     * separate call to setParameters with below sequence.
     *
     * 1. change scene mode with separate call to setParameters() as
     *    scene will override the controls given at the same time.
     * 2. call getParameters() to update parameters changed by the scene
     * 3. check the support and state of AWBMappingMode using getAWBMappingMode()
     *    and getSupportedAWBMappingMode()
     * 4. set changes to mode if needed (setAWBMappingMode())
     * 5. call setParameters()
     * @param value new awb mapping mode.
     * @see #get getAWBMappingMode()
     * @hide
     */
    public void setAWBMappingMode(String value, Parameters params) {
        params.set(KEY_AWB_MAPPING_MODE, value);
    }


    /**
     * Gets the current AWB mapping mode.
     *
     * @return current awb mapping mode. null if awb mapping mode is not supported.
     * @see #AWB_MAPPING_INDOOR
     * @see #AWB_MAPPING_OUTDOOR
     * @hide
     */
    public String getAWBMappingMode(Parameters params) {
        return params.get(KEY_AWB_MAPPING_MODE);
    }

    /**
     * Gets the supported awb mapping mode.
     *
     * @return a list of supported awb mapping mode. null if awb mapping mode setting
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedAWBMappingModes(Parameters params) {
        String str = params.get(KEY_AWB_MAPPING_MODE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Gets the current color temperature.
     *
     * @return current color temperature.
     * @hide
     */
    public String getColorTemperature(Parameters params) {
        return params.get(KEY_COLOR_TEMPERATURE);
    }

    /**
     * Sets the current color temperature.
     *
     * @param value new color temperature.
     * @see #get getColorTemperature()
     * @hide
     */
    public void setColorTemperature(int value, Parameters params) {
        params.set(KEY_COLOR_TEMPERATURE, value);
    }

    /**
     * Gets the current af metering mode.
     *
     * @return current af metering mode. null if af metering mode is not supported.
     * @see #AF_METERING_MODE_AUTO
     * @see #AF_METERING_MODE_SPOT
     * @hide
     */
    public String getAFMeteringMode(Parameters params) {
        return params.get(KEY_AF_METERING_MODE);
    }

    /**
     * Sets the current af metering mode.
     *
     * @param value new af metering mode.
     * @see #get getAFMeteringMode()
     * @hide
     */
    public void setAFMeteringMode(String value, Parameters params) {
        params.set(KEY_AF_METERING_MODE, value);
    }

    /**
     * Gets the supported af metering mode.
     *
     * @return a list of supported af metering mode. null if af metering mode setting
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedAFMeteringModes(Parameters params) {
        String str = params.get(KEY_AF_METERING_MODE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Gets burst mode capture length.
     *
     * @return burst mode capture length.
     * @hide
     */
    public int getBurstLength(Parameters params) {
        String str = params.get(KEY_BURST_LENGTH);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    /**
     * Sets burst mode capture length.
     * NOTE: after starting burst capturing, application must wait
     * for all jpeg callbacks to return before starting preview.
     *
     * @param value burst mode capture length.
     * @hide
     */
    public void setBurstLength(int value, Parameters params) {
        params.set(KEY_BURST_LENGTH, value);
    }

    /**
     * Gets the supported burst mode capture length.
     *
     * @return a list of supported burst mode capure length. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedBurstLength(Parameters params) {
        String str = params.get(KEY_BURST_LENGTH + SUPPORTED_VALUES_SUFFIX);
        if (str == null || str.equals("") || str.equals("1")) {
            Log.v(TAG, "Return null for key:" + KEY_BURST_LENGTH + SUPPORTED_VALUES_SUFFIX);
            return null;
        } else {
            return split(str);
        }
    }

    /**
     * Gets burst mode fps.
     *
     * @return burst mode fps.
     * @hide
     */
    public int getBurstFps(Parameters params) {
        String str = params.get(KEY_BURST_FPS);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    /**
     * Sets burst mode fps.
     *
     * @param value burst mode fps.
     * @hide
     */
    public void setBurstFps(int value, Parameters params) {
        params.set(KEY_BURST_FPS, value);
    }

    /**
     * Gets the supported burst mode fps.
     *
     * @return a list of supported burst fps. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedBurstFps(Parameters params) {
        String str = params.get(KEY_BURST_FPS + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Gets burst mode speed.
     *
     * @return burst mode speed.
     * @hide
     */
    public String getBurstSpeed(Parameters params) {
        return params.get(KEY_BURST_SPEED);
    }

    /**
     * Sets burst mode speed.
     *
     * @param value burst mode speed. The value could be one which is got from the getSupportedBurstSpeed
     *         "fast", "medium", "low" could be set right now.
     *         "medium" is about 1/2 speed as "fast". "low" is about 1/4 speed as "fast".
     * @hide
     */
    public void setBurstSpeed(String value, Parameters params) {
        params.set(KEY_BURST_SPEED, value);
    }

    /**
     * Gets the supported burst mode speed.
     *
     * @return a list of supported burst speed. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedBurstSpeed(Parameters params) {
        String str = params.get(KEY_BURST_SPEED + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Gets start index for burst.
     *
     * @return burst mode capture length.
     * @hide
     */
    public int getBurstStartIndex(Parameters params) {
        return getInt(KEY_BURST_START_INDEX, 1, params);
    }

    /**
     * Sets start index for burst
     *
     * @param value burst mode capture length.
     * @hide
     */
    public void setBurstStartIndex(int value, Parameters params) {
        params.set(KEY_BURST_START_INDEX, value);
    }

    /**
     * Gets the supported burst start indexes.
     *
     * @return a list of supported burst mode capure length. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedBurstStartIndex(Parameters params) {
        return getSupportedValues(KEY_BURST_START_INDEX + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Gets max burst length that can use when negative start index is in use.
     *
     * @return max burst length with negative start index.
     * @hide
     */
    public int getMaxBurstLengthWithNegativeStartIndex(Parameters params) {
        return getInt(KEY_MAX_BURST_LENGTH_WITH_NEGATIVE_START_INDEX, 1, params);
    }

    /**
     * Deprecated (TODO: remove)
     * @hide
     */
    public List<String> getSupportedContinuousViewfinder() {
        return null;
    }

    /**
     * Deprecated (TODO: remove)
     * @hide
     */
    public void setContinuousViewfinder(boolean value, Parameters params) {}

    /**
     * Gets the supported values of preview update mode.
     *
     * Note: Even if supported by the platform, continuous preview
     * mode may not be available with all parameter combinations (e.g. it
     * may not work with all picture-sizes).
     *
     * @return a list of supported values for preview update mode. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedPreviewUpdateMode(Parameters params) {
        return getSupportedValues(KEY_PREVIEW_UPDATE_MODE + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Gets the current preview update mode.
     *
     * @see setPreviewUpdateMode()
     * @return current preview update mode (@see setPreviewUpdateMode())
     * @hide
     */
    public String getPreviewUpdateMode(Parameters params) {
        return params.get(KEY_PREVIEW_UPDATE_MODE);
    }

    /**
     * Sets the preview update mode
     *
     * When set to PREVIEW_UPDATE_MODE_STANDARD, standard camera API semantics
     * are followed. Preview updates will stop when takePicture() is
     * called.
     *
     * When set to PREVIEW_UPDATE_MODE_DURING_CAPTURE, preview will continue
     * to be updated during the take picture process, but preview frame
     * rate may drop. Application must not reset the preview window,
     * and must not assume preview to be automatically stopped after
     * takePicture(), as happens with default Camera API.
     *
     * When set to PREVIEW_UPDATE_MODE_CONTINUOUS, preview will continue to be
     * updated during take picture process (like "update-during-capture""), and
     * priority will be given to preview processing. This option may
     * produce a higher and more stable preview frame rate during
     * capture, but may negatively impact the time it takes to get
     * the output pictures.
     *
     * When set to PREVIEW_UPDATE_MODE_WINDOWLESS, preview will start without
     * call to android.hardware.Camera.setPreviewDisplay(). This is provided in
     * purpose to give application an oportunity to run camera use cases without
     * the preview window. In case the preview surface is anyhow given using
     * setPreviewDisplay(), the mode is switched to standard.
     *
     * @hide
     */
    public void setPreviewUpdateMode(String value, Parameters params) {
        params.set(KEY_PREVIEW_UPDATE_MODE, value);
    }

    /**
     * Gets the current raw data format.
     *
     * @return current raw data format. null if this feature is not supported.
     * @hide
     */
    public String getRAWDataFormat(Parameters params) {
        return params.get(KEY_RAW_DATA_FORMAT);
    }

    /**
     * Sets the current raw picture format.
     *
     * @param value new raw picture format
     * @see #get getRAWDataFormat()
     * @hide
     */
    public void setRAWDataFormat(String value, Parameters params) {
        params.set(KEY_RAW_DATA_FORMAT, value);
    }

    /**
     * Gets the supported raw data formats.
     *
     * @return a list of supported af raw data formats. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedRAWDataFormats(Parameters params) {
        String str = params.get(KEY_RAW_DATA_FORMAT + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Gets the current capture bracket mode.
     *
     * @return current capture bracket mode. null if this feature is not supported.
     * @hide
     */
    public String getCaptureBracket(Parameters params) {
        return params.get(KEY_CAPTURE_BRACKET);
    }

    /**
     * Sets the current capture bracket mode.
     * NOTE: after starting capturing with bracketing, application must wait
     * for all jpeg callbacks to return before starting preview.
     *
     * @param value new capture bracket mode
     * @hide
     */
    public void setCaptureBracket(String value, Parameters params) {
        params.set(KEY_CAPTURE_BRACKET, value);
    }

    /**
     * Gets the supported capture bracket mode.
     *
     * @return a list of supported capture bracket mode. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedCaptureBracket(Parameters params) {
        String str = params.get(KEY_CAPTURE_BRACKET + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Get the supported hw overlay rendering modes.
     *
     * @return the supported hw overlay rendering modes. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedHWOverlayRendering(Parameters params) {
        return split(params.get(KEY_HW_OVERLAY_RENDERING + SUPPORTED_VALUES_SUFFIX));
    }

    /**
     * Set the hw overlay rendering mode.
     * This can only be set before the preview is started
     * otherwise the command will be ignored. The application can check whether
     * the value of the parameter KEY_HW_OVERLAY_RENDERING changed to check
     * whether the command succeeded or not
     * @param value new overlay rendering mode
     * @hide
     */
    public void setHWOverlayRendering(String value, Parameters params) {
        params.set(KEY_HW_OVERLAY_RENDERING, value);
    }

    /**
     * Gets the current rotation mode.
     *
     * @return current rotation mode. null if this feature is not supported.
     * @hide
     */
    public String getRotationMode(Parameters params) {
        return params.get(KEY_ROTATION_MODE);
    }

    /**
     * Sets the current rotation mode.
     *
     * @param value new rotation mode
     * @hide
     */
    public void setRotationMode(String value, Parameters params) {
        params.set(KEY_ROTATION_MODE, value);
    }

    /**
     * Gets the supported rotation modes.
     *
     * @return a list of supported af rotation modes. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedRotationModes(Parameters params) {
        String str = params.get(KEY_ROTATION_MODE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Sets the focus distances
     * @hide
     */
    public void setFocusDistances(float[] input, Parameters params) {
        if (input == null || input.length != 3) {
            throw new IllegalArgumentException(
                    "input must be an float array with three elements.");
        }
        params.set(KEY_FOCUS_DISTANCES, "" + input[Parameters.FOCUS_DISTANCE_NEAR_INDEX] + "," +
                input[Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX] + "," + input[mParameters.FOCUS_DISTANCE_FAR_INDEX]);
    }

    /**
     * Sets the focus distances - for single input
     * @hide
     */
    public void setFocusDistance(float input, Parameters params) {
        params.set(KEY_FOCUS_DISTANCES, "" + input + "," + input + "," + input);
    }

    /**
     * Gets the focus distances - for single output
     * @hide
     */
    public float getFocusDistance(Parameters params) {
        float[] output = new float[3];
        splitFloat(params.get(KEY_FOCUS_DISTANCES), output);
        return output[Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX];
    }

    /**
     * Sets the focus distances - for single input
     * @hide
     */
    public void setFocusWindow(int input[], Parameters params) {
        if (input == null || input.length != 4) {
            throw new IllegalArgumentException(
                    "input must be an int array with four elements.");
        }
        params.set(KEY_FOCUS_WINDOW, "" + input[0]
                + "," + input[1] + "," + input[2] + "," + input[3]);
    }

    /**
     * Gets the current Ultra-low light (ULL) status.
     *
     * @return current ULL status.
     * @hide
     */
    public String getULL(Parameters params) {
        return params.get(KEY_ULL);
    }

    /**
     * Sets the Ultra-low light (ULL) status.
     *
     * @param value new Ultra-low light mode
     *
     * The behavior differs upon the set parameter value:
     * "on": forces ULL on, bypassing the 3A logic that normally would trigger the ULL processing.
     * NOTE: If the value is set to "on" when preview has already been started, a preview re-start is triggered. (for testing)
     * "off": ULL will not be used
     * "auto": ULL shooting is determined and triggered by the 3A logic (this is the normal mode)
     *
     * When the ULL image is taken (parameter values "on" and "auto"), the application will receive 2 JPEG images:
     * the first one is the normal snapshot via PictureCallback. Second one is the ULL image, received via the UllListener interface.
     *
     * @see IntelCamera.UllListener
     * @see IntelCamera.UllSnapshot
     * @hide
     */
    public void setULL(String value, Parameters params) {
        params.set(KEY_ULL, value);
    }

    /**
     * Gets the supported Ultra-low light (ULL) values.
     *
     * @return supported ULL values.
     *
     * @see IntelCamera#setULL(String)
     * @hide
     */
    public List<String> getSupportedULL(Parameters params) {
        return getSupportedValues(KEY_ULL + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Gets the current HDR Imaging mode.
     *
     * @return current HDR imaging mode. null if this feature is not supported.
     * @hide
     */
    public String getHDRImaging(Parameters params) {
        return params.get(KEY_HDR_IMAGING);
    }

    /**
     * Sets the current HDR Imaging mode.
     *
     * @param value new HDR Imaging mode
     * @hide
     */
    public void setHDRImaging(String value, Parameters params) {
        params.set(KEY_HDR_IMAGING, value);
    }

    /**
     * Gets the supported HDR Imaging mode.
     *
     * @return a list of supported HDR Imaging mode. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedHDRImaging(Parameters params) {
        return getSupportedValues(KEY_HDR_IMAGING + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Gets the current HDR Save Original mode.
     *
     * @return current HDR Save Original mode. null if this feature is not supported.
     * @hide
     */
    public String getHDRSaveOriginal(Parameters params) {
        return params.get(KEY_HDR_SAVE_ORIGINAL);
    }

    /**
     * Sets the current HDR Save Original mode.
     * NOTE: after starting HDR capturing with HDR Save Original on, application must wait
     * for all jpeg callbacks to return before starting preview.
     *
     * @param value new HDR Save Original mode
     * @hide
     */
    public void setHDRSaveOriginal(String value, Parameters params) {
        params.set(KEY_HDR_SAVE_ORIGINAL, value);
    }

    /**
     * Gets the supported HDR Save Original mode.
     *
     * @return a list of supported HDR Save Original mode. null if this feature
     *         is not supported.
     * @hide
     */
    public List<String> getSupportedHDRSaveOriginal(Parameters params) {
        String str = params.get(KEY_HDR_SAVE_ORIGINAL + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    private Camera.Size parseSize(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str, "x");
        int width = 0, height = 0;
        if (tokenizer.hasMoreElements())
            width = Integer.parseInt(tokenizer.nextToken());
        if (tokenizer.hasMoreElements())
            height = Integer.parseInt(tokenizer.nextToken());

        return mCameraDevice.new Size(width, height);
    }

    /**
     * Gets if the scene detection (hdr hint) is supported
     *
     * @return on or off if the feature is supported null if this feature
    is not supported
     * @hide
     */
    public List<String> getSupportedSceneDetection(Parameters params) {
        return getSupportedValues(KEY_SCENE_DETECTION + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Gets if the face recognition is supported
     *
     * @return on or off if the feature is supported null if this feature
    is not supported
     * @hide
     */
    public List<String> getSupportedFaceRecognition(Parameters params) {
        return getSupportedValues(KEY_FACE_RECOGNITION + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Gets if the panorama mode is supported
     *
     * @return on or off if the feature is supported null if this feature
    is not supported
     * @hide
     */
    public List<String> getSupportedPanorama(Parameters params) {
        return getSupportedValues(KEY_PANORAMA + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Gets the supported panorama live preview sizes.
     * @return a list of Size object.
     * @hide
     */
    public List<Camera.Size> getSupportedPanoramaLivePreviewSizes(Parameters params) {
        String str = params.get(KEY_SUPPORTED_PANORAMA_LIVE_PREVIEW_SIZES);
        ArrayList<String> sizeStrings = split(str);
        List<Camera.Size> sizes = new ArrayList<Camera.Size>();
        if (sizeStrings == null)
            return sizes;

        Iterator<String> it = sizeStrings.iterator();
        while(it.hasNext()) {
            String size = it.next();
            sizes.add(parseSize(size));
        }
        return sizes;
    }

    /**
     * Gets the maximum panorama snapshot count.
     * @return the max count
     * @hide
     */
    public int getMaximumPanoramaSnapshotCount(Parameters params) {
        return getInt(KEY_PANORAMA_MAX_SNAPSHOT_COUNT, 0, params);
    }

    /**
     * Returns the current panorama live preview size
     * @return size of live preview images
     * @hide
     */
    public Camera.Size getPanoramaLivePreviewSize(Parameters params) {
        String str = params.get(KEY_PANORAMA_LIVE_PREVIEW_SIZE);
        return parseSize(str);
    }

    /**
     * Sets the panorama live preview size. Live preview images are delivered via the
     * PanoramaListener. The size of the live preview size must be among the list of
     * resolutions in {@link #getSupportedPanoramaLivePreviewSizes()}
     * @hide
     */
    public void setPanoramaLivePreviewSize(int width, int height, Parameters params) {
        params.set(KEY_PANORAMA_LIVE_PREVIEW_SIZE, "" + width + "x" + height);
    }

    /**
     * Sets the smile detection threshold for smile shutter.
     *
     * @param value for smile detection in smart shutter (0 = not-strict to 100 = strict)
     * @hide
     */
    public void setSmileShutterThreshold(String value, Parameters params) {
        params.set(KEY_SMILE_SHUTTER_THRESHOLD, value);
    }

    /**
     * Gets if the smile detection smart shutter is supported
     *
     * @return on or off if the feature is supported null if this feature
    is not supported
     * @hide
     */
    public List<String> getSupportedSmileShutter(Parameters params) {
        return getSupportedValues(KEY_SMILE_SHUTTER + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Sets the blink detection threshold for blink shutter.
     *
     * @param value for blink detection in smart shutter (0 = strict to 100 = not-strict)
     * @hide
     */
    public void setBlinkShutterThreshold(String value, Parameters params) {
        params.set(KEY_BLINK_SHUTTER_THRESHOLD, value);
    }

    /**
     * Gets if the blink detection smart shutter is supported
     *
     * @return on or off if the feature is supported null if this feature
    is not supported
     * @hide
     */
    public List<String> getSupportedBlinkShutter(Parameters params) {
        return getSupportedValues(KEY_BLINK_SHUTTER + SUPPORTED_VALUES_SUFFIX, params);
    }


    /**
     * Gets the current slow motion rate value.
     *
     * @hide
     */
    public String getSlowMotionRate(Parameters params) {
        return params.get(KEY_SLOW_MOTION_RATE);
    }

    /**
     * Sets the slow motion rate value.
     *
     * @hide
     */
    public void setSlowMotionRate(String value, Parameters params) {
        params.set(KEY_SLOW_MOTION_RATE, value);
    }

    /**
     * Gets the support list for slow motion value
     *
     * @hide
     */
    public List<String> getSupportedSlowMotionRate(Parameters params) {
        String str = params.get(KEY_SLOW_MOTION_RATE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Gets the support list for the pair of resolution and fps in recording mode
     *
     * @hide
     */
    public List<String> getSupportedHighSpeedResolutionFps(Parameters params) {
        return getSupportedValues(KEY_HIGH_SPEED_RESOLUTION_FPS + SUPPORTED_VALUES_SUFFIX, params);
    }

    /**
     * Gets the current recording frame rate.
     *
     * @hide
     */
    public int getRecordingFrameRate(Parameters params) {
        return getInt(KEY_RECORDING_FRAME_RATE, 0, params);
    }

    /**
     * Sets the recording frame rate.
     *
     * @hide
     */
    public void setRecordingFrameRate(int fps, Parameters params) {
        params.set(KEY_RECORDING_FRAME_RATE, fps);
    }

    /**
     * Gets the supported recording frame rates.
     *
     * @hide
     */
    public List<String> getSupportedRecordingFrameRate(Parameters params) {
        String str = params.get(KEY_RECORDING_FRAME_RATE + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Sets the save mirrored mode.
     *
     * @hide
     */
    public void setSaveMirrored(String value, Parameters params) {
        params.set(KEY_SAVE_MIRRORED, value);
    }

    /**
     * Gets the supported values for save mirrored.
     *
     * @return on or off if the feature is supported. null if this feature
    is not supported
     * @hide
     */
    public List<String> getSupportedSaveMirrored(Parameters params) {
        String str = params.get(KEY_SAVE_MIRRORED + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Sets GPS Image direction. This will be stored in JPEG EXIF header.
     *
     * @param value, the range of it is from 0.00 to 359.99.
     * @hide
     */
    public void setGpsImgDirection(double value, Parameters params) {
        params.set(KEY_GPS_IMG_DIRECTION, Double.toString(value));
    }

    /**
     * Set GPS Image direction reference. This will be stored in JPEG EXIF header.
     *
     * the supported value could be get from the function getSupportedGpsImgDirectionRef
     * @param value, it should be one of GPS_IMG_DIRECTION_REF_TRUE and GPS_IMG_DIRECTION_REF_MAGNETIC
     * @hide
     */
    public void setGpsImgDirectionRef(String value, Parameters params) {
        params.set(KEY_GPS_IMG_DIRECTION_REF, value);
    }

    /**
     * Gets the supported values for GPS Image direction reference.
     *
     * @return one of GPS_IMG_DIRECTION_REF_TRUE and GPS_IMG_DIRECTION_REF_MAGNETIC
     * @hide
     */
    public List<String> getSupportedGpsImgDirectionRef(Parameters params) {
        String str = params.get(KEY_GPS_IMG_DIRECTION_REF + SUPPORTED_VALUES_SUFFIX);
        return split(str);
    }

    /**
     * Gets the intelligent mode which are supported
     *
     * @return the supported intelligent mode
     * @hide
     */
    public List<String> getSupportedIntelligentMode(Parameters params) {
        return getSupportedValues(KEY_INTELLIGENT_MODE + SUPPORTED_VALUES_SUFFIX, params);
    }

    public void setIntelligentMode(boolean toggle, Parameters params) {
        params.set(KEY_INTELLIGENT_MODE, toggle ? TRUE : FALSE);
    }

    public String getIntelligentMode(Parameters params) {
        return params.get(KEY_INTELLIGENT_MODE);
    }

    /**
     * Sets dual video state
     *
     * @hide
     */
    public void setDualVideo(boolean toggle, Parameters params) {
        params.set(KEY_DUAL_VIDEO, toggle ? TRUE : FALSE);
    }

    /**
     * Gets dual video state
     *
     * @return true if dual video is enabled.
     * @hide
     */
    public boolean getDualVideo(Parameters params) {
        String str = params.get(KEY_DUAL_VIDEO);
        return TRUE.equals(str);
    }

    /**
     * Gets if dual video is supported
     *
     * @return true if dual video is supported.
     * @hide
     */
    public boolean isDualVideoSupported(Parameters params) {
        String str = params.get(KEY_DUAL_VIDEO_SUPPORTED);
        return TRUE.equals(str);
    }

    /**
     * Sets dual mode state
     *
     * @hide
     */
    public void setDualMode(boolean toggle, Parameters params) {
        params.set(KEY_DUAL_MODE, toggle ? TRUE : FALSE);
    }

    /**
     * Gets dual mode state
     *
     * @return true if dual mode is enabled.
     * @hide
     */
    public boolean getDualMode(Parameters params) {
        String str = params.get(KEY_DUAL_MODE);
        return TRUE.equals(str);
    }

    /**
     * Gets if dual mode is supported
     *
     * @return true if dual mode is supported.
     * @hide
     */
    public boolean isDualModeSupported(Parameters params) {
        String str = params.get(KEY_DUAL_MODE_SUPPORTED);
        return TRUE.equals(str);
    }

    /**
     * Gets the dual camera mode which are supported
     *
     * @return the supported dual camera mode
     * @hide
     */
    public List<String> getSupportedDualCameraMode(Parameters params) {
        return getSupportedValues(KEY_DUAL_CAMERA_MODE + SUPPORTED_VALUES_SUFFIX, params);
    }

    public void setDualCameraMode(String value, Parameters params) {
        params.set(KEY_DUAL_CAMERA_MODE, value);
    }

    public String getDualCameraMode(Parameters params) {
        return params.get(KEY_DUAL_CAMERA_MODE);
    }

    /**
     * Sets continuous shooting state
     *
     * @hide
     */
    public void setContinuousShooting(String value, Parameters params) {
        params.set(KEY_CONTINUOUS_SHOOTING, value);
    }

    /**
     * Gets continuous shooting state
     *
     * @return true if continuous shooting is enabled.
     * @hide
     */
    public boolean getContinuousShooting(Parameters params) {
        String str = params.get(KEY_CONTINUOUS_SHOOTING);
        return TRUE.equals(str);
    }

    /**
     * Gets if continuous shooting is supported
     *
     * @return true if continuous shooting is supported.
     * @hide
     */
    public boolean isContinuousShootingSupported(Parameters params) {
        String str = params.get(KEY_CONTINUOUS_SHOOTING_SUPPORTED);
        return TRUE.equals(str);
    }

    /**
     * Splits a comma delimited string to an ArrayList of String.
     * @Return null if the passing string is null or the size is 0.
     */
    private ArrayList<String> split(String str) {
        if (str == null) return null;

        // Use StringTokenizer because it is faster than split.
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        ArrayList<String> substrings = new ArrayList<String>();
        while (tokenizer.hasMoreElements()) {
            substrings.add(tokenizer.nextToken());
        }
        return substrings;
    }

    // Get supported values for a key
    private List<String> getSupportedValues(String key, Parameters params) {
        String str = params.get(key);
        if (str == null || str.equals("")) {
            Log.v(TAG, "Return null for key:" + key);
            return null;
        }
        return split(str);
    }

    // Splits a comma delimited string to an ArrayList of Float.
    private void splitFloat(String str, float[] output) {
        if (str == null) return;

        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        int index = 0;
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            output[index++] = Float.parseFloat(token);
        }
    }

    // Returns the value of a integer parameter.
    private int getInt(String key, int defaultValue, Parameters params) {
        try {
            return Integer.parseInt(params.get(key));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
