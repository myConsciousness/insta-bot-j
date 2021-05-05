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

        final List<ActionFollowingUser> actionFollowingUsers = new ArrayList<>();
        final List<ActionFollower> actionFollowers = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        super.getWebPage(String.format(InstagramUrl.USER_PROFILE.getTag(), this.actionUser.getUserName()));

        final int followerCount = this.fetchFollowerCount();
        super.findByXpath(ElementXPath.PROFILE_FOLLOWERS_LINK).click();

        for (int i = 1; i < followerCount + 1; i++) {
            final WebElement row = super.findElement(
                    By.xpath(String.format(ElementXPath.PROFILE_MODAL_LIST.getTag(), i)));
            System.out.println(row.getText());

            super.executeScript(JavaScriptCommand.SCROLL_VIEW, row);
        }

        // this.fetchFollowingCount();

        // super.findByXpath(ElementXPath.PROFILE_FOLLOWING_LINK).click();

        final AutoScrapeUserProfileResult.AutoScrapeUserProfileResultBuilder autoScrapeUserProfileResultBuilder = AutoScrapeUserProfileResult
                .builder();
        autoScrapeUserProfileResultBuilder.actionStatus(ActionStatus.COMPLETED);
        autoScrapeUserProfileResultBuilder.actionFollowingUsers(actionFollowingUsers);
        autoScrapeUserProfileResultBuilder.actionFollowers(actionFollowers);
        autoScrapeUserProfileResultBuilder.actionErrors(actionErrors);

        return autoScrapeUserProfileResultBuilder.build();
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
