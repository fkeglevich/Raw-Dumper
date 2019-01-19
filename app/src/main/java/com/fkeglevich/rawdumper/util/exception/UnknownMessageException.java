/*
 * Copyright 2019, Flávio Keglevich
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

package com.fkeglevich.rawdumper.util.exception;

import android.content.Context;

import com.fkeglevich.rawdumper.R;

public class UnknownMessageException extends MessageException
{
    public UnknownMessageException(Throwable cause)
    {
        super(cause);
    }

    @Override
    public String getMessageResource(Context context)
    {
        return context.getResources().getString(R.string.unknown_error, getCause().getMessage());
    }
}
