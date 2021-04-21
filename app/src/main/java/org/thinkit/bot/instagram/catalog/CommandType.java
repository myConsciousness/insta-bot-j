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
 * The catalog that manages command type.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum CommandType implements Catalog<CommandType> {

    /**
     * The login command
     */
    LOGIN(0),

    /**
     * The auto like command
     */
    AUTO_LIKE(1),

    /**
     * The auto comment command
     */
    AUTO_COMMENT(2),

    /**
     * The auto follow command
     */
    AUTO_FOLLOW(3),

    /**
     * The auto unfollow command
     */
    AUTO_UNFOLLOW(4),

    /**
     * The close web browser
     */
    CLOSE_WEB_BROWSER(800),

    /**
     * The reversal entry hashtag command
     */
    REVERSAL_ENTRY_HASHTAG(900);

    /**
     * The code
     */
    @Getter
    private final int code;
}
