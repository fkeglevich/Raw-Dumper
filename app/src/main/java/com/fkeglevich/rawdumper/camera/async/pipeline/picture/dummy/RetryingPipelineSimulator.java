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

package com.fkeglevich.rawdumper.camera.async.pipeline.picture.dummy;

import android.hardware.Camera;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.io.Directories;
import com.fkeglevich.rawdumper.su.MainSUShell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 11/11/17.
 */

public class RetryingPipelineSimulator
{
    private static final String TAG = "RetryingSimulator";

    public static void simulate(CameraContext cameraContext, final Camera.ErrorCallback errorCallback)
    {
        String dumpDirectory = cameraContext.getDeviceInfo().getDumpDirectoryLocation();
        File dummyPicture = new File(Directories.getPicturesDirectory(), "dummy.i3av4");

        byte[] buffer = new byte[calculateDummyPictureSize(cameraContext)];
        Arrays.fill(buffer, (byte)127);

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(dummyPicture);
            fos.write(buffer);
            Log.i(TAG, "Dummy picture successfully created!");
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "FileNotFoundException: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    Log.e(TAG, "IOException while close(): " + e.getMessage());
                }
            }
        }

        MainSUShell.getInstance().addSingleCommand("mv " + dummyPicture.getAbsolutePath() + " " + dumpDirectory,
                result ->
                {
                    if (result.isSuccess())
                    {
                        Log.i(TAG, "Dummy picture successfully moved!");
                        errorCallback.onError(0, null);
                    }
                });
    }

    private static int calculateDummyPictureSize(CameraContext cameraContext)
    {
        //just return a quite large value
        return 26087424 + 5;
    }
}
