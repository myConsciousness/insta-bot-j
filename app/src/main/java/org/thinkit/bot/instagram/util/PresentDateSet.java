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

import java.io.Serializable;
import java.util.Calendar;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The class that manages the present date set.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
public final class PresentDateSet implements Serializable {

    /**
     * The year
     */
    @Getter
    private int year;

    /**
     * The month
     */
    @Getter
    private int month;

    /**
     * The day
     */
    @Getter
    private int day;

    /**
     * The default constructor.
     */
    private PresentDateSet() {
        final Calendar now = Calendar.getInstance();
        this.year = now.get(Calendar.YEAR);
        this.month = now.get(Calendar.MONTH);
        this.day = now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns the new instance of {@link PresentDateSet} .
     *
     * @return The new instance of {@link PresentDateSet}
     */
    public static PresentDateSet newInstance() {
        return new PresentDateSet();
    }
}
