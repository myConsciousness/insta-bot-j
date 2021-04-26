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

package org.thinkit.bot.instagram.batch;

import com.mongodb.lang.NonNull;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.InstaBotJ;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoFollowTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoLikeTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoUnfollowTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteLoginTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ForecastFollowBackUserTasklet;
import org.thinkit.bot.instagram.batch.tasklet.NotifyResultTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ReversalEntryHashtagTasklet;
import org.thinkit.bot.instagram.catalog.BatchJob;
import org.thinkit.bot.instagram.catalog.BatchStep;
import org.thinkit.bot.instagram.mongo.entity.UserAccount;
import org.thinkit.bot.instagram.mongo.repository.ActionRecordRepository;
import org.thinkit.bot.instagram.mongo.repository.ErrorRepository;
import org.thinkit.bot.instagram.mongo.repository.HashtagRepository;
import org.thinkit.bot.instagram.mongo.repository.LastActionRepository;
import org.thinkit.bot.instagram.mongo.repository.LikedPhotoRepository;
import org.thinkit.bot.instagram.mongo.repository.MessageMetaRepository;
import org.thinkit.bot.instagram.mongo.repository.UserAccountRepository;
import org.thinkit.bot.instagram.mongo.repository.VariableRepository;

@Configuration
@EnableScheduling
@EnableBatchProcessing
public class BatchConfiguration {

    /**
     * The job builder factory
     */
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    /**
     * The step builder factory
     */
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * The job launcher
     */
    @Autowired
    private SimpleJobLauncher jobLauncher;

    /**
     * The variable repository
     */
    @Autowired
    private VariableRepository variableRepository;

    /**
     * The user account repository
     */
    @Autowired
    private UserAccountRepository userAccountRepository;

    /**
     * The hashtag repository
     */
    @Autowired
    private HashtagRepository hashtagRepository;

    /**
     * The liked photo repository
     */
    @Autowired
    private LikedPhotoRepository likedPhotoRepository;

    /**
     * The error repository
     */
    @Autowired
    private ErrorRepository errorRepository;

    /**
     * The action record repository
     */
    @Autowired
    private ActionRecordRepository actionRecordRepository;

    /**
     * The last action repository
     */
    @Autowired
    private LastActionRepository lastActionRepository;

    /**
     * The message meta repository
     */
    @Autowired
    private MessageMetaRepository messageMetaRepository;

    /**
     * The insta bot
     */
    private InstaBot instaBot;

    /**
     * The mongo collection
     */
    private MongoCollection mongoCollection;

    private boolean logined;

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Tokyo")
    public void performScheduledJob() throws Exception {

        JobParameters param = new JobParametersBuilder()
                .addString(BatchJob.INSTA_BOT.getTag(), String.valueOf(System.currentTimeMillis())).toJobParameters();

        this.jobLauncher.run(this.instaBotJob(), param);
    }

    public Job instaBotJob() {

        if (this.instaBot == null) {
            this.instaBot = InstaBotJ.newInstance();
        }

        final JobBuilder jobBuilder = this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag());
        FlowBuilder<FlowJobBuilder> flowBuilder = null;

        for (final UserAccount userAccount : this.userAccountRepository.findAll()) {
            if (!this.logined) {
                this.logined = true;
                flowBuilder = jobBuilder.flow(this.executeLoginStep(userAccount)).next(this.reversalEntryHashtagStep())
                        .next(this.executeAutoLikeStep());
            } else {
                flowBuilder = jobBuilder.flow(this.reversalEntryHashtagStep()).next(this.executeAutoLikeStep());
            }

            flowBuilder.next(this.notifyResultStep());
        }

        return flowBuilder.end().build();
    }

    public Step executeLoginStep(@NonNull final UserAccount userAccount) {
        return this.stepBuilderFactory.get(BatchStep.LOGIN.getTag())
                .tasklet(ExecuteLoginTasklet.from(this.instaBot, userAccount, this.getMongoCollection())).build();
    }

    public Step reversalEntryHashtagStep() {
        return this.stepBuilderFactory.get(BatchStep.REVERSAL_ENTRY_HASHTAG.getTag())
                .tasklet(ReversalEntryHashtagTasklet.from(this.getMongoCollection())).build();
    }

    public Step forecastFollowBackUserStep() {
        return this.stepBuilderFactory.get(BatchStep.FORECAST_FOLLOW_BACK_USER.getTag())
                .tasklet(ForecastFollowBackUserTasklet.from(this.instaBot, this.getMongoCollection())).build();
    }

    public Step executeAutoLikeStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_LIKE.getTag())
                .tasklet(ExecuteAutoLikeTasklet.from(this.instaBot, this.getMongoCollection())).build();
    }

    public Step executeAutoFollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_FOLLOW.getTag())
                .tasklet(ExecuteAutoFollowTasklet.from(this.instaBot, this.getMongoCollection())).build();
    }

    public Step executeAutoUnfollowStep() {
        return this.stepBuilderFactory.get(BatchStep.EXECUTE_AUTO_UNFOLLOW.getTag())
                .tasklet(ExecuteAutoUnfollowTasklet.from(this.instaBot, this.getMongoCollection())).build();
    }

    private Step notifyResultStep() {
        return this.stepBuilderFactory.get(BatchStep.NOTIFY_RESULT.getTag())
                .tasklet(NotifyResultTasklet.from(this.getMongoCollection())).build();
    }

    private MongoCollection getMongoCollection() {

        if (this.mongoCollection != null) {
            return this.mongoCollection;
        }

        final MongoCollection.MongoCollectionBuilder mongoCollectionBuilder = MongoCollection.builder();
        mongoCollectionBuilder.variableRepository(this.variableRepository);
        mongoCollectionBuilder.hashtagRepository(this.hashtagRepository);
        mongoCollectionBuilder.likedPhotoRepository(this.likedPhotoRepository);
        mongoCollectionBuilder.errorRepository(this.errorRepository);
        mongoCollectionBuilder.actionRecordRepository(this.actionRecordRepository);
        mongoCollectionBuilder.lastActionRepository(this.lastActionRepository);
        mongoCollectionBuilder.messageMetaRepository(this.messageMetaRepository);

        return mongoCollectionBuilder.build();
    }

    @Bean
    public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        return launcher;
    }
}
