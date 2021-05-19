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
import org.thinkit.bot.instagram.param.ActionUser;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ExecuteAutoLoginTasklet extends AbstractTasklet {

    private ExecuteAutoLoginTasklet() {
        super(TaskType.AUTO_LOGIN);
    }

    public static Tasklet newInstance() {
        return new ExecuteAutoLoginTasklet();
    }

    @Override
    public BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final String userName = super.getRunningUserName();

        super.getInstaBot().executeLogin(ActionUser.from(userName, super.getRunningUserPassword()));
        log.info("The login to Instagram has been successfully completed.");

        final UserAccountRepository userAccountRepository = super.getMongoCollections().getUserAccountRepository();
        final UserAccount userAccount = userAccountRepository.findByUserName(userName);

        userAccount.setLoggedIn(true);
        userAccount.setUpdatedAt(new Date());

        userAccountRepository.save(userAccount);
        log.debug("Updated user account: {}", userAccount);

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(1);
        batchTaskResultBuilder.resultCount(1);

        log.debug("END");
        return batchTaskResultBuilder.build();
    }
}
