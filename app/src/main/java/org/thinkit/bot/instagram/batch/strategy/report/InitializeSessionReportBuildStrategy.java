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

package org.thinkit.bot.instagram.batch.strategy.report;

import static org.thinkit.bot.instagram.util.IndentUtils.newline;

import java.io.Serializable;
import java.util.List;

import org.thinkit.api.catalog.Catalog;
import org.thinkit.bot.instagram.batch.data.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.batch.policy.BatchTask;
import org.thinkit.bot.instagram.catalog.TaskType;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(staticName = "newInstance")
public final class InitializeSessionReportBuildStrategy implements ReportBuildStrategy, Serializable {

    @Override
    public String buildReport(@NonNull final List<MessageMeta> messageMetas) {

        final StringBuilder message = new StringBuilder(newline());

        for (final MessageMeta messageMeta : messageMetas) {
            final BatchTask batchTask = BatchTask.from(Catalog.getEnum(TaskType.class, messageMeta.getTaskTypeCode()));

            if (batchTask.isInitializeSessionTask()) {
                message.append(newline(this.createMessage(batchTask, messageMeta)));
            }
        }

        return message.toString();
    }

    private String createMessage(@NonNull final BatchTask batchTask, @NonNull final MessageMeta messageMeta) {
        return "Initialized session.";
    }
}
