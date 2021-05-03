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

package org.thinkit.bot.instagram.message;

import static org.thinkit.bot.instagram.util.IndentUtils.newline;
import static org.thinkit.bot.instagram.util.IndentUtils.space;

import java.util.List;

import com.mongodb.lang.NonNull;

import org.thinkit.api.catalog.Catalog;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.content.ActionStatusMessageMapper;
import org.thinkit.bot.instagram.content.LineMessagePhraseMapper;
import org.thinkit.bot.instagram.content.entity.ActionStatusMessage;
import org.thinkit.bot.instagram.content.entity.LineMessagePhrase;
import org.thinkit.bot.instagram.mongo.entity.MessageMeta;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class LineMessageBuilder extends AbstractMessageBuilder {

    /**
     * The message metas
     */
    private List<MessageMeta> messageMetas;

    @Override
    public String build() {

        final StringBuilder message = new StringBuilder(newline());

        for (final MessageMeta messageMeta : this.messageMetas) {
            final TaskType taskType = Catalog.getEnum(TaskType.class, messageMeta.getTaskTypeCode());
            message.append(newline(this.createMessage(taskType, messageMeta)));
        }

        return message.toString();
    }

    private String createMessage(@NonNull final TaskType taskType, @NonNull final MessageMeta messageMeta) {

        final StringBuilder message = new StringBuilder();

        message.append(this.getActionStatusMessage(messageMeta));
        message.append(space());
        message.append(this.getTaskMessage(taskType, messageMeta));

        return message.toString();
    }

    private String getTaskMessage(@NonNull final TaskType taskType, @NonNull final MessageMeta messageMeta) {
        final LineMessagePhraseMapper lineMessagePhraseMapper = LineMessagePhraseMapper.from(taskType.getCode());
        final LineMessagePhrase lineMessagePhrase = lineMessagePhraseMapper.scan().get(0);

        return String.format(lineMessagePhrase.getPhrase(), messageMeta.getCount());
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
