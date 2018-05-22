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

package com.fkeglevich.rawdumper.debug;

import com.fkeglevich.rawdumper.BuildConfig;

/**
 * Various flags for debugging purposes.
 * All of them are automatically set to their default values on release builds.
 *
 * Created by Flávio Keglevich on 06/11/17.
 */

@SuppressWarnings("SimplifiableConditionalExpression")
public class DebugFlag
{
    private static final DebugFlag DISABLE_MANDATORY_ROOT   = new DebugFlag(false, false);
    private static final DebugFlag OPEN_FRONT_CAMERA_FIRST  = new DebugFlag(false, false);
    private static final DebugFlag RETRY_PIPELINE_SIMULATOR = new DebugFlag(false, false);
    private static final DebugFlag FORCE_RETRY_PIPELINE     = new DebugFlag(false, false);
    private static final DebugFlag FORCE_RAW_ZENFONE_ZOOM   = new DebugFlag(false, false);
    private static final DebugFlag DISABLE_LOGCAT_SERVICE   = new DebugFlag(false, false);
    private static final DebugFlag IGNORE_PIC_SIZE_MISMATCH = new DebugFlag(false, false);
    private static final DebugFlag DONT_SAVE_PICTURES       = new DebugFlag(false, false);
    private static final DebugFlag ENABLE_EMULATOR          = new DebugFlag(false, false);
    private static final DebugFlag DONT_USE_GAIN_MAPS       = new DebugFlag(false, false);

    private static boolean or(DebugFlag first, DebugFlag second)
    {
        return first.get() || second.get();
    }

    private final boolean value;
    private final boolean defaultValue;

    private DebugFlag(boolean value, boolean defaultValue)
    {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    private boolean get()
    {
        return BuildConfig.DEBUG ? value : defaultValue;
    }

    public static boolean isDisableMandatoryRoot()
    {
        return or(ENABLE_EMULATOR, DISABLE_MANDATORY_ROOT);
    }

    public static boolean shouldOpenFrontCameraFirst()
    {
        return OPEN_FRONT_CAMERA_FIRST.get();
    }

    public static boolean usingRetryPipelineSimulator()
    {
        return RETRY_PIPELINE_SIMULATOR.get();
    }

    public static boolean isForceRetryingPipeline()
    {
        return FORCE_RETRY_PIPELINE.get();
    }

    public static boolean isForceRawZenfoneZoom()
    {
        return FORCE_RAW_ZENFONE_ZOOM.get();
    }

    public static boolean isDisableLogcatService()
    {
        return DISABLE_LOGCAT_SERVICE.get();
    }

    public static boolean shouldIgnorePicSizeMismatch()
    {
        return or(ENABLE_EMULATOR, IGNORE_PIC_SIZE_MISMATCH);
    }

    public static boolean dontSavePictures()
    {
        return DONT_SAVE_PICTURES.get();
    }

    public static boolean getDontUseGainMaps()
    {
        return DONT_USE_GAIN_MAPS.get();
    }
}
