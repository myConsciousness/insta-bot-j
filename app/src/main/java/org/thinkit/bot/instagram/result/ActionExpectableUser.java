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

package org.thinkit.bot.instagram.result;

import java.io.Serializable;

import org.thinkit.bot.instagram.catalog.FollowBackPossibility;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The class that manages the action expectable user.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActionExpectableUser implements Serializable {

    /**
     * The user name
     */
    @Getter
    private String userName;

    /**
     * The follow back possibility
     */
    @Getter
    private FollowBackPossibility followBackPossibility;

    /**
     * The post
     */
    @Getter
    private int post;

    /**
     * The following
     */
    @Getter
    private int following;

    /**
     * The follower
     */
    @Getter
    private int follower;

    /**
     * The follow diff
     */
    @Getter
    private int followDiff;
}
