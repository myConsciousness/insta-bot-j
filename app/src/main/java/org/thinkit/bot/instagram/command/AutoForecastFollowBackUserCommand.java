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
import org.thinkit.bot.instagram.batch.data.content.entity.NumberUnitResource;
import org.thinkit.bot.instagram.batch.data.mongo.entity.FollowBackPossibilityIndicator;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.Delimiter;
import org.thinkit.bot.instagram.catalog.ElementCssSelector;
import org.thinkit.bot.instagram.catalog.FollowBackPossibility;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.WaitType;
import org.thinkit.bot.instagram.config.AutoForecastFollowBackUserConfig;
import org.thinkit.bot.instagram.param.ForecastUser;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionExpectableUser;
import org.thinkit.bot.instagram.result.ActionUnexpectableUser;
import org.thinkit.bot.instagram.result.AutoForecastFollowBackResult;

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
public final class AutoForecastFollowBackUserCommand extends AbstractBotCommand<AutoForecastFollowBackResult> {

    /**
     * The number text for NaN
     */
    private static final String NUMBER_TEXT_NAN = "0";

    /**
     * The forecast users
     */
    private List<ForecastUser> forecastUsers;

    /**
     * The auto forecast follow back user config
     */
    private AutoForecastFollowBackUserConfig autoForecastFollowBackUserConfig;

    @Override
    protected AutoForecastFollowBackResult executeBotProcess() {

        final List<ActionExpectableUser> expectableUsers = new ArrayList<>();
        final List<ActionUnexpectableUser> unexpectableUsers = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        final NumberUnitResource numberUnitResource = this.autoForecastFollowBackUserConfig.getNumberUnitResource();

        for (final ForecastUser forecastUser : this.forecastUsers) {
            try {
                super.wait(WaitType.HUMAN_LIKE_INTERVAL);

                final String userName = forecastUser.getUserName();
                final String userProfileUrl = String.format(InstagramUrl.USER_PROFILE.getTag(), userName);
                super.getWebPage(userProfileUrl);

                if (this.isUserInfluencer(numberUnitResource)) {
                    continue;
                }

                final int postCount = this.fetchPostCount();
                final int followerCount = this.fetchFollowerCount();
                final int followingCount = this.fetchFollowingCount();

                final FollowBackPossibility followBackPossibility = this.getFollowBackPossibility(followerCount,
                        followingCount);

                if (followBackPossibility != FollowBackPossibility.NONE) {
                    final ActionExpectableUser.ActionExpectableUserBuilder actionExpectableUserBuilder = ActionExpectableUser
                            .builder();
                    actionExpectableUserBuilder.userName(userName);
                    actionExpectableUserBuilder.followBackPossibility(followBackPossibility);
                    actionExpectableUserBuilder.post(postCount);
                    actionExpectableUserBuilder.follower(followerCount);
                    actionExpectableUserBuilder.following(followingCount);
                    actionExpectableUserBuilder.followDiff(followerCount - followingCount);

                    expectableUsers.add(actionExpectableUserBuilder.build());
                } else {
                    unexpectableUsers.add(ActionUnexpectableUser.builder().userName(userName).build());
                }
            } catch (Exception recoverableException) {
                // The possibility exists that a timeout may occur due to wrong css selector was
                // located, etc. Anyway, let's move on to the next profile.
                unexpectableUsers.add(ActionUnexpectableUser.builder().userName(forecastUser.getUserName()).build());
                actionErrors.add(super.getActionError(recoverableException, TaskType.AUTO_FORECAST_FOLLOW_BACK_USER));
            }
        }

        final AutoForecastFollowBackResult.AutoForecastFollowBackResultBuilder autoForecastFollowBackResultBuilder = AutoForecastFollowBackResult
                .builder();
        autoForecastFollowBackResultBuilder.actionStatus(ActionStatus.COMPLETED);
        autoForecastFollowBackResultBuilder.actionCount(expectableUsers.size() + unexpectableUsers.size());
        autoForecastFollowBackResultBuilder.expectableUsers(expectableUsers);
        autoForecastFollowBackResultBuilder.unexpectableUsers(unexpectableUsers);

        if (!actionErrors.isEmpty()) {
            autoForecastFollowBackResultBuilder.actionErrors(actionErrors);
        }

        return autoForecastFollowBackResultBuilder.build();
    }

    private boolean isUserInfluencer(@NonNull final NumberUnitResource numberUnitResource) {
        final String follower = super.findByCssSelector(ElementCssSelector.PROFILE_FOLLOWER_COUNT).getText();
        return follower.contains(numberUnitResource.getUnit1()) || follower.contains(numberUnitResource.getUnit2());
    }

    private int fetchPostCount() {
        return Integer.parseInt(this.getNumberText(ElementCssSelector.PROFILE_POST_COUNT));
    }

    private int fetchFollowerCount() {
        return Integer.parseInt(this.getNumberText(ElementCssSelector.PROFILE_FOLLOWER_COUNT));
    }

    private int fetchFollowingCount() {
        return Integer.parseInt(this.getNumberText(ElementCssSelector.PROFILE_FOLLOWING_COUNT));
    }

    private String getNumberText(@NonNull final ElementCssSelector elementCssSelector) {

        final String numberText = super.findByCssSelector(elementCssSelector).getText();
        final String notNumberText = this.autoForecastFollowBackUserConfig.getNotNumberText().getText();

        if (notNumberText.equals(numberText)) {
            return NUMBER_TEXT_NAN;
        }

        return this.removeComma(numberText);
    }

    private String removeComma(@NonNull final String number) {
        return StringUtils.remove(number, Delimiter.COMMA.getTag());
    }

    private FollowBackPossibility getFollowBackPossibility(final int follower, final int following) {

        if (following >= autoForecastFollowBackUserConfig.getFollowingNearLimit()) {
            // Consider users who are close to the maximum number of following as unlikely
            // to follow back.
            return FollowBackPossibility.NONE;
        }

        final int indicator = follower - following;
        final FollowBackPossibilityIndicator followBackPossibilityIndicator = this.autoForecastFollowBackUserConfig
                .getFollowBackPossibilityIndicator();

        if (indicator > followBackPossibilityIndicator.getLowestIndicator()) {
            return FollowBackPossibility.NONE;
        }

        if (indicator <= followBackPossibilityIndicator.getHighestIndicator()) {
            return FollowBackPossibility.HIGHEST;
        } else if (indicator <= followBackPossibilityIndicator.getHighIndicator()) {
            return FollowBackPossibility.HIGH;
        } else if (indicator <= followBackPossibilityIndicator.getMiddleIndicator()) {
            return FollowBackPossibility.MIDDLE;
        } else if (indicator <= followBackPossibilityIndicator.getLowIndicator()) {
            return FollowBackPossibility.LOW;
        }

        return FollowBackPossibility.LOWEST;
    }
}
