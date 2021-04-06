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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.thinkit.bot.instagram.catalog.ElementName;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.InstagramUrl;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoginCommand extends AbstractBotCommand {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 5301155638988060027L;

    /**
     * The user name
     */
    private String userName;

    /**
     * The password
     */
    @ToString.Exclude
    private String password;

    /**
     * The constructor.
     *
     * @param userName The user name
     * @param password The password
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    private LoginCommand(@NonNull final String userName, @NonNull final String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Returns the new instance of {@link LoginCommand} based on arguments.
     *
     * @param userName The user name
     * @param password The password
     * @return The new instance of {@link LoginCommand}
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    public static BotCommand from(@NonNull final String userName, @NonNull final String password) {
        return new LoginCommand(userName, password);
    }

    @Override
    public boolean execute(@NonNull final WebDriver webDriver) {

        webDriver.get(InstagramUrl.LOGIN.getTag());
        webDriver.findElement(By.name(ElementName.USER_NAME.getTag())).sendKeys(this.userName);
        webDriver.findElement(By.name(ElementName.PASSWORD.getTag())).sendKeys(this.password);
        webDriver.findElement(By.xpath(ElementXPath.LOGIN.getTag())).click();

        this.waitUntilLoginCompleted(webDriver);

        return true;
    }

    /**
     * Waits next step until the login process is completed.
     *
     * @param webDriver The web driver
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    private void waitUntilLoginCompleted(@NonNull final WebDriver webDriver) {
        new WebDriverWait(webDriver, 10)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(ElementXPath.LOGIN_COMPLETED.getTag())));
    }
}
