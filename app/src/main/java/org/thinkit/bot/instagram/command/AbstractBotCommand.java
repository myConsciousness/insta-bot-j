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

import java.io.Serializable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.WaitType;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.util.StackTraceUtils;
import org.thinkit.bot.instagram.util.WaitTimeUtils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public abstract class AbstractBotCommand<R> implements BotCommand<R>, Serializable {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = -1990878890028788690L;

    private WebDriver webDriver;

    protected abstract R executeBotProcess();

    protected final WebElement findElement(@NonNull final By by) {
        this.waitUntilElementLocated(by);
        return webDriver.findElement(by);
    }

    @Override
    public R execute(@NonNull final WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait(WaitType.DEFAULT);
        return this.executeBotProcess();
    }

    protected final void getWebPage(@NonNull final String url) {
        this.webDriver.get(url);
    }

    protected final String getCurrentUrl() {
        return this.webDriver.getCurrentUrl();
    }

    protected final void wait(@NonNull final WaitType waitType) {
        try {
            Thread.sleep(WaitTimeUtils.create(waitType));
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    protected final void waitUntilElementLocated(@NonNull final By by) {
        new WebDriverWait(this.webDriver, 10).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected final void waitUntilElementClickable(@NonNull final By by) {
        new WebDriverWait(this.webDriver, 10).until(ExpectedConditions.elementToBeClickable(by));
    }

    protected ActionError getActionError(@NonNull final Exception exception, @NonNull final TaskType taskType) {

        final ActionError.ActionErrorBuilder actionErrorBuilder = ActionError.builder();
        actionErrorBuilder.taskType(taskType);
        actionErrorBuilder.message(exception.getMessage());
        actionErrorBuilder.localizedMessage(exception.getLocalizedMessage());
        actionErrorBuilder.stackTrace(StackTraceUtils.toString(exception));

        return actionErrorBuilder.build();
    }
}
