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

import org.thinkit.bot.instagram.batch.catalog.BatchScheduleType;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.strategy.report.ReportCloseSessionStrategy;
import org.thinkit.bot.instagram.batch.strategy.report.ReportMainStreamStrategy;
import org.thinkit.bot.instagram.batch.strategy.report.ReportStartSessionStrategy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class ReportContext implements Context<String> {

    /**
     * The batch schedule type
     */
    private BatchScheduleType batchScheduleType;

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
        return switch (batchScheduleType) {
            case START_SESSION -> ReportStartSessionStrategy
                    .from(this.runningUserName, mongoCollections.getSessionRepository()).buildReport();
            case MAIN_STREAM -> ReportMainStreamStrategy
                    .from(this.runningUserName, this.mongoCollections.getMessageMetaRepository()).buildReport();
            case CLOSE_SESSION -> ReportCloseSessionStrategy.from(this.runningUserName, this.mongoCollections)
                    .buildReport();
        };
    }
}
