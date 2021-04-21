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

package org.thinkit.bot.instagram.batch.tasklet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.batch.MongoCollection;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.CommandType;
import org.thinkit.bot.instagram.config.ActionHashtag;
import org.thinkit.bot.instagram.mongo.entity.ActionRecord;
import org.thinkit.bot.instagram.mongo.entity.Error;
import org.thinkit.bot.instagram.mongo.entity.Hashtag;
import org.thinkit.bot.instagram.mongo.entity.LikedPhoto;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionLikedPhoto;
import org.thinkit.bot.instagram.result.AutoLikeResult;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class ExecuteAutolikeTasklet implements Tasklet {

    /**
     * The insta bot
     */
    private InstaBot instaBot;

    /**
     * The mongo collection
     */
    private MongoCollection mongoCollection;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.debug("START");

        final List<AutoLikeResult> autolikeResults = this.instaBot.executeAutoLikes(this.getActionHashtags());
        log.info("The autolike has completed the process successfully.");

        int sumLikes = 0;

        for (final AutoLikeResult autolikeResult : autolikeResults) {
            final String hashtag = autolikeResult.getHashtag();
            sumLikes += autolikeResult.getCountLikes();

            for (final ActionLikedPhoto actionLikedPhoto : autolikeResult.getActionLikedPhotos()) {
                final LikedPhoto likedPhoto = new LikedPhoto();
                likedPhoto.setUserName(actionLikedPhoto.getUserName());
                likedPhoto.setUrl(actionLikedPhoto.getUrl());
                likedPhoto.setHashtag(hashtag);

                final LikedPhoto insertedLikedPhoto = this.mongoCollection.getLikedPhotoRepository().insert(likedPhoto);
                log.debug("Inserted liked photo: {}", insertedLikedPhoto);
            }

            if (autolikeResult.getActionErrors() != null) {
                log.debug("Autolike runtime error detected.");

                for (final ActionError actionError : autolikeResult.getActionErrors()) {
                    final Error error = new Error();
                    error.setCommandTypeCode(actionError.getCommandType().getCode());
                    error.setMessage(actionError.getMessage());
                    error.setLocalizedMessage(actionError.getLocalizedMessage());
                    error.setStackTrace(actionError.getStackTrace());

                    final Error insertedError = this.mongoCollection.getErrorRepository().insert(error);
                    log.debug("Inserted error: {}", insertedError);
                }
            }
        }

        final ActionRecord actionRecord = new ActionRecord();
        actionRecord.setCommandTypeCode(CommandType.AUTO_LIKE.getCode());
        actionRecord.setCountAttempt(sumLikes);
        actionRecord.setActionStatusCode(ActionStatus.COMPLETED.getCode());

        this.mongoCollection.getActionRecordRepository().insert(actionRecord);
        log.debug("Inserted action record: {}", actionRecord);

        log.debug("END");
        return RepeatStatus.FINISHED;
    }

    private List<ActionHashtag> getActionHashtags() {
        log.debug("START");

        final List<Hashtag> hashtags = this.mongoCollection.getHashtagRepository().findAll();
        final List<ActionHashtag> actionHashtags = new ArrayList<>(hashtags.size());

        hashtags.forEach(hashtag -> {
            log.debug("The using hashtags: {}", hashtag);
            actionHashtags.add(ActionHashtag.from(hashtag.getTag()));
        });

        log.debug("END");
        return actionHashtags;
    }
}
