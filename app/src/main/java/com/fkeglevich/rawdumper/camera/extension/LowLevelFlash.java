/*
 * Copyright 2018, Flávio Keglevich
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

import android.util.Log;

import com.fkeglevich.rawdumper.camera.data.DualLed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 03/07/18.
 */
public class LowLevelFlash implements LowLevelFlashInterface
{
    private static final String FLASH_BRIGHTNESS_PATH = "/proc/driver/asus_flash_brightness";
    private static final File FLASH_BRIGHTNESS_FILE = new File(FLASH_BRIGHTNESS_PATH);

    private static final String TAG = "LowLevelFlash";

    @Override
    public boolean isAvailable()
    {
        return FLASH_BRIGHTNESS_FILE.canWrite();
    }

    @Override
    public DualLed getDualLedValue()
    {
        return new DualLed(DualLed.LED_VALUE_TURNED_OFF, getLowLevelFlashIntensity());
    }

    @Override
    public void setDualLedValue(DualLed value)
    {
        /*
        07-03 19:54:29.130 24432-24432/? I/evich.rawdumper: type=1400 audit(0.0:3880): avc: denied { write } for name="asus_flash_brightness" dev="proc" ino=4026532400 scontext=u:r:untrusted_app:s0:c512,c768 tcontext=u:object_r:proc:s0 tclass=file permissive=1
         */
        setLowLevelFlashIntensity(value.getHighLedValue());
    }

    private void setLowLevelFlashIntensity(int intensity)
    {
        try(Writer writer = new BufferedWriter(new FileWriter(FLASH_BRIGHTNESS_PATH)))
        {
            writer.write("" + intensity);
        }
        catch (IOException e)
        {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private int getLowLevelFlashIntensity()
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(FLASH_BRIGHTNESS_PATH)))
        {
            return Integer.parseInt(reader.readLine());
        }
        catch (IOException | NumberFormatException e)
        {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
