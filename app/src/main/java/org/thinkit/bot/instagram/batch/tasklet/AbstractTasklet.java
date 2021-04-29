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
import java.util.List;

import com.mongodb.lang.NonNull;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.batch.Task;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.VariableName;
import org.thinkit.bot.instagram.content.DefaultVariableMapper;
import org.thinkit.bot.instagram.mongo.MongoCollection;
import org.thinkit.bot.instagram.mongo.entity.ActionRecord;
import org.thinkit.bot.instagram.mongo.entity.ActionRestriction;
import org.thinkit.bot.instagram.mongo.entity.Error;
import org.thinkit.bot.instagram.mongo.entity.LastAction;
import org.thinkit.bot.instagram.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.mongo.entity.Variable;
import org.thinkit.bot.instagram.mongo.repository.ActionRestrictionRepository;
import org.thinkit.bot.instagram.mongo.repository.ErrorRepository;
import org.thinkit.bot.instagram.mongo.repository.LastActionRepository;
import org.thinkit.bot.instagram.mongo.repository.MessageMetaRepository;
import org.thinkit.bot.instagram.mongo.repository.VariableRepository;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.common.base.precondition.Preconditions;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public abstract class AbstractTasklet implements Tasklet {

    /**
     * The task
     */
    private final Task task;

    /**
     * The insta bot
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private InstaBot instaBot;

    /**
     * The mongo collection
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private MongoCollection mongoCollection;

    protected AbstractTasklet(@NonNull final TaskType taskType) {
        this.task = Task.from(taskType);
    }

    protected abstract BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.debug("START");

        final List<ActionRestriction> actionRestrictions = this.mongoCollection.getActionRestrictionRepository()
                .findAll();

        if (actionRestrictions != null && this.task.isRestrictable()) {
            log.debug("END");
            return this.executeRestrictedProcess();
        }

        log.debug("END");
        return this.executeTaskProcess(contribution, chunkContext);
    }

    protected String getVariableValue(@NonNull final VariableName variableName) {
        log.debug("START");

        final VariableRepository variableRepository = this.mongoCollection.getVariableRepository();
        Variable variable = variableRepository.findByName(variableName.getTag());

        if (variable == null) {
            variable = new Variable();
            variable.setName(variableName.getTag());
            variable.setValue(this.getDefaultVariableValue(variableName));

            variableRepository.insert(variable);
            log.debug("Inserted variable: {}", variable);
        }

        log.debug("END");
        return variable.getValue();
    }

    private RepeatStatus executeTaskProcess(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        this.updateStartAction();

        final BatchTaskResult batchTaskResult = this.executeTask(contribution, chunkContext);
        final int countAttempt = batchTaskResult.getCountAttempt();
        final ActionStatus actionStatus = batchTaskResult.getActionStatus();
        final List<ActionError> actionErrors = batchTaskResult.getActionErrors();

        this.saveActionRecord(countAttempt, actionStatus);

        if (!actionErrors.isEmpty()) {
            this.saveActionError(actionErrors);
        }

        if (actionStatus == ActionStatus.INTERRUPTED) {
            this.saveActionRestriction();
        }

        if (this.task.canSendResultMessage()) {
            this.saveMessageMeta(countAttempt, actionStatus);
        }

        this.updateEndAction();

        log.debug("END");
        return batchTaskResult.getRepeatStatus();
    }

    private RepeatStatus executeRestrictedProcess() {
        log.debug("START");

        this.updateStartAction();

        if (this.task.canSendResultMessage()) {
            this.saveMessageMeta(0, ActionStatus.SKIPPED);
        }

        this.updateEndAction();

        log.debug("END");
        return RepeatStatus.FINISHED;
    }

    private void saveActionRecord(final int countAttempt, @NonNull final ActionStatus actionStatus) {
        log.debug("START");

        final ActionRecord actionRecord = new ActionRecord();
        actionRecord.setTaskTypeCode(this.task.getTypeCode());
        actionRecord.setCountAttempt(countAttempt);
        actionRecord.setActionStatusCode(actionStatus.getCode());

        this.mongoCollection.getActionRecordRepository().insert(actionRecord);
        log.debug("Inserted action record: {}", actionRecord);

        log.debug("END");
    }

    private void saveActionError(@NonNull final List<ActionError> actionErrors) {
        log.debug("START");

        final ErrorRepository errorRepository = this.mongoCollection.getErrorRepository();

        for (final ActionError actionError : actionErrors) {
            final Error error = new Error();
            error.setTaskTypeCode(actionError.getTaskType().getCode());
            error.setMessage(actionError.getMessage());
            error.setLocalizedMessage(actionError.getLocalizedMessage());
            error.setStackTrace(actionError.getStackTrace());

            final Error insertedError = errorRepository.insert(error);
            log.debug("Inserted error: {}", insertedError);
        }

        log.debug("END");
    }

    private void saveActionRestriction() {
        log.debug("START");

        final ActionRestrictionRepository actionRestrictionRepository = this.mongoCollection
                .getActionRestrictionRepository();

        final ActionRestriction actionRestriction = new ActionRestriction();
        actionRestriction.setTaskTypeCode(this.task.getTypeCode());

        actionRestrictionRepository.insert(actionRestriction);
        log.debug("Inserted action restriction: {}", actionRestriction);

        log.debug("END");
    }

    private void saveMessageMeta(final int countAttempt, @NonNull final ActionStatus actionStatus) {
        log.debug("START");
        Preconditions.requirePositive(countAttempt);

        final MessageMetaRepository messageMetaRepository = this.mongoCollection.getMessageMetaRepository();
        MessageMeta messageMeta = messageMetaRepository.findByTaskTypeCode(this.task.getTypeCode());

        if (messageMeta == null) {
            messageMeta = new MessageMeta();
        }

        messageMeta.setTaskTypeCode(this.task.getTypeCode());
        messageMeta.setCountAttempt(countAttempt);
        messageMeta.setInterrupted(actionStatus == ActionStatus.INTERRUPTED);
        messageMeta.setUpdatedAt(new Date());

        messageMetaRepository.save(messageMeta);
        log.debug("Updated message meta: {}", messageMeta);

        log.debug("END");
    }

    private void updateStartAction() {
        log.debug("START");

        final LastActionRepository lastActionRepository = this.mongoCollection.getLastActionRepository();
        LastAction lastAction = lastActionRepository.findByTaskTypeCode(this.task.getTypeCode());

        if (lastAction == null) {
            lastAction = new LastAction();
            lastAction.setTaskTypeCode(this.task.getTypeCode());
        }

        lastAction.setStart(new Date());
        lastAction.setEnd(null);
        lastAction.setUpdatedAt(new Date());

        lastActionRepository.save(lastAction);
        log.debug("Updated last action: {}", lastAction);

        log.debug("END");
    }

    private void updateEndAction() {
        log.debug("START");

        final LastActionRepository lastActionRepository = this.mongoCollection.getLastActionRepository();
        final LastAction lastAction = lastActionRepository.findByTaskTypeCode(this.task.getTypeCode());

        lastAction.setEnd(new Date());
        lastAction.setUpdatedAt(new Date());

        lastActionRepository.save(lastAction);
        log.debug("Updated last action: {}", lastAction);

        log.debug("END");
    }

    private String getDefaultVariableValue(@NonNull final VariableName variableName) {
        final DefaultVariableMapper defaultVariableMapper = DefaultVariableMapper.newInstance();
        defaultVariableMapper.setVariableName(variableName.getTag());
        return defaultVariableMapper.scan().get(0).getValue();
    }
}
