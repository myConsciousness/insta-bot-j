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

package org.thinkit.bot.instagram.batch.strategy.context;

import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.thinkit.bot.instagram.batch.catalog.BatchStartSessionFlowStrategyPattern;
import org.thinkit.bot.instagram.batch.dto.BatchStepCollections;
import org.thinkit.bot.instagram.batch.strategy.flow.BatchStartSessionLoggedInFlowStrategy;
import org.thinkit.bot.instagram.batch.strategy.flow.BatchStartSessionNotLoggedInFlowStrategy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class BatchStartSessionFlowContext implements Context<FlowBuilder<FlowJobBuilder>> {

    /**
     * The batch start session flow strategy pattern
     */
    private BatchStartSessionFlowStrategyPattern batchStartSessionFlowStrategyPattern;

    /**
     * The job builder
     */
    private JobBuilder jobBuilder;

    /**
     * The batch step collections
     */
    private BatchStepCollections batchStepCollections;

    @Override
    public FlowBuilder<FlowJobBuilder> evaluate() {
        return switch (this.batchStartSessionFlowStrategyPattern) {
            case NOT_LOGGED_IN -> BatchStartSessionNotLoggedInFlowStrategy.newInstance()
                    .createJobFlowBuilder(jobBuilder, batchStepCollections);
            case LOGGED_IN -> BatchStartSessionLoggedInFlowStrategy.newInstance().createJobFlowBuilder(jobBuilder,
                    batchStepCollections);
        };
    }
}
