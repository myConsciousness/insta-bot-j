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

package org.thinkit.bot.instagram.batch.notification.message;

import java.util.List;

import org.thinkit.bot.instagram.batch.catalog.BatchScheduleType;
import org.thinkit.bot.instagram.batch.data.mongo.entity.MessageMeta;
import org.thinkit.bot.instagram.batch.strategy.context.ReportBuildContext;

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
     * The batch schedule type
     */
    private BatchScheduleType batchScheduleType;

    /**
     * The message metas
     */
    private List<MessageMeta> messageMetas;

    @Override
    public String build() {
        return ReportBuildContext.from(batchScheduleType, messageMetas).evaluate();
    }
}
