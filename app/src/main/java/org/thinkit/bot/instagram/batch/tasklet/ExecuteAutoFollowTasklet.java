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
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.DateFormat;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.VariableName;
import org.thinkit.bot.instagram.config.AutoFollowConfig;
import org.thinkit.bot.instagram.mongo.entity.FollowBackExpectableUser;
import org.thinkit.bot.instagram.mongo.entity.FollowedUser;
import org.thinkit.bot.instagram.mongo.repository.FollowBackExpectableUserRepository;
import org.thinkit.bot.instagram.mongo.repository.FollowedUserRepository;
import org.thinkit.bot.instagram.param.FollowUser;
import org.thinkit.bot.instagram.result.ActionFollowFailedUser;
import org.thinkit.bot.instagram.result.ActionFollowedUser;
import org.thinkit.bot.instagram.result.AutoFollowResult;
import org.thinkit.bot.instagram.util.DateUtils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ExecuteAutoFollowTasklet extends AbstractTasklet {

    /**
     * The insta bot
     */
    @Autowired
    private InstaBot instaBot;

    private ExecuteAutoFollowTasklet() {
        super(TaskType.AUTO_FOLLOW);
    }

    public static Tasklet newInstance() {
        return new ExecuteAutoFollowTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        if (this.isAlreadyAttemptedToday()) {
            return BatchTaskResult.builder().actionStatus(ActionStatus.SKIP).build();
        }

        final AutoFollowResult autoFollowResult = this.instaBot.executeAutoFollow(this.getFollowUsers(),
                this.getAutoFollowConfig());
        log.info("The auto follow has completed the process successfully.");

        final List<ActionFollowedUser> actionFollowedUsers = autoFollowResult.getActionFollowedUsers();
        final List<ActionFollowFailedUser> actionFollowFailedUsers = autoFollowResult.getActionFollowFailedUsers();

        final FollowBackExpectableUserRepository followBackExpectableUserRepository = super.getMongoCollections()
                .getFollowBackExpectableUserRepository();
        final FollowedUserRepository followedUserRepository = super.getMongoCollections().getFollowedUserRepository();

        for (final ActionFollowedUser actionFollowedUser : actionFollowedUsers) {
            FollowedUser followedUser = new FollowedUser();
            followedUser.setUserName(actionFollowedUser.getUserName());
            followedUser.setUrl(actionFollowedUser.getUrl());
            followedUser.setMutual(false);

            followedUser = followedUserRepository.insert(followedUser);
            log.debug("Inserted followed user: {}", followedUser);

            // Delete followed user from expectable user repository
            followBackExpectableUserRepository.deleteByUserName(actionFollowedUser.getUserName());
        }

        for (final ActionFollowFailedUser actionFollowFailedUser : actionFollowFailedUsers) {
            // Delete follow failed user from expectable user repository
            followBackExpectableUserRepository.deleteByUserName(actionFollowFailedUser.getUserName());
        }

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(actionFollowedUsers.size() + actionFollowFailedUsers.size());
        batchTaskResultBuilder.resultCount(actionFollowedUsers.size());
        batchTaskResultBuilder.actionErrors(autoFollowResult.getActionErrors());

        log.debug("END");
        return batchTaskResultBuilder.build();
    }

    private List<FollowUser> getFollowUsers() {
        log.debug("START");

        final int followPerDay = this.getFollowPerDay();
        final List<FollowUser> followUsers = new ArrayList<>();

        final FollowBackExpectableUserRepository followBackExpectableUserRepository = super.getMongoCollections()
                .getFollowBackExpectableUserRepository();
        final List<FollowBackExpectableUser> followBackExpectableUsers = followBackExpectableUserRepository
                .findOrderByFollowBackPossibilityCodeAsc();

        for (final FollowBackExpectableUser followBackExpectableUser : followBackExpectableUsers) {
            if (!this.isDuplicateUser(followUsers, followBackExpectableUser)) {
                followUsers.add(FollowUser.from(followBackExpectableUser.getUserName()));
            }

            if (followUsers.size() >= followPerDay) {
                break;
            }
        }

        log.debug("END");
        return followUsers;
    }

    private boolean isDuplicateUser(@NonNull final List<FollowUser> followUsers,
            @NonNull final FollowBackExpectableUser followBackExpectableUser) {
        log.debug("START");

        if (this.isAlreadyForecastedUser(followUsers, followBackExpectableUser)) {
            log.debug("The user {} is already followed.", followBackExpectableUser.getUserName());
            log.debug("END");
            return true;
        }

        for (final FollowUser followUser : followUsers) {
            if (followUser.getUserName().equals(followBackExpectableUser.getUserName())) {
                log.debug("The duplicate user has detected.");
                log.debug("END");
                return true;
            }
        }

        log.debug("The duplicate user has not detected.");
        log.debug("END");
        return false;
    }

    private boolean isAlreadyForecastedUser(@NonNull final List<FollowUser> followUsers,
            @NonNull final FollowBackExpectableUser followBackExpectableUser) {
        final FollowedUserRepository followedUserRepository = super.getMongoCollections().getFollowedUserRepository();
        return followedUserRepository.findByUserName(followBackExpectableUser.getUserName()) != null;
    }

    private AutoFollowConfig getAutoFollowConfig() {
        log.debug("START");

        final AutoFollowConfig.AutoFollowConfigBuilder autoFollowConfigBuilder = AutoFollowConfig.builder();
        autoFollowConfigBuilder.followInterval(this.getFollowInterval());

        log.debug("END");
        return autoFollowConfigBuilder.build();
    }

    private boolean isAlreadyAttemptedToday() {
        log.debug("START");

        final String lastDateAttemptedAutoFollow = this.getLastDateAttemptedAutoFollow();
        final String today = DateUtils.toString(new Date(), DateFormat.YYYY_MM_DD);

        log.debug("START");
        return lastDateAttemptedAutoFollow.equals(today);
    }

    private String getLastDateAttemptedAutoFollow() {
        return super.getVariableValue(VariableName.LAST_DATE_ATTEMPTED_AUTO_FOLLOW);
    }

    private int getFollowPerDay() {
        return super.getIntVariableValue(VariableName.FOLLOW_PER_DAY);
    }

    private int getFollowInterval() {
        return super.getIntVariableValue(VariableName.FOLLOW_INTERVAL);
    }
}
