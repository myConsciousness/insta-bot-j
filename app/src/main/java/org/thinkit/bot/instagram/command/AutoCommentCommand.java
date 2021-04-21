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
import org.openqa.selenium.WebElement;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.param.TargetHashtag;
import org.thinkit.bot.instagram.result.ActionLikedPhoto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
@Deprecated
public final class AutoCommentCommand extends AbstractBotCommand<ActionLikedPhoto> {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = -1758807819257637600L;

    /**
     * The hash tag
     */
    private TargetHashtag hashTag;

    @Override
    public ActionLikedPhoto executeBotProcess() {

        super.getWebPage(String.format(InstagramUrl.TAGS.getTag(), hashTag.getTag()));
        super.waitUntilElementLocated(By.xpath(ElementXPath.TAGS_FIRST_ELEMENT.getTag()));
        // super.click(webDriver, By.xpath(ElementXPath.TAGS_FIRST_ELEMENT.getTag()));

        int countComments = 0;
        while (countComments < 1) {

            super.waitUntilElementLocated(
                    By.xpath("/html/body/div[4]/div[2]/div/article/div[3]/section[3]/div/form/textarea"));
            final WebElement commentArea = super.findElement(
                    By.xpath("/html/body/div[4]/div[2]/div/article/div[3]/section[3]/div/form/textarea"));
            commentArea.click();
            commentArea.sendKeys("cool!");
            // webDriver.findElement(By.xpath("/html/body/div[5]/div[2]/div/article/div[3]/section[3]/div/form/button[2]"))
            // .click();

            this.clickNextArrorw();
            countComments++;
        }

        return ActionLikedPhoto.builder().build();
    }

    private void clickNextArrorw() {
        // super.click(webDriver,
        // By.cssSelector(ElementCssSelector.NEXT_ARROW.getTag()));
    }
}
