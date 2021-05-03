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

import org.thinkit.api.catalog.Catalog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The catalog that manages task type.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum TaskType implements Catalog<TaskType> {

    /**
     * The login task
     */
    LOGIN(000),

    /**
     * The auto like task
     */
    AUTO_LIKE(001),

    /**
     * The auto forecast follow back user
     */
    AUTO_FORECAST_FOLLOW_BACK_USER(002),

    /**
     * The auto comment task
     */
    AUTO_COMMENT(003),

    /**
     * The auto follow task
     */
    AUTO_FOLLOW(004),

    /**
     * The auto unfollow task
     */
    AUTO_UNFOLLOW(005),

    /**
     * The initialize
     */
    INITIALIZE(800),

    /**
     * The update auto like config
     */
    UPDATE_AUTO_LIKE_CONFIG(801),

    /**
     * The update auto forecast follow back user config
     */
    UPDATE_AUTO_FORECAST_FOLLOW_BACK_USER_CONFIG(802),

    /**
     * The notify result
     */
    NOTIFY_RESULT(900);

    /**
     * The code
     */
    @Getter
    private final int code;
}
