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

package org.thinkit.bot.instagram.catalog;

import org.thinkit.api.catalog.BiCatalog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The catalog that manages batch step.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum BatchStep implements BiCatalog<BatchStep, String> {

    /**
     * The login step
     */
    LOGIN(0, "LoginStep"),

    /**
     * The execute auto like step
     */
    EXECUTE_AUTO_LIKE(1, "ExecuteAutoLikeStep"),

    /**
     * The execute auto forecast follow back user
     */
    EXECUTE_AUTO_FORECAST_FOLLOW_BACK_USER(2, "ExecuteAutoForecastFollowBackUser"),

    /**
     * The execute auto follow step
     */
    EXECUTE_AUTO_FOLLOW(3, "ExecuteAutoFollow"),

    /**
     * The execute auto unfollow step
     */
    EXECUTE_AUTO_UNFOLLOW(4, "ExecuteAutoUnfollow"),

    /**
     * The update auto like config step
     */
    UPDATE_AUTO_LIKE_CONFIG(800, "UpdateAutoLikeConfig"),

    /**
     * The update auto forecast follow back user config step
     */
    UPDATE_AUTO_FORECAST_FOLLOW_BACK_USER_CONFIG(800, "UpdateAutoForecastFollowBackUserConfig"),

    /**
     * The notify result
     */
    NOTIFY_RESULT(900, "NotifyResult");

    /**
     * The code
     */
    @Getter
    private final int code;

    /**
     * The tag
     */
    @Getter
    private final String tag;
}
