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

import java.util.List;

import org.thinkit.bot.instagram.batch.catalog.BatchScheduleType;
import org.thinkit.bot.instagram.batch.data.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.batch.strategy.report.CloseSessionReportBuildStrategy;
import org.thinkit.bot.instagram.batch.strategy.report.InitializeSessionReportBuildStrategy;
import org.thinkit.bot.instagram.batch.strategy.report.MainStreamReportBuildStrategy;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(staticName = "from")
public final class ReportBuildContext implements Context<String> {

    /**
     * The batch schedule type
     */
    private BatchScheduleType batchScheduleType;

    /**
     * The message metas
     */
    private List<MessageMeta> messageMetas;

    @Override
    public String evaluate() {
        return switch (batchScheduleType) {
            case INITIALIZE_SESSION -> InitializeSessionReportBuildStrategy.newInstance().buildReport(messageMetas);
            case MAIN_STREAM -> MainStreamReportBuildStrategy.newInstance().buildReport(messageMetas);
            case CLOSE_SESSION -> CloseSessionReportBuildStrategy.newInstance().buildReport(messageMetas);
        };
    }
}
