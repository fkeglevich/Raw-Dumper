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

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.feature.FeatureController;
import com.fkeglevich.rawdumper.ui.drawer.FeatureItem;
import com.fkeglevich.rawdumper.ui.drawer.FeatureOptionItem;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by flavio on 21/11/17.
 */

public abstract class ItemFeatureController<T> extends FeatureController
{
    private final ExpandableDrawerItem featureItem;
    private final Drawer drawer;

    private IDrawerItem lastSelection = null;

    public ItemFeatureController(Drawer drawer, @StringRes int name, @DrawableRes int icon)
    {
        this.featureItem = new FeatureItem(name, icon);
        this.drawer = drawer;
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        final WritableFeature<T, List<T>> feature = selectFeature(camera);
        if (!feature.isAvailable())
        {
            reset();
            return;
        }

        List<IDrawerItem> subItems = new ArrayList<>();
        List<T> values = feature.getAvailableValues();
        FeatureOptionItem<T> optionItem;
        for (T value : values)
        {
            optionItem = new FeatureOptionItem<>(displayValue(value), value);
            optionItem.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
            {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                {
                    if (lastSelection != null)
                        drawer.deselect(lastSelection.getIdentifier());

                    lastSelection = drawerItem;
                    FeatureOptionItem<T> item = (FeatureOptionItem<T>) drawerItem;
                    feature.setValue(item.getData());
                    return true;
                }
            });
            subItems.add(optionItem);
        }

        featureItem.withSubItems(subItems);
        drawer.updateItem(featureItem);
    }

    protected abstract String displayValue(T value);

    protected abstract WritableFeature<T,List<T>> selectFeature(TurboCamera camera);

    @Override
    protected void reset()
    {
        disable();
        featureItem.withSubItems(Collections.<IDrawerItem>emptyList());
        drawer.updateItem(featureItem);
    }

    @Override
    protected void disable()
    {

    }

    @Override
    protected void enable()
    {

    }
}
