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
import java.util.List;

import org.thinkit.bot.instagram.catalog.ActionStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * The class that manages the result of auto scrape user profile command.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AutoScrapeUserProfileResult implements Serializable {

    /**
     * The action status
     */
    @Getter
    @NonNull
    private ActionStatus actionStatus;

    /**
     * The action following users
     */
    @Getter
    @NonNull
    private List<ActionFollowingUser> actionFollowingUsers;

    /**
     * The action followers
     */
    @Getter
    @NonNull
    private List<ActionFollower> actionFollowers;

    /**
     * The action errors
     */
    @Getter
    private List<ActionError> actionErrors;
}
