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

import com.mongodb.lang.NonNull;

import org.thinkit.bot.instagram.catalog.IndentType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IndentUtils {

    /**
     * The newline
     */
    private static final String NEWLINE = IndentType.NEWLINE.getTag();

    /**
     * The space
     */
    private static final String SPACE = IndentType.SPACE.getTag();

    public static String newline() {
        return NEWLINE;
    }

    public static String newline(@NonNull final String sequence) {
        return new StringBuilder(sequence).append(NEWLINE).toString();
    }

    public static String space() {
        return SPACE;
    }

    public static String space(@NonNull final String sequence) {
        return new StringBuilder(sequence).append(SPACE).toString();
    }
}
