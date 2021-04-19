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
import org.openqa.selenium.WebElement;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.CommandType;
import org.thinkit.bot.instagram.catalog.ElementAttribute;
import org.thinkit.bot.instagram.catalog.ElementCssSelector;
import org.thinkit.bot.instagram.catalog.ElementTag;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.catalog.WaitType;
import org.thinkit.bot.instagram.config.ActionHashtag;
import org.thinkit.bot.instagram.content.CompletedLikeStateMapper;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionedLikedPhoto;
import org.thinkit.bot.instagram.result.AutolikeResult;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AutoLikeCommand extends AbstractBotCommand<AutolikeResult> {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 6084564883236221860L;

    /**
     * The hash tag
     */
    private ActionHashtag actionHashtag;

    /**
     * The count of max like
     */
    private int maxLikes;

    private AutoLikeCommand(@NonNull final ActionHashtag actionHashtag, final int maxLikes) {
        this.actionHashtag = actionHashtag;
        this.maxLikes = maxLikes;
    }

    public static BotCommand<AutolikeResult> from(@NonNull final ActionHashtag actionHashtag, final int maxLikes) {
        return new AutoLikeCommand(actionHashtag, maxLikes);
    }

    @Override
    public AutolikeResult executeBotProcess() {

        final AutolikeResult.AutolikeResultBuilder autolikeResultBuilder = AutolikeResult.builder();

        super.getWebPage(String.format(InstagramUrl.TAGS.getTag(), this.actionHashtag.getTag()));
        super.findElement(By.xpath(ElementXPath.TAGS_FIRST_ELEMENT.getTag())).click();

        int countLikes = 0;
        final String completedLikeState = this.getCompletedLikeState();
        final List<ActionedLikedPhoto> actionedLikedPhotos = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        while (countLikes < maxLikes) {
            try {
                if (countLikes != 0 && countLikes % 25 == 0) {
                    super.wait(WaitType.LIKE);
                }

                final WebElement likeButton = this.findLikeButton();
                final String likeState = likeButton.findElement(By.tagName(ElementTag.SVG.getTag()))
                        .getAttribute(ElementAttribute.ARIA_LABEL.getTag());

                if (likeState.contains(completedLikeState)) {
                    this.clickNextArrorw();
                    continue;
                }

                final ActionedLikedPhoto.ActionedLikedPhotoBuilder actionedLikedPhotoBuilder = ActionedLikedPhoto
                        .builder();
                final WebElement userProfileLink = super.findElement(
                        By.cssSelector(ElementCssSelector.USER_NAME_ON_EXPLORE.getTag()));

                actionedLikedPhotoBuilder.userName(userProfileLink.getText());
                actionedLikedPhotoBuilder.url(super.getCurrentUrl());
                actionedLikedPhotos.add(actionedLikedPhotoBuilder.build());

                System.out.println(actionedLikedPhotos);

                likeButton.click();
                this.clickNextArrorw();
                countLikes++;
            } catch (Exception e) {
                // The possibility exists that a timeout may occur due to delays during
                // communication, etc. Anyway, let's move on to the next post.
                actionErrors.add(super.getActionError(e, CommandType.AUTO_LIKE));
                this.clickNextArrorw();
            }
        }

        autolikeResultBuilder.ActionStatus(ActionStatus.COMPLETED);
        autolikeResultBuilder.hashtag(this.actionHashtag.getTag());
        autolikeResultBuilder.countLikes(countLikes);
        autolikeResultBuilder.actionedLikedPhotos(actionedLikedPhotos);
        autolikeResultBuilder.actionErrors(actionErrors);

        return autolikeResultBuilder.build();
    }

    private String getCompletedLikeState() {
        return CompletedLikeStateMapper.newInstance().scan().get(0).getCompletedLikeState();
    }

    private WebElement findLikeButton() {
        try {
            return super.findElement(By.xpath(ElementXPath.LIKE_BUTTON.getTag()));
        } catch (Exception e) {
            // The condition for this to occur is unknown, but there are two types of XPaths
            // for the Like button.
            return super.findElement(By.xpath(ElementXPath.LIKE_BUTTON_2.getTag()));
        }
    }

    private void clickNextArrorw() {
        super.findElement(By.cssSelector(ElementCssSelector.NEXT_ARROW.getTag())).click();
        ;
    }
}
