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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoFollowTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoLikeTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoUnfollowTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteLoginTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ForecastFollowBackUserTasklet;
import org.thinkit.bot.instagram.batch.tasklet.NotifyResultTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ReversalEntryHashtagTasklet;
import org.thinkit.bot.instagram.catalog.BatchStep;
import org.thinkit.bot.instagram.mongo.MongoCollection;
import org.thinkit.bot.instagram.mongo.entity.UserAccount;

@Configuration
public class BatchStepConfiguration {

    /**
     * The step builder factory
     */
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * The insta bot
     */
    @Autowired
    private InstaBot instaBot;

    /**
     * The mongo collection
     */
    @Autowired
    private MongoCollection mongoCollection;

    @Bean
    public Step executeLoginStep() {
        final UserAccount userAccount = this.mongoCollection.getUserAccountRepository().findAll().get(0);
        return this.stepBuilderFactory.get(BatchStep.LOGIN.getTag())
                .tasklet(ExecuteLoginTasklet.from(this.instaBot, userAccount, this.mongoCollection)).build();
    }

    @Bean
    public Step reversalEntryHashtagStep() {
        return this.stepBuilderFactory.get(BatchStep.REVERSAL_ENTRY_HASHTAG.getTag())
                .tasklet(ReversalEntryHashtagTasklet.from(this.mongoCollection)).build();
    }

    @Bean
    public Step forecastFollowBackUserStep() {
        return this.stepBuilderFactory.get(BatchStep.FORECAST_FOLLOW_BACK_USER.getTag())
                .tasklet(ForecastFollowBackUserTasklet.from(this.instaBot, this.mongoCollection)).build();
    }

    @Bean
    public Step executeAutoLikeStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_LIKE.getTag())
                .tasklet(ExecuteAutoLikeTasklet.from(this.instaBot, this.mongoCollection)).build();
    }

    @Bean
    public Step executeAutoFollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_FOLLOW.getTag())
                .tasklet(ExecuteAutoFollowTasklet.from(this.instaBot, this.mongoCollection)).build();
    }

    @Bean
    public Step executeAutoUnfollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_UNFOLLOW.getTag())
                .tasklet(ExecuteAutoUnfollowTasklet.from(this.instaBot, this.mongoCollection)).build();
    }

    @Bean
    public Step notifyResultStep() {
        return this.stepBuilderFactory.get(BatchStep.NOTIFY_RESULT.getTag())
                .tasklet(NotifyResultTasklet.from(this.mongoCollection)).build();
    }
}
