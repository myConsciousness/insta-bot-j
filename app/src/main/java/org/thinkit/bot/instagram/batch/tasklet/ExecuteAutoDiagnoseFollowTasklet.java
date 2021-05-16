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

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.data.mongo.entity.FollowMutualUser;
import org.thinkit.bot.instagram.batch.data.mongo.entity.FollowUnmutualUser;
import org.thinkit.bot.instagram.batch.data.mongo.entity.FollowedUser;
import org.thinkit.bot.instagram.batch.data.mongo.entity.LeftUser;
import org.thinkit.bot.instagram.batch.data.mongo.entity.UserFollower;
import org.thinkit.bot.instagram.batch.data.mongo.entity.UserFollowing;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowMutualUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowUnmutualUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowedUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.LeftUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.UserFollowerRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.UserFollowingRepository;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.util.DateUtils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ExecuteAutoDiagnoseFollowTasklet extends AbstractTasklet {

    private ExecuteAutoDiagnoseFollowTasklet() {
        super(TaskType.AUTO_DIAGNOSE_FOLLOW);
    }

    public static Tasklet newInstance() {
        return new ExecuteAutoDiagnoseFollowTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        int attemptCount = 0;
        attemptCount += this.diagnoseFollowBack();
        attemptCount += this.diagnoseFollowMutuality();

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(attemptCount);
        batchTaskResultBuilder.resultCount(attemptCount);

        log.debug("END");
        return batchTaskResultBuilder.build();
    }

    private int diagnoseFollowBack() {
        log.debug("START");

        final UserFollowerRepository userFollowerRepository = super.getMongoCollections().getUserFollowerRepository();
        final FollowedUserRepository followedUserRepository = super.getMongoCollections().getFollowedUserRepository();
        final LeftUserRepository leftUserRepository = super.getMongoCollections().getLeftUserRepository();
        final List<FollowedUser> followedUsers = followedUserRepository
                .findByChargeUserName(super.getRunningUserName());

        final String chargeUserName = super.getRunningUserName();
        final String mutualExpiredDate = DateUtils.getDateAfter(30);

        for (final FollowedUser followedUser : followedUsers) {
            final UserFollower userFollower = userFollowerRepository
                    .findByUserNameAndChargeUserName(followedUser.getUserName(), chargeUserName);

            if (userFollower == null) {
                if (followedUser.isMutual()) {
                    // When the user once followed back but then unfollow charge user
                    followedUser.setMutual(false);
                    followedUser.setMutualExpiredDate("");
                    followedUser.setUpdatedAt(new Date());

                    followedUserRepository.save(followedUser);
                    log.debug("Updated followed user: {}", followedUser);

                    LeftUser leftUser = new LeftUser();
                    leftUser.setUserName(followedUser.getUserName());
                    leftUser.setChargeUserName(followedUser.getChargeUserName());

                    leftUser = leftUserRepository.insert(leftUser);
                    log.debug("Inserted left user: {}", leftUser);
                }
            } else {
                // When the user followed back
                followedUser.setMutual(true);
                followedUser.setMutualExpiredDate(mutualExpiredDate);
                followedUser.setUpdatedAt(new Date());

                followedUserRepository.save(followedUser);
                log.debug("Updated followed user: {}", followedUser);

                final LeftUser leftUser = leftUserRepository.findByUserNameAndChargeUserName(followedUser.getUserName(),
                        chargeUserName);

                if (leftUser != null) {
                    // When the user unfollowed charge user but followed again
                    leftUserRepository.delete(leftUser);
                }
            }
        }

        log.debug("END");
        return followedUsers.size();
    }

    private int diagnoseFollowMutuality() {
        log.debug("START");

        final MongoCollections mongoCollections = super.getMongoCollections();
        final UserFollowerRepository userFollowerRepository = mongoCollections.getUserFollowerRepository();
        final UserFollowingRepository userFollowingRepository = mongoCollections.getUserFollowingRepository();
        final FollowMutualUserRepository followMutualUserRepository = mongoCollections.getFollowMutualUserRepository();
        final FollowUnmutualUserRepository followUnmutualUserRepository = mongoCollections
                .getFollowUnmutualUserRepository();

        final String chargeUserName = super.getRunningUserName();
        followMutualUserRepository.deleteByChargeUserName(chargeUserName);
        followUnmutualUserRepository.deleteByChargeUserName(chargeUserName);

        final List<UserFollowing> userFollowings = userFollowingRepository.findByChargeUserName(chargeUserName);
        final List<UserFollower> userFollowers = userFollowerRepository.findByChargeUserName(chargeUserName);

        for (final UserFollowing userFollowing : userFollowings) {
            if (this.isFollowMutual(userFollowing, userFollowers)) {
                final FollowMutualUser followMutualUser = new FollowMutualUser();
                followMutualUser.setUserName(userFollowing.getUserName());
                followMutualUser.setChargeUserName(chargeUserName);
                followMutualUserRepository.insert(followMutualUser);
                log.debug("Inserted follow mutual user: {}", followMutualUser);
            } else {
                final FollowUnmutualUser followUnmutualUser = new FollowUnmutualUser();
                followUnmutualUser.setUserName(userFollowing.getUserName());
                followUnmutualUser.setChargeUserName(chargeUserName);
                followUnmutualUserRepository.insert(followUnmutualUser);
                log.debug("Inserted follow unmutual user: {}", followUnmutualUser);
            }
        }

        log.debug("END");
        return userFollowings.size();
    }

    private boolean isFollowMutual(@NonNull final UserFollowing userFollowing,
            @NonNull final List<UserFollower> userFollowers) {
        log.debug("START");

        final String followingUser = userFollowing.getUserName();

        for (final UserFollower userFollower : userFollowers) {
            if (followingUser.equals(userFollower.getUserName())) {
                log.debug("END");
                return true;
            }
        }

        log.debug("END");
        return false;
    }
}
