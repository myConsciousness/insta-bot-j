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

package org.thinkit.bot.instagram.batch;

import java.io.Serializable;

import org.thinkit.bot.instagram.catalog.TaskType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The class that manages task.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class Task implements Serializable {

    /**
     * The task type
     */
    private TaskType taskType;

    public int getTypeCode() {
        return this.taskType.getCode();
    }

    public boolean canSendResultMessage() {
        return this.taskType == TaskType.AUTO_LIKE || this.taskType == TaskType.FORECAST_FOLLOW_BACK_USER;
    }

    public boolean isRestrictable() {
        return this.taskType == TaskType.REVERSAL_ENTRY_HASHTAG || this.taskType == TaskType.AUTO_LIKE
                || this.taskType == TaskType.AUTO_FOLLOW || this.taskType == TaskType.AUTO_UNFOLLOW;
    }

    public boolean canSkip() {
        return this.taskType == TaskType.AUTO_LIKE;
    }
}
