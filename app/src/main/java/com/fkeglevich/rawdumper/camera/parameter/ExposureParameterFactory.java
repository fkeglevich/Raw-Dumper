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
import com.fkeglevich.rawdumper.camera.extension.Parameters;
import com.fkeglevich.rawdumper.raw.info.ExposureInfo;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 21/09/17.
 */

public class ExposureParameterFactory
{
    public static Parameter<Iso> createIsoParameter(ExposureInfo exposureInfo)
    {
        ValueDecoder<Iso> isoDecoder = createExposureParameterDecoder(Iso.AUTO, exposureInfo.getIsoAutoValue(), new ValueDecoder<Iso>() {
            @Override
            public Iso decode(String value)
            {
                return Iso.create(Integer.parseInt(value));
            }
        });

        ValueEncoder<Iso> isoEncoder = createExposureParameterEncoder(Iso.AUTO, exposureInfo.getIsoAutoValue(), new ValueEncoder<Iso>()
        {
            @Override
            public String encode(Iso value)
            {
                return "" + value.getNumericValue();
            }
        });

        return StaticParameter.create(exposureInfo.getIsoParameter(), isoDecoder, isoEncoder);
    }

    public static Parameter<ShutterSpeed> createSSParameter(ExposureInfo exposureInfo)
    {
        ValueDecoder<ShutterSpeed> ssDecoder = createExposureParameterDecoder(ShutterSpeed.AUTO, exposureInfo.getShutterSpeedAutoValue(), new ValueDecoder<ShutterSpeed>()
        {
            @Override
            public ShutterSpeed decode(String value)
            {
                if (value.endsWith("s"))
                    return ShutterSpeed.create(Double.parseDouble(value.replace("s", "")));

                return ShutterSpeed.create(1d / Double.parseDouble(value));
            }
        });

        final boolean isUsingOneWithoutS = exposureInfo.getShutterSpeedValues().contains("1");

        ValueEncoder<ShutterSpeed> ssEncoder = createExposureParameterEncoder(ShutterSpeed.AUTO, exposureInfo.getShutterSpeedAutoValue(), new ValueEncoder<ShutterSpeed>()
        {
            @Override
            public String encode(ShutterSpeed value)
            {
                double exposure = value.getExposureInSeconds();
                int comparing = Double.compare(exposure, 1d);

                if (comparing > 0) return exposure + "s";
                if (comparing < 0) return "" + Math.round(1d / exposure);

                return isUsingOneWithoutS ? "1" : "1s";
            }
        });

        return StaticParameter.create(exposureInfo.getShutterSpeedParameter(), ssDecoder, ssEncoder);
    }

    public static Parameter<Ev> createEvParameter(ParameterCollection parameterCollection)
    {
        final float step = parameterCollection.get(Parameters.EXPOSURE_COMPENSATION_STEP);

        ValueDecoder<Ev> evDecoder = new ValueDecoder<Ev>()
        {
            @Override
            public Ev decode(String value)
            {
                return Ev.create(step * Float.parseFloat(value));
            }
        };

        ValueEncoder<Ev> evEncoder = new ValueEncoder<Ev>()
        {
            @Override
            public String encode(Ev value)
            {
                return "" + Math.round(value.getValue() / step);
            }
        };

        return StaticParameter.create("exposure-compensation", evDecoder, evEncoder);
    }

    private static <T> ValueDecoder<T> createExposureParameterDecoder(final T auto,
                                                                      final String encodedAuto,
                                                                      final ValueDecoder<T> actualDecoder)
    {
        return new ValueDecoder<T>()
        {
            @Override
            public T decode(String value)
            {
                if (value == null || encodedAuto == null || encodedAuto.equals(value))
                    return auto;

                return actualDecoder.decode(value);
            }
        };
    }

    private static <T> ValueEncoder<T> createExposureParameterEncoder(final T auto,
                                                                      final String encodedAuto,
                                                                      final ValueEncoder<T> actualEncoder)
    {
        return new ValueEncoder<T>()
        {
            @Override
            public String encode(T value)
            {
                if (value == auto)
                    return encodedAuto;

                return actualEncoder.encode(value);
            }
        };
    }

}
