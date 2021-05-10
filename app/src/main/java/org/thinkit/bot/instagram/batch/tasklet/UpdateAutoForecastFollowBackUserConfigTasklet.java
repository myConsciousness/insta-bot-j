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

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.data.mongo.entity.FollowBackPossibilityIndicator;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowBackPossibilityIndicatorRepository;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.content.mapper.FollowBackPossibilityIndicatorMapper;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class UpdateAutoForecastFollowBackUserConfigTasklet extends AbstractTasklet {

    private UpdateAutoForecastFollowBackUserConfigTasklet() {
        super(TaskType.UPDATE_AUTO_FORECAST_FOLLOW_BACK_USER_CONFIG);
    }

    public static Tasklet newInstance() {
        return new UpdateAutoForecastFollowBackUserConfigTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final FollowBackPossibilityIndicatorRepository followBackPossibilityIndicatorRepository = super.getMongoCollections()
                .getFollowBackPossibilityIndicatorRepository();
        followBackPossibilityIndicatorRepository.deleteAll();

        FollowBackPossibilityIndicatorMapper.newInstance().scan().forEach(contentFollowBackPossibilityIndicator -> {
            FollowBackPossibilityIndicator followBackPossibilityIndicator = new FollowBackPossibilityIndicator();
            followBackPossibilityIndicator.setHighestIndicator(contentFollowBackPossibilityIndicator.getHighest());
            followBackPossibilityIndicator.setHighIndicator(contentFollowBackPossibilityIndicator.getHigh());
            followBackPossibilityIndicator.setMiddleIndicator(contentFollowBackPossibilityIndicator.getMiddle());
            followBackPossibilityIndicator.setLowIndicator(contentFollowBackPossibilityIndicator.getLow());
            followBackPossibilityIndicator.setLowestIndicator(contentFollowBackPossibilityIndicator.getLowest());

            followBackPossibilityIndicator = followBackPossibilityIndicatorRepository
                    .insert(followBackPossibilityIndicator);
            log.debug("Inserted follow back possibility indicator: {}", followBackPossibilityIndicator);
        });

        log.debug("END");
        return BatchTaskResult.builder().actionCount(1).build();
    }
}
