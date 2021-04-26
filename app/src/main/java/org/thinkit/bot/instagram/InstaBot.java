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

import java.util.List;

import org.thinkit.bot.instagram.config.AutoFollowConfig;
import org.thinkit.bot.instagram.config.AutoLikeConfig;
import org.thinkit.bot.instagram.config.AutoUnfollowConfig;
import org.thinkit.bot.instagram.param.ActionUser;
import org.thinkit.bot.instagram.param.FollowUser;
import org.thinkit.bot.instagram.param.ForecastUser;
import org.thinkit.bot.instagram.param.TargetHashtag;
import org.thinkit.bot.instagram.param.UnfollowUser;
import org.thinkit.bot.instagram.result.AutoFollowResult;
import org.thinkit.bot.instagram.result.AutoLikeResult;
import org.thinkit.bot.instagram.result.AutoUnfollowResult;
import org.thinkit.bot.instagram.result.ForecastFollowBackResult;
import org.thinkit.bot.instagram.result.LoginResult;

import lombok.NonNull;

public interface InstaBot {

    public LoginResult executeLogin(@NonNull final ActionUser actionUser);

    public List<AutoLikeResult> executeAutoLikes(@NonNull final List<TargetHashtag> targetHashtags,
            @NonNull final AutoLikeConfig autoLikeConfig);

    public AutoFollowResult executeAutoFollow(@NonNull final List<FollowUser> followUsers,
            @NonNull final AutoFollowConfig autoFollowConfig);

    public AutoUnfollowResult executeAutoUnfollow(@NonNull final List<UnfollowUser> unfollowUsers,
            @NonNull final AutoUnfollowConfig autoUnfollowConfig);

    public ForecastFollowBackResult executeForecastFollowBackUser(@NonNull final List<ForecastUser> forecastUsers);
}
