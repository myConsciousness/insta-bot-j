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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.thinkit.bot.instagram.catalog.Delimiter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuntimeMxUtils {

    private static final RuntimeMXBean RUNTIME_MX_BEAN = ManagementFactory.getRuntimeMXBean();

    public static long getPid() {
        return Long.valueOf(getJvmName().split(Delimiter.AT_MARK.getTag())[0]);
    }

    public static String getJvmName() {
        return RUNTIME_MX_BEAN.getName();
    }

    public static String getVmName() {
        return RUNTIME_MX_BEAN.getVmName();
    }

    public static String getVmVersion() {
        return RUNTIME_MX_BEAN.getVmVersion();
    }

    public static String getVmVendor() {
        return RUNTIME_MX_BEAN.getVmVendor();
    }

    public static String getSpecName() {
        return RUNTIME_MX_BEAN.getSpecName();
    }

    public static String getSpecVersion() {
        return RUNTIME_MX_BEAN.getSpecVersion();
    }

    public static String getManagementSpecVersion() {
        return RUNTIME_MX_BEAN.getManagementSpecVersion();
    }

    public static String getInputArgs() {
        return RUNTIME_MX_BEAN.getInputArguments().toString();
    }

    public static String getClassPath() {
        return RUNTIME_MX_BEAN.getClassPath();
    }

    public static String getLibraryPath() {
        return RUNTIME_MX_BEAN.getLibraryPath();
    }

    public static String getBootClassPath() {
        return RUNTIME_MX_BEAN.getBootClassPath();
    }
}
