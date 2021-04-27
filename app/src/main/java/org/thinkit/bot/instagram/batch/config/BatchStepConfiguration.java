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

package org.thinkit.bot.instagram.batch.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thinkit.bot.instagram.catalog.BatchStep;

@Configuration
public class BatchStepConfiguration {

    /**
     * The step builder factory
     */
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Tasklet executeLoginTasklet;

    @Autowired
    private Tasklet reversalEntryHashtagTasklet;

    @Autowired
    private Tasklet forecastFollowBackUserTasklet;

    @Autowired
    private Tasklet executeAutoLikeTasklet;

    @Autowired
    private Tasklet executeAutoFollowTasklet;

    @Autowired
    private Tasklet executeAutoUnfollowTasklet;

    @Autowired
    private Tasklet notifyResultTasklet;

    @Bean
    public Step executeLoginStep() {
        return this.stepBuilderFactory.get(BatchStep.LOGIN.getTag()).tasklet(this.executeLoginTasklet).build();
    }

    @Bean
    public Step reversalEntryHashtagStep() {
        return this.stepBuilderFactory.get(BatchStep.REVERSAL_ENTRY_HASHTAG.getTag())
                .tasklet(this.reversalEntryHashtagTasklet).build();
    }

    @Bean
    public Step forecastFollowBackUserStep() {
        return this.stepBuilderFactory.get(BatchStep.FORECAST_FOLLOW_BACK_USER.getTag())
                .tasklet(this.forecastFollowBackUserTasklet).build();
    }

    @Bean
    public Step executeAutoLikeStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_LIKE.getTag()).tasklet(this.executeAutoLikeTasklet)
                .build();
    }

    @Bean
    public Step executeAutoFollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_FOLLOW.getTag())
                .tasklet(this.executeAutoFollowTasklet).build();
    }

    @Bean
    public Step executeAutoUnfollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_UNFOLLOW.getTag())
                .tasklet(this.executeAutoUnfollowTasklet).build();
    }

    @Bean
    public Step notifyResultStep() {
        return this.stepBuilderFactory.get(BatchStep.NOTIFY_RESULT.getTag()).tasklet(this.notifyResultTasklet).build();
    }
}
