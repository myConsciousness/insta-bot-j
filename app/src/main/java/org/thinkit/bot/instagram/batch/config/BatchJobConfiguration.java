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

import com.mongodb.lang.NonNull;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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
import org.thinkit.api.catalog.Catalog;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.InstaBotJ;
import org.thinkit.bot.instagram.batch.catalog.BatchJob;
import org.thinkit.bot.instagram.batch.catalog.BatchMainStreamFlowStrategyPattern;
import org.thinkit.bot.instagram.batch.catalog.BatchScheduleType;
import org.thinkit.bot.instagram.batch.catalog.VariableName;
import org.thinkit.bot.instagram.batch.data.content.mapper.DefaultVariableMapper;
import org.thinkit.bot.instagram.batch.data.mongo.entity.Variable;
import org.thinkit.bot.instagram.batch.data.mongo.repository.VariableRepository;
import org.thinkit.bot.instagram.batch.dto.BatchStepCollections;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.strategy.context.BatchMainStreamFlowContext;
import org.thinkit.bot.instagram.batch.strategy.flow.BatchFlowStrategy;

/**
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
public class BatchJobConfiguration {

    /**
     * The schedule cron of initialize session
     */
    private static final String SCHEDULE_CRON_INITIALIZE_SESSION = "${spring.batch.schedule.cron.initialize}";

    /**
     * The schedule cron of main stream
     */
    private static final String SCHEDULE_CRON_MAIN_STREAM = "${spring.batch.schedule.cron.mainstream}";

    /**
     * The schedule cron of close session
     */
    private static final String SCHEDULE_CRON_CLOSE_SESSION = "${spring.batch.schedule.cron.close}";

    /**
     * The timezone
     */
    private static final String TIME_ZONE = "${spring.batch.schedule.timezone}";

    /**
     * The job builder factory
     */
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    /**
     * The job launcher
     */
    @Autowired
    private SimpleJobLauncher simpleJobLauncher;

    /**
     * The batch step collections
     */
    @Autowired
    private BatchStepCollections batchStepCollections;

    /**
     * The mongo collections
     */
    @Autowired
    private MongoCollections mongoCollections;

    @Bean
    public InstaBot instaBot() {
        return InstaBotJ.newInstance();
    }

    @Scheduled(cron = SCHEDULE_CRON_INITIALIZE_SESSION, zone = TIME_ZONE)
    public void performScheduledInitializeSession() throws Exception {
        this.runJobLauncher(BatchScheduleType.INITIALIZE_SESSION);
    }

    @Scheduled(cron = SCHEDULE_CRON_MAIN_STREAM, zone = TIME_ZONE)
    public void performScheduledMainStream() throws Exception {
        this.runJobLauncher(BatchScheduleType.MAIN_STREAM);
    }

    @Scheduled(cron = SCHEDULE_CRON_CLOSE_SESSION, zone = TIME_ZONE)
    public void performScheduledCloseSession() throws Exception {
        this.runJobLauncher(BatchScheduleType.CLOSE_SESSION);
    }

    private void runJobLauncher(@NonNull final BatchScheduleType batchScheduleType) throws Exception {
        final JobParameters param = new JobParametersBuilder()
                .addString(BatchJob.INSTA_BOT.getTag(), String.valueOf(System.currentTimeMillis())).toJobParameters();

        this.simpleJobLauncher.run(this.createInstaBotJob(batchScheduleType), param);
    }

    private Job createInstaBotJob(@NonNull final BatchScheduleType batchScheduleType) {
        return switch (batchScheduleType) {
            case INITIALIZE_SESSION -> this.createInitializeSessionJobFlowBuilder().end().build();
            case MAIN_STREAM -> this.createMainStreamJobFlowBuilder().end().build();
            case CLOSE_SESSION -> this.createCloseSessionJobFlowBuilder().end().build();
        };
    }

    private FlowBuilder<FlowJobBuilder> createInitializeSessionJobFlowBuilder() {
        return this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag())
                .flow(this.batchStepCollections.getInitializeSessionStep())
                .next(this.batchStepCollections.getExecuteAutoLoginStep())
                .next(this.batchStepCollections.getExecuteAutoScrapeUserProfileTasklet())
                .next(this.batchStepCollections.getExecuteAutoDiagnoseFollowTasklet());
    }

    private FlowBuilder<FlowJobBuilder> createMainStreamJobFlowBuilder() {
        final JobBuilder jobBuilder = this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag());
        final BatchFlowStrategy batchFlowStrategy = BatchMainStreamFlowContext
                .from(this.getBatchMainStreamFlowStrategyPattern()).evaluate();

        return batchFlowStrategy.createJobFlowBuilder(jobBuilder, this.batchStepCollections);
    }

    private FlowBuilder<FlowJobBuilder> createCloseSessionJobFlowBuilder() {
        return this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag())
                .flow(this.batchStepCollections.getCloseSessionStep());
    }

    private BatchMainStreamFlowStrategyPattern getBatchMainStreamFlowStrategyPattern() {

        final VariableRepository variableRepository = mongoCollections.getVariableRepository();
        Variable variable = variableRepository.findByName(VariableName.BATCH_MAIN_STREAM_FLOW_STRATEGY.getTag());

        if (variable == null) {
            variable = new Variable();
            variable.setName(VariableName.BATCH_MAIN_STREAM_FLOW_STRATEGY.getTag());
            variable.setValue(this.getDefaultBatchMainStreamFlowStrategy());
            variable = variableRepository.insert(variable);
        }

        final BatchMainStreamFlowStrategyPattern batchFlowStrategyPattern = Catalog
                .getEnum(BatchMainStreamFlowStrategyPattern.class, Integer.parseInt(variable.getValue()));

        return batchFlowStrategyPattern;
    }

    private String getDefaultBatchMainStreamFlowStrategy() {
        return DefaultVariableMapper.from(VariableName.BATCH_MAIN_STREAM_FLOW_STRATEGY.getTag()).scan().get(0)
                .getValue();
    }
}
