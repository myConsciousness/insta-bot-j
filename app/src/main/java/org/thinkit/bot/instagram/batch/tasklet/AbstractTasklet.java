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
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.policy.Task;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.VariableName;
import org.thinkit.bot.instagram.content.DefaultVariableMapper;
import org.thinkit.bot.instagram.mongo.entity.ActionRecord;
import org.thinkit.bot.instagram.mongo.entity.ActionRestriction;
import org.thinkit.bot.instagram.mongo.entity.ActionSkip;
import org.thinkit.bot.instagram.mongo.entity.Error;
import org.thinkit.bot.instagram.mongo.entity.LastAction;
import org.thinkit.bot.instagram.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.mongo.entity.Variable;
import org.thinkit.bot.instagram.mongo.repository.ActionRestrictionRepository;
import org.thinkit.bot.instagram.mongo.repository.ActionSkipRepository;
import org.thinkit.bot.instagram.mongo.repository.ErrorRepository;
import org.thinkit.bot.instagram.mongo.repository.LastActionRepository;
import org.thinkit.bot.instagram.mongo.repository.VariableRepository;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.util.DateUtils;
import org.thinkit.bot.instagram.util.RandomUtils;
import org.thinkit.common.base.precondition.Preconditions;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
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
     * The mongo collections
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private MongoCollections mongoCollections;

    /**
     * The constructor.
     *
     * @param taskType The task type
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    protected AbstractTasklet(@NonNull final TaskType taskType) {
        this.task = Task.from(taskType);
    }

    /**
     * Given the current context in the form of a step contribution, do whatever is
     * necessary to process this unit inside a transaction.
     *
     * <p>
     * Implementations return {@link RepeatStatus#FINISHED} if finished. If not they
     * return {@link RepeatStatus#CONTINUABLE}. On failure throws an exception.
     *
     * @param contribution The mutable state to be passed back to update the current
     *                     step execution
     * @param chunkContext The attributes shared between invocations but not between
     *                     restarts
     * @return The batch task result
     */
    protected abstract BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.debug("START");

        if (this.task.isRestrictable() && this.isActionRestricted()) {
            log.debug("END");
            return this.executeRestrictedProcess();
        }

        if (this.task.canSkip() && this.isSkipMood()) {
            log.debug("END");
            return this.executeSkipMoodProcess();
        }

        log.debug("END");
        return this.executeTaskProcess(contribution, chunkContext);
    }

    /**
     * Returns the variable from {@code Variable} collection on MongoDB linked by
     * the {@code variableName} passed as an argument. If the corresponding variable
     * document does not exist in the {@code Variable} collection, it will be
     * generated with the default value.
     *
     * @param variableName The variable name
     * @return The variable linked by the {@code variableName} passed as an argument
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    protected Variable getVariable(@NonNull final VariableName variableName) {
        log.debug("START");

        final VariableRepository variableRepository = this.mongoCollections.getVariableRepository();
        Variable variable = variableRepository.findByName(variableName.getTag());

        if (variable == null) {
            variable = new Variable();
            variable.setName(variableName.getTag());
            variable.setValue(this.getDefaultVariableValue(variableName));

            variable = variableRepository.insert(variable);
            log.debug("Inserted variable: {}", variable);
        }

        log.debug("END");
        return variable;
    }

    private RepeatStatus executeTaskProcess(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        this.updateStartAction();

        final BatchTaskResult batchTaskResult = this.executeTask(contribution, chunkContext);
        log.debug("The batch task result: {}", batchTaskResult);

        final int actionCount = batchTaskResult.getActionCount();
        final ActionStatus actionStatus = batchTaskResult.getActionStatus();
        final List<ActionError> actionErrors = batchTaskResult.getActionErrors();

        this.saveActionRecord(actionCount, actionStatus);

        if (!actionErrors.isEmpty()) {
            this.saveActionError(actionErrors);
        }

        if (actionStatus == ActionStatus.INTERRUPTED) {
            this.saveActionRestriction();
        }

        if (this.task.canSendResultMessage()) {
            this.saveMessageMeta(batchTaskResult.getResultCount(), actionStatus);
        }

        if (this.task.canSkip()) {
            this.resetSkippedCount();
        }

        this.updateEndAction();

        log.debug("END");
        return batchTaskResult.getRepeatStatus();
    }

    private RepeatStatus executeRestrictedProcess() {
        log.debug("START");

        this.updateStartAction();

        final ActionRestrictionRepository actionRestrictionRepository = this.mongoCollections
                .getActionRestrictionRepository();
        final ActionRestriction actionRestriction = actionRestrictionRepository.findAll().get(0);

        final Date restrictedDate = actionRestriction.getCreatedAt();
        final int actionRestrictionWaitHour = this.getActionRestrictionWaitHour();

        if (DateUtils.isHourElapsed(restrictedDate, actionRestrictionWaitHour)) {
            actionRestrictionRepository.deleteAll();
            log.info("The waiting time for the action restriction has passed.");
        }

        if (this.task.canSendResultMessage()) {
            this.saveMessageMeta(0, ActionStatus.SKIP);
        }

        this.updateEndAction();

        log.debug("END");
        return RepeatStatus.FINISHED;
    }

    private RepeatStatus executeSkipMoodProcess() {
        log.debug("START");

        this.updateStartAction();

        if (this.task.canSendResultMessage()) {
            this.saveMessageMeta(0, ActionStatus.SKIP_MOOD);
        }

        this.incrementSkippedCount();
        this.updateEndAction();

        log.debug("END");
        return RepeatStatus.FINISHED;
    }

    private boolean isActionRestricted() {
        return !this.mongoCollections.getActionRestrictionRepository().findAll().isEmpty();
    }

    private boolean isSkipMood() {

        final ActionSkip actionSkip = this.mongoCollections.getActionSkipRepository()
                .findByTaskTypeCode(this.task.getTypeCode());

        if (actionSkip.getCount() > 1) {
            // Prevent too much skipping and too many attemps per execution
            return false;
        }

        // TODO: To variable
        return RandomUtils.nextInt(6, 1) % 5 == 0;
    }

    private void incrementSkippedCount() {
        log.debug("START");

        final ActionSkipRepository actionSkipRepository = this.mongoCollections.getActionSkipRepository();
        final ActionSkip actionSkip = actionSkipRepository.findByTaskTypeCode(this.task.getTypeCode());
        actionSkip.setCount(actionSkip.getCount() + 1);

        actionSkipRepository.save(actionSkip);
        log.debug("Updated action skip: {}", actionSkip);

        log.debug("END");
    }

    private void resetSkippedCount() {
        log.debug("START");

        final ActionSkipRepository actionSkipRepository = this.mongoCollections.getActionSkipRepository();
        final ActionSkip actionSkip = actionSkipRepository.findByTaskTypeCode(this.task.getTypeCode());
        actionSkip.setCount(0);

        actionSkipRepository.save(actionSkip);
        log.debug("Updated action skip: {}", actionSkip);

        log.debug("END");
    }

    private void saveActionRecord(final int actionCount, @NonNull final ActionStatus actionStatus) {
        log.debug("START");

        final ActionRecord actionRecord = new ActionRecord();
        actionRecord.setTaskTypeCode(this.task.getTypeCode());
        actionRecord.setCount(actionCount);
        actionRecord.setActionStatusCode(actionStatus.getCode());

        this.mongoCollections.getActionRecordRepository().insert(actionRecord);
        log.debug("Inserted action record: {}", actionRecord);

        log.debug("END");
    }

    private void saveActionError(@NonNull final List<ActionError> actionErrors) {
        log.debug("START");

        final ErrorRepository errorRepository = this.mongoCollections.getErrorRepository();

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

        final ActionRestrictionRepository actionRestrictionRepository = this.mongoCollections
                .getActionRestrictionRepository();

        final ActionRestriction actionRestriction = new ActionRestriction();
        actionRestriction.setTaskTypeCode(this.task.getTypeCode());

        actionRestrictionRepository.insert(actionRestriction);
        log.debug("Inserted action restriction: {}", actionRestriction);

        log.debug("END");
    }

    private void saveMessageMeta(final int resultCount, @NonNull final ActionStatus actionStatus) {
        log.debug("START");
        Preconditions.requirePositive(resultCount);

        MessageMeta messageMeta = new MessageMeta();
        messageMeta.setTaskTypeCode(this.task.getTypeCode());
        messageMeta.setCount(resultCount);

        if (actionStatus == ActionStatus.INTERRUPTED) {
            messageMeta.setInterrupted(true);
        } else if (actionStatus == ActionStatus.SKIP) {
            messageMeta.setSkipped(true);
        } else if (actionStatus == ActionStatus.SKIP_MOOD) {
            messageMeta.setSkippedByMood(true);
        }

        messageMeta.setAlreadySent(false);

        messageMeta = this.mongoCollections.getMessageMetaRepository().insert(messageMeta);
        log.debug("Inserted message meta: {}", messageMeta);

        log.debug("END");
    }

    private void updateStartAction() {
        log.debug("START");

        final LastActionRepository lastActionRepository = this.mongoCollections.getLastActionRepository();
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

        final LastActionRepository lastActionRepository = this.mongoCollections.getLastActionRepository();
        final LastAction lastAction = lastActionRepository.findByTaskTypeCode(this.task.getTypeCode());

        lastAction.setEnd(new Date());
        lastAction.setUpdatedAt(new Date());

        lastActionRepository.save(lastAction);
        log.debug("Updated last action: {}", lastAction);

        log.debug("END");
    }

    private String getDefaultVariableValue(@NonNull final VariableName variableName) {
        final DefaultVariableMapper defaultVariableMapper = DefaultVariableMapper.from(variableName.getTag());
        return defaultVariableMapper.scan().get(0).getValue();
    }

    private int getActionRestrictionWaitHour() {
        return Integer.parseInt(this.getVariable(VariableName.ACTION_RESTRICTION_WAIT_HOUR).getValue());
    }
}
