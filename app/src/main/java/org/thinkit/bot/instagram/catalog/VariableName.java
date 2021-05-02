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
 * The catalog that manages variable name.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum VariableName implements BiCatalog<VariableName, String> {

    /**
     * {@code "LIKE_PER_TASK"}
     */
    LIKE_PER_TASK(0, "LIKE_PER_TASK"),

    /**
     * {@code "LIKE_INTERVAL"}
     */
    LIKE_INTERVAL(1, "LIKE_INTERVAL"),

    /**
     * {@code "FOLLOW_PER_DAY"}
     */
    FOLLOW_PER_DAY(100, "FOLLOW_PER_DAY"),

    /**
     * {@code "UNFOLLOW_PER_DAY"}
     */
    UNFOLLOW_PER_DAY(200, "UNFOLLOW_PER_DAY"),

    /**
     * {@code "BATCH_FLOW_STRATEGY"}
     */
    BATCH_FLOW_STRATEGY(700, "BATCH_FLOW_STRATEGY"),

    /**
     * {@code "HASHTAG_GROUP_COUNT"}
     */
    HASHTAG_GROUP_COUNT(800, "HASHTAG_GROUP_COUNT"),

    /**
     * {@code "FORECAST_FOLLOW_BACK_EXPECTABLE_USER_PER_TASK"}
     */
    FORECAST_FOLLOW_BACK_EXPECTABLE_USER_PER_TASK(801, "FORECAST_FOLLOW_BACK_EXPECTABLE_USER_PER_TASK"),

    /**
     * {@code "ACTION_RESTRICTION_WAIT_HOUR"}
     */
    ACTION_RESTRICTION_WAIT_HOUR(802, "ACTION_RESTRICTION_WAIT_HOUR"),

    /**
     * {@code "SKIP_MOOD_OCCURRENCE_PROBABILITY"}
     */
    SKIP_MOOD_OCCURRENCE_PROBABILITY(803, "SKIP_MOOD_OCCURRENCE_PROBABILITY"),

    /**
     * {@code "LINE_NOTIFY_TOKEN"}
     */
    LINE_NOTIFY_TOKEN(900, "LINE_NOTIFY_TOKEN"),

    /**
     * {@code "LOGIN_STRATEGY"}
     */
    LOGIN_STRATEGY(901, "LOGIN_STRATEGY"),

    /**
     * {@code "LOGIN_TWO_FACTOR_TOKEN"}
     */
    LOGIN_TWO_FACTOR_TOKEN(902, "LOGIN_TWO_FACTOR_TOKEN");

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
