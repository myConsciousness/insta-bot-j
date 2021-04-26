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

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.param.ForecastUser;
import org.thinkit.bot.instagram.result.FollowBackExpectableUser;
import org.thinkit.bot.instagram.result.ForecastFollowBackResult;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class ForecastFollowBackUserCommand extends AbstractBotCommand<ForecastFollowBackResult> {

    /**
     * The forecast users
     */
    private List<ForecastUser> forecastUsers;

    @Override
    protected ForecastFollowBackResult executeBotProcess() {

        final List<FollowBackExpectableUser> followBackExpectableUsers = new ArrayList<>();

        for (final ForecastUser forecastUser : this.forecastUsers) {

            final String userProfileUrl = String.format(InstagramUrl.USER_PRODILE.getTag(), forecastUser.getUserName());
            super.getWebPage(userProfileUrl);

            System.out.println(
                    super.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/header/section/div[2]/h1"))
                            .getText());
            System.out.println(super.findElement(
                    By.xpath("//*[@id=\"react-root\"]/section/main/div/header/section/ul/li[1]/span/span")).getText());
            System.out.println(super.findElement(
                    By.xpath("//*[@id=\"react-root\"]/section/main/div/header/section/ul/li[2]/a/span")).getText());
            System.out.println(super.findElement(
                    By.xpath("//*[@id=\"react-root\"]/section/main/div/header/section/ul/li[3]/a/span")).getText());

        }

        return ForecastFollowBackResult.builder().followBackExpectableUsers(followBackExpectableUsers).build();
    }
}
