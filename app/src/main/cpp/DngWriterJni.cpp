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

/**
 * Native implementation of the TiffWriter class
 *
 * Created by Flávio Keglevich on 10/01/2017.
 */

#include <jni.h>
#include "dng_sdk/source/dng_host.h"
#include "dng_sdk/source/dng_simple_image.h"
#include "dng_sdk/source/dng_tag_values.h"
#include "dng_sdk/source/dng_matrix.h"
#include "dng_sdk/source/dng_orientation.h"
#include "dng_sdk/source/dng_preview.h"
#include "dng_sdk/source/dng_render.h"
#include "dng_sdk/source/dng_color_space.h"
#include "dng_sdk/source/dng_image_writer.h"
#include "dng_sdk/source/dng_file_stream.h"
#include "dng_sdk/source/dng_camera_profile.h"

//extern "C"
//{
    void dummy()
    {
        dng_host dngHost;
        dngHost.SetSaveLinearDNG(false);

        uint32 width = 4000;
        uint32 height = 3000;

        dng_rect imageRect(static_cast<uint32>(height), static_cast<uint32>(width));

        AutoPtr<dng_image> dngImage(new dng_simple_image(imageRect, 3, ttShort, dngHost.Allocator()));

        uint16 *dstData = new uint16[width * height * 4];

        for (int i = 0; i < (width * height * 4); ++i) {
            *(dstData++) = 0;
        }

        dng_pixel_buffer buffer;
        buffer.fArea = imageRect;
        buffer.fPlane = 0;
        buffer.fPlanes = 4;
        buffer.fRowStep = 4 * width;
        buffer.fColStep = 4;
        buffer.fPlaneStep = 1;
        buffer.fPixelType = ttShort;
        buffer.fPixelSize = TagTypeSize(ttShort);
        buffer.fData = dstData;
        dngImage->Put(buffer);

        AutoPtr<dng_negative> dngNegative(dngHost.Make_dng_negative());

        dngNegative->SetDefaultScale(dng_urational(1, 1), dng_urational(1, 1));
        dngNegative->SetDefaultCropOrigin(0, 0);
        dngNegative->SetDefaultCropSize(width, height);
        dngNegative->SetActiveArea(imageRect);
        dngNegative->SetModelName("Dummy");
        dngNegative->SetLocalName("Dummy");
        dngNegative->SetOriginalRawFileName("dummy.dng");
        dngNegative->SetColorChannels(4);
        dngNegative->SetColorKeys(colorKeyRed, colorKeyGreen, colorKeyGreen, colorKeyBlue);
        dngNegative->SetWhiteLevel((0x01 << 16) - 1);
        dngNegative->SetBlackLevel(0);
        dngNegative->SetNoiseReductionApplied(dng_urational(0, 1));
        dngNegative->SetBaseOrientation(dng_orientation::Normal());

        // -----------------------------------------------------------------------------------------
        // Fixed properties

        dngNegative->SetBaselineExposure(0.0);                       // should be fixed per camera
        dngNegative->SetBaselineNoise(1.0);
        dngNegative->SetBaselineSharpness(1.0);

        // default
        dngNegative->SetAntiAliasStrength(dng_urational(100, 100));  // = no aliasing artifacts
        dngNegative->SetLinearResponseLimit(1.0);                    // = no non-linear sensor response
        dngNegative->SetAnalogBalance(dng_vector_3(1.0, 1.0, 1.0));
        dngNegative->SetShadowScale(dng_urational(1, 1));

        AutoPtr<dng_camera_profile> prof(new dng_camera_profile);
        prof->SetName("dummy profile");
        dng_matrix_3by3 camXYZ(1.0f, 1.0f, 1.0f);
        prof->SetColorMatrix1(static_cast<dng_matrix>(camXYZ));
        prof->SetCalibrationIlluminant1(lsD65);
        prof->SetWasReadFromDNG(true);
        dngNegative->AddProfile(prof);

        dngNegative->SetStage1Image(dngImage);
        dngNegative->BuildStage2Image(dngHost);
        dngNegative->BuildStage3Image(dngHost);
        dngNegative->SynchronizeMetadata();
        //dngNegative->RebuildIPTC(true);//, false);

        dng_image_preview thumbnail;
        dng_render render(dngHost, *dngNegative);
        render.SetFinalSpace(dng_space_sRGB::Get());
        render.SetFinalPixelType(ttByte);
        render.SetMaximumSize(256);
        thumbnail.fImage.Reset(render.Render());

        dng_exif *negExif;
        negExif->SetExposureTime();

        //dng_preview_list* previewList = new dng_preview_list();

        //AutoPtr<dng_preview> prev((dng_preview)thumbnail);
        //previewList->Append(prev);

        dng_image_writer writer;
        dng_file_stream stream("/sdcard/dummy.dng", true);
        //writer.WriteDNG(dngHost, stream, *dngNegative.Get(), previewList, 1400, false);
        writer.WriteDNG(dngHost, stream, *dngNegative.Get());

        //delete[] dstData;
    }
//};

extern "C"
JNIEXPORT void JNICALL
Java_com_fkeglevich_rawdumper_dng2_DngWriter2_dummy(JNIEnv *env, jobject instance)
{

    dummy();

}