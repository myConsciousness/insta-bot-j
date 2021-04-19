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
import org.thinkit.bot.instagram.command.AutoLikeCommand;
import org.thinkit.bot.instagram.command.LoginCommand;
import org.thinkit.bot.instagram.config.ActionConfig;
import org.thinkit.bot.instagram.config.ActionHashtag;
import org.thinkit.bot.instagram.config.ActionUser;
import org.thinkit.bot.instagram.result.AutolikeResult;
import org.thinkit.bot.instagram.result.BotResult;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class InstaBotJ extends AbstractInstaBot {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = -7380913294460202882L;

    /**
     * The constructor.
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    private InstaBotJ() {
        super();
    }

    /**
     * Returns the new instance of {@link InstaBotJ} based on the arguments.
     *
     * @return The new instance of {@link InstaBotJ}
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    public static InstaBot newInstance() {
        return new InstaBotJ();
    }

    /**
     * The constructor.
     *
     * @param actionConfig The action config
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    private InstaBotJ(@NonNull final ActionConfig actionConfig) {
        super(actionConfig);
    }

    /**
     * Returns the new instance of {@link InstaBotJ} based on the arguments.
     *
     * @param botConfig The bot config
     * @return The new instance of {@link InstaBotJ}
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    public static InstaBot from(@NonNull final ActionConfig botConfig) {
        return new InstaBotJ(botConfig);
    }

    @Override
    public BotResult executeLogin(@NonNull final ActionUser actionUser) {

        LoginCommand.from(actionUser.getUserName(), actionUser.getPassword()).execute(super.getWebDriver());

        return null;
    }

    @Override
    public List<AutolikeResult> executeAutoLikes(@NonNull final List<ActionHashtag> hashTags) {

        if (hashTags.isEmpty()) {
            throw new IllegalArgumentException("The hash tag is required to execute autolikes.");
        }

        final List<AutolikeResult> autolikeResults = new ArrayList<>();
        final int maxLikesPerTag = super.getMaxAttempt() / hashTags.size();

        for (final ActionHashtag hashTag : hashTags) {
            final AutolikeResult autolikeResult = AutoLikeCommand.from(hashTag, maxLikesPerTag)
                    .execute(super.getWebDriver());
            autolikeResults.add(autolikeResult);

            if (autolikeResult.getActionStatus() == ActionStatus.INTERRUPTED) {
                return autolikeResults;
            }
        }

        return autolikeResults;
    }
}
