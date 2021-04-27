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

import com.mongodb.lang.NonNull;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.MessageMetaStatus;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.mongo.MongoCollection;
import org.thinkit.bot.instagram.mongo.entity.LastAction;
import org.thinkit.bot.instagram.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.mongo.repository.LastActionRepository;
import org.thinkit.bot.instagram.mongo.repository.MessageMetaRepository;
import org.thinkit.common.base.precondition.Preconditions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public abstract class AbstractTasklet implements Tasklet {

    /**
     * The task type
     */
    private TaskType taskType;

    /**
     * The mongo collection
     */
    @Getter(AccessLevel.PROTECTED)
    private MongoCollection mongoCollection;

    protected abstract BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.debug("START");

        final LastActionRepository lastActionRepository = this.mongoCollection.getLastActionRepository();

        this.updateStartAction(lastActionRepository);
        final BatchTaskResult batchTaskResult = this.executeTask(contribution, chunkContext);
        this.updateEndAction(lastActionRepository);

        log.debug("END");
        return batchTaskResult.getRepeatStatus();
    }

    protected void createMessageMeta(final int countAttempt) {
        log.debug("START");
        this.createMessageMeta(countAttempt, MessageMetaStatus.COMPLETED);
        log.debug("END");
    }

    protected void createMessageMeta(final int countAttempt, @NonNull final MessageMetaStatus messageMetaStatus) {
        log.debug("START");
        Preconditions.requirePositive(countAttempt);

        final MessageMetaRepository messageMetaRepository = this.mongoCollection.getMessageMetaRepository();
        MessageMeta messageMeta = messageMetaRepository.findByTaskTypeCode(this.taskType.getCode());

        if (messageMeta == null) {
            messageMeta = new MessageMeta();
        }

        messageMeta.setTaskTypeCode(this.taskType.getCode());
        messageMeta.setCountAttempt(countAttempt);
        messageMeta.setInterrupted(messageMetaStatus == MessageMetaStatus.INTERRUPTED);
        messageMeta.setUpdatedAt(new Date());

        messageMetaRepository.save(messageMeta);
        log.debug("Updated message meta: {}", messageMeta);

        log.debug("END");
    }

    private void updateStartAction(@NonNull final LastActionRepository lastActionRepository) {
        log.debug("START");

        LastAction lastAction = lastActionRepository.findByTaskTypeCode(this.taskType.getCode());

        if (lastAction == null) {
            lastAction = new LastAction();
            lastAction.setTaskTypeCode(this.taskType.getCode());
        }

        lastAction.setStart(new Date());
        lastAction.setEnd(null);
        lastAction.setUpdatedAt(new Date());

        lastActionRepository.save(lastAction);
        log.debug("Updated last action: {}", lastAction);

        log.debug("END");
    }

    private void updateEndAction(@NonNull final LastActionRepository lastActionRepository) {
        log.debug("START");

        LastAction lastAction = lastActionRepository.findByTaskTypeCode(this.taskType.getCode());

        lastAction.setEnd(new Date());
        lastAction.setUpdatedAt(new Date());

        lastActionRepository.save(lastAction);
        log.debug("Updated last action: {}", lastAction);

        log.debug("END");
    }
}
