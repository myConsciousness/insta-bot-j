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
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.VariableName;
import org.thinkit.bot.instagram.config.AutoLikeConfig;
import org.thinkit.bot.instagram.mongo.MongoCollection;
import org.thinkit.bot.instagram.mongo.entity.Hashtag;
import org.thinkit.bot.instagram.mongo.entity.LikedPhoto;
import org.thinkit.bot.instagram.param.TargetHashtag;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.result.ActionLikedPhoto;
import org.thinkit.bot.instagram.result.AutoLikeResult;
import org.thinkit.bot.instagram.util.RandomUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ExecuteAutoLikeTasklet extends AbstractTasklet {

    private ExecuteAutoLikeTasklet() {
        super(TaskType.AUTO_LIKE);
    }

    public static Tasklet newInstance() {
        return new ExecuteAutoLikeTasklet();
    }

    @Override
    public BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final List<AutoLikeResult> autolikeResults = super.getInstaBot().executeAutoLikes(this.getTargetHashtags(),
                this.getAutoLikeConfig());
        log.info("The autolike has completed the process successfully.");

        final MongoCollection mongoCollection = super.getMongoCollection();
        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();

        int sumLikes = 0;
        final List<ActionError> actionErrors = new ArrayList<>();

        for (final AutoLikeResult autolikeResult : autolikeResults) {
            final String hashtag = autolikeResult.getHashtag();
            sumLikes += autolikeResult.getCountLikes();

            for (final ActionLikedPhoto actionLikedPhoto : autolikeResult.getActionLikedPhotos()) {
                final LikedPhoto likedPhoto = new LikedPhoto();
                likedPhoto.setUserName(actionLikedPhoto.getUserName());
                likedPhoto.setUrl(actionLikedPhoto.getUrl());
                likedPhoto.setHashtag(hashtag);

                final LikedPhoto insertedLikedPhoto = mongoCollection.getLikedPhotoRepository().insert(likedPhoto);
                log.debug("Inserted liked photo: {}", insertedLikedPhoto);
            }

            if (autolikeResult.getActionErrors() != null) {
                log.debug("Auto Like runtime error detected.");
                autolikeResult.getActionErrors().forEach(actionError -> {
                    actionErrors.add(actionError);
                });
            }

            if (autolikeResult.getActionStatus() == ActionStatus.INTERRUPTED) {
                batchTaskResultBuilder.actionStatus(ActionStatus.INTERRUPTED);
            }
        }

        batchTaskResultBuilder.countAttempt(sumLikes);
        batchTaskResultBuilder.actionErrors(actionErrors);

        log.debug("END");
        return batchTaskResultBuilder.build();
    }

    private List<TargetHashtag> getTargetHashtags() {
        log.debug("START");

        final List<Hashtag> hashtags = super.getMongoCollection().getHashtagRepository()
                .findByGroupCode(this.getTargetGroupCode());
        final List<TargetHashtag> targetHashtags = new ArrayList<>(hashtags.size());

        hashtags.forEach(hashtag -> {
            log.debug("The using hashtags: {}", hashtag);
            targetHashtags.add(TargetHashtag.from(hashtag.getTag()));
        });

        log.debug("END");
        return targetHashtags;
    }

    private AutoLikeConfig getAutoLikeConfig() {
        log.debug("START");

        final int maxLike = this.getMaxLike();
        final int skippedCount = this.getSkippedCount();

        final AutoLikeConfig autoLikeConfig = AutoLikeConfig.builder()
                .maxLike(skippedCount > 0 ? maxLike * skippedCount : maxLike).likeInterval(this.getLikeInterval())
                .build();
        log.debug("The auto like config: {}", autoLikeConfig);

        log.debug("END");
        return autoLikeConfig;
    }

    private int getTargetGroupCode() {
        return RandomUtils.generate(this.getGroupCount() - 1);
    }

    private int getMaxLike() {
        return Integer.parseInt(super.getVariable(VariableName.LIKE_PER_HOUR_PER_TASK).getValue());
    }

    private int getLikeInterval() {
        return Integer.parseInt(super.getVariable(VariableName.LIKE_INTERVAL).getValue());
    }

    private int getGroupCount() {
        return Integer.parseInt(super.getVariable(VariableName.HASHTAG_GROUP_COUNT).getValue());
    }

    private int getSkippedCount() {
        return Integer.parseInt(super.getVariable(VariableName.AUTO_LIKE_SKIPPED_COUNT).getValue());
    }
}
