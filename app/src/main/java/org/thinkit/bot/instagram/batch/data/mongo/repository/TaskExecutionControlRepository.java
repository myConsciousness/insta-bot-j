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

package org.thinkit.bot.instagram.batch.data.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.thinkit.bot.instagram.batch.data.mongo.entity.TaskExecutionControl;

/**
 * @author Kato Shinya
 * @since 1.0.0
 */
@Repository
public interface TaskExecutionControlRepository extends MongoRepository<TaskExecutionControl, String> {

    /**
     * Returns the task execution control based on charge user name and task type
     * code passed as argument.
     *
     * @param chargeUserName The charge user name
     * @param taskTypeCode   The task type code
     * @return The task execution control
     */
    public TaskExecutionControl findByChargeUserNameAndTaskTypeCode(String chargeUserName, int taskTypeCode);
}
