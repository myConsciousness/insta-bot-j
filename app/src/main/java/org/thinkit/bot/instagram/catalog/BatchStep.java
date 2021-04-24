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
     * The prepare autolike step
     */
    PREPARE_AUTOLIKE(1, "PrepareAutolikeStep"),

    /**
     * The execute autolike step
     */
    EXECUTE_AUTOLIKE(2, "ExecuteAutolikeStep"),

    /**
     * The complete autolike step
     */
    COMPLETE_AUTOLIKE(3, "CompleteAutolikeStep"),

    /**
     * The reversal entry hashtag step
     */
    REVERSAL_ENTRY_HASHTAG(800, "ReversalEntryHashtag"),

    /**
     * The logout step
     */
    LOGOUT(900, "Logout");

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
