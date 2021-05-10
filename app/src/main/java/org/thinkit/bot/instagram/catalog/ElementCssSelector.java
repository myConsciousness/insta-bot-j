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
 * The catalog that manages css selector of the element.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum ElementCssSelector implements BiCatalog<ElementCssSelector, String> {

    /**
     * The next arrow
     */
    NEXT_ARROW(0, "a.coreSpriteRightPaginationArrow"),

    /**
     * The user name on explore
     */
    USER_NAME_ON_EXPLORE(1,
            "body > div._2dDPU.CkGkG > div.zZYga > div > article > header > div.o-MQd.z8cbW > div.PQo_0.RqtMr > div.e1e1d > span > a"),

    /**
     * The post count on profile
     */
    PROFILE_POST_COUNT(2, "#react-root > section > main > div > header > section > ul > li:nth-child(1) > span > span"),

    /**
     * The follower count on profile
     */
    PROFILE_FOLLOWER_COUNT(3,
            "#react-root > section > main > div > header > section > ul > li:nth-child(2) > a > span"),

    /**
     * The following count on profile
     */
    PROFILE_FOLLOWING_COUNT(4,
            "#react-root > section > main > div > header > section > ul > li:nth-child(3) > a > span");

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
