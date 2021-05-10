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

package org.thinkit.bot.instagram.batch.catalog;

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
     * The initialize step
     */
    INITIALIZE(-1, "InitializeStep"),

    /**
     * The execute auto login step
     */
    EXECUTE_AUTO_LOGIN(0, "ExecuteAutoLoginStep"),

    /**
     * The execute auto like step
     */
    EXECUTE_AUTO_LIKE(1, "ExecuteAutoLikeStep"),

    /**
     * The execute auto forecast follow back user step
     */
    EXECUTE_AUTO_FORECAST_FOLLOW_BACK_USER(2, "ExecuteAutoForecastFollowBackUser"),

    /**
     * The execute auto scrape user profile step
     */
    EXECUTE_AUTO_SCRAPE_USER_PROFILE(3, "ExecuteAutoScrapeUserProfile"),

    /**
     * The execute auto diagnose follow step
     */
    EXECUTE_AUTO_DIAGNOSE_FOLLOW(4, "executeAutoDiagnoseFollow"),

    /**
     * The execute auto follow step
     */
    EXECUTE_AUTO_FOLLOW(5, "ExecuteAutoFollow"),

    /**
     * The execute auto unfollow step
     */
    EXECUTE_AUTO_UNFOLLOW(6, "ExecuteAutoUnfollow"),

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
    NOTIFY_RESULT(900, "NotifyResult"),

    /**
     * The close session
     */
    CLOSE_SESSION(999, "CloseSession");

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
