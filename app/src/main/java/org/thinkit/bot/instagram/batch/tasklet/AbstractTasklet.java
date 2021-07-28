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
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.thinkit.api.catalog.BiCatalog;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.batch.catalog.BatchScheduleType;
import org.thinkit.bot.instagram.batch.catalog.VariableName;
import org.thinkit.bot.instagram.batch.data.content.mapper.DefaultTaskExecutionRuleMapper;
import org.thinkit.bot.instagram.batch.data.content.mapper.DefaultVariableMapper;
import org.thinkit.bot.instagram.batch.data.content.mapper.ExecutionControlledVariableMapper;
import org.thinkit.bot.instagram.batch.data.mongo.entity.ActionRecord;
import org.thinkit.bot.instagram.batch.data.mongo.entity.ActionRestriction;
import org.thinkit.bot.instagram.batch.data.mongo.entity.ActionSkip;
import org.thinkit.bot.instagram.batch.data.mongo.entity.DailyActionTotal;
import org.thinkit.bot.instagram.batch.data.mongo.entity.Error;
import org.thinkit.bot.instagram.batch.data.mongo.entity.LastAction;
import org.thinkit.bot.instagram.batch.data.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.batch.data.mongo.entity.TaskExecutionControl;
import org.thinkit.bot.instagram.batch.data.mongo.entity.Variable;
import org.thinkit.bot.instagram.batch.data.mongo.repository.ActionRestrictionRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.ActionSkipRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.DailyActionTotalRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.ErrorRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.LastActionRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.TaskExecutionControlRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.VariableRepository;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.policy.BatchTask;
import org.thinkit.bot.instagram.batch.policy.RunningUser;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.result.ActionError;
import org.thinkit.bot.instagram.util.DateUtils;
import org.thinkit.bot.instagram.util.PresentDateSet;
import org.thinkit.bot.instagram.util.RandomUtils;
import org.thinkit.common.base.precondition.Preconditions;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kato Shinya
 * @since 1.0.0
 */
@Slf4j
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Component
public abstract class AbstractTasklet implements Tasklet {

    /**
     * The batch task
     */
    private final BatchTask batchTask;

    /**
     * The configurable application context
     */
    @Autowired
    private ConfigurableApplicationContext context;

    /**
     * The insta bot
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private InstaBot instaBot;

    /**
     * The running user
     */
    @Autowired
    private RunningUser runningUser;

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
        this.batchTask = BatchTask.from(taskType);
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

        if (!this.isTaskActivated()) {
            log.debug("END");
            return RepeatStatus.FINISHED;
        }

        this.updateProcessingBatchSchedule();

        if (this.batchTask.isRestrictable() && this.isActionRestricted()) {
            log.debug("END");
            return this.executeRestrictedProcess(contribution, chunkContext);
        }

        if (this.batchTask.canSkip() && this.isSkipMood()) {
            log.debug("END");
            return this.executeSkipMoodProcess();
        }

        log.debug("END");
        return this.executeTaskProcess(contribution, chunkContext);
    }

    protected String getVariableValue(@NonNull final VariableName variableName) {
        return this.getVariable(variableName).getValue();
    }

    protected int getIntVariableValue(@NonNull final VariableName variableName) {
        return Integer.parseInt(this.getVariable(variableName).getValue());
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

        log.debug("The variable: {}", variable);
        log.debug("END");
        return variable;
    }

    protected void saveVariable(@NonNull final VariableName variableName, @NonNull final Object value) {
        log.debug("START");

        final Variable variable = this.getVariable(variableName);
        variable.setValue(String.valueOf(value));
        variable.setUpdatedAt(new Date());

        this.mongoCollections.getVariableRepository().save(variable);
        log.debug("Updated variable: {}", variable);

        log.debug("END");
    }

    protected void createSession() {
        this.runningUser.createSession();
    }

    protected boolean hasRunningUser() {
        return this.runningUser != null && this.runningUser.isAvailable();
    }

    protected String getRunningUserName() {
        return this.runningUser.getUserName();
    }

    protected String getRunningUserPassword() {
        return this.runningUser.getPassword();
    }

    protected void closeRunningSession() {
        this.runningUser.closeSession();
    }

    private RepeatStatus executeTaskProcess(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        this.updateStartAction();

        final BatchTaskResult batchTaskResult = this.executeTask(contribution, chunkContext);
        log.debug("The batch task result: {}", batchTaskResult);

        final int actionCount = batchTaskResult.getActionCount();
        final ActionStatus actionStatus = batchTaskResult.getActionStatus();
        final List<ActionError> actionErrors = batchTaskResult.getActionErrors();

        if (this.batchTask.isActionTotalable()) {
            this.saveActionTotal(actionCount);
        }

        if (this.batchTask.isExecutionControlled()) {
            this.saveExecutionControlledVariable();
        }

        this.saveActionRecord(actionCount, actionStatus);

        if (!actionErrors.isEmpty()) {
            this.saveActionError(actionErrors);
        }

        if (actionStatus == ActionStatus.INTERRUPTED) {
            this.saveActionRestriction();
        }

        if (this.batchTask.canSendResultMessage()) {
            this.saveMessageMeta(batchTaskResult.getResultCount(), actionStatus);
        }

        if (this.batchTask.canSkip()) {
            this.resetSkippedCount();
        }

        this.updateEndAction();

        if (this.batchTask.isClosable()) {
            log.debug("END");
            this.closeBatchSession();
        }

        log.debug("END");
        return batchTaskResult.getRepeatStatus();
    }

    private RepeatStatus executeRestrictedProcess(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final ActionRestrictionRepository actionRestrictionRepository = this.mongoCollections
                .getActionRestrictionRepository();
        final ActionRestriction actionRestriction = actionRestrictionRepository.findAll().get(0);

        if (DateUtils.isHourElapsed(actionRestriction.getCreatedAt(), this.getActionRestrictionWaitHour())) {
            actionRestrictionRepository.deleteAll();
            log.info("The waiting time for the action restriction has passed.");

            // Perform task processing when user-defined time limit has elapsed.
            return this.executeTaskProcess(contribution, chunkContext);
        }

        this.updateStartAction();

        if (this.batchTask.canSendResultMessage()) {
            this.saveMessageMeta(0, ActionStatus.SKIP);
        }

        this.updateEndAction();

        log.debug("END");
        return RepeatStatus.FINISHED;
    }

    private RepeatStatus executeSkipMoodProcess() {
        log.debug("START");

        this.updateStartAction();

        if (this.batchTask.canSendResultMessage()) {
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
        log.debug("STRAT");

        final ActionSkipRepository actionSkipRepository = this.mongoCollections.getActionSkipRepository();
        ActionSkip actionSkip = actionSkipRepository.findByTaskTypeCode(this.batchTask.getTypeCode());

        if (actionSkip == null) {
            actionSkip = new ActionSkip();
            actionSkip.setTaskTypeCode(TaskType.AUTO_LIKE.getCode());
            actionSkip.setCount(0);

            actionSkip = actionSkipRepository.insert(actionSkip);
            log.debug("Inserted action skip: {}", actionSkip);
        } else {
            if (actionSkip.getCount() > 1) {
                // Prevent too much skipping and too many attemps per execution
                return false;
            }
        }

        final int occurrenceProbability = this.getSkipMoodOccurrenceProbability();

        log.debug("END");
        return RandomUtils.nextInt(occurrenceProbability + 1, 1) % occurrenceProbability == 0;
    }

    private void incrementSkippedCount() {
        log.debug("START");

        final ActionSkipRepository actionSkipRepository = this.mongoCollections.getActionSkipRepository();
        final ActionSkip actionSkip = actionSkipRepository.findByTaskTypeCode(this.batchTask.getTypeCode());
        actionSkip.setCount(actionSkip.getCount() + 1);
        actionSkip.setUpdatedAt(new Date());

        actionSkipRepository.save(actionSkip);
        log.debug("Updated action skip: {}", actionSkip);

        log.debug("END");
    }

    private void resetSkippedCount() {
        log.debug("START");

        final ActionSkipRepository actionSkipRepository = this.mongoCollections.getActionSkipRepository();
        final ActionSkip actionSkip = actionSkipRepository.findByTaskTypeCode(this.batchTask.getTypeCode());
        actionSkip.setCount(0);
        actionSkip.setUpdatedAt(new Date());

        actionSkipRepository.save(actionSkip);
        log.debug("Updated action skip: {}", actionSkip);

        log.debug("END");
    }

    private void saveActionTotal(final int actionCount) {
        log.debug("START");

        final PresentDateSet presentDateSet = PresentDateSet.newInstance();

        final DailyActionTotalRepository dailyActionTotalRepository = this.mongoCollections
                .getDailyActionTotalRepository();
        DailyActionTotal dailyActionTotal = dailyActionTotalRepository.findByTaskTypeCodeAndYearAndMonthAndDay(
                this.batchTask.getTypeCode(), presentDateSet.getYear(), presentDateSet.getMonth(),
                presentDateSet.getDay());

        if (dailyActionTotal == null) {
            dailyActionTotal = new DailyActionTotal();
            dailyActionTotal.setTaskTypeCode(this.batchTask.getTypeCode());
            dailyActionTotal.setYear(presentDateSet.getYear());
            dailyActionTotal.setMonth(presentDateSet.getMonth());
            dailyActionTotal.setDay(presentDateSet.getDay());

            dailyActionTotal = dailyActionTotalRepository.insert(dailyActionTotal);
            log.debug("Inserted daily action total: {}", dailyActionTotal);
        }

        dailyActionTotal.setTotal(dailyActionTotal.getTotal() + actionCount);
        dailyActionTotal.setUpdatedAt(new Date());

        dailyActionTotalRepository.save(dailyActionTotal);
        log.debug("Updated daily action total: {}", dailyActionTotal);

        log.debug("END");
    }

    private void saveExecutionControlledVariable() {
        log.debug("START");

        final VariableName variableName = BiCatalog.getEnum(VariableName.class, this.getExecutionControlledVariable());
        this.saveVariable(variableName, DateUtils.toString(new Date()));

        log.debug("END");
    }

    private void saveActionRecord(final int actionCount, @NonNull final ActionStatus actionStatus) {
        log.debug("START");

        final ActionRecord actionRecord = new ActionRecord();
        actionRecord.setTaskTypeCode(this.batchTask.getTypeCode());
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
        actionRestriction.setTaskTypeCode(this.batchTask.getTypeCode());

        actionRestrictionRepository.insert(actionRestriction);
        log.debug("Inserted action restriction: {}", actionRestriction);

        log.debug("END");
    }

    private void saveMessageMeta(final int resultCount, @NonNull final ActionStatus actionStatus) {
        log.debug("START");
        Preconditions.requirePositive(resultCount);

        MessageMeta messageMeta = new MessageMeta();
        messageMeta.setChargeUserName(this.getRunningUserName());
        messageMeta.setTaskTypeCode(this.batchTask.getTypeCode());
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
        LastAction lastAction = lastActionRepository.findByTaskTypeCode(this.batchTask.getTypeCode());

        if (lastAction == null) {
            lastAction = new LastAction();
            lastAction.setTaskTypeCode(this.batchTask.getTypeCode());
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
        final LastAction lastAction = lastActionRepository.findByTaskTypeCode(this.batchTask.getTypeCode());

        lastAction.setEnd(new Date());
        lastAction.setUpdatedAt(new Date());

        lastActionRepository.save(lastAction);
        log.debug("Updated last action: {}", lastAction);

        log.debug("END");
    }

    private void updateProcessingBatchSchedule() {

        if (this.batchTask.isCommonSchedule()) {
            // The common schedule is excluded from the update peocessing batch schedule.
            return;
        }

        this.saveVariable(VariableName.PROCESSING_BATCH_SCHEDULE, this.getProcessingBatchSchedule().getCode());
    }

    private BatchScheduleType getProcessingBatchSchedule() {

        if (this.batchTask.isStartSessionTask()) {
            return BatchScheduleType.START_SESSION;
        } else if (this.batchTask.isMainStreamTask()) {
            return BatchScheduleType.MAIN_STREAM;
        }

        return BatchScheduleType.CLOSE_SESSION;
    }

    private boolean isTaskActivated() {

        if (this.batchTask.isStartSession()) {
            // Before the initialization process, the session of the running user has not
            // been created yet, so the process of judging the effectiveness of the task
            // cannot be performed. The initialization process must be performed.
            return true;
        }

        final TaskExecutionControlRepository taskExecutionControlRepository = this.mongoCollections
                .getTaskExecutionControlRepository();
        TaskExecutionControl taskExecutionControl = taskExecutionControlRepository
                .findByChargeUserNameAndTaskTypeCode(this.getRunningUserName(), this.batchTask.getTypeCode());

        if (taskExecutionControl == null) {
            taskExecutionControl = new TaskExecutionControl();
            taskExecutionControl.setTaskTypeCode(this.batchTask.getTypeCode());
            taskExecutionControl.setChargeUserName(this.getRunningUserName());
            taskExecutionControl.setActive(this.getDefaultTaskExecutionRule());

            taskExecutionControl = taskExecutionControlRepository.insert(taskExecutionControl);
            log.debug("Inserted task execution control: {}", taskExecutionControl);
        }

        return taskExecutionControl.isActive();
    }

    private void closeBatchSession() {
        log.info("Close web browser and application.");
        this.instaBot.closeWebBrowser();
        final int exitCode = SpringApplication.exit(this.context, () -> 0);
        System.exit(exitCode);
    }

    private String getDefaultVariableValue(@NonNull final VariableName variableName) {
        final DefaultVariableMapper defaultVariableMapper = DefaultVariableMapper.from(variableName.getTag());
        return defaultVariableMapper.scan().get(0).getValue();
    }

    private int getActionRestrictionWaitHour() {
        return this.getIntVariableValue(VariableName.ACTION_RESTRICTION_WAIT_HOUR);
    }

    private int getSkipMoodOccurrenceProbability() {
        return this.getIntVariableValue(VariableName.SKIP_MOOD_OCCURRENCE_PROBABILITY);
    }

    private int getExecutionControlledVariable() {
        return ExecutionControlledVariableMapper.from(this.batchTask.getTypeCode()).scan().get(0).getVariableCode();
    }

    private boolean getDefaultTaskExecutionRule() {
        return DefaultTaskExecutionRuleMapper.from(this.batchTask.getTypeCode()).scan().get(0).isActive();
    }
}
