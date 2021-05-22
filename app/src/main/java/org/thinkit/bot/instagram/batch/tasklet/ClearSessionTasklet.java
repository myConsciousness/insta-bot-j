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
import org.thinkit.bot.instagram.batch.data.mongo.entity.UserAccount;
import org.thinkit.bot.instagram.batch.data.mongo.repository.UserAccountRepository;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ClearSessionTasklet extends AbstractTasklet {

    private ClearSessionTasklet() {
        super(TaskType.CLEAR_SESSION);
    }

    public static Tasklet newInstance() {
        return new ClearSessionTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        if (super.hasRunningUser()) {
            final UserAccountRepository userAccountRepository = super.getMongoCollections().getUserAccountRepository();
            final UserAccount userAccount = userAccountRepository.findByUserName(super.getRunningUserName());

            if (userAccount != null) {
                userAccount.setLoggedIn(false);
                userAccount.setUpdatedAt(new Date());

                userAccountRepository.save(userAccount);
                log.debug("Updated user account: {}", userAccount);
            } else {
                log.warn(
                        "Could not find the user account information for the user name '{}' who is the charge of the running session. The application session will be closed successfully, but please make sure that the user account information has not been illegally deleted.",
                        super.getRunningUserName());
            }
        } else {
            log.warn(
                    "The session for the startup user does not exist. Ignore this message if this is the first time you are processing a task after starting the batch process. The session will be created before login.");
        }

        super.closeRunningSession();
        log.info("The running session was cleared successfully");

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(1);
        batchTaskResultBuilder.resultCount(1);

        log.debug("END");
        return batchTaskResultBuilder.build();
    }
}
