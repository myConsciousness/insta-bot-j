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

import java.util.Date;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.data.mongo.entity.FollowedUser;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowedUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.MissingUserRepository;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.util.DateUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ExecuteAutoRefreshRetryUserTasklet extends AbstractTasklet {

    private ExecuteAutoRefreshRetryUserTasklet() {
        super(TaskType.AUTO_REFRESH_RETRY_USER);
    }

    public static Tasklet newInstance() {
        return new ExecuteAutoRefreshRetryUserTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final MongoCollections mongoCollections = super.getMongoCollections();
        final FollowedUserRepository followedUserRepository = mongoCollections.getFollowedUserRepository();
        final MissingUserRepository missingUserRepository = mongoCollections.getMissingUserRepository();

        missingUserRepository.findAll().forEach(missingUser -> {
            FollowedUser followedUser = new FollowedUser();
            followedUser.setUserName(missingUser.getUserName());
            followedUser.setChargeUserName(super.getRunningUserName());
            followedUser.setExpiredDate(DateUtils.toString(new Date()));

            followedUser = followedUserRepository.insert(followedUser);
            log.debug("Inserted retry followed user: {}", followedUser);

            // Delete retry user from missing user repository
            missingUserRepository.delete(missingUser);
        });

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(1);
        batchTaskResultBuilder.resultCount(1);

        log.debug("END");
        return batchTaskResultBuilder.build();
    }
}
