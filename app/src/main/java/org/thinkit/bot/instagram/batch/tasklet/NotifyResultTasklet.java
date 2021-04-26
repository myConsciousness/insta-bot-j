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

import com.mongodb.lang.NonNull;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.thinkit.bot.instagram.batch.MongoCollection;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.catalog.VariableName;
import org.thinkit.bot.instagram.notification.LineNotify;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
public final class NotifyResultTasklet extends AbstractTasklet {

    private NotifyResultTasklet(@NonNull final MongoCollection mongoCollection) {
        super(TaskType.NOTIFY_RESULT, mongoCollection);
    }

    public static Tasklet from(@NonNull final MongoCollection mongoCollection) {
        return new NotifyResultTasklet(mongoCollection);
    }

    @Override
    protected RepeatStatus executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        final String token = super.getMongoCollection().getVariableRepository()
                .findByName(VariableName.LINE_NOTIFY_TOKEN.getTag()).getValue();
        LineNotify.from(token).sendMessage("completed!");

        log.debug("END");
        return RepeatStatus.FINISHED;
    }
}
