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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.FollowStateType;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.WaitType;
import org.thinkit.bot.instagram.config.AutoFollowConfig;
import org.thinkit.bot.instagram.content.FollowStateMapper;
import org.thinkit.bot.instagram.param.FollowUser;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionFollowFailedUser;
import org.thinkit.bot.instagram.result.ActionFollowedUser;
import org.thinkit.bot.instagram.result.AutoFollowResult;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class AutoFollowCommand extends AbstractBotCommand<AutoFollowResult> {

    /**
     * The follow users
     */
    private List<FollowUser> followUsers;

    /**
     * The auto follow config
     */
    private AutoFollowConfig autoFollowConfig;

    @Override
    protected AutoFollowResult executeBotProcess() {

        final List<ActionFollowedUser> actionFollowedUsers = new ArrayList<>();
        final List<ActionFollowFailedUser> actionFollowFailedUsers = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        final String followBaclState = this.getFollowBackState();
        String userName = "";

        for (final FollowUser followUser : this.followUsers) {
            try {
                super.wait(WaitType.FOLLOW);

                if (!actionFollowedUsers.isEmpty()
                        && actionFollowedUsers.size() % autoFollowConfig.getFollowInterval() == 0) {
                    super.wait(WaitType.HUMAN_LIKE_INTERVAL);
                }

                userName = followUser.getUserName();
                super.getWebPage(String.format(InstagramUrl.USER_PROFILE.getTag(), userName));

                super.waitUntilElementClickable(By.xpath(ElementXPath.FOLLOW_BUTTON.getTag()));
                final WebElement followButton = super.findByXpath(ElementXPath.FOLLOW_BUTTON);

                if (followBaclState.equals(followButton.getText())) {
                    // Already followed by this user
                    actionFollowFailedUsers.add(ActionFollowFailedUser.builder().userName(userName).build());
                } else {

                    followButton.click();

                    final ActionFollowedUser.ActionFollowedUserBuilder actionFollowedUserBuilder = ActionFollowedUser
                            .builder();
                    actionFollowedUserBuilder.userName(userName);
                    actionFollowedUserBuilder.url(super.getCurrentUrl());
                    actionFollowedUsers.add(actionFollowedUserBuilder.build());
                }
            } catch (Exception recoverableException) {
                // The possibility exists that a timeout may occur due to wrong css selector was
                // located, or the profile is private, etc. Anyway, let's move on to the next
                // follow action.
                actionFollowFailedUsers.add(ActionFollowFailedUser.builder().userName(userName).build());
                actionErrors.add(super.getActionError(recoverableException, TaskType.AUTO_FOLLOW));
            }
        }

        final AutoFollowResult.AutoFollowResultBuilder autoFollowResultBuilder = AutoFollowResult.builder();
        autoFollowResultBuilder.ActionStatus(ActionStatus.COMPLETED);
        autoFollowResultBuilder.countFollowed(actionFollowedUsers.size());
        autoFollowResultBuilder.actionFollowedUsers(actionFollowedUsers);
        autoFollowResultBuilder.actionFollowFailedUsers(actionFollowFailedUsers);
        autoFollowResultBuilder.actionErrors(actionErrors);

        return autoFollowResultBuilder.build();
    }

    private String getFollowBackState() {
        return FollowStateMapper.from(FollowStateType.FOLLOWER.getCode()).scan().get(0).getState();
    }
}
