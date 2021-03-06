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

package com.fkeglevich.rawdumper.camera.parameter;

import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.extension.Parameters;
import com.fkeglevich.rawdumper.raw.info.ExposureInfo;
import com.fkeglevich.rawdumper.util.Nullable;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 21/09/17.
 */

public class ExposureParameterFactory
{
    private static final ValueDecoder<Iso> BASIC_ISO_DECODER = value -> Iso.create(Integer.parseInt(value));
    private static final ValueEncoder<Iso> BASIC_ISO_ENCODER = value -> "" + value.getNumericValue();

    public static Parameter<Iso> createIsoParameter(ExposureInfo exposureInfo)
    {
        ValueDecoder<Iso> isoDecoder = createExposureParameterDecoder(Iso.AUTO, exposureInfo.getIsoAutoValue(), BASIC_ISO_DECODER);
        ValueEncoder<Iso> isoEncoder = createExposureParameterEncoder(Iso.AUTO, exposureInfo.getIsoAutoValue(), BASIC_ISO_ENCODER);
        return StaticParameter.create(exposureInfo.getIsoParameter(), isoDecoder, isoEncoder);
    }

    public static Parameter<ShutterSpeed> createSSParameter(ExposureInfo exposureInfo)
    {
        ValueDecoder<ShutterSpeed> ssDecoder = createExposureParameterDecoder(ShutterSpeed.AUTO, exposureInfo.getShutterSpeedAutoValue(), value ->
        {
            if (value.endsWith("s"))
                return ShutterSpeed.create(Double.parseDouble(value.replace("s", "")));

            return ShutterSpeed.create(1d / Double.parseDouble(value));
        });

        final boolean isUsingOneWithoutS = exposureInfo.getShutterSpeedValues().contains("1");

        ValueEncoder<ShutterSpeed> ssEncoder = createExposureParameterEncoder(ShutterSpeed.AUTO, exposureInfo.getShutterSpeedAutoValue(), value ->
        {
            double exposure = value.getExposureInSeconds();
            int comparing = Double.compare(exposure, 1d);

            if (comparing > 0) return exposure + "s";
            if (comparing < 0) return "" + Math.round(1d / exposure);

            return isUsingOneWithoutS ? "1" : "1s";
        });

        return StaticParameter.create(exposureInfo.getShutterSpeedParameter(), ssDecoder, ssEncoder);
    }

    public static Parameter<Ev> createEvParameter(ParameterCollection parameterCollection)
    {
        final float step = parameterCollection.get(Parameters.EXPOSURE_COMPENSATION_STEP);

        ValueDecoder<Ev> evDecoder = value -> Ev.create(step * Float.parseFloat(value));

        ValueEncoder<Ev> evEncoder = value -> "" + Math.round(value.getValue() / step);

        return StaticParameter.create("exposure-compensation", evDecoder, evEncoder);
    }

    public static Parameter<Nullable<Iso>> createIsoMeteringParameter()
    {
        return StaticParameter.createReadOnly(AsusParameters.ASUS_XENON_ISO, value ->
        {
            int numeric = Integer.parseInt(value);
            return Nullable.of(numeric == 0 ? null : Iso.create(numeric));
        });
    }

    public static Parameter<Nullable<ShutterSpeed>> createSSMeteringParameter()
    {
        return StaticParameter.createReadOnly(AsusParameters.ASUS_XENON_EXPOSURE_TIME, value ->
        {
            if (value.contains("."))
            {
                double numeric = Double.parseDouble(value);
                return Nullable.of(numeric < 0.00001 ? null : ShutterSpeed.create(numeric));
            }
            else
            {
                int numeric = Integer.parseInt(value);
                return Nullable.of(numeric == 0 ? null : ShutterSpeed.decodeMicrosecondExposure(numeric));
            }
        });
    }

    private static <T> ValueDecoder<T> createExposureParameterDecoder(final T auto,
                                                                      final String encodedAuto,
                                                                      final ValueDecoder<T> actualDecoder)
    {
        return value ->
        {
            if (value == null || encodedAuto == null || encodedAuto.equals(value))
                return auto;

            try
            {
                return actualDecoder.decode(value);
            }
            catch (Exception nfe)
            {
                return auto;
            }
        };
    }

    private static <T> ValueEncoder<T> createExposureParameterEncoder(final T auto,
                                                                      final String encodedAuto,
                                                                      final ValueEncoder<T> actualEncoder)
    {
        return value ->
        {
            if (value == auto)
                return encodedAuto;

            return actualEncoder.encode(value);
        };
    }

}
