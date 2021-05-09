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
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.mongo.entity.Session;
import org.thinkit.bot.instagram.mongo.repository.SessionRepository;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class CloseSessionTasklet extends AbstractTasklet {

    private CloseSessionTasklet() {
        super(TaskType.CLOSE_SESSION);
    }

    public static Tasklet newInstance() {
        return new CloseSessionTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final SessionRepository sessionRepository = super.getMongoCollections().getSessionRepository();
        final Session session = sessionRepository.findByUserName(super.getChargeUserName());

        session.setRunning(false);
        session.setUpdatedAt(new Date());
        sessionRepository.save(session);
        log.debug("Updated session: {}", session);

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(1);
        batchTaskResultBuilder.resultCount(1);

        log.debug("END");
        return batchTaskResultBuilder.build();
    }
}
