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
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.IOException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/11/17.
 */

public class CameraRestartFunction extends ThrowingAsyncFunction<RestartableCamera, Nothing, MessageException>
{
    @Override
    protected Nothing call(RestartableCamera camera) throws MessageException
    {
        try
        {
            camera.restartCamera();
        }
        catch (IOException e)
        {
            throw new CameraOpenException();
        }

        return Nothing.NOTHING;
    }
}
