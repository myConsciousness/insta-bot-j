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
 * The catalog that manages element XPath.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum ElementXPath implements BiCatalog<ElementXPath, String> {

    /**
     * The login
     */
    LOGIN(0, "//*[@id=\"loginForm\"]/div/div[3]/button/div"),

    /**
     * The login completed
     */
    LOGIN_COMPLETED(1, "//*[@id=\"react-root\"]/section/main/div/div/div/section/div/button"),

    /**
     * The first element of tags
     */
    TAGS_FIRST_ELEMENT(2, "//*[@id=\"react-root\"]/section/main/article/div[2]/div/div[1]/div[1]/a/div/div[2]"),

    /**
     * The first element of tags
     */
    TAGS_FIRST_ELEMENT_2(3, "//*[@id=\"react-root\"]/section/main/article/div[2]/div/div[1]/div[1]/a/div/div[2]"),

    /**
     * The like button
     */
    LIKE_BUTTON(4, "/html/body/div[5]/div[2]/div/article/div[3]/section[1]/span[1]/button"),

    /**
     * The like button
     */
    LIKE_BUTTON_2(5, "/html/body/div[4]/div[2]/div/article/div[3]/section[1]/span[1]/button"),

    /**
     * The comment button
     */
    COMMENT_BUTTON(6, "/html/body/div[5]/div[2]/div/article/div[3]/section[1]/span[2]/button");

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
