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

package org.thinkit.bot.instagram;

import java.util.ArrayList;
import java.util.List;

import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.command.AutoFollowCommand;
import org.thinkit.bot.instagram.command.AutoForecastFollowBackUserCommand;
import org.thinkit.bot.instagram.command.AutoLikeCommand;
import org.thinkit.bot.instagram.command.AutoLoginCommand;
import org.thinkit.bot.instagram.command.AutoScrapeUserProfileCommand;
import org.thinkit.bot.instagram.command.AutoUnfollowCommand;
import org.thinkit.bot.instagram.config.AutoFollowConfig;
import org.thinkit.bot.instagram.config.AutoForecastFollowBackUserConfig;
import org.thinkit.bot.instagram.config.AutoLikeConfig;
import org.thinkit.bot.instagram.config.AutoUnfollowConfig;
import org.thinkit.bot.instagram.param.ActionUser;
import org.thinkit.bot.instagram.param.FollowUser;
import org.thinkit.bot.instagram.param.ForecastUser;
import org.thinkit.bot.instagram.param.TargetHashtag;
import org.thinkit.bot.instagram.param.UnfollowUser;
import org.thinkit.bot.instagram.result.AutoFollowResult;
import org.thinkit.bot.instagram.result.AutoForecastFollowBackResult;
import org.thinkit.bot.instagram.result.AutoLikeResult;
import org.thinkit.bot.instagram.result.AutoLoginResult;
import org.thinkit.bot.instagram.result.AutoScrapeUserProfileResult;
import org.thinkit.bot.instagram.result.AutoUnfollowResult;
import org.thinkit.common.base.precondition.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(staticName = "newInstance")
public final class InstaBotJ extends AbstractInstaBot {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = -7380913294460202882L;

    @Override
    public AutoLoginResult executeLogin(@NonNull final ActionUser actionUser) {
        return AutoLoginCommand.from(actionUser.getUserName(), actionUser.getPassword()).execute(super.getWebDriver());
    }

    @Override
    public List<AutoLikeResult> executeAutoLike(@NonNull final List<TargetHashtag> targetHashtags,
            @NonNull final AutoLikeConfig autoLikeConfig) {
        Preconditions.requireNonEmpty(targetHashtags, "The hash tag is required to execute auto like command.");
        Preconditions.requirePositive(autoLikeConfig.getMaxLikePerHashtag(),
                "The count of max like must not be negative.");
        Preconditions.requirePositive(autoLikeConfig.getInterval(), "The count of like interval must not be negative.");
        Preconditions.requireNonNull(autoLikeConfig.getCompletedLikeState(), "The completed like state is required.");

        final List<AutoLikeResult> autolikeResults = new ArrayList<>();

        for (final TargetHashtag targetHashtag : targetHashtags) {
            final AutoLikeResult autolikeResult = AutoLikeCommand.from(targetHashtag, autoLikeConfig)
                    .execute(super.getWebDriver());
            autolikeResults.add(autolikeResult);

            if (autolikeResult.getActionStatus() == ActionStatus.INTERRUPTED) {
                return autolikeResults;
            }
        }

        return autolikeResults;
    }

    @Override
    public AutoForecastFollowBackResult executeAutoForecastFollowBackUser(
            @NonNull final List<ForecastUser> forecastUsers,
            @NonNull final AutoForecastFollowBackUserConfig autoForecastFollowBackUserConfig) {
        Preconditions.requireNonEmpty(forecastUsers,
                "The forecast user is required to execute auto forecast follow back user command.");
        return AutoForecastFollowBackUserCommand.from(forecastUsers, autoForecastFollowBackUserConfig)
                .execute(super.getWebDriver());
    }

    @Override
    public AutoScrapeUserProfileResult executeAutoScrapeUserProfile(@NonNull final ActionUser actionUser) {
        return AutoScrapeUserProfileCommand.from(actionUser).execute(super.getWebDriver());
    }

    @Override
    public AutoFollowResult executeAutoFollow(@NonNull final List<FollowUser> followUsers,
            @NonNull final AutoFollowConfig autoFollowConfig) {
        Preconditions.requireNonEmpty(followUsers, "The follow user is required to execute auto follow command.");
        Preconditions.requirePositive(autoFollowConfig.getInterval(),
                "The count of follow interval must not be negative.");

        return AutoFollowCommand.from(followUsers, autoFollowConfig).execute(super.getWebDriver());
    }

    @Override
    public AutoUnfollowResult executeAutoUnfollow(@NonNull final List<UnfollowUser> unfollowUsers,
            @NonNull final AutoUnfollowConfig autoUnfollowConfig) {
        Preconditions.requireNonEmpty(unfollowUsers, "The unfollow user is required to execute auto unfollow command.");
        Preconditions.requirePositive(autoUnfollowConfig.getInterval(),
                "The count of unfollow interval must not be negative.");

        return AutoUnfollowCommand.from(unfollowUsers, autoUnfollowConfig).execute(super.getWebDriver());
    }
}
