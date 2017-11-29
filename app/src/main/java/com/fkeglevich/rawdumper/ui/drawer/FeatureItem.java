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

package com.fkeglevich.rawdumper.ui.drawer;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;

/**
 * Created by flavio on 21/11/17.
 */

public class FeatureItem extends ExpandableDrawerItem
{
    static final int ICON_COLOR = 0xB3FFFFFF;

    public FeatureItem(@StringRes int name, @DrawableRes int icon)
    {
        this.withName(name)
            .withIcon(icon)
            .withIconColor(ICON_COLOR)
            .withIconTintingEnabled(true)
            .withSelectable(false);
    }
}
