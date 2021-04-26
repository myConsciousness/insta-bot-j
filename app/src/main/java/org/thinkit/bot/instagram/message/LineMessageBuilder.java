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

            if (taskType == TaskType.AUTO_LIKE) {
                message.append(newline(this.createMessageAutoLike(messageMeta)));
            }
        }

        return message.toString();
    }

    private String createMessageAutoLike(@NonNull final MessageMeta messageMeta) {

        final StringBuilder messageAutoLike = new StringBuilder();

        if (messageMeta.isInterrupted()) {
            messageAutoLike.append("[INTERRUPTED]");
        } else {
            messageAutoLike.append("[COMPLETED]");
        }

        messageAutoLike.append(space());
        messageAutoLike.append(String.format("AutoLike executed for %s photos.", messageMeta.getCountAttempt()));

        return messageAutoLike.toString();
    }
}
