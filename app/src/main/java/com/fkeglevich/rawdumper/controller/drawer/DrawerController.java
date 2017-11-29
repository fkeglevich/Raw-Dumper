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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.util.event.DefaultPreventer;
import com.fkeglevich.rawdumper.util.event.EventListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

/**
 * Created by flavio on 20/11/17.
 */

public class DrawerController
{
    private final Drawer drawer;
    private final ActivityReference reference;

    public DrawerController(ActivityReference reference)
    {
        this.reference = reference;
        this.drawer = buildDrawer(reference);
        setupDrawerButton();
        setupEvents();
    }

    private void setupDrawerButton()
    {
        AppCompatActivity activity = reference.weaklyGet();

        ImageButton modesButton = (ImageButton)activity.findViewById(R.id.modesButton);
        modesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawer.openDrawer();
            }
        });
    }

    private void setupEvents()
    {
        reference.onBackPressed.addListener(new EventListener<DefaultPreventer>()
        {
            @Override
            public void onEvent(DefaultPreventer eventData)
            {
                if (drawer.isDrawerOpen())
                {
                    eventData.preventDefault();
                    drawer.closeDrawer();
                }
            }
        });
    }

    private Drawer buildDrawer(ActivityReference reference)
    {
        Drawer result = new DrawerBuilder()
                .withActivity(reference.weaklyGet())
                .withFullscreen(true)
                .withCloseOnClick(false)
                .withSelectedItem(-1)
                .withDisplayBelowStatusBar(false)
                .withMultiSelect(true)
                .addDrawerItems(
                        new SecondaryDrawerItem()
                                .withName("Picture Settings")
                                .withSelectable(false),

                        new ExpandableDrawerItem()
                                .withName("Picture Mode")
                                .withIcon(R.drawable.ic_photo_camera_white_24dp)
                                .withIconColor(0xB3FFFFFF)
                                .withIconTintingEnabled(true)
                                .withDescription("Normal")
                                .withSelectable(false)
                                .withSubItems(
                                        new SecondaryDrawerItem().withName("Normal").withLevel(2).withIcon(R.drawable.ic_photo_white_24dp).withIconColor(0xB3FFFFFF).withIconTintingEnabled(true),
                                        new SecondaryDrawerItem().withName("Low Light").withLevel(2).withIcon(R.drawable.ic_brightness_3_white_24dp).withIconColor(0xB3FFFFFF).withIconTintingEnabled(true)
                                ),
                        new ExpandableDrawerItem()
                                .withName("Picture Size")
                                .withIcon(R.drawable.ic_photo_size_select_large_white_24dp)
                                .withIconTintingEnabled(true)
                                .withIconTintingEnabled(true)
                                .withDescription("4096x3072")
                                .withSelectable(false)
                                .withSubItems(
                                    new SecondaryDrawerItem().withName("13M 4096x3072 (4:3)").withLevel(2),
                                    new SecondaryDrawerItem().withName("10M 4096x2304 (16:9)").withLevel(2),
                                    new SecondaryDrawerItem().withName("3M 2048x1536 (4:3)").withLevel(2)
                                ),

                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName("About")
                )
                .build();


        if (Build.VERSION.SDK_INT >= 19) {
            result.getDrawerLayout().setFitsSystemWindows(false);
        }

        return result;
    }
}
