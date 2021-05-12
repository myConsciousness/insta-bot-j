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

import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.thinkit.bot.instagram.batch.dto.BatchStepCollections;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(staticName = "newInstance")
public final class RestrictableBatchMainStreamFlowStrategy implements BatchFlowStrategy {

    @Override
    public FlowBuilder<FlowJobBuilder> createJobFlowBuilder(@NonNull final JobBuilder jobBuilder,
            @NonNull final BatchStepCollections batchStepCollections) {
        return jobBuilder.flow(batchStepCollections.getUpdateAutoLikeConfigStep())
                .next(batchStepCollections.getExecuteAutoLikeStep())
                .next(batchStepCollections.getUpdateAutoForecastFollowBackUserConfigStep())
                .next(batchStepCollections.getExecuteAutoForecastFollowBackUserStep())
                .next(batchStepCollections.getExecuteAutoFollowStep())
                .next(batchStepCollections.getExecuteAutoUnfollowStep())
                .next(batchStepCollections.getNotifyResultReportStep());
    }
}
