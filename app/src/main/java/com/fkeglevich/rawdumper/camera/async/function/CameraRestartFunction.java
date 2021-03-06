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

package com.fkeglevich.rawdumper.camera.async.function;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunction;
import com.fkeglevich.rawdumper.camera.async.direct.RestartableCamera;
import com.fkeglevich.rawdumper.camera.exception.CameraOpenException;
import com.fkeglevich.rawdumper.util.ThreadUtil;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.IOException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/11/17.
 */

public class CameraRestartFunction extends ThrowingAsyncFunction<RestartableCamera, Void, MessageException>
{
    private static final int SAFE_RESTART_DELAY = 250;

    @Override
    protected Void call(RestartableCamera camera) throws MessageException
    {
        try
        {
            camera.restartCamera();
            ThreadUtil.simpleDelay(SAFE_RESTART_DELAY);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new CameraOpenException();
        }

        return null;
    }
}
