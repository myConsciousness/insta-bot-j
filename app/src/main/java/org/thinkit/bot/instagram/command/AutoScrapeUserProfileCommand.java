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

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.Delimiter;
import org.thinkit.bot.instagram.catalog.ElementCssSelector;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.catalog.JavaScriptCommand;
import org.thinkit.bot.instagram.catalog.Separator;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.WaitType;
import org.thinkit.bot.instagram.param.ActionUser;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionFollower;
import org.thinkit.bot.instagram.result.ActionFollowingUser;
import org.thinkit.bot.instagram.result.AutoScrapeUserProfileResult;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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

        final List<ActionFollower> actionFollowers = new ArrayList<>();
        final List<ActionFollowingUser> actionFollowingUsers = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        this.scrapeActionFollowers(actionFollowers, actionErrors);
        this.scrapeActionFollowingUsers(actionFollowingUsers, actionErrors);

        final AutoScrapeUserProfileResult.AutoScrapeUserProfileResultBuilder autoScrapeUserProfileResultBuilder = AutoScrapeUserProfileResult
                .builder();
        autoScrapeUserProfileResultBuilder.actionStatus(ActionStatus.COMPLETED);
        autoScrapeUserProfileResultBuilder.actionFollowingUsers(actionFollowingUsers);
        autoScrapeUserProfileResultBuilder.actionFollowers(actionFollowers);
        autoScrapeUserProfileResultBuilder.actionErrors(actionErrors);

        return autoScrapeUserProfileResultBuilder.build();
    }

    private void scrapeActionFollowers(@NonNull final List<ActionFollower> actionFollowers,
            @NonNull final List<ActionError> actionErrors) {
        this.getProfileUsers(ElementXPath.PROFILE_FOLLOWERS_LINK, actionErrors).forEach(follower -> {
            actionFollowers.add(ActionFollower.builder().userName(follower).build());
        });
    }

    private void scrapeActionFollowingUsers(@NonNull final List<ActionFollowingUser> actionFollowingUsers,
            @NonNull final List<ActionError> actionErrors) {
        this.getProfileUsers(ElementXPath.PROFILE_FOLLOWING_LINK, actionErrors).forEach(followingUser -> {
            actionFollowingUsers.add(ActionFollowingUser.builder().userName(followingUser).build());
        });
    }

    private List<String> getProfileUsers(@NonNull final ElementXPath profileModalLink,
            @NonNull final List<ActionError> actionErrors) {

        final List<String> profileUsers = new ArrayList<>();

        super.wait(WaitType.HUMAN_LIKE_INTERVAL);
        super.getWebPage(String.format(InstagramUrl.USER_PROFILE.getTag(), this.actionUser.getUserName()));
        super.wait(WaitType.HUMAN_LIKE_INTERVAL);

        final int loopCount = profileModalLink == ElementXPath.PROFILE_FOLLOWERS_LINK ? this.fetchFollowerCount()
                : this.fetchFollowingCount();
        super.findByXpath(profileModalLink).click();

        for (int i = 1; i <= loopCount; i++) {
            WebElement row = null;
            try {
                row = super.findElement(By.xpath(String.format(ElementXPath.PROFILE_MODAL_LIST.getTag(), i)));
                final String userText = row.getText();

                if (userText.contains(Separator.WHITESPACE.getTag())) {
                    profileUsers.add(StringUtils.split(userText)[0]);
                } else if (userText.contains(Separator.NEWLINE.getTag())) {
                    profileUsers.add(StringUtils.split(userText, Separator.NEWLINE.getTag())[0]);
                } else {
                    profileUsers.add(userText);
                }

                super.executeScript(JavaScriptCommand.SCROLL_VIEW, row);

            } catch (Exception recoverableException) {
                // There is an inconsistency between the number of followers shown in profile
                // and the actual number of followers. This exception is mainly caused by the
                // inconsistency in the number of followers. At least, once this exception is
                // reached, the data of all users is considered to have been scraped.
                actionErrors.add(super.getActionError(recoverableException, TaskType.AUTO_SCRAPE_USER_PROFILE));
            }
        }

        return profileUsers;
    }

    private int fetchFollowerCount() {
        return Integer.parseInt(this.getNumberText(ElementCssSelector.PROFILE_FOLLOWER_COUNT));
    }

    private int fetchFollowingCount() {
        return Integer.parseInt(this.getNumberText(ElementCssSelector.PROFILE_FOLLOWING_COUNT));
    }

    private String getNumberText(@NonNull final ElementCssSelector elementCssSelector) {
        return this.removeComma(super.findByCssSelector(elementCssSelector).getText());
    }

    private String removeComma(@NonNull final String number) {
        return StringUtils.remove(number, Delimiter.COMMA.getTag());
    }
}
