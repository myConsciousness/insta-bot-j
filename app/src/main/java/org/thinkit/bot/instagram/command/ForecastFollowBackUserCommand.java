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

import org.thinkit.bot.instagram.catalog.ElementCssSelector;
import org.thinkit.bot.instagram.catalog.FollowBackPossibility;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.content.FollowBackPossibilityIndicatorMapper;
import org.thinkit.bot.instagram.content.NumberUnitResourceMapper;
import org.thinkit.bot.instagram.content.entity.FollowBackPossibilityIndicator;
import org.thinkit.bot.instagram.content.entity.NumberUnitResource;
import org.thinkit.bot.instagram.param.ForecastUser;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ExpectableUser;
import org.thinkit.bot.instagram.result.ForecastFollowBackResult;
import org.thinkit.bot.instagram.util.NumberUnitResolver;

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
public final class ForecastFollowBackUserCommand extends AbstractBotCommand<ForecastFollowBackResult> {

    /**
     * The forecast users
     */
    private List<ForecastUser> forecastUsers;

    @Override
    protected ForecastFollowBackResult executeBotProcess() {

        final List<ExpectableUser> expectableUsers = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        final NumberUnitResolver numberUnitResolver = NumberUnitResolver.from(this.getNumberUnitResource());
        final FollowBackPossibilityIndicator followBackPossibilityIndicator = this.getFollowBackPossibilityIndicator();

        for (final ForecastUser forecastUser : this.forecastUsers) {
            try {
                final String userName = forecastUser.getUserName();
                final String userProfileUrl = String.format(InstagramUrl.USER_PROFILE.getTag(), userName);
                super.getWebPage(userProfileUrl);

                final int postCount = this.fetchPostCount(numberUnitResolver);
                final int followerCount = this.fetchFollowerCount(numberUnitResolver);
                final int followingCount = this.fetchFollowingCount(numberUnitResolver);

                final int followDiff = followerCount - followingCount;
                final FollowBackPossibility followBackPossibility = this.getFollowBackPossibility(followDiff,
                        followBackPossibilityIndicator);

                if (followBackPossibility != FollowBackPossibility.NONE) {
                    final ExpectableUser.ExpectableUserBuilder expectableUserBuilder = ExpectableUser.builder();
                    expectableUserBuilder.userName(userName);
                    expectableUserBuilder.followBackPossibility(followBackPossibility);
                    expectableUserBuilder.post(postCount);
                    expectableUserBuilder.follower(followerCount);
                    expectableUserBuilder.following(followingCount);
                    expectableUserBuilder.followDiff(followDiff);

                    expectableUsers.add(expectableUserBuilder.build());
                }
            } catch (Exception recoverableException) {
                // The possibility exists that a timeout may occur due to wrong css selector was
                // located, etc. Anyway, let's move on to the next profile.
                actionErrors.add(super.getActionError(recoverableException, TaskType.FORECAST_FOLLOW_BACK_USER));
            }
        }

        final ForecastFollowBackResult.ForecastFollowBackResultBuilder forecastFollowBackResultBuilder = ForecastFollowBackResult
                .builder();
        forecastFollowBackResultBuilder.expectableUsers(expectableUsers);

        if (!actionErrors.isEmpty()) {
            forecastFollowBackResultBuilder.actionErrors(actionErrors);
        }

        return forecastFollowBackResultBuilder.build();
    }

    private int fetchPostCount(@NonNull final NumberUnitResolver numberUnitResolver) {
        return numberUnitResolver.deduceInt(super.findByCssSelector(ElementCssSelector.PROFILE_POST_COUNT).getText());
    }

    private int fetchFollowerCount(@NonNull final NumberUnitResolver numberUnitResolver) {
        return numberUnitResolver
                .deduceInt(super.findByCssSelector(ElementCssSelector.PROFILE_FOLLOWER_COUNT).getText());
    }

    private int fetchFollowingCount(@NonNull final NumberUnitResolver numberUnitResolver) {
        return numberUnitResolver
                .deduceInt(super.findByCssSelector(ElementCssSelector.PROFILE_FOLLOWING_COUNT).getText());
    }

    private FollowBackPossibility getFollowBackPossibility(final int indicator,
            @NonNull final FollowBackPossibilityIndicator followBackPossibilityIndicator) {

        if (indicator > followBackPossibilityIndicator.getLowest()) {
            return FollowBackPossibility.NONE;
        }

        if (indicator <= followBackPossibilityIndicator.getHighest()) {
            return FollowBackPossibility.HIGHEST;
        } else if (indicator <= followBackPossibilityIndicator.getHigh()) {
            return FollowBackPossibility.HIGH;
        } else if (indicator <= followBackPossibilityIndicator.getMiddle()) {
            return FollowBackPossibility.MIDDLE;
        } else if (indicator <= followBackPossibilityIndicator.getLow()) {
            return FollowBackPossibility.LOW;
        }

        return FollowBackPossibility.LOWEST;
    }

    private NumberUnitResource getNumberUnitResource() {
        return NumberUnitResourceMapper.newInstance().scan().get(0);
    }

    private FollowBackPossibilityIndicator getFollowBackPossibilityIndicator() {
        return FollowBackPossibilityIndicatorMapper.newInstance().scan().get(0);
    }
}
