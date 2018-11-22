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

/**
 * Collection of (mostly) every Intel specific camera parameter.
 *
 * Created by Flávio Keglevich on 31/07/2017.
 */

@SuppressWarnings("unused")
public class IntelParameters
{
    public static final String SUPPORTED_VALUES_SUFFIX = "-values";
    public static final String KEY_FOCUS_WINDOW = "focus-window";
    public static final String KEY_XNR = "xnr";
    public static final String KEY_ANR = "anr";
    public static final String KEY_GDC = "gdc";
    public static final String KEY_TEMPORAL_NOISE_REDUCTION = "temporal-noise-reduction";
    public static final String KEY_NOISE_REDUCTION_AND_EDGE_ENHANCEMENT = "noise-reduction-and-edge-enhancement";
    public static final String KEY_MULTI_ACCESS_COLOR_CORRECTION = "multi-access-color-correction";
    public static final String KEY_AE_MODE = "ae-mode";
    public static final String KEY_AE_METERING_MODE = "ae-metering-mode";
    public static final String KEY_SHUTTER = "shutter";
    public static final String KEY_APERTURE = "aperture";
    public static final String KEY_ISO = "iso";
    public static final String KEY_AF_METERING_MODE = "af-metering-mode";
    public static final String KEY_AWB_MAPPING_MODE = "awb-mapping-mode";
    public static final String KEY_COLOR_TEMPERATURE = "color-temperature";
    public static final String KEY_CAPTURE_BRACKET = "capture-bracket";
    public static final String KEY_ROTATION_MODE = "rotation-mode";

    public static final String KEY_CONTRAST_MODE = "contrast-mode";
    public static final String KEY_SATURATION_MODE = "saturation-mode";
    public static final String KEY_SHARPNESS_MODE = "sharpness-mode";

    // raw
    public static final String KEY_RAW_DATA_FORMAT = "raw-data-format";
    public static final String RAW_DATA_FORMAT_NONE = "none";
    public static final String RAW_DATA_FORMAT_YUV = "yuv";
    public static final String RAW_DATA_FORMAT_BAYER = "bayer";

    // HDR
    public static final String KEY_HDR_IMAGING = "hdr-imaging";
    public static final String KEY_HDR_SAVE_ORIGINAL = "hdr-save-original";

    // Ultra low light
    public static final String KEY_ULL = "ull";

    // panorama
    public static final String KEY_PANORAMA = "panorama";

    // face detection and recognition
    public static final String KEY_FACE_RECOGNITION = "face-recognition";

    // scene detection
    public static final String KEY_SCENE_DETECTION = "scene-detection";

    // smart shutter
    public static final String KEY_SMILE_SHUTTER = "smile-shutter";
    public static final String KEY_SMILE_SHUTTER_THRESHOLD = "smile-shutter-threshold";
    public static final String KEY_BLINK_SHUTTER = "blink-shutter";
    public static final String KEY_BLINK_SHUTTER_THRESHOLD = "blink-shutter-threshold";

    // GPS extension
    public static final String KEY_GPS_IMG_DIRECTION = "gps-img-direction";
    public static final String KEY_GPS_IMG_DIRECTION_REF = "gps-img-direction-ref";
    // possible value for the KEY_GPS_IMG_DIRECTION_REF
    public static final String GPS_IMG_DIRECTION_REF_TRUE = "true-direction";
    public static final String GPS_IMG_DIRECTION_REF_MAGNETIC = "magnetic-direction";

    // intelligent mode
    public static final String KEY_INTELLIGENT_MODE = "intelligent-mode";

    // hw overlay rendering
    public static final String KEY_HW_OVERLAY_RENDERING = "overlay-render";

    // burst capture
    public static final String KEY_BURST_LENGTH = "burst-length";
    public static final String KEY_BURST_FPS = "burst-fps";
    public static final String KEY_BURST_SPEED = "burst-speed";
    public static final String KEY_BURST_START_INDEX = "burst-start-index";
    public static final String KEY_MAX_BURST_LENGTH_WITH_NEGATIVE_START_INDEX = "burst-max-length-negative";

    // values for af metering mode
    public static final String AF_METERING_MODE_AUTO = "auto";
    public static final String AF_METERING_MODE_SPOT = "spot";

    public static final String KEY_FOCUS_DISTANCES = "focus-distances";
    public static final String KEY_ANTIBANDING = "antibanding";

    public static final String KEY_PANORAMA_LIVE_PREVIEW_SIZE = "panorama-live-preview-size";
    public static final String KEY_SUPPORTED_PANORAMA_LIVE_PREVIEW_SIZES = "panorama-live-preview-sizes";
    public static final String KEY_PANORAMA_MAX_SNAPSHOT_COUNT = "panorama-max-snapshot-count";

    // preview update
    public static final String KEY_PREVIEW_UPDATE_MODE = "preview-update-mode";
    public static final String PREVIEW_UPDATE_MODE_STANDARD = "standard";
    public static final String PREVIEW_UPDATE_MODE_DURING_CAPTURE = "during-capture";
    public static final String PREVIEW_UPDATE_MODE_CONTINUOUS = "continuous";
    public static final String PREVIEW_UPDATE_MODE_WINDOWLESS = "windowless";

    // preview keep alive
    public static final String KEY_PREVIEW_KEEP_ALIVE = "preview-keep-alive";

    // high speed recording, slow motion playback
    public static final String KEY_SLOW_MOTION_RATE = "slow-motion-rate";
    public static final String KEY_HIGH_SPEED_RESOLUTION_FPS = "high-speed-resolution-fps";
    public static final String KEY_RECORDING_FRAME_RATE = "recording-fps";

    // dual mode
    public static final String KEY_DUAL_MODE = "dual-mode";
    public static final String KEY_DUAL_MODE_SUPPORTED = "dual-mode-supported";

    // dual video
    public static final String KEY_DUAL_VIDEO = "dual-video";
    public static final String KEY_DUAL_VIDEO_SUPPORTED = "dual-video-supported";

    /* dual camera mode */
    public static final String KEY_DUAL_CAMERA_MODE = "dual-camera-mode";
    public static final String DUAL_CAMERA_MODE_NORMAL = "normal";
    public static final String DUAL_CAMERA_MODE_DEPTH = "depth";

    // Exif data
    public static final String KEY_EXIF_MAKER = "exif-maker-name";
    public static final String KEY_EXIF_MODEL = "exif-model-name";
    public static final String KEY_EXIF_SOFTWARE = "exif-software-name";

    // Save still image and recorded video as mirrored (for front camera only)
    public static final String KEY_SAVE_MIRRORED = "save-mirrored";

    // continuous shooting.
    public static final String KEY_CONTINUOUS_SHOOTING = "continuous-shooting";
    public static final String KEY_CONTINUOUS_SHOOTING_SUPPORTED = "continuous-shooting-supported";

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    // values for ae mode setting.
    public static final String AE_MODE_AUTO = "auto";
    public static final String AE_MODE_MANUAL = "manual";
    public static final String AE_MODE_SHUTTER_PRIORITY = "shutter-priority";
    public static final String AE_MODE_APERTURE_PRIORITY = "aperture-priority";

    // Values for ae metering setting.
    public static final String AE_METERING_AUTO = "auto";
    public static final String AE_METERING_SPOT = "spot";
    public static final String AE_METERING_CENTER = "center";
    public static final String AE_METERING_CUSTOMIZED = "customized";

    // Values for awb mapping mode.
    public static final String AWB_MAPPING_AUTO = "auto";
    public static final String AWB_MAPPING_INDOOR = "indoor";
    public static final String AWB_MAPPING_OUTDOOR = "outdoor";

    public static final String FLASH_MODE_DAY_SYNC = "day-sync";
    public static final String FLASH_MODE_SLOW_SYNC = "slow-sync";
    public static final String FOCUS_MODE_MANUAL = "manual";
    public static final String FOCUS_MODE_TOUCH = "touch";

    public static final String SLOW_MOTION_RATE_1x = "1x";
    public static final String SLOW_MOTION_RATE_2x = "2x";
    public static final String SLOW_MOTION_RATE_3x = "3x";
    public static final String SLOW_MOTION_RATE_4x = "4x";

    // value of contrast mode
    public static final String CONTRAST_MODE_NORMAL = "normal";
    public static final String CONTRAST_MODE_SOFT = "soft";
    public static final String CONTRAST_MODE_HARD = "hard";

    // value of saturation mode
    public static final String SATURATION_MODE_NORMAL = "normal";
    public static final String SATURATION_MODE_LOW = "low";
    public static final String SATURATION_MODE_HIGH = "high";

    // value of sharpness mode
    public static final String SHARPNESS_MODE_NORMAL = "normal";
    public static final String SHARPNESS_MODE_SOFT = "soft";
    public static final String SHARPNESS_MODE_HARD = "hard";

    // Extra continuous burst parameters
    public static final String CONTINUOUS_BURST_NUM = "continuous-burst-num";
    public static final String CONTINUOUS_BURST_FPS = "continuous-burst-fps";
}
