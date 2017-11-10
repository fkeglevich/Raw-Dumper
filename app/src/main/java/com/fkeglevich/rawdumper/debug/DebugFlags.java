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
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 06/11/17.
 */

@SuppressWarnings("SimplifiableConditionalExpression")
public class DebugFlags
{
    private static final boolean DISABLE_MANDATORY_ROOT = false;
    private static final boolean OPEN_FRONT_CAMERA_FIRST = true;

    public static boolean isDisableMandatoryRoot()
    {
        return returnFlagIfDebugging(DISABLE_MANDATORY_ROOT, false);
    }

    public static boolean shouldOpenFrontCameraFirst()
    {
        return returnFlagIfDebugging(OPEN_FRONT_CAMERA_FIRST, false);
    }

    private static boolean returnFlagIfDebugging(boolean flagValue, boolean defaultValue)
    {
        return BuildConfig.DEBUG ? flagValue : defaultValue;
    }
}
