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
import static org.thinkit.bot.instagram.util.IndentUtils.space;

import java.io.Serializable;
import java.util.List;

import org.thinkit.api.catalog.Catalog;
import org.thinkit.bot.instagram.batch.data.content.entity.ActionStatusMessage;
import org.thinkit.bot.instagram.batch.data.content.entity.LineMessagePhrase;
import org.thinkit.bot.instagram.batch.data.content.entity.TaskName;
import org.thinkit.bot.instagram.batch.data.content.mapper.ActionStatusMessageMapper;
import org.thinkit.bot.instagram.batch.data.content.mapper.LineMessagePhraseMapper;
import org.thinkit.bot.instagram.batch.data.content.mapper.TaskNameMapper;
import org.thinkit.bot.instagram.batch.data.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.batch.policy.BatchTask;
import org.thinkit.bot.instagram.catalog.TaskType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class MainStreamReportBuildStrategy implements ReportBuildStrategy, Serializable {

    /**
     * The running user name
     */
    private String runningUserName;

    /**
     * The mongo collections
     */
    private MongoCollections mongoCollections;

    @Override
    public String buildReport() {

        final StringBuilder message = new StringBuilder(newline());
        final List<MessageMeta> messageMetas = this.mongoCollections.getMessageMetaRepository()
                .findByChargeUserNameAndAlreadySentFalse(this.runningUserName);

        for (final MessageMeta messageMeta : messageMetas) {
            final BatchTask batchTask = BatchTask.from(Catalog.getEnum(TaskType.class, messageMeta.getTaskTypeCode()));

            if (batchTask.isMainStreamTask()) {
                message.append(newline(this.createMessage(batchTask, messageMeta)));
            }
        }

        return message.toString();
    }

    private String createMessage(@NonNull final BatchTask batchTask, @NonNull final MessageMeta messageMeta) {

        final StringBuilder message = new StringBuilder();

        message.append(this.getActionStatusMessage(batchTask, messageMeta));
        message.append(space());
        message.append(this.getTaskMessage(batchTask, messageMeta));

        return message.toString();
    }

    private String getTaskMessage(@NonNull final BatchTask batchTask, @NonNull final MessageMeta messageMeta) {
        final LineMessagePhraseMapper lineMessagePhraseMapper = LineMessagePhraseMapper.from(batchTask.getTypeCode());
        final LineMessagePhrase lineMessagePhrase = lineMessagePhraseMapper.scan().get(0);

        return String.format(lineMessagePhrase.getPhrase(), messageMeta.getCount());
    }

    private String getActionStatusMessage(@NonNull final BatchTask batchTask, @NonNull final MessageMeta messageMeta) {
        final TaskName taskName = TaskNameMapper.from(batchTask.getTypeCode()).scan().get(0);
        return String.format(this.getActionStatusMessage(messageMeta), taskName.getName());
    }

    private String getActionStatusMessage(@NonNull final MessageMeta messageMeta) {

        final ActionStatusMessage actionStatusMessage = ActionStatusMessageMapper.newInstance().scan().get(0);

        if (messageMeta.isInterrupted()) {
            return actionStatusMessage.getInterrupted();
        } else if (messageMeta.isSkipped()) {
            return actionStatusMessage.getSkipped();
        } else if (messageMeta.isSkippedByMood()) {
            return actionStatusMessage.getSkippedByMood();
        }

        return actionStatusMessage.getCompleted();
    }
}
