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

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.mongo.entity.UserAccount;
import org.thinkit.bot.instagram.mongo.entity.UserFollowing;
import org.thinkit.bot.instagram.mongo.repository.UserFollowingRepository;
import org.thinkit.bot.instagram.param.ActionUser;
import org.thinkit.bot.instagram.result.ActionFollower;
import org.thinkit.bot.instagram.result.ActionFollowingUser;
import org.thinkit.bot.instagram.result.AutoScrapeUserProfileResult;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ExecuteAutoScrapeUserProfileTasklet extends AbstractTasklet {

    /**
     * The user account
     */
    @Autowired
    private UserAccount userAccount;

    private ExecuteAutoScrapeUserProfileTasklet() {
        super(TaskType.AUTO_SCRAPE_USER_PROFILE);
    }

    public static Tasklet newInstance() {
        return new ExecuteAutoScrapeUserProfileTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final AutoScrapeUserProfileResult autoScrapeUserProfileResult = super.getInstaBot()
                .executeAutoScrapeUserProfile(
                        ActionUser.from(this.userAccount.getUserName(), this.userAccount.getPassword()));
        final List<ActionFollowingUser> actionFollowingUsers = autoScrapeUserProfileResult.getActionFollowingUsers();
        final List<ActionFollower> actionFollowers = autoScrapeUserProfileResult.getActionFollowers();

        final UserFollowingRepository userFollowingRepository = super.getMongoCollections()
                .getUserFollowingRepository();
        userFollowingRepository.deleteByChargeUserName(this.userAccount.getUserName());

        for (final ActionFollowingUser actionFollowingUser : actionFollowingUsers) {
            UserFollowing userFollowing = new UserFollowing();
            userFollowing.setUserName(actionFollowingUser.getUserName());
            userFollowing.setChargeUserName(this.userAccount.getUserName());

            userFollowing = userFollowingRepository.insert(userFollowing);
            log.debug("Inserted user following: {}", userFollowing);
        }

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(actionFollowingUsers.size() + actionFollowers.size());
        batchTaskResultBuilder.resultCount(actionFollowingUsers.size() + actionFollowers.size());
        batchTaskResultBuilder.actionErrors(autoScrapeUserProfileResult.getActionErrors());

        log.debug("END");
        return batchTaskResultBuilder.build();
    }

}
