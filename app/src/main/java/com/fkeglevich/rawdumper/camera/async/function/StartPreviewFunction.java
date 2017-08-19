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

import com.fkeglevich.rawdumper.async.Locked;
import com.fkeglevich.rawdumper.async.function.AsyncFunction;
import com.fkeglevich.rawdumper.camera.shared.SharedCamera;

/**
 * Created by Flávio Keglevich on 14/08/2017.
 * TODO: Add a class header comment!
 */

public class StartPreviewFunction extends AsyncFunction<Locked<SharedCamera>, Void>
{
    @Override
    protected Void call(Locked<SharedCamera> argument)
    {
        synchronized (argument.getLock())
        {
            argument.get().getCamera().startPreview();
        }
        return null;
    }
}
