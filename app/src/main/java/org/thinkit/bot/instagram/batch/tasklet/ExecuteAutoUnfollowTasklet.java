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
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.catalog.VariableName;
import org.thinkit.bot.instagram.batch.data.content.entity.UnfollowText;
import org.thinkit.bot.instagram.batch.data.content.mapper.UnfollowTextMapper;
import org.thinkit.bot.instagram.batch.data.mongo.entity.FollowedUser;
import org.thinkit.bot.instagram.batch.data.mongo.entity.MissingUser;
import org.thinkit.bot.instagram.batch.data.mongo.entity.UnfollowedUser;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowedUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.MissingUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.UnfollowedUserRepository;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.config.AutoUnfollowConfig;
import org.thinkit.bot.instagram.param.UnfollowUser;
import org.thinkit.bot.instagram.result.ActionUnfollowFailedUser;
import org.thinkit.bot.instagram.result.ActionUnfollowedUser;
import org.thinkit.bot.instagram.result.AutoUnfollowResult;
import org.thinkit.bot.instagram.util.DateUtils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ExecuteAutoUnfollowTasklet extends AbstractTasklet {

    private ExecuteAutoUnfollowTasklet() {
        super(TaskType.AUTO_UNFOLLOW);
    }

    public static Tasklet newInstance() {
        return new ExecuteAutoUnfollowTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final List<UnfollowUser> unfollowUsers = this.getUnfollowUsers();

        // Do unfollow until there is no target user.
        if (unfollowUsers.isEmpty()) {
            return BatchTaskResult.builder().actionStatus(ActionStatus.SKIP).build();
        }

        final AutoUnfollowResult autoUnfollowResult = super.getInstaBot().executeAutoUnfollow(unfollowUsers,
                this.getAutoUnfollowConfig());
        log.info("The auto unfollow has completed the process successfully.");

        final List<ActionUnfollowedUser> actionUnfollowedUsers = autoUnfollowResult.getActionUnfollowedUsers();
        final List<ActionUnfollowFailedUser> actionUnfollowFailedUsers = autoUnfollowResult
                .getActionUnfollowFailedUsers();

        final String chargeUserName = super.getRunningUserName();
        final UnfollowedUserRepository unfollowedUserRepository = super.getMongoCollections()
                .getUnfollowedUserRepository();
        final FollowedUserRepository followedUserRepository = super.getMongoCollections().getFollowedUserRepository();
        final MissingUserRepository missingUserRepository = super.getMongoCollections().getMissingUserRepository();

        for (final ActionUnfollowedUser actionUnfollowedUser : actionUnfollowedUsers) {
            UnfollowedUser unfollowedUser = new UnfollowedUser();
            unfollowedUser.setUserName(actionUnfollowedUser.getUserName());
            unfollowedUser.setChargeUserName(chargeUserName);
            unfollowedUser.setUrl(actionUnfollowedUser.getUrl());

            unfollowedUser = unfollowedUserRepository.insert(unfollowedUser);
            log.debug("Inserted unfollowed user: {}", unfollowedUser);

            // Delete unfollowed user from followed user repository
            followedUserRepository.deleteByUserNameAndChargeUserName(actionUnfollowedUser.getUserName(),
                    chargeUserName);
        }

        for (final ActionUnfollowFailedUser actionUnfollowFailedUser : actionUnfollowFailedUsers) {
            MissingUser missingUser = new MissingUser();
            missingUser.setUserName(actionUnfollowFailedUser.getUserName());
            missingUser.setChargeUserName(chargeUserName);

            missingUser = missingUserRepository.insert(missingUser);
            log.debug("Inserted missing user: {}", missingUser);

            // Delete unfollow failed user from followed user repository
            followedUserRepository.deleteByUserNameAndChargeUserName(actionUnfollowFailedUser.getUserName(),
                    chargeUserName);
        }

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(actionUnfollowedUsers.size() + actionUnfollowFailedUsers.size());
        batchTaskResultBuilder.resultCount(actionUnfollowedUsers.size());
        batchTaskResultBuilder.actionErrors(autoUnfollowResult.getActionErrors());

        log.debug("END");
        return batchTaskResultBuilder.build();
    }

    private List<UnfollowUser> getUnfollowUsers() {
        log.debug("START");

        final int unfollowPerDay = this.getUnfollowPerDay();
        final List<UnfollowUser> unfollowUsers = new ArrayList<>();

        final FollowedUserRepository followedUserRepository = this.getMongoCollections().getFollowedUserRepository();
        final List<FollowedUser> followedUsers = followedUserRepository
                .findByChargeUserName(super.getRunningUserName());

        for (final FollowedUser followedUser : followedUsers) {
            if (this.isFollowExpiredUser(followedUser) && !this.isDuplicateUser(unfollowUsers, followedUser)) {
                unfollowUsers.add(UnfollowUser.from(followedUser.getUserName()));
            }

            if (unfollowUsers.size() >= unfollowPerDay) {
                break;
            }
        }

        log.debug("END");
        return unfollowUsers;
    }

    private boolean isFollowExpiredUser(@NonNull final FollowedUser followedUser) {
        return DateUtils.toString(new Date()).equals(this.getFollowExpiredDate(followedUser));
    }

    private String getFollowExpiredDate(@NonNull final FollowedUser followedUser) {
        return followedUser.isMutual() ? followedUser.getMutualExpiredDate() : followedUser.getExpiredDate();
    }

    private boolean isDuplicateUser(@NonNull final List<UnfollowUser> unfollowUsers,
            @NonNull final FollowedUser followedUser) {
        log.debug("START");

        for (final UnfollowUser unfollowUser : unfollowUsers) {
            if (unfollowUser.getUserName().equals(followedUser.getUserName())) {
                log.debug("The duplicate user has detected.");
                log.debug("END");
                return true;
            }
        }

        log.debug("The duplicate user has not detected.");
        log.debug("END");
        return false;
    }

    private AutoUnfollowConfig getAutoUnfollowConfig() {
        log.debug("START");

        final AutoUnfollowConfig.AutoUnfollowConfigBuilder autoUnfollowConfigBuilder = AutoUnfollowConfig.builder();
        autoUnfollowConfigBuilder.interval(this.getUnfollowInterval());
        autoUnfollowConfigBuilder.unfollowText(this.getUnfollowText());

        log.debug("END");
        return autoUnfollowConfigBuilder.build();
    }

    private int getUnfollowPerDay() {
        return super.getIntVariableValue(VariableName.UNFOLLOW_PER_DAY);
    }

    private int getUnfollowInterval() {
        return super.getIntVariableValue(VariableName.UNFOLLOW_INTERVAL);
    }

    private UnfollowText getUnfollowText() {
        return UnfollowTextMapper.newInstance().scan().get(0);
    }
}
