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
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.result.LogoutResult;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(staticName = "newInstance")
public final class LogoutCommand extends AbstractBotCommand<LogoutResult> {

    @Override
    protected LogoutResult executeBotProcess() {

        super.getWebPage(InstagramUrl.HOME.getTag());

        super.waitUntilElementClickable(By.xpath(ElementXPath.DENY_NOTIFICATION_BUTTON.getTag()));
        super.findElement(By.xpath(ElementXPath.DENY_NOTIFICATION_BUTTON.getTag())).click();

        super.waitUntilElementClickable(By.xpath(ElementXPath.MY_PROFILE_ICON.getTag()));
        super.findElement(By.xpath(ElementXPath.MY_PROFILE_ICON.getTag())).click();

        super.waitUntilElementClickable(By.xpath(ElementXPath.LOGOUT.getTag()));
        super.findElement(By.xpath(ElementXPath.LOGOUT.getTag())).click();

        return LogoutResult.builder().actionStatus(ActionStatus.COMPLETED).build();
    }
}
