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

package com.fkeglevich.rawdumper.controller.feature;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.adapter.ButtonController;
import com.fkeglevich.rawdumper.controller.adapter.DismissibleManagerAdapter;
import com.fkeglevich.rawdumper.controller.adapter.ToastNotificationController;
import com.fkeglevich.rawdumper.controller.adapter.WheelViewAdapter;
import com.fkeglevich.rawdumper.ui.CameraPreviewTexture;
import com.fkeglevich.rawdumper.util.Nullable;
import com.lantouzi.wheelview.WheelView;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 23/10/17.
 */

public class FeatureControllerFactory
{
    private final DismissibleManagerAdapter dismissibleManager;

    FeatureControllerFactory()
    {
        dismissibleManager = new DismissibleManagerAdapter();
    }

    DisplayableFeatureController createISOController(ActivityReference reference)
    {
        WheelView wheelView = (WheelView) reference.weaklyGet().findViewById(R.id.isoValueChooser);
        WheelViewAdapter viewAdapter = new WheelViewAdapter(wheelView);
        ButtonController btController = createButtonController(reference, R.id.isoBt, viewAdapter,
                wheelView, R.string.iso_changed_notification);

        return new DisplayableFeatureController(btController)
        {
            @Override
            protected WritableFeature selectFeature(TurboCamera camera)
            {
                return camera.getIsoFeature();
            }
        };
    }

    DisplayableFeatureController createSSController(ActivityReference reference)
    {
        WheelView wheelView = (WheelView) reference.weaklyGet().findViewById(R.id.ssValueChooser);
        WheelViewAdapter viewAdapter = new WheelViewAdapter(wheelView);
        ButtonController btController = createButtonController(reference, R.id.shutterSpeedBt, viewAdapter,
                wheelView, R.string.ss_changed_notification);

        return new DisplayableFeatureController(btController)
        {
            @Override
            protected WritableFeature selectFeature(TurboCamera camera)
            {
                return camera.getShutterSpeedFeature();
            }
        };
    }

    DisplayableFeatureController createEVController(ActivityReference reference)
    {
        WheelView wheelView = (WheelView) reference.weaklyGet().findViewById(R.id.evValueChooser);
        WheelViewAdapter viewAdapter = new WheelViewAdapter(wheelView);
        ButtonController btController = createButtonController(reference, R.id.evBt, viewAdapter,
                wheelView, R.string.ev_changed_notification);

        return new DisplayableFeatureController(btController)
        {
            @Override
            protected WritableFeature selectFeature(TurboCamera camera)
            {
                return camera.getEVFeature();
            }
        };
    }

    FlashController createFlashController(ActivityReference reference)
    {
        ImageButton flashButton = (ImageButton) reference.weaklyGet().findViewById(R.id.flashButton);
        return new FlashController(flashButton);
    }

    TouchFocusController createTouchFocusController(ActivityReference reference)
    {
        View textureView = reference.weaklyGet().findViewById(R.id.textureView);
        return new TouchFocusController(textureView);
    }

    ValueMeteringController<Iso> createIsoMeteringController(ActivityReference reference)
    {
        TextView textView = (TextView) reference.weaklyGet().findViewById(R.id.isoText);
        return new ValueMeteringController<Iso>(textView, Iso.AUTO)
        {
            @Override
            protected Feature<Nullable<Iso>> getMeteringFeature(TurboCamera turboCamera)
            {
                return turboCamera.getIsoMeteringFeature();
            }

            @Override
            protected Feature<Iso> getFallbackFeature(TurboCamera turboCamera)
            {
                return turboCamera.getIsoFeature();
            }
        };
    }

    ValueMeteringController<ShutterSpeed> createSSMeteringController(ActivityReference reference)
    {
        TextView textView = (TextView) reference.weaklyGet().findViewById(R.id.ssText);
        return new ValueMeteringController<ShutterSpeed>(textView, ShutterSpeed.AUTO)
        {
            @Override
            protected Feature<Nullable<ShutterSpeed>> getMeteringFeature(TurboCamera turboCamera)
            {
                return turboCamera.getSSMeteringFeature();
            }

            @Override
            protected Feature<ShutterSpeed> getFallbackFeature(TurboCamera turboCamera)
            {
                return turboCamera.getShutterSpeedFeature();
            }
        };
    }

    ValueMeteringController<Ev> createEvMeteringController(ActivityReference reference)
    {
        TextView textView = (TextView) reference.weaklyGet().findViewById(R.id.evText);
        return new ValueMeteringController<Ev>(textView, Ev.DEFAULT)
        {
            @Override
            protected Feature<Nullable<Ev>> getMeteringFeature(TurboCamera turboCamera)
            {
                return null;
            }

            @Override
            protected Feature<Ev> getFallbackFeature(TurboCamera turboCamera)
            {
                return turboCamera.getEVFeature();
            }
        };
    }

    TakePictureController createCaptureButtonController(ActivityReference reference)
    {
        View captureButton = reference.weaklyGet().findViewById(R.id.captureButton);
        View pictureLayer = reference.weaklyGet().findViewById(R.id.captureLayer);
        CameraPreviewTexture previewTexture = (CameraPreviewTexture) reference.weaklyGet().findViewById(R.id.textureView);
        return new TakePictureController(captureButton, pictureLayer, previewTexture);
    }

    private ButtonController createButtonController(ActivityReference reference,
                                                           @IdRes int buttonId,
                                                           DisplayableFeatureUi adapter,
                                                           View chooser,
                                                           @StringRes int messageResource)
    {
        View button = reference.weaklyGet().findViewById(buttonId);

        ToastNotificationController toastNotification = new ToastNotificationController(button.getContext(), messageResource);
        ButtonController result = new ButtonController(button, adapter, (View)chooser.getParent(), toastNotification, dismissibleManager);
        dismissibleManager.addDismissible(result);
        return result;
    }
}
