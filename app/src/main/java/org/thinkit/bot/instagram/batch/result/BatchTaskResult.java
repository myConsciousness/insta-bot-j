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

package org.thinkit.bot.instagram.batch.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.lang.NonNull;

import org.springframework.batch.repeat.RepeatStatus;
import org.thinkit.bot.instagram.catalog.ActionStatus;
import org.thinkit.bot.instagram.result.ActionError;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The class that manages the result of batch task.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class BatchTaskResult implements Serializable {

    /**
     * The count attempt
     */
    @Getter
    private int countAttempt;

    /**
     * The action status
     */
    @Getter
    @NonNull
    @Builder.Default
    private ActionStatus actionStatus = ActionStatus.COMPLETED;

    /**
     * The repeat status
     */
    @Getter
    @NonNull
    @Builder.Default
    private RepeatStatus repeatStatus = RepeatStatus.FINISHED;

    /**
     * The action errors
     */
    @Getter
    @NonNull
    @Builder.Default
    private List<ActionError> actionErrors = new ArrayList<>(0);
}
