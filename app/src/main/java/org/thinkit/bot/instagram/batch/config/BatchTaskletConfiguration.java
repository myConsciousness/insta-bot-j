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

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoFollowTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoForecastFollowBackUserTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoLikeTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoScrapeUserProfileTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteAutoUnfollowTasklet;
import org.thinkit.bot.instagram.batch.tasklet.ExecuteLoginTasklet;
import org.thinkit.bot.instagram.batch.tasklet.InitializeTasklet;
import org.thinkit.bot.instagram.batch.tasklet.NotifyResultTasklet;
import org.thinkit.bot.instagram.batch.tasklet.UpdateAutoForecastFollowBackUserConfigTasklet;
import org.thinkit.bot.instagram.batch.tasklet.UpdateAutoLikeConfigTasklet;

@Configuration
public class BatchTaskletConfiguration {

    @Bean
    public Tasklet initialize() {
        return InitializeTasklet.newInstance();
    }

    @Bean
    public Tasklet executeLoginTasklet() {
        return ExecuteLoginTasklet.newInstance();
    }

    @Bean
    public Tasklet updateAutoLikeConfigTasklet() {
        return UpdateAutoLikeConfigTasklet.newInstance();
    }

    @Bean
    public Tasklet executeAutoLikeTasklet() {
        return ExecuteAutoLikeTasklet.newInstance();
    }

    @Bean
    public Tasklet updateAutoForecastFollowBackUserConfigTasklet() {
        return UpdateAutoForecastFollowBackUserConfigTasklet.newInstance();
    }

    @Bean
    public Tasklet executeAutoForecastFollowBackUserTasklet() {
        return ExecuteAutoForecastFollowBackUserTasklet.newInstance();
    }

    @Bean
    public Tasklet executeAutoScrapeUserProfileTasklet() {
        return ExecuteAutoScrapeUserProfileTasklet.newInstance();
    }

    @Bean
    public Tasklet executeAutoFollowTasklet() {
        return ExecuteAutoFollowTasklet.newInstance();
    }

    @Bean
    public Tasklet executeAutoUnfollowTasklet() {
        return ExecuteAutoUnfollowTasklet.newInstance();
    }

    @Bean
    public Tasklet notifyResultTasklet() {
        return NotifyResultTasklet.newInstance();
    }
}
