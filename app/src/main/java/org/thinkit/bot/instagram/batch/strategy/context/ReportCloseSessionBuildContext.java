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

import org.thinkit.api.catalog.Catalog;
import org.thinkit.bot.instagram.batch.catalog.BatchCloseSessionFlowStrategyPattern;
import org.thinkit.bot.instagram.batch.catalog.VariableName;
import org.thinkit.bot.instagram.batch.data.mongo.entity.Variable;
import org.thinkit.bot.instagram.batch.data.mongo.repository.VariableRepository;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.strategy.report.ReportCloseSessionBuildStrategy;
import org.thinkit.bot.instagram.batch.strategy.report.ReportContinueSessionBuildStrategy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class ReportCloseSessionBuildContext implements Context<String> {

    /**
     * The running user name
     */
    private String runningUserName;

    /**
     * The mongo collections
     */
    private MongoCollections mongoCollections;

    @Override
    public String evaluate() {
        return switch (this.getBatchCloseSessionFlowStrategyPattern()) {
            case CONTINUE -> ReportContinueSessionBuildStrategy
                    .from(this.runningUserName, this.mongoCollections.getDailyActionTotalRepository()).build();
            case FINISH -> ReportCloseSessionBuildStrategy
                    .from(this.runningUserName, this.mongoCollections.getDailyActionTotalRepository()).build();
        };
    }

    private BatchCloseSessionFlowStrategyPattern getBatchCloseSessionFlowStrategyPattern() {

        final VariableRepository variableRepository = this.mongoCollections.getVariableRepository();
        final Variable variable = variableRepository
                .findByName(VariableName.BATCH_SESSION_CLOSE_FLOW_STRATEGY.getTag());

        if (variable == null) {
            throw new IllegalStateException("Could not detect the batch session close flow strategy.");
        }

        return Catalog.getEnum(BatchCloseSessionFlowStrategyPattern.class, Integer.parseInt(variable.getValue()));
    }
}
