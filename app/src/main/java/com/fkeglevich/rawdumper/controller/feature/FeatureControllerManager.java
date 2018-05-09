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

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 23/10/17.
 */

class FeatureControllerManager
{
    private final FeatureControllerFactory controllerFactory;
    private final List<FeatureController> controllersList;

    FeatureControllerManager()
    {
        controllerFactory = new FeatureControllerFactory();
        controllersList = new ArrayList<>();
    }

    void createControllers(ActivityReference reference)
    {
        controllersList.add(controllerFactory.createISOController(reference));
        controllersList.add(controllerFactory.createSSController(reference));
        controllersList.add(controllerFactory.createEVController(reference));
        controllersList.add(controllerFactory.createFlashController(reference));
        controllersList.add(controllerFactory.createTouchFocusController(reference));
        controllersList.add(controllerFactory.createFocusController(reference));
        controllersList.add(controllerFactory.createManualFocusController(reference));
        controllersList.add(controllerFactory.createFocusMeteringController(reference));
        controllersList.add(controllerFactory.createWbController(reference));
        controllersList.add(controllerFactory.createManualWbController(reference));
        controllersList.add(controllerFactory.createWbMeteringController(reference));
        List<ValueMeteringController> meteringControllers = createMeteringControllers(reference);
        controllersList.add(controllerFactory.createCaptureButtonController(reference, meteringControllers));
    }

    private List<ValueMeteringController> createMeteringControllers(ActivityReference reference)
    {
        List<ValueMeteringController> meteringControllers = new ArrayList<>();

        meteringControllers.add(controllerFactory.createIsoMeteringController(reference));
        meteringControllers.add(controllerFactory.createSSMeteringController(reference));
        meteringControllers.add(controllerFactory.createEvMeteringController(reference));

        controllersList.addAll(meteringControllers);
        return meteringControllers;
    }

    void setupControllers(TurboCamera camera, EventDispatcher<Void> onCameraClose)
    {
        for (FeatureController featureController : controllersList)
            featureController.setupFeature(camera, onCameraClose);
    }
}
