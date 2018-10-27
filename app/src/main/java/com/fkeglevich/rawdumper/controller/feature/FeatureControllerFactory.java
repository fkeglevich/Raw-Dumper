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
import com.fkeglevich.rawdumper.camera.data.CameraPreview;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.controller.adapter.ButtonController;
import com.fkeglevich.rawdumper.controller.adapter.DismissibleManagerAdapter;
import com.fkeglevich.rawdumper.controller.adapter.ToastNotificationController;
import com.fkeglevich.rawdumper.controller.adapter.WheelViewAdapter;
import com.fkeglevich.rawdumper.ui.TouchFocusView;
import com.lantouzi.wheelview.WheelView;

import java.util.List;

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
        WheelView wheelView = reference.weaklyGet().findViewById(R.id.isoValueChooser);
        WheelViewAdapter viewAdapter = new WheelViewAdapter(wheelView);
        ButtonController btController = createButtonController(reference, R.id.isoBt, viewAdapter,
                wheelView, R.string.iso_changed_notification);

        return new DisplayableFeatureController<>(btController, Iso.class);
    }

    DisplayableFeatureController createSSController(ActivityReference reference)
    {
        WheelView wheelView = reference.weaklyGet().findViewById(R.id.ssValueChooser);
        WheelViewAdapter viewAdapter = new WheelViewAdapter(wheelView);
        ButtonController btController = createButtonController(reference, R.id.shutterSpeedBt, viewAdapter,
                wheelView, R.string.ss_changed_notification);

        return new DisplayableFeatureController<>(btController, ShutterSpeed.class);
    }

    DisplayableFeatureController createEVController(ActivityReference reference)
    {
        WheelView wheelView = reference.weaklyGet().findViewById(R.id.evValueChooser);
        WheelViewAdapter viewAdapter = new WheelViewAdapter(wheelView);
        ButtonController btController = createButtonController(reference, R.id.evBt, viewAdapter,
                wheelView, R.string.ev_changed_notification);

        return new DisplayableFeatureController<>(btController, Ev.class);
    }

    FlashController createFlashController(ActivityReference reference)
    {
        ImageButton flashButton = reference.weaklyGet().findViewById(R.id.flashButton);
        return new FlashController(flashButton, reference);
    }

    TouchFocusMeteringAreaController createTouchFocusController(ActivityReference reference)
    {
        View cameraSurfaceView = reference.weaklyGet().findViewById(R.id.cameraSurfaceView);
        TouchFocusView focusView = reference.weaklyGet().findViewById(R.id.focusView);
        return new TouchFocusMeteringAreaController(cameraSurfaceView, focusView, reference);
    }

    ValueMeteringController<Iso> createIsoMeteringController(ActivityReference reference)
    {
        TextView textView = reference.weaklyGet().findViewById(R.id.isoText);
        return new ValueMeteringController<>(textView, Iso.AUTO);
    }

    ValueMeteringController<ShutterSpeed> createSSMeteringController(ActivityReference reference)
    {
        TextView textView = reference.weaklyGet().findViewById(R.id.ssText);
        return new ValueMeteringController<>(textView, ShutterSpeed.AUTO);
    }

    ValueMeteringController<Ev> createEvMeteringController(ActivityReference reference)
    {
        TextView textView = reference.weaklyGet().findViewById(R.id.evText);
        return new ValueMeteringController<>(textView, Ev.DEFAULT);
    }

    FocusMeteringController createFocusMeteringController(ActivityReference reference)
    {
        TextView textView = reference.weaklyGet().findViewById(R.id.focusText);
        return new FocusMeteringController(textView);
    }

    TakePictureController createCaptureButtonController(ActivityReference reference, List<ValueMeteringController> meteringControllers)
    {
        View captureButton = reference.weaklyGet().findViewById(R.id.captureButton);
        View pictureLayer = reference.weaklyGet().findViewById(R.id.captureLayer);
        View progressBar = reference.weaklyGet().findViewById(R.id.progressBar);
        CameraPreview cameraPreview = reference.weaklyGet().findViewById(R.id.cameraSurfaceView);
        return new TakePictureController(captureButton, pictureLayer, cameraPreview, meteringControllers, progressBar, reference);
    }

    FocusController createFocusController(ActivityReference reference)
    {
        CameraPreview cameraPreview = reference.weaklyGet().findViewById(R.id.cameraSurfaceView);
        FocusController result = new FocusController(reference, dismissibleManager, cameraPreview);
        dismissibleManager.addDismissible(result);
        return result;
    }

    ManualFocusController createManualFocusController(ActivityReference reference)
    {
        return new ManualFocusController(reference);
    }

    WhiteBalancePresetController createWbController(ActivityReference reference)
    {
        WhiteBalancePresetController result = new WhiteBalancePresetController(reference, dismissibleManager);
        dismissibleManager.addDismissible(result);
        return result;
    }

    WhiteBalanceMeteringController createWbMeteringController(ActivityReference reference)
    {
        TextView textView = reference.weaklyGet().findViewById(R.id.wbText);
        return new WhiteBalanceMeteringController(textView);
    }

    ManualTemperatureController createManualWbController(ActivityReference reference)
    {
        return new ManualTemperatureController(reference);
    }

    PictureSizeController createPictureSizeController(ActivityReference reference)
    {
        return new PictureSizeController(reference);
    }

    PictureFormatController createPictureFormatController(ActivityReference reference)
    {
        return new PictureFormatController(reference);
    }

    PictureModeController createPictureModeController(ActivityReference reference)
    {
        return new PictureModeController(reference);
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

    FeatureController createSwitchsController(ActivityReference reference)
    {
        return new SwitchesController(reference);
    }
}
