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
import java.util.List;

import com.mongodb.lang.NonNull;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.mongo.entity.FollowBackExpectableUser;
import org.thinkit.bot.instagram.mongo.entity.LikedPhoto;
import org.thinkit.bot.instagram.mongo.repository.FollowBackExpectableUserRepository;
import org.thinkit.bot.instagram.param.ForecastUser;
import org.thinkit.bot.instagram.result.ExpectableUser;
import org.thinkit.bot.instagram.result.ForecastFollowBackResult;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ForecastFollowBackUserTasklet extends AbstractTasklet {

    private ForecastFollowBackUserTasklet() {
        super(TaskType.FORECAST_FOLLOW_BACK_USER);
    }

    public static Tasklet newInstance() {
        return new ForecastFollowBackUserTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final ForecastFollowBackResult followBackResult = super.getInstaBot()
                .executeForecastFollowBackUser(this.getForecastUsers());
        log.info("The forecast follow back user has completed the process successfully.");

        final FollowBackExpectableUserRepository followBackExpectableUserRepository = super.getMongoCollection()
                .getFollowBackExpectableUserRepository();
        final List<ExpectableUser> expectableUsers = followBackResult.getExpectableUsers();

        for (final ExpectableUser expectableUser : expectableUsers) {
            final FollowBackExpectableUser followBackExpectableUser = new FollowBackExpectableUser();
            followBackExpectableUser.setUserName(expectableUser.getUserName());
            followBackExpectableUser.setFollowBackPossibilityCode(expectableUser.getFollowBackPossibility().getCode());
            followBackExpectableUser.setPost(expectableUser.getPost());
            followBackExpectableUser.setFollower(expectableUser.getFollower());
            followBackExpectableUser.setFollowing(expectableUser.getFollowing());
            followBackExpectableUser.setFollowDiff(expectableUser.getFollowDiff());

            followBackExpectableUserRepository.insert(followBackExpectableUser);
            log.debug("Interted follow back expectable user: {}", followBackExpectableUser);

            if (followBackResult.getActionErrors() != null) {
                log.debug("Forecast follow back user runtime error detected.");
                super.saveActionError(followBackResult.getActionErrors());
            }
        }

        super.saveMessageMeta(expectableUsers.size());

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionStatus(followBackResult.getActionStatus());
        batchTaskResultBuilder.repeatStatus(RepeatStatus.FINISHED);

        log.debug("END");
        return batchTaskResultBuilder.build();
    }

    private List<ForecastUser> getForecastUsers() {
        log.debug("START");

        final List<LikedPhoto> likedPhotos = this.getMongoCollection().getLikedPhotoRepository()
                .findDistinctUserNameBy();
        final List<ForecastUser> forecastUsers = new ArrayList<>(likedPhotos.size());

        likedPhotos.forEach(likedPhoto -> {
            if (!this.isDuplicateUser(forecastUsers, likedPhoto)) {
                forecastUsers.add(ForecastUser.from(likedPhoto.getUserName()));
            }
        });

        log.debug("END");
        return forecastUsers;
    }

    private boolean isDuplicateUser(@NonNull final List<ForecastUser> forecastUsers,
            @NonNull final LikedPhoto likedPhoto) {
        log.debug("START");

        for (final ForecastUser forecastUser : forecastUsers) {
            if (forecastUser.getUserName().equals(likedPhoto.getUserName())) {
                log.debug("The duplicate user has detected.");
                log.debug("END");
                return true;
            }
        }

        log.debug("The duplicate user has not detected.");
        log.debug("END");
        return false;
    }
}