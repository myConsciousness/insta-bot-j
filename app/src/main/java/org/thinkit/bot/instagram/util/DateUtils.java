/*
 * Copyright 2021 Kato Shinya.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.thinkit.bot.instagram.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mongodb.lang.NonNull;

import org.thinkit.bot.instagram.catalog.DateFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    public static boolean isHourElapsed(@NonNull final Date baseDate, final int elapsedHour) {

        final Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR, -9);

        return baseDate.getTime() - now.getTime().getTime() >= elapsedHour;
    }

    public static String toString(@NonNull final Date date, @NonNull final DateFormat format) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format.getTag());
        return dateFormat.format(date);
    }
}
