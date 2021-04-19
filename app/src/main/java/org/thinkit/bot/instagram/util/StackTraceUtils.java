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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.mongodb.lang.NonNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StackTraceUtils {

    public static String toString(@NonNull final Exception exception) {
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
            exception.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }
}
