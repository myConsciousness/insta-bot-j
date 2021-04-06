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

import org.openqa.selenium.WebDriver;
import org.thinkit.bot.instagram.command.AutoLikeCommand;
import org.thinkit.bot.instagram.command.LoginCommand;
import org.thinkit.bot.instagram.tag.HashTag;

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
     * @param userName The user name
     * @param password The password
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    private InstaBotJ(@NonNull final String userName, @NonNull final String password) {
        super(userName, password);
    }

    /**
     * Returns the new instance of {@link InstaBotJ} based on the arguments.
     *
     * @param userName The user name
     * @param password The password
     * @return The new instance of {@link InstaBotJ}
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    public static InstaBot from(@NonNull final String userName, @NonNull final String password) {
        return new InstaBotJ(userName, password);
    }

    @Override
    public boolean executeAutoLikes() {

        final WebDriver webDriver = super.getWebDriver();

        LoginCommand.from(super.getUserName(), super.getPassword()).execute(webDriver);
        AutoLikeCommand.from(HashTag.from("love")).execute(webDriver);

        return true;
    }
}
