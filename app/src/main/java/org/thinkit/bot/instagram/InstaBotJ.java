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

import org.thinkit.bot.instagram.command.AutoLikeCommand;
import org.thinkit.bot.instagram.config.BotConfig;
import org.thinkit.bot.instagram.result.BotResult;
import org.thinkit.bot.instagram.tag.HashTag;
import org.thinkit.bot.instagram.user.InstagramUser;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
final class InstaBotJ extends AbstractInstaBot {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = -7380913294460202882L;

    /**
     * The constructor.
     *
     * @param instagramUser The user of Instagram
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    private InstaBotJ(@NonNull final InstagramUser instagramUser) {
        super(instagramUser);
    }

    /**
     * Returns the new instance of {@link InstaBotJ} based on the arguments.
     *
     * @param instagramUser The user of Instagram
     * @return The new instance of {@link InstaBotJ}
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    public static InstaBot from(@NonNull final InstagramUser instagramUser) {
        return new InstaBotJ(instagramUser);
    }

    /**
     * The constructor.
     *
     * @param instagramUser The user of Instagram
     * @param botConfig     The bot config
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    private InstaBotJ(@NonNull final InstagramUser instagramUser, @NonNull final BotConfig botConfig) {
        super(instagramUser, botConfig);
    }

    /**
     * Returns the new instance of {@link InstaBotJ} based on the arguments.
     *
     * @param instagramUser The user of Instagram
     * @param botConfig     The bot config
     * @return The new instance of {@link InstaBotJ}
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    public static InstaBot from(@NonNull final InstagramUser instagramUser, @NonNull final BotConfig botConfig) {
        return new InstaBotJ(instagramUser, botConfig);
    }

    @Override
    public BotResult executeAutoLikes(@NonNull final List<HashTag> hashTags) {

        if (hashTags.isEmpty()) {
            throw new IllegalArgumentException("The hash tag is required to execute auto likes.");
        }

        final BotResult.BotResultBuilder resultBuilder = BotResult.builder();

        int countLikes = 0;
        final int maxLikesPerTag = super.getMaxAttempt() / hashTags.size();

        for (final HashTag hashTag : hashTags) {
            countLikes += AutoLikeCommand.from(hashTag, maxLikesPerTag).execute(super.getWebDriver());
        }

        return resultBuilder.countAttempt(countLikes).build();
    }
}
