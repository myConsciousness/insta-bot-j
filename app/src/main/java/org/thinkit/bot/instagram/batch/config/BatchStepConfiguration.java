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
import org.thinkit.bot.instagram.batch.catalog.BatchStep;
import org.thinkit.bot.instagram.batch.dto.BatchStepCollections;

@Configuration
public class BatchStepConfiguration {

    /**
     * The step builder factory
     */
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Tasklet initializeSessionTasklet;

    @Autowired
    private Tasklet executeAutoLoginTasklet;

    @Autowired
    private Tasklet updateAutoLikeConfigTasklet;

    @Autowired
    private Tasklet executeAutoLikeTasklet;

    @Autowired
    private Tasklet updateAutoForecastFollowBackUserConfigTasklet;

    @Autowired
    private Tasklet executeAutoForecastFollowBackUserTasklet;

    @Autowired
    private Tasklet executeAutoScrapeUserProfileTasklet;

    @Autowired
    private Tasklet executeAutoDiagnoseFollowTasklet;

    @Autowired
    private Tasklet executeAutoRefreshRetryUserTasklet;

    @Autowired
    private Tasklet executeAutoFollowTasklet;

    @Autowired
    private Tasklet executeAutoUnfollowTasklet;

    @Autowired
    private Tasklet notifyResultReportTasklet;

    @Autowired
    private Tasklet closeSessionTasklet;

    @Bean
    public BatchStepCollections batchStepCollections() {
        final BatchStepCollections.BatchStepCollectionsBuilder batchStepCollectionsBuilder = BatchStepCollections
                .builder();
        batchStepCollectionsBuilder.initializeSessionStep(this.initializeSessionStep());
        batchStepCollectionsBuilder.executeAutoLoginStep(this.executeAutoLoginStep());
        batchStepCollectionsBuilder.updateAutoLikeConfigStep(this.updateAutoLikeConfigStep());
        batchStepCollectionsBuilder.executeAutoLikeStep(this.executeAutoLikeStep());
        batchStepCollectionsBuilder
                .updateAutoForecastFollowBackUserConfigStep(this.updateAutoForecastFollowBackUserConfigStep());
        batchStepCollectionsBuilder.executeAutoForecastFollowBackUserStep(this.executeAutoForecastFollowBackUserStep());
        batchStepCollectionsBuilder.executeAutoScrapeUserProfileStep(this.executeAutoScrapeUserProfileStep());
        batchStepCollectionsBuilder.executeAutoDiagnoseFollowStep(this.executeAutoDiagnoseFollowStep());
        batchStepCollectionsBuilder.executeAutoRefreshRetryStep(this.executeAutoRefreshRetryUserStep());
        batchStepCollectionsBuilder.executeAutoFollowStep(this.executeAutoFollowStep());
        batchStepCollectionsBuilder.executeAutoUnfollowStep(this.executeAutoUnfollowStep());
        batchStepCollectionsBuilder.notifyResultReportStep(this.notifyResultReportStep());
        batchStepCollectionsBuilder.closeSessionStep(this.closeSessionStep());

        return batchStepCollectionsBuilder.build();
    }

    private Step initializeSessionStep() {
        return this.stepBuilderFactory.get(BatchStep.INITIALIZE_SESSION.getTag()).tasklet(this.initializeSessionTasklet)
                .build();
    }

    private Step executeAutoLoginStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_LOGIN.getTag()).tasklet(this.executeAutoLoginTasklet)
                .build();
    }

    private Step updateAutoLikeConfigStep() {
        return this.stepBuilderFactory.get(BatchStep.UPDATE_AUTO_LIKE_CONFIG.getTag())
                .tasklet(this.updateAutoLikeConfigTasklet).build();
    }

    private Step executeAutoLikeStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_LIKE.getTag()).tasklet(this.executeAutoLikeTasklet)
                .build();
    }

    private Step updateAutoForecastFollowBackUserConfigStep() {
        return this.stepBuilderFactory.get(BatchStep.UPDATE_AUTO_FORECAST_FOLLOW_BACK_USER_CONFIG.getTag())
                .tasklet(this.updateAutoForecastFollowBackUserConfigTasklet).build();
    }

    private Step executeAutoForecastFollowBackUserStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_FORECAST_FOLLOW_BACK_USER.getTag())
                .tasklet(this.executeAutoForecastFollowBackUserTasklet).build();
    }

    private Step executeAutoScrapeUserProfileStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_SCRAPE_USER_PROFILE.getTag())
                .tasklet(this.executeAutoScrapeUserProfileTasklet).build();
    }

    private Step executeAutoDiagnoseFollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_DIAGNOSE_FOLLOW.getTag())
                .tasklet(this.executeAutoDiagnoseFollowTasklet).build();
    }

    private Step executeAutoRefreshRetryUserStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_REFRESH_RETRY_USER.getTag())
                .tasklet(this.executeAutoRefreshRetryUserTasklet).build();
    }

    private Step executeAutoFollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_FOLLOW.getTag())
                .tasklet(this.executeAutoFollowTasklet).build();
    }

    private Step executeAutoUnfollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_UNFOLLOW.getTag())
                .tasklet(this.executeAutoUnfollowTasklet).build();
    }

    private Step notifyResultReportStep() {
        return this.stepBuilderFactory.get(BatchStep.NOTIFY_RESULT_REPORT.getTag())
                .tasklet(this.notifyResultReportTasklet).build();
    }

    private Step closeSessionStep() {
        return this.stepBuilderFactory.get(BatchStep.CLOSE_SESSION.getTag()).tasklet(this.closeSessionTasklet).build();
    }
}
