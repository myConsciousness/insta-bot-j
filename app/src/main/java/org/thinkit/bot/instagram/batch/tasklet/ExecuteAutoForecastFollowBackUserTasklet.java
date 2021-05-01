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
import java.util.Collections;
import java.util.List;

import com.mongodb.lang.NonNull;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.VariableName;
import org.thinkit.bot.instagram.config.AutoForecastFollowBackUserConfig;
import org.thinkit.bot.instagram.mongo.entity.FollowBackExpectableUser;
import org.thinkit.bot.instagram.mongo.entity.FollowBackPossibilityIndicator;
import org.thinkit.bot.instagram.mongo.entity.LikedPhoto;
import org.thinkit.bot.instagram.mongo.repository.FollowBackExpectableUserRepository;
import org.thinkit.bot.instagram.mongo.repository.LikedPhotoRepository;
import org.thinkit.bot.instagram.param.ForecastUser;
import org.thinkit.bot.instagram.result.ExpectableUser;
import org.thinkit.bot.instagram.result.ForecastFollowBackResult;
import org.thinkit.bot.instagram.result.UnexpectableUser;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class ExecuteAutoForecastFollowBackUserTasklet extends AbstractTasklet {

    /**
     * The constructor.
     */
    private ExecuteAutoForecastFollowBackUserTasklet() {
        super(TaskType.AUTO_FORECAST_FOLLOW_BACK_USER);
    }

    /**
     * Returns the new instance of {@link ExecuteAutoForecastFollowBackUserTasklet}
     * .
     *
     * @return The new instance of {@link ExecuteAutoForecastFollowBackUserTasklet}
     */
    public static Tasklet newInstance() {
        return new ExecuteAutoForecastFollowBackUserTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final List<ForecastUser> forecastUsers = this.getForecastUsers();

        if (forecastUsers.isEmpty()) {
            return BatchTaskResult.builder().actionStatus(ActionStatus.SKIP).build();
        }

        final ForecastFollowBackResult followBackResult = super.getInstaBot()
                .executeAutoForecastFollowBackUser(forecastUsers, this.getAutoForecastFollowBackUserConfig());
        log.info("The forecast follow back user has completed the process successfully.");

        final FollowBackExpectableUserRepository followBackExpectableUserRepository = super.getMongoCollections()
                .getFollowBackExpectableUserRepository();
        final LikedPhotoRepository likedPhotoRepository = this.getMongoCollections().getLikedPhotoRepository();

        final List<ExpectableUser> expectableUsers = followBackResult.getExpectableUsers();
        final List<UnexpectableUser> unexpectableUsers = followBackResult.getUnexpectableUsers();

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

            // Delete forecasted expectable users
            likedPhotoRepository.deleteByUserName(expectableUser.getUserName());
        }

        for (final UnexpectableUser unexpectableUser : unexpectableUsers) {
            // Delete forecasted unexpectable users
            likedPhotoRepository.deleteByUserName(unexpectableUser.getUserName());
        }

        final BatchTaskResult.BatchTaskResultBuilder batchTaskResultBuilder = BatchTaskResult.builder();
        batchTaskResultBuilder.actionCount(followBackResult.getActionCount());
        batchTaskResultBuilder.resultCount(expectableUsers.size());
        batchTaskResultBuilder.actionStatus(followBackResult.getActionStatus());

        if (followBackResult.getActionErrors() != null) {
            log.debug("Forecast follow back user runtime error detected.");
            batchTaskResultBuilder.actionErrors(followBackResult.getActionErrors());
        }

        log.debug("END");
        return batchTaskResultBuilder.build();
    }

    private List<ForecastUser> getForecastUsers() {
        log.debug("START");

        final List<LikedPhoto> likedPhotos = this.getMongoCollections().getLikedPhotoRepository().findAll();
        final List<ForecastUser> forecastUsers = new ArrayList<>(likedPhotos.size());

        Collections.shuffle(likedPhotos);
        final int maxUserCount = this.getMaxUserCount();

        for (final LikedPhoto likedPhoto : likedPhotos) {
            if (!this.isDuplicateUser(forecastUsers, likedPhoto)) {
                forecastUsers.add(ForecastUser.from(likedPhoto.getUserName()));

                if (forecastUsers.size() >= maxUserCount) {
                    break;
                }
            }
        }

        log.debug("The forecast users: {}", forecastUsers);
        log.debug("END");
        return forecastUsers;
    }

    private boolean isDuplicateUser(@NonNull final List<ForecastUser> forecastUsers,
            @NonNull final LikedPhoto likedPhoto) {
        log.debug("START");

        if (this.isAlreadyForecastedUser(forecastUsers, likedPhoto)) {
            return true;
        }

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

    private AutoForecastFollowBackUserConfig getAutoForecastFollowBackUserConfig() {
        log.debug("START");

        final List<FollowBackPossibilityIndicator> followBackPossibilityIndicators = super.getMongoCollections()
                .getFollowBackPossibilityIndicatorRepository().findAll();

        if (followBackPossibilityIndicators == null || followBackPossibilityIndicators.isEmpty()) {
            throw new IllegalStateException();
        }

        final AutoForecastFollowBackUserConfig.AutoForecastFollowBackUserConfigBuilder autoForecastFollowBackUserConfigBuilder = AutoForecastFollowBackUserConfig
                .builder();
        autoForecastFollowBackUserConfigBuilder.followBackPossibilityIndicator(followBackPossibilityIndicators.get(0));

        log.debug("END");
        return autoForecastFollowBackUserConfigBuilder.build();
    }

    private boolean isAlreadyForecastedUser(@NonNull final List<ForecastUser> forecastUsers,
            @NonNull final LikedPhoto likedPhoto) {
        final FollowBackExpectableUserRepository followBackExpectableUserRepository = this.getMongoCollections()
                .getFollowBackExpectableUserRepository();
        return followBackExpectableUserRepository.findByUserName(likedPhoto.getUserName()) != null;
    }

    private int getMaxUserCount() {
        return Integer
                .parseInt(super.getVariable(VariableName.FORECAST_FOLLOW_BACK_EXPECTABLE_USER_PER_TASK).getValue());
    }
}
