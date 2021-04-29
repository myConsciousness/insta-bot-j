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

package org.thinkit.bot.instagram.batch.strategy.flow;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.catalog.BatchJob;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(staticName = "newInstance")
@Component
public final class RestrictableBatchFlowStrategy implements BatchFlowStrategy {

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

    @Override
    public FlowBuilder<FlowJobBuilder> createLoginJobFlowBuilder() {
        return this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag()).flow(this.executeLoginStep)
                .next(this.reversalEntryHashtagStep).next(this.executeAutoLikeStep)
                .next(this.forecastFollowBackUserStep).next(this.notifyResultStep);
    }

    @Override
    public FlowBuilder<FlowJobBuilder> createJobFlowBuilder() {
        return this.jobBuilderFactory.get(BatchJob.INSTA_BOT.getTag()).flow(this.reversalEntryHashtagStep)
                .next(this.executeAutoLikeStep).next(this.forecastFollowBackUserStep).next(this.notifyResultStep);
    }
}
