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

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.data.mongo.entity.FollowedUser;
import org.thinkit.bot.instagram.batch.data.mongo.repository.MissingUserRepository;
import org.thinkit.bot.instagram.batch.exception.AvailableUserAccountNotFoundException;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class InitializeTasklet extends AbstractTasklet {

    private InitializeTasklet() {
        super(TaskType.INITIALIZE);
    }

    public static Tasklet newInstance() {
        return new InitializeTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        super.createSession();

        if (!super.hasRunningUser()) {
            throw new AvailableUserAccountNotFoundException("""
                    There are no available users to run the process.
                    All users are already running or no valid user information has been defined.""");
        }

        final MissingUserRepository missingUserRepository = super.getMongoCollections().getMissingUserRepository();

        missingUserRepository.findAll().forEach(missingUser -> {
            final FollowedUser followedUser = new FollowedUser();
            followedUser.setUserName(missingUser.getUserName());
            followedUser.setChargeUserName(super.getRunningUserName());
            followedUser.setExpiredDate("2021/05/11");

            super.getMongoCollections().getFollowedUserRepository().insert(followedUser);
            missingUserRepository.delete(missingUser);
        });

        log.debug("END");
        return BatchTaskResult.builder().actionCount(1).build();
    }
}
