/*
 * Copyright 2018, Flávio Keglevich
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
 * Native implementation of the DngExifTagWriter class
 *
 * Created by Flávio Keglevich on 01/11/2018.
 */

#include <jni.h>
#include "dng_sdk/source/dng_host.h"
#include "dng_sdk/source/dng_negative.h"
#include "dng_sdk/source/dng_exif.h"

extern "C"
{
    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeExposureTimeTagsNative(JNIEnv *env,
                                                                                    jobject instance,
                                                                                    jlong pointer,
                                                                                    jdouble exposureTime)
    {
        ((dng_exif *) pointer)->SetExposureTime(exposureTime, true);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeISOTagNative(JNIEnv *env, jobject instance,
                                                                          jlong pointer, jint iso)
    {
        ((dng_exif *) pointer)->fISOSpeedRatings[0] = (uint32)iso;
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeDateTimeOriginalTagsNative(JNIEnv *env,
                                                                                        jobject instance,
                                                                                        jlong pointer,
                                                                                        jstring dateIso8601_)
    {
        const char *dateIso8601 = env->GetStringUTFChars(dateIso8601_, 0);
        ((dng_exif *) pointer)->fDateTimeOriginal.Decode_ISO_8601(dateIso8601);
        env->ReleaseStringUTFChars(dateIso8601_, dateIso8601);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeDateTimeDigitizedTagsNative(JNIEnv *env,
                                                                                         jobject instance,
                                                                                         jlong pointer,
                                                                                         jstring dateIso8601_)
    {
        const char *dateIso8601 = env->GetStringUTFChars(dateIso8601_, 0);
        ((dng_exif *) pointer)->fDateTimeDigitized.Decode_ISO_8601(dateIso8601);
        env->ReleaseStringUTFChars(dateIso8601_, dateIso8601);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeApertureTagsNative(JNIEnv *env,
                                                                                jobject instance,
                                                                                jlong pointer,
                                                                                jdouble aperture)
    {
        ((dng_exif *) pointer)->SetApertureValue(aperture);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeExifVersionTagNative(JNIEnv *env,
                                                                                  jobject instance,
                                                                                  jlong pointer,
                                                                                  jbyteArray exifVersion_)
    {
        jbyte *exifVersion = env->GetByteArrayElements(exifVersion_, NULL);
        uint32 intVersion;
        memcpy(&intVersion, exifVersion, 4);
        ((dng_exif *) pointer)->fExifVersion = intVersion;
        env->ReleaseByteArrayElements(exifVersion_, exifVersion, 0);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeExposureBiasTagNative(JNIEnv *env,
                                                                                   jobject instance,
                                                                                   jlong pointer,
                                                                                   jfloat bias)
    {
        ((dng_exif *) pointer)->fExposureBiasValue.Set_real64(bias);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeFlashTagNative(JNIEnv *env,
                                                                            jobject instance,
                                                                            jlong pointer,
                                                                            jshort exifValue)
    {

        ((dng_exif *) pointer)->fFlash = (uint32)exifValue;
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeFocalLengthTagNative(JNIEnv *env,
                                                                                  jobject instance,
                                                                                  jlong pointer,
                                                                                  jfloat focalLength)
    {
        ((dng_exif *) pointer)->fFocalLength.Set_real64(focalLength);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeSoftwareTagNative(JNIEnv *env,
                                                                               jobject instance,
                                                                               jlong nativeHandle,
                                                                               jstring software_)
    {
        const char *software = env->GetStringUTFChars(software_, 0);
        ((dng_exif *) nativeHandle)->fSoftware.Set_UTF8(software);
        env->ReleaseStringUTFChars(software_, software);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeMakeTagNative(JNIEnv *env,
                                                                           jobject instance,
                                                                           jlong nativeHandle,
                                                                           jstring make_)
    {
        const char *make = env->GetStringUTFChars(make_, 0);
        ((dng_exif *) nativeHandle)->fMake.Set_UTF8(make);
        env->ReleaseStringUTFChars(make_, make);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeModelTagNative(JNIEnv *env,
                                                                            jobject instance,
                                                                            jlong nativeHandle,
                                                                            jstring model_)
    {
        const char *model = env->GetStringUTFChars(model_, 0);
        ((dng_exif *) nativeHandle)->fModel.Set_UTF8(model);
        env->ReleaseStringUTFChars(model_, model);
    }
};