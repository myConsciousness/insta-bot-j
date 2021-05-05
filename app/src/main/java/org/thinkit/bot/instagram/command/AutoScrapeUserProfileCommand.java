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

package org.thinkit.bot.instagram.command;

import java.util.ArrayList;
import java.util.List;

import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.param.ActionUser;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionFollower;
import org.thinkit.bot.instagram.result.ActionFollowingUser;
import org.thinkit.bot.instagram.result.AutoScrapeUserProfileResult;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class AutoScrapeUserProfileCommand extends AbstractBotCommand<AutoScrapeUserProfileResult> {

    /**
     * The action user
     */
    private ActionUser actionUser;

    @Override
    protected AutoScrapeUserProfileResult executeBotProcess() {

        final List<ActionFollowingUser> actionFollowingUsers = new ArrayList<>();
        final List<ActionFollower> actionFollowers = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        final AutoScrapeUserProfileResult.AutoScrapeUserProfileResultBuilder autoScrapeUserProfileResultBuilder = AutoScrapeUserProfileResult
                .builder();
        autoScrapeUserProfileResultBuilder.actionStatus(ActionStatus.COMPLETED);
        autoScrapeUserProfileResultBuilder.actionFollowingUsers(actionFollowingUsers);
        autoScrapeUserProfileResultBuilder.actionFollowers(actionFollowers);
        autoScrapeUserProfileResultBuilder.actionErrors(actionErrors);

        return autoScrapeUserProfileResultBuilder.build();
    }
}
