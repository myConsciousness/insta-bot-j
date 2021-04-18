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
import org.thinkit.bot.instagram.batch.tasklet.AutoLikeTasklet;
import org.thinkit.bot.instagram.batch.tasklet.LoginTasklet;
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
     * The liked photo repository
     */
    @Autowired
    private LikedPhotoRepository likedPhotoRepository;

    /**
     * The insta bot
     */
    private InstaBot instaBot = InstaBotJ.newInstance();

    @Bean
    public Job loginJob() {
        return jobBuilderFactory.get("LoginJob").flow(this.loginStep()).end().build();
    }

    @Bean
    public Job fooJob() {
        return jobBuilderFactory.get("AutoLikeJob").flow(helloStep()).end().build();
    }

    @Bean
    public Job barJob() {
        System.out.println("barJob メソッドを実行");
        return jobBuilderFactory.get("myBarJob").flow(helloStep()).next(worldStep()).end().build();
    }

    @Bean
    public Step loginStep() {
        return stepBuilderFactory.get("loginStep").tasklet(LoginTasklet.from(this.instaBot)).build();
    }

    @Bean
    public Step helloStep() {
        System.out.println("helloStep メソッドを実行");
        return stepBuilderFactory.get("myHelloStep").tasklet(AutoLikeTasklet.from(likedPhotoRepository)).build();
    }

    @Bean
    public Step worldStep() {
        System.out.println("worldStep メソッドを実行");
        return stepBuilderFactory.get("myWorldStep").tasklet(AutoLikeTasklet.from(likedPhotoRepository)).build();
    }
}
