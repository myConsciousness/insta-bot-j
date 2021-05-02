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
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.WaitType;
import org.thinkit.bot.instagram.config.AutoUnfollowConfig;
import org.thinkit.bot.instagram.param.UnfollowUser;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionUnfollowFailedUser;
import org.thinkit.bot.instagram.result.ActionUnfollowedUser;
import org.thinkit.bot.instagram.result.AutoUnfollowResult;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class AutoUnfollowCommand extends AbstractBotCommand<AutoUnfollowResult> {

    /**
     * The unfollow users
     */
    private List<UnfollowUser> unfollowUsers;

    /**
     * The auto unfollow config
     */
    private AutoUnfollowConfig autoUnfollowConfig;

    @Override
    protected AutoUnfollowResult executeBotProcess() {

        final List<ActionUnfollowedUser> actionUnfollowedUsers = new ArrayList<>();
        final List<ActionUnfollowFailedUser> actionUnfollowFailedUsers = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        String userName = "";
        for (final UnfollowUser unfollowUser : this.unfollowUsers) {
            try {
                super.wait(WaitType.HUMAN_LIKE_INTERVAL);

                userName = unfollowUser.getUserName();
                super.getWebPage(String.format(InstagramUrl.USER_PROFILE.getTag(), userName));

                super.waitUntilElementClickable(By.xpath(ElementXPath.UNFOLLOW_BUTTON.getTag()));
                super.findByXpath(ElementXPath.FOLLOW_BUTTON).click();

                super.waitUntilElementClickable(By.xpath(ElementXPath.UNFOLLOW_BUTTON_ON_MODAL.getTag()));
                super.findByXpath(ElementXPath.UNFOLLOW_BUTTON_ON_MODAL).click();

                final ActionUnfollowedUser.ActionUnfollowedUserBuilder actionUnfollowedUserBuilder = ActionUnfollowedUser
                        .builder();
                actionUnfollowedUserBuilder.userName(userName);
                actionUnfollowedUserBuilder.url(super.getCurrentUrl());

                actionUnfollowedUsers.add(actionUnfollowedUserBuilder.build());

            } catch (Exception recoverableException) {
                // The possibility exists that a timeout may occur due to wrong css selector was
                // located, etc. Anyway, let's move on to the next
                // unfollow action.
                actionUnfollowFailedUsers.add(ActionUnfollowFailedUser.builder().userName(userName).build());
                actionErrors.add(super.getActionError(recoverableException, TaskType.AUTO_UNFOLLOW));
            }
        }

        final AutoUnfollowResult.AutoUnfollowResultBuilder autoUnfollowResultBuilder = AutoUnfollowResult.builder();
        autoUnfollowResultBuilder.actionStatus(ActionStatus.COMPLETED);
        autoUnfollowResultBuilder.countUnfollowed(actionUnfollowedUsers.size());
        autoUnfollowResultBuilder.actionUnfollowedUsers(actionUnfollowedUsers);
        autoUnfollowResultBuilder.actionUnfollowFailedUsers(actionUnfollowFailedUsers);
        autoUnfollowResultBuilder.actionErrors(actionErrors);

        return autoUnfollowResultBuilder.build();
    }
}
