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

package com.fkeglevich.rawdumper.controller.camera.preview;

/**
 * Represents the controller preview state when requesting permissions
 *
 * Created by Flávio Keglevich on 24/08/2017.
 */

enum PermissionsState
{
    INITIAL,                    //Initial, basic state when permissions can be requested
    REQUESTING_PERMISSIONS,     //State when the permissions are being requested by the user
    HAD_DIALOG_PROMPT           /*  State when some dialog prompt had appeared after requesting permissions
                                        and the OnResume will be called twice*/
}
