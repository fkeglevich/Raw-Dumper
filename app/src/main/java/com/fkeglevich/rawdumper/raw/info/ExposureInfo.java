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

package com.fkeglevich.rawdumper.raw.info;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Contains all implementation-specific information
 * related to manual exposure available on the device.
 *
 * Created by Flávio Keglevich on 18/06/2017.
 */

@Keep
@SuppressWarnings("unused")
public class ExposureInfo
{
    private List<String> isoValues;
    private String isoParameter;
    private String isoAutoValue;

    private List<String> shutterSpeedValues;
    private String shutterSpeedParameter;
    private String shutterSpeedAutoValue;

    public List<String> getIsoValues()
    {
        return isoValues;
    }

    public String getIsoParameter()
    {
        return isoParameter;
    }

    public String getIsoAutoValue()
    {
        return isoAutoValue;
    }

    public List<String> getShutterSpeedValues()
    {
        return shutterSpeedValues;
    }

    public String getShutterSpeedParameter()
    {
        return shutterSpeedParameter;
    }

    public String getShutterSpeedAutoValue()
    {
        return shutterSpeedAutoValue;
    }
}
