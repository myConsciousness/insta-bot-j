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
import org.thinkit.bot.instagram.catalog.ElementName;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.result.ActionedLikedPhoto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class LoginCommand extends AbstractBotCommand<ActionedLikedPhoto> {

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

    @Override
    public ActionedLikedPhoto executeBotProcess() {

        super.getWebPage(InstagramUrl.LOGIN.getTag());
        super.findElement(By.name(ElementName.USER_NAME.getTag())).sendKeys(this.userName);
        super.findElement(By.name(ElementName.PASSWORD.getTag())).sendKeys(this.password);
        super.findElement(By.xpath(ElementXPath.LOGIN.getTag())).click();

        super.waitUntilElementLocated(By.xpath(ElementXPath.LOGIN_COMPLETED.getTag()));

        return ActionedLikedPhoto.builder().build();
    }
}
