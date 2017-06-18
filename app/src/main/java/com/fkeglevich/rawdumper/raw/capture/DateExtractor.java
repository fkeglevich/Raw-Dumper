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

package com.fkeglevich.rawdumper.raw.capture;

import java.util.GregorianCalendar;

/**
 * Created by Flávio Keglevich on 09/06/2017.
 * TODO: Add a class header comment!
 */

public class DateExtractor
{
    public DateExtractor()
    {   }

    public DateInfo extractFromFilename(String filename)
    {
        int year = Integer.parseInt(filename.substring(4, 8));
        int month = Integer.parseInt(filename.substring(8, 10));
        int day = Integer.parseInt(filename.substring(10, 12));
        int hour = Integer.parseInt(filename.substring(13, 15));
        int minute = Integer.parseInt(filename.substring(15, 17));
        int second = Integer.parseInt(filename.substring(17, 19));

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, month, day, hour, minute, second);

        DateInfo result = new DateInfo();
        result.captureDate = calendar;
        return result;
    }

    public DateInfo extractFromCurrentTime()
    {
        DateInfo result = new DateInfo();
        result.captureDate = (GregorianCalendar)GregorianCalendar.getInstance();
        return result;
    }
}
