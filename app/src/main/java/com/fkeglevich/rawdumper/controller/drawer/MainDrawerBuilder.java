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

package com.fkeglevich.rawdumper.controller.drawer;

import android.os.Build;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

/**
 * Created by flavio on 21/11/17.
 */

public class MainDrawerBuilder
{
    private DrawerBuilder drawerBuilder;

    public MainDrawerBuilder(ActivityReference reference)
    {
        drawerBuilder = new DrawerBuilder()
                .withActivity(reference.weaklyGet())
                .withFullscreen(true)
                .withCloseOnClick(false)
                .withSelectedItem(-1)
                .withDisplayBelowStatusBar(false)
                .withMultiSelect(true);
    }

    public Drawer build()
    {
        Drawer result = drawerBuilder.build();
        if (Build.VERSION.SDK_INT >= 19)
            result.getDrawerLayout().setFitsSystemWindows(false);

        return result;
    }
}
