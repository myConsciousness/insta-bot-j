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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.thinkit.bot.instagram.command.LoginCommand;
import org.thinkit.bot.instagram.config.BotConfig;
import org.thinkit.bot.instagram.content.DefaultBotConfigMapper;
import org.thinkit.bot.instagram.user.BotUser;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
abstract class AbstractInstaBot implements InstaBot, Serializable {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = -7602453727507633803L;

    /**
     * The web driver
     */
    @Getter(AccessLevel.PROTECTED)
    private WebDriver webDriver;

    /**
     * The maxAttempt
     */
    @Getter(AccessLevel.PROTECTED)
    private int maxAttempt;

    protected AbstractInstaBot(@NonNull final BotUser botUser) {
        this.setupWebDriver();
        this.maxAttempt = DefaultBotConfigMapper.newInstance().scan().get(0).getMaxAttempt();

        LoginCommand.from(botUser.getUserName(), botUser.getPassword()).execute(this.webDriver);
    }

    protected AbstractInstaBot(@NonNull final BotUser botUser, @NonNull final BotConfig botConfig) {
        this.setupWebDriver();
        this.maxAttempt = botConfig.getMaxAttempt();

        LoginCommand.from(botUser.getUserName(), botUser.getPassword()).execute(this.webDriver);
    }

    private void setupWebDriver() {
        WebDriverManager.chromedriver().setup();
        this.webDriver = new ChromeDriver();
        this.webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
}
