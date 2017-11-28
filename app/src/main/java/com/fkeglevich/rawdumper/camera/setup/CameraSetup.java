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

package com.fkeglevich.rawdumper.camera.setup;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.CameraSelector;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.SurfaceTextureSource;
import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionManager;
import com.fkeglevich.rawdumper.util.Assert;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/09/17.
 */

public class CameraSetup
{
    private final SetupStageLink setupStageLink;
    private final List<SetupStage> setupStages;
    private final List<SetupStage> stageQueue = new ArrayList<>();

    private boolean setupIsRunning = false;
    TurboCamera turboCamera = null;

    public final EventDispatcher<MessageException> onException = new SimpleDispatcher<>();
    public final EventDispatcher<TurboCamera> onComplete = new SimpleDispatcher<>();

    public CameraSetup(SurfaceTextureSource textureSource, ActivityReference activityReference,
                       MandatoryPermissionManager permissionManager, CameraSelector cameraSelector)
    {
        this.setupStageLink = new SetupStageLinkImpl(textureSource, activityReference, permissionManager, cameraSelector, this);
        this.setupStages = new LinkedList<>(Arrays.asList(  new DeviceInfoStage(),
                                                            new PermissionsStage(),
                                                            new DirectoryStage(),
                                                            new WorkaroundStage(),
                                                            new SurfaceTextureStage(),
                                                            new CameraOpenStage()));
    }

    public void setupCamera()
    {
        if (!setupIsRunning)
        {
            stageQueue.addAll(setupStages);
            processNextStage();
            setupIsRunning = true;
        }
    }

    void processNextStage()
    {
        if (!stageQueue.isEmpty())
            stageQueue.remove(0).executeStage(setupStageLink);
        else
        {
            Assert.isNotNull(turboCamera);
            onComplete.dispatchEvent(turboCamera);
            turboCamera = null;
            setupIsRunning = false;
        }
    }

    void sendException(MessageException exception)
    {
        onException.dispatchEvent(exception);
        stageQueue.clear();
        turboCamera = null;
        setupIsRunning = false;
    }

    <T> void removeAllStagesOfType(Class<T> typeClass)
    {
        ListIterator<SetupStage> iter = setupStages.listIterator();
        while(iter.hasNext())
            if(iter.next().getClass() == typeClass)
                iter.remove();
    }
}
