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

package com.asus.camera.extensions;

import android.hardware.Camera;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.data.Aperture;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.util.event.AsyncEventDispatcher;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

import java.lang.ref.WeakReference;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

/**
 * Extension shim for AsusCameraExtension
 *
 * Created by Flávio Keglevich on 26/06/18.
 */
@SuppressWarnings({"JniMissingFunction", "unused"})
@Keep
public class AsusCameraExtension
{
    private static final String TAG                   = "AsusCameraExt";
    private static final String LIBRARY_NAME          = "asuscameraext_jni";
    private static final int CAPTURE_FRAME_STATUS_MSG = 24577;
    private static final int PRO_DATA_MSG             = 24579;

    private static final boolean available;

    static
    {
        boolean success = false;
        try
        {
            System.loadLibrary(LIBRARY_NAME);
            success = true;
        }
        catch (UnsatisfiedLinkError ignored)
        {   }

        available = success;
    }

    public static boolean isAvailable()
    {
        return available;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final Camera mCameraDevice;

    private int mNativeContext;
    private long mNativeContextLong;

    public final EventDispatcher<CaptureFrameData> onGotCaptureFrameData = new AsyncEventDispatcher<>();
    public final EventDispatcher<ProfessionalData> onGotProfessionalData = new AsyncEventDispatcher<>();

    public AsusCameraExtension(Camera camera)
    {
        mCameraDevice = camera;
        if (isAvailable())
            native_setup(new WeakReference<>(this), this.mCameraDevice);
    }

    private static void postEventFromNative(Object extensionRef, int what, int arg1, int arg2, Object obj)
    {
        AsusCameraExtension cameraExtension = (AsusCameraExtension)((WeakReference)extensionRef).get();
        switch (what)
        {
            case CAPTURE_FRAME_STATUS_MSG:
                cameraExtension.onGotCaptureFrameData.dispatchEvent((CaptureFrameData) obj);
                Log.i(TAG, "frame status: " + obj.toString());
                break;

            case PRO_DATA_MSG:
                cameraExtension.onGotProfessionalData.dispatchEvent((ProfessionalData) obj);
                //Log.i(TAG, "Aperture: " + obj.toString());
                break;

            default:
                Log.i(TAG, "Unknown message type: " + what);
                break;
        }
    }

    public static class CaptureFrameData
    {
        private int mStatus;

        private int getStatus()
        {
            return mStatus;
        }

        @Override
        public String toString()
        {
            return "{ status: " + mStatus + " }";
        }
    }

    public static class ProfessionalData
    {
        private int   mISO;
        private float mShutterSpeed;
        private float mAperture;
        private float mOriginalEV;
        private float mDifferentialEV;

        @Nullable
        public Iso getIso()
        {
            if (Iso.isInvalidIso(mISO)) return null;
            return Iso.create(mISO);
        }

        @Nullable
        public ShutterSpeed getShutterSpeed()
        {
            if (ShutterSpeed.isInvalidExposure(mShutterSpeed)) return null;
            return ShutterSpeed.create(mShutterSpeed);
        }

        @Nullable
        public Aperture getAperture()
        {
            if (Aperture.isInvalidAperture(mAperture)) return null;
            return Aperture.create(mAperture);
        }

        @Nullable
        public Ev getOriginalEv()
        {
            if (Ev.isInvalidEv(mOriginalEV)) return null;
            return Ev.create(mOriginalEV);
        }

        @Nullable
        public Ev getDifferentialEv()
        {
            if (Ev.isInvalidEv(mDifferentialEV)) return null;
            return Ev.create(mDifferentialEV);
        }

        public void copyFrom(ProfessionalData other)
        {
            mISO            = other.mISO;
            mShutterSpeed   = other.mShutterSpeed;
            mAperture       = other.mAperture;
            mOriginalEV     = other.mOriginalEV;
            mDifferentialEV = other.mDifferentialEV;
        }

        @Override
        public String toString()
        {
            return "{ iso: " + mISO +
                    ", shutterSpeed: " + mShutterSpeed +
                    ", aperture: " + mAperture +
                    ", originalEv: " + mOriginalEV + "" +
                    ", differentialEv: " + mDifferentialEV + " }";
        }
    }

    public void release()
    {
        onGotCaptureFrameData.removeAllListeners();
        onGotProfessionalData.removeAllListeners();

        if (isAvailable())
        {
            stopQueryingData();
            native_release();
        }
    }

    public void startQueryData(int queryInterval)
    {
        if (isAvailable())
        {
            native_startQueryCaptureFrameStatus();
            native_startQueryProfessionalData(queryInterval);
        }
    }

    public void stopQueryingData()
    {
        if (isAvailable())
        {
            native_stopQueryCaptureFrameStatus();
            native_stopQueryProfessionalData();
        }
    }

    private native void native_release();
    private native void native_setup(Object weakRef, Camera camera);
    private native void native_startQueryCaptureFrameStatus();
    private native void native_startQueryProfessionalData(int interval);
    private native void native_stopQueryCaptureFrameStatus();
    private native void native_stopQueryProfessionalData();
}
