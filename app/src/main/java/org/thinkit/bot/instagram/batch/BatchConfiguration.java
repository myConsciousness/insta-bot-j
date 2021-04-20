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

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thinkit.bot.instagram.InstaBot;
import org.thinkit.bot.instagram.InstaBotJ;
import org.thinkit.bot.instagram.batch.tasklet.CloseBrowserTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutolikeTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteLoginTasklet;
import org.thinkit.bot.instagram.catalog.BatchJob;
import org.thinkit.bot.instagram.catalog.BatchStep;
import org.thinkit.bot.instagram.mongo.repository.ActionRecordRepository;
import org.thinkit.bot.instagram.mongo.repository.ErrorRepository;
import org.thinkit.bot.instagram.mongo.repository.HashtagRepository;
import org.thinkit.bot.instagram.mongo.repository.LastActionRepository;
import org.thinkit.bot.instagram.mongo.repository.LikedPhotoRepository;

@Configuration
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
     * The insta bot
     */
    private InstaBot instaBot = InstaBotJ.newInstance();

    /**
     * The mongo collection
     */
    private MongoCollection mongoCollection;

    @Bean
    public Job InstaBotJob() {
        return this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag()).flow(this.loginStep()).next(this.autolikeStep())
                .next(this.closeWebBrowserStep()).end().build();
    }

    @Bean
    public Step loginStep() {
        return this.stepBuilderFactory.get(BatchStep.LOGIN.getTag()).tasklet(ExecuteLoginTasklet.from(this.instaBot))
                .build();
    }

    @Bean
    public Step autolikeStep() {
        return this.stepBuilderFactory.get(BatchStep.AUTOLIKE.getTag())
                .tasklet(ExecuteAutolikeTasklet.from(this.instaBot, this.getMongoCollection())).build();
    }

    @Bean
    public Step closeWebBrowserStep() {
        return this.stepBuilderFactory.get(BatchStep.CLOSE_WEB_BROWSER.getTag())
                .tasklet(CloseBrowserTasklet.from(this.instaBot)).build();
    }

    private MongoCollection getMongoCollection() {

        if (this.mongoCollection != null) {
            return this.mongoCollection;
        }

        final MongoCollection.MongoCollectionBuilder mongoCollectionBuilder = MongoCollection.builder();
        mongoCollectionBuilder.hashtagRepository(this.hashtagRepository);
        mongoCollectionBuilder.likedPhotoRepository(this.likedPhotoRepository);
        mongoCollectionBuilder.errorRepository(this.errorRepository);
        mongoCollectionBuilder.actionRecordRepository(this.actionRecordRepository);
        mongoCollectionBuilder.lastActionRepository(this.lastActionRepository);

        return mongoCollectionBuilder.build();
    }
}
