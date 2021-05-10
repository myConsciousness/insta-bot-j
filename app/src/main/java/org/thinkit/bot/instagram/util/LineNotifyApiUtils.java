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

import java.util.StringJoiner;

import com.mongodb.lang.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.thinkit.bot.instagram.batch.catalog.LineNotifyParameter;
import org.thinkit.bot.instagram.catalog.Delimiter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LineNotifyApiUtils {

    public static String toMessageContent(@NonNull final String message) {

        if (StringUtils.isEmpty(message)) {
            return "";
        }

        final StringJoiner messageJoiner = new StringJoiner(Delimiter.EQUAL.getTag());
        messageJoiner.add(LineNotifyParameter.MESSAGE.getTag());
        messageJoiner.add(message);

        return messageJoiner.toString();
    }
}
