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
import org.thinkit.bot.instagram.catalog.ElementAttribute;
import org.thinkit.bot.instagram.catalog.ElementCssSelector;
import org.thinkit.bot.instagram.catalog.ElementTag;
import org.thinkit.bot.instagram.catalog.ElementXPath;
import org.thinkit.bot.instagram.catalog.InstagramUrl;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.WaitType;
import org.thinkit.bot.instagram.content.CompletedLikeStateMapper;
import org.thinkit.bot.instagram.content.DefaultLikeIntervalMapper;
import org.thinkit.bot.instagram.param.TargetHashtag;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionLikedPhoto;
import org.thinkit.bot.instagram.result.AutoLikeResult;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AutoLikeCommand extends AbstractBotCommand<AutoLikeResult> {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 6084564883236221860L;

    /**
     * The target hashtag
     */
    private TargetHashtag targetHashtag;

    /**
     * The count of max like
     */
    private int maxLikes;

    private AutoLikeCommand(@NonNull final TargetHashtag targetHashtag, final int maxLikes) {
        this.targetHashtag = targetHashtag;
        this.maxLikes = maxLikes;
    }

    public static BotCommand<AutoLikeResult> from(@NonNull final TargetHashtag targetHashtag, final int maxLikes) {
        return new AutoLikeCommand(targetHashtag, maxLikes);
    }

    @Override
    public AutoLikeResult executeBotProcess() {

        final AutoLikeResult.AutoLikeResultBuilder autolikeResultBuilder = AutoLikeResult.builder();

        super.getWebPage(String.format(InstagramUrl.TAGS.getTag(), this.targetHashtag.getTag()));
        this.findFirstElement().click();

        final List<ActionLikedPhoto> actionedLikedPhotos = new ArrayList<>();
        final List<ActionError> actionErrors = new ArrayList<>();

        int countLikes = 0;
        boolean likedPhoto = false;
        final int likeInterval = this.getLikeInterval();
        final String completedLikeState = this.getCompletedLikeState();

        while (countLikes < maxLikes) {
            try {
                if (countLikes != 0 && countLikes % likeInterval == 0) {
                    super.wait(WaitType.LIKE);
                } else {
                    if (likedPhoto) {
                        super.wait(WaitType.HUMAN_LIKE_INTERVAL);
                    }
                }

                final WebElement likeButton = this.findLikeButton();
                final String likeState = likeButton.findElement(By.tagName(ElementTag.SVG.getTag()))
                        .getAttribute(ElementAttribute.ARIA_LABEL.getTag());

                if (likeState.contains(completedLikeState)) {
                    likedPhoto = false;
                    this.clickNextArrorw();
                    continue;
                }

                final ActionLikedPhoto.ActionLikedPhotoBuilder actionedLikedPhotoBuilder = ActionLikedPhoto.builder();
                final WebElement userProfileLink = super.findElement(
                        By.cssSelector(ElementCssSelector.USER_NAME_ON_EXPLORE.getTag()));

                actionedLikedPhotoBuilder.userName(userProfileLink.getText());
                actionedLikedPhotoBuilder.url(super.getCurrentUrl());
                actionedLikedPhotos.add(actionedLikedPhotoBuilder.build());

                likeButton.click();
                likedPhoto = true;
                this.clickNextArrorw();
                countLikes++;
            } catch (Exception recoverableException) {
                // The possibility exists that a timeout may occur due to delays during
                // communication, etc. Anyway, let's move on to the next post.
                actionErrors.add(super.getActionError(recoverableException, TaskType.AUTO_LIKE));

                try {
                    this.clickNextArrorw();
                } catch (Exception unrecoverableException) {
                    // Errors that reach here may be due to restricted actions by Instagram.
                    actionErrors.add(super.getActionError(unrecoverableException, TaskType.AUTO_LIKE));

                    autolikeResultBuilder.ActionStatus(ActionStatus.INTERRUPTED);
                    autolikeResultBuilder.hashtag(this.targetHashtag.getTag());
                    autolikeResultBuilder.countLikes(countLikes);
                    autolikeResultBuilder.actionLikedPhotos(actionedLikedPhotos);
                    autolikeResultBuilder.actionErrors(actionErrors);

                    return autolikeResultBuilder.build();
                }

                likedPhoto = false;
            }
        }

        autolikeResultBuilder.ActionStatus(ActionStatus.COMPLETED);
        autolikeResultBuilder.hashtag(this.targetHashtag.getTag());
        autolikeResultBuilder.countLikes(countLikes);
        autolikeResultBuilder.actionLikedPhotos(actionedLikedPhotos);

        if (!actionErrors.isEmpty()) {
            autolikeResultBuilder.actionErrors(actionErrors);
        }

        return autolikeResultBuilder.build();
    }

    private int getLikeInterval() {
        return DefaultLikeIntervalMapper.newInstance().scan().get(0).getInterval();
    }

    private String getCompletedLikeState() {
        return CompletedLikeStateMapper.newInstance().scan().get(0).getCompletedLikeState();
    }

    private WebElement findFirstElement() {
        try {
            return super.findElement(By.xpath(ElementXPath.TAGS_FIRST_ELEMENT.getTag()));
        } catch (Exception e) {
            // The condition for this to occur is unknown, but there are two types of XPaths
            // for the first element.
            return super.findElement(By.xpath(ElementXPath.TAGS_FIRST_ELEMENT_2.getTag()));
        }
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
