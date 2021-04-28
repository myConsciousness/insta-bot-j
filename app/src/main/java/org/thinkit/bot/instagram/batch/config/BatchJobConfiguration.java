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

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.InstaBotJ;
import org.thinkit.bot.instagram.catalog.BatchJob;

@Configuration
@EnableScheduling
public class BatchJobConfiguration {

    /**
     * The job launcher
     */
    @Autowired
    private SimpleJobLauncher simpleJobLauncher;

    /**
     * The job builder factory
     */
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    /**
     * The login step
     */
    @Autowired
    private Step executeLoginStep;

    /**
     * The reversal entry hashtag step
     */
    @Autowired
    private Step reversalEntryHashtagStep;

    /**
     * The auto like step
     */
    @Autowired
    private Step executeAutoLikeStep;

    /**
     * The forecast follow back user step
     */
    @Autowired
    private Step forecastFollowBackUserStep;

    /**
     * The notify result step
     */
    @Autowired
    private Step notifyResultStep;

    /**
     * The login flag
     */
    private boolean logined;

    @Bean
    public InstaBot instaBot() {
        return InstaBotJ.newInstance();
    }

    @Scheduled(cron = "* * * * * *", zone = "Asia/Tokyo")
    public void performScheduledJob() throws Exception {

        JobParameters param = new JobParametersBuilder()
                .addString(BatchJob.INSTA_BOT.getTag(), String.valueOf(System.currentTimeMillis())).toJobParameters();

        this.simpleJobLauncher.run(this.instaBotJob(), param);
    }

    private Job instaBotJob() {
        return this.createInstaBotFlowBuilder().end().build();
    }

    private FlowBuilder<FlowJobBuilder> createInstaBotFlowBuilder() {

        final JobBuilder jobBuilder = this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag());

        if (!this.logined) {
            this.logined = true;
            return jobBuilder.flow(this.executeLoginStep).next(this.reversalEntryHashtagStep)
                    .next(this.executeAutoLikeStep).next(this.forecastFollowBackUserStep).next(this.notifyResultStep);
        }

        return jobBuilder.flow(this.reversalEntryHashtagStep).next(this.executeAutoLikeStep)
                .next(this.forecastFollowBackUserStep).next(this.notifyResultStep);
    }
}
