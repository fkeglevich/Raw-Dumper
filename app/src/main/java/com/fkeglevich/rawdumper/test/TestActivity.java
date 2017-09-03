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

package com.fkeglevich.rawdumper.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fkeglevich.rawdumper.BuildConfig;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Flávio Keglevich on 03/09/2017.
 * TODO: Add a class header comment!
 */

public class TestActivity extends AppCompatActivity
{
    private volatile int timeout;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG)
        {
            Test test = TestSelector.getCurrentTest();
            if (test == null) return;

            timeout = test.getTimeout();

            if (timeout > 0)
            {
                Timer timer = new Timer();
                timer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        Log.i("test-timeout", "Timeout after " + timeout + " ms, now exiting!");
                        System.exit(0);
                    }
                }, timeout);
            }

            try
            {
                test.executeTest();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            throw new RuntimeException("Tests should only be used in debug mode!");
        }
    }
}
