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
import org.thinkit.bot.instagram.batch.data.mongo.entity.Variable;
import org.thinkit.bot.instagram.batch.data.mongo.repository.VariableRepository;
import org.thinkit.bot.instagram.batch.dto.BatchStepCollections;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.strategy.context.BatchFlowContext;
import org.thinkit.bot.instagram.batch.strategy.flow.BatchFlowStrategy;
import org.thinkit.bot.instagram.catalog.BatchFlowStrategyPattern;
import org.thinkit.bot.instagram.catalog.BatchFlowType;
import org.thinkit.bot.instagram.catalog.BatchJob;
import org.thinkit.bot.instagram.catalog.VariableName;
import org.thinkit.bot.instagram.content.mapper.DefaultVariableMapper;

/**
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
public class BatchJobConfiguration {

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

    /**
     * The login flag
     */
    private boolean logined;

    @Bean
    public InstaBot instaBot() {
        return InstaBotJ.newInstance();
    }

    @Scheduled(cron = "${spring.batch.schedule.cron}", zone = "${spring.batch.schedule.timezone}")
    public void performScheduledJob() throws Exception {

        final JobParameters param = new JobParametersBuilder()
                .addString(BatchJob.INSTA_BOT.getTag(), String.valueOf(System.currentTimeMillis())).toJobParameters();

        this.simpleJobLauncher.run(this.createInstaBotJob(BatchFlowType.BOOT), param);
    }

    @Scheduled(cron = "${spring.batch.schedule.session.cron}", zone = "${spring.batch.schedule.timezone}")
    public void performScheduledCloseSession() throws Exception {

        final JobParameters param = new JobParametersBuilder()
                .addString(BatchJob.INSTA_BOT.getTag(), String.valueOf(System.currentTimeMillis())).toJobParameters();

        this.simpleJobLauncher.run(this.createInstaBotJob(BatchFlowType.CLOSE), param);
    }

    private Job createInstaBotJob(@NonNull final BatchFlowType batchFlowType) {
        return switch (batchFlowType) {
            case BOOT -> this.createBootJobFlowBuilder().end().build();
            case CLOSE -> this.createCloseJobFlowBuilder().end().build();
        };
    }

    private FlowBuilder<FlowJobBuilder> createBootJobFlowBuilder() {

        final JobBuilder jobBuilder = this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag());
        final BatchFlowStrategy batchFlowStrategy = BatchFlowContext.from(this.getBatchFlowStrategyPattern())
                .evaluate();

        if (!this.logined) {
            this.logined = true;
            return batchFlowStrategy.createLoginJobFlowBuilder(jobBuilder, this.batchStepCollections);
        }

        return batchFlowStrategy.createJobFlowBuilder(jobBuilder, this.batchStepCollections);
    }

    private FlowBuilder<FlowJobBuilder> createCloseJobFlowBuilder() {
        return this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag())
                .flow(this.batchStepCollections.getCloseSessionStep());
    }

    private BatchFlowStrategyPattern getBatchFlowStrategyPattern() {

        final VariableRepository variableRepository = mongoCollections.getVariableRepository();
        Variable variable = variableRepository.findByName(VariableName.BATCH_FLOW_STRATEGY.getTag());

        if (variable == null) {
            variable = new Variable();
            variable.setName(VariableName.BATCH_FLOW_STRATEGY.getTag());
            variable.setValue(this.getDefaultBatchFlowStrategy());
            variable = variableRepository.insert(variable);
        }

        final BatchFlowStrategyPattern batchFlowStrategyPattern = Catalog.getEnum(BatchFlowStrategyPattern.class,
                Integer.parseInt(variable.getValue()));

        return batchFlowStrategyPattern;
    }

    private String getDefaultBatchFlowStrategy() {
        return DefaultVariableMapper.from(VariableName.BATCH_FLOW_STRATEGY.getTag()).scan().get(0).getValue();
    }
}
