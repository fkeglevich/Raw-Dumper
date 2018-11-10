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

#include <jni.h>
#include "dng_sdk/source/dng_host.h"
#include "dng_sdk/source/dng_simple_image.h"
#include "dng_sdk/source/dng_tag_values.h"
#include "dng_sdk/source/dng_matrix.h"
#include "dng_sdk/source/dng_exif.h"
#include "dng_sdk/source/dng_orientation.h"
#include "dng_sdk/source/dng_preview.h"
#include "dng_sdk/source/dng_render.h"
#include "dng_sdk/source/dng_color_space.h"
#include "dng_sdk/source/dng_image_writer.h"
#include "dng_sdk/source/dng_file_stream.h"
#include "dng_sdk/source/dng_tone_curve.h"
#include "dng_sdk/source/dng_camera_profile.h"
#include "dng_sdk/source/dng_point.h"
#include "dng_sdk/source/dng_gain_map.h"
#include "dng_sdk/source/dng_opcodes.h"
#include "dng_sdk/source/dng_memory_stream.h"
#include <android/log.h>

//extern "C"
//{
    void dummy2()
    {
        dng_host dngHost;
        dngHost.SetSaveLinearDNG(false);

        uint32 width = 4000;
        uint32 height = 3000;

        dng_rect imageRect(height, width);

        //if (4 > 3)
        //    return;

        //AutoPtr<dng_simple_image> dngImage(new dng_simple_image(imageRect, 1, ttShort, dngHost.Allocator()));

        dng_simple_image* image = new dng_simple_image(imageRect, 1, ttShort, dngHost.Allocator());


        dng_pixel_buffer buffer; image->GetPixelBuffer(buffer);



        //memcpy(imageBuffer, dstData, width * height * sizeof(uint16));

        AutoPtr<dng_negative> dngNegative(dngHost.Make_dng_negative());

                dngNegative->SetDefaultScale(dng_urational(1, 1), dng_urational(1, 1));

                // -- Sizes
                dngNegative->SetDefaultCropOrigin(0, 0);
                dngNegative->SetDefaultCropSize(width, height);
                dngNegative->SetActiveArea(imageRect);

                dngNegative->SetModelName("Dummy");
                            //dngNegative->SetLocalName("Dummy");
                dngNegative->SetOriginalRawFileName("dummy.dng");

                dngNegative->SetColorChannels(3);
                dngNegative->SetColorKeys(colorKeyRed, colorKeyGreen, colorKeyBlue);

                dngNegative->SetBayerMosaic(0);

                dngNegative->SetWhiteLevel(1023);
                dngNegative->SetQuadBlacks(0,0,0,0);

                dng_vector_3 cameraNeutral(0,0,0);
                dngNegative->SetCameraNeutral(cameraNeutral);

                dngNegative->SetNoiseReductionApplied(dng_urational(0, 1));

                //Orientatin
                dngNegative->SetBaseOrientation(dng_orientation::Normal());

        //Profile
        AutoPtr<dng_camera_profile> prof(new dng_camera_profile);
        prof->SetName("dummy profile");
        dng_matrix_3by3 camXYZ(1.0f, 1.0f, 1.0f);
        prof->SetColorMatrix1(static_cast<dng_matrix>(camXYZ));
        prof->SetCalibrationIlluminant1(lsD65);
        prof->SetWasReadFromDNG(true);

        dngNegative->AddProfile(prof);

        AutoPtr<dng_image> castImage(dynamic_cast<dng_image*>(image));
        dngNegative->SetStage1Image(castImage);

        //dngNegative->SetStage1Image(image);
        //dngNegative->BuildStage2Image(dngHost);
        //dngNegative->BuildStage3Image(dngHost);
        dngNegative->SynchronizeMetadata();

        /*
         Missing:

         ~Colors
         ~Exif
         ~Tonecurves
         ~CameraCalibration tags
         ~Opcodes
         ~Noise

         Full opcodelists1 e 2

         Camera Info in exif
         Software in exif
         Makernote

         Previews

         */


        //dngNegative->SetMakerNote()
        //dngNegative->RebuildIPTC(true);//, false);

        /*dng_image_preview thumbnail;
        dng_render render(dngHost, *dngNegative);
        render.SetFinalSpace(dng_space_sRGB::Get());
        render.SetFinalPixelType(ttByte);
        render.SetMaximumSize(256);
        thumbnail.fImage.Reset(render.Render());*/

        //dng_exif *negExif;
        //negExif->SetExposureTime();

        //dng_preview_list* previewList = new dng_preview_list();

        //AutoPtr<dng_preview> prev((dng_preview)thumbnail);
        //previewList->Append(prev);

        dng_image_writer writer;
        dng_file_stream stream("/sdcard/dummy2.dng", true);
        //writer.WriteDNG(dngHost, stream, *dngNegative.Get(), previewList, 1400, false);
        writer.WriteDNG(dngHost, stream, *dngNegative.Get());

        //delete[] dstData;
    }
//};

dng_matrix_3by3 get3x3Matrix(JNIEnv *env, jfloatArray matrix3x3)
{
    jfloat *raw = env->GetFloatArrayElements(matrix3x3, NULL);
    dng_matrix_3by3 dngMatrix(raw[0], raw[1], raw[2],
                              raw[3], raw[4], raw[5],
                              raw[6], raw[7], raw[8]);
    env->ReleaseFloatArrayElements(matrix3x3, raw, 0);
    return dngMatrix;
}

dng_tone_curve getToneCurve(JNIEnv *env, jfloatArray toneCurve)
{
    dng_tone_curve result; result.SetInvalid();
    int len = env->GetArrayLength(toneCurve);
    if (len % 2 != 0)
    {
        result.SetNull();
        return result;
    }

    jfloat *raw = env->GetFloatArrayElements(toneCurve, NULL);

    for (int i = 0; i < len; i += 2)
    {
        dng_point_real64 point(raw[i + 1], raw[i]);
        result.fCoord.push_back(point);
    }

    env->ReleaseFloatArrayElements(toneCurve, raw, 0);
    return result;
}

extern "C"
{
    dng_host globalHost;
    bool hostWasInitialized = false;

    void initializeHost()
    {
        if (!hostWasInitialized)
        {
            globalHost.SetSaveLinearDNG(false);
            hostWasInitialized = true;
        }
    }

    void setDefaultValues(dng_negative* negative)
    {
        negative->SetColorChannels(3);
        negative->SetColorKeys(colorKeyRed, colorKeyGreen, colorKeyBlue);
        negative->SetDefaultScale(dng_urational(1, 1), dng_urational(1, 1));
        negative->SetNoiseReductionApplied(dng_urational(0, 1));
        negative->SetBaselineExposure(0.0);
        negative->SetBaselineNoise(1.0);
        negative->SetBaselineSharpness(1.0);
        negative->SetAntiAliasStrength(dng_urational(100, 100));
        negative->SetLinearResponseLimit(1.0);
        negative->SetAnalogBalance(dng_vector_3(1.0, 1.0, 1.0));
        negative->SetShadowScale(dng_urational(1, 1));
    }

    JNIEXPORT jlong JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_nativeConstructor(JNIEnv *env,
                                                                           jobject instance)
    {
        initializeHost();
        dng_negative *negative = globalHost.Make_dng_negative();
        setDefaultValues(negative);
        return (jlong) negative;
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_nativeDispose(JNIEnv *env, jobject instance,
                                                                       jlong pointer)
    {
        delete ((dng_negative*) pointer);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_setModelNative(JNIEnv *env, jobject instance,
                                                                        jlong pointer, jstring model_)
    {
        const char *model = env->GetStringUTFChars(model_, 0);
        ((dng_negative*) pointer)->SetModelName(model);
        env->ReleaseStringUTFChars(model_, model);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_setOriginalRawFileNameNative(JNIEnv *env,
                                                                                      jobject instance,
                                                                                      jlong pointer,
                                                                                      jstring fileName_)
    {
        const char *fileName = env->GetStringUTFChars(fileName_, 0);
        ((dng_negative*) pointer)->SetOriginalRawFileName(fileName);
        env->ReleaseStringUTFChars(fileName_, fileName);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_setSensorInfoNative(JNIEnv *env,
                                                                             jobject instance,
                                                                             jlong pointer,
                                                                             jint whiteLevel,
                                                                             jfloatArray blackLevels_,
                                                                             jint bayerPhase)
    {
        jfloat *blackLevels = env->GetFloatArrayElements(blackLevels_, NULL);
        ((dng_negative*) pointer)->SetWhiteLevel((uint32) whiteLevel);
        ((dng_negative*) pointer)->SetQuadBlacks(blackLevels[0], blackLevels[1], blackLevels[2], blackLevels[3]);
        ((dng_negative*) pointer)->SetBayerMosaic((uint32) bayerPhase);
        env->ReleaseFloatArrayElements(blackLevels_, blackLevels, 0);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_setCameraNeutralNative(JNIEnv *env,
                                                                                jobject instance,
                                                                                jlong pointer,
                                                                                jfloatArray cameraNeutral_)
    {
        jfloat *cameraNeutral = env->GetFloatArrayElements(cameraNeutral_, NULL);
        dng_vector_3 vector3(cameraNeutral[0], cameraNeutral[1], cameraNeutral[2]);
        ((dng_negative*) pointer)->SetCameraNeutral(vector3);
        env->ReleaseFloatArrayElements(cameraNeutral_, cameraNeutral, 0);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_setImageSizeAndOrientationNative(JNIEnv *env,
                                                                              jobject instance,
                                                                              jlong pointer,
                                                                              jint width,
                                                                              jint height,
                                                                              jint orientationExifCode)
    {
        dng_rect rect((uint32) height, (uint32) width);
        dng_orientation orientation; orientation.SetTIFF((uint32) orientationExifCode);

        ((dng_negative*) pointer)->SetDefaultCropOrigin(0, 0);
        ((dng_negative*) pointer)->SetDefaultCropSize((uint32) width, (uint32) height);
        ((dng_negative*) pointer)->SetActiveArea(rect);
        ((dng_negative*) pointer)->SetBaseOrientation(orientation);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_setCameraCalibrationNative(JNIEnv *env,
                                                                                    jobject instance,
                                                                                    jlong pointer,
                                                                                    jfloatArray cameraCalibration1_,
                                                                                    jfloatArray cameraCalibration2_)
    {
        if (cameraCalibration1_ != NULL)
            ((dng_negative*) pointer)->SetCameraCalibration1(get3x3Matrix(env, cameraCalibration1_));
        if (cameraCalibration2_ != NULL)
            ((dng_negative*) pointer)->SetCameraCalibration2(get3x3Matrix(env, cameraCalibration2_));
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_addColorProfileNative(JNIEnv *env,
                                                                               jobject instance,
                                                                               jlong pointer,
                                                                               jstring name_,
                                                                               jfloatArray colorMatrix1_,
                                                                               jfloatArray colorMatrix2_,
                                                                               jfloatArray forwardMatrix1_,
                                                                               jfloatArray forwardMatrix2_,
                                                                               jint calibrationIlluminant1,
                                                                               jint calibrationIlluminant2,
                                                                               jfloatArray toneCurve_)
    {
        AutoPtr<dng_camera_profile> profile(new dng_camera_profile);

        const char *name = env->GetStringUTFChars(name_, 0);
        profile->SetName(name);
        env->ReleaseStringUTFChars(name_, name);

        if (colorMatrix1_ != NULL)
            profile->SetColorMatrix1(get3x3Matrix(env, colorMatrix1_));

        if (colorMatrix2_ != NULL)
            profile->SetColorMatrix2(get3x3Matrix(env, colorMatrix2_));

        if (forwardMatrix1_ != NULL)
            profile->SetForwardMatrix1(get3x3Matrix(env, forwardMatrix1_));

        if (forwardMatrix2_ != NULL)
            profile->SetForwardMatrix2(get3x3Matrix(env, forwardMatrix2_));

        if (calibrationIlluminant1 != lsUnknown)
            profile->SetCalibrationIlluminant1((uint32) calibrationIlluminant1);

        if (calibrationIlluminant2 != lsUnknown)
            profile->SetCalibrationIlluminant2((uint32) calibrationIlluminant2);

        if (toneCurve_ != NULL)
            profile->SetToneCurve(getToneCurve(env, toneCurve_));

        profile->SetWasReadFromDNG(true);

        ((dng_negative*) pointer)->AddProfile(profile);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_setOpcodeList3Native(JNIEnv *env,
                                                                              jobject instance,
                                                                              jlong pointer,
                                                                              jbyteArray bytes_)
    {
        int numBytes = env->GetArrayLength(bytes_);

        jbyte *bytes = env->GetByteArrayElements(bytes_, NULL);
        dng_memory_stream gainMapStream(globalHost.Allocator());
        gainMapStream.Put(bytes, (uint32) numBytes);
        env->ReleaseByteArrayElements(bytes_, bytes, 0);

        ((dng_negative*) pointer)->OpcodeList3().Parse(globalHost, gainMapStream, (uint32) numBytes, 0);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_setNoiseProfileNative(JNIEnv *env,
                                                                               jobject instance,
                                                                               jlong pointer,
                                                                               jdoubleArray noiseProfile)
    {
        dng_std_vector<dng_noise_function> noiseFunctions;
        int len = env->GetArrayLength(noiseProfile);
        if (len % 2 != 0)
            return;

        jdouble *raw = env->GetDoubleArrayElements(noiseProfile, NULL);

        for (int i = 0; i < len; i += 2)
        {
            dng_noise_function function(raw[i], raw[i + 1]);
            noiseFunctions.push_back(function);
        }

        env->ReleaseDoubleArrayElements(noiseProfile, raw, 0);

        ((dng_negative*) pointer)->SetNoiseProfile(dng_noise_profile(noiseFunctions));
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_writeImageToFileNative(JNIEnv *env,
                                                                                jobject instance,
                                                                                jlong pointer,
                                                                                jstring fileName_,
                                                                                jint width,
                                                                                jint height,
                                                                                jbyteArray imageData_)
    {

        jbyte *imageData = env->GetByteArrayElements(imageData_, NULL);

        dng_rect imageRect((uint32) height, ((uint32) width));
        dng_simple_image* image = new dng_simple_image(imageRect, 1, ttShort, globalHost.Allocator());

        dng_pixel_buffer buffer; image->GetPixelBuffer(buffer);
        memcpy(buffer.fData, imageData, width * height * sizeof(uint16));
        env->ReleaseByteArrayElements(imageData_, imageData, 0);

        AutoPtr<dng_image> castImage(dynamic_cast<dng_image*>(image));
        ((dng_negative*) pointer)->SetStage1Image(castImage);

        ((dng_negative*) pointer)->SynchronizeMetadata();

        const char *fileName = env->GetStringUTFChars(fileName_, 0);
        dng_image_writer writer;
        dng_file_stream stream("/sdcard/dummy3.dng", true);
        writer.WriteDNG(globalHost, stream, *((dng_negative*) pointer));// , NULL, dngVersion_1_4_0_0, true);
        env->ReleaseStringUTFChars(fileName_, fileName);

    }

    JNIEXPORT jlong JNICALL
    Java_com_fkeglevich_rawdumper_dng_dngsdk_DngNegative_getExifHandleNative(JNIEnv *env,
                                                                             jobject instance,
                                                                             jlong pointer)
    {
        return (jlong ) ((dng_negative*) pointer)->GetExif();
    }
}