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

package org.thinkit.bot.instagram.batch.tasklet;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.catalog.VariableName;
import org.thinkit.bot.instagram.batch.data.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.batch.data.mongo.repository.MessageMetaRepository;
import org.thinkit.bot.instagram.batch.notification.LineNotify;
import org.thinkit.bot.instagram.batch.notification.message.LineMessageBuilder;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class NotifyResultTasklet extends AbstractTasklet {

    private NotifyResultTasklet() {
        super(TaskType.NOTIFY_RESULT);
    }

    public static Tasklet newInstance() {
        return new NotifyResultTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final MessageMetaRepository messageMetaRepository = this.getMongoCollections().getMessageMetaRepository();
        final List<MessageMeta> messageMetas = messageMetaRepository
                .findByChargeUserNameAndAlreadySentFalse(super.getRunningUserName());

        final String token = super.getMongoCollections().getVariableRepository()
                .findByName(VariableName.LINE_NOTIFY_TOKEN.getTag()).getValue();

        LineNotify.from(token).sendMessage(LineMessageBuilder.from(messageMetas).build());
        log.info("The message has been sent.");

        for (final MessageMeta messageMeta : messageMetas) {
            messageMeta.setAlreadySent(true);
            messageMeta.setUpdatedAt(new Date());
            messageMetaRepository.save(messageMeta);
            log.debug("Updated message meta: {}", messageMeta);
        }

        log.debug("END");
        return BatchTaskResult.builder().actionCount(1).build();
    }
}
