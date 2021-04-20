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

package org.thinkit.bot.instagram.mongo.repository;

import com.mongodb.lang.NonNull;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.thinkit.bot.instagram.mongo.entity.Variable;

/**
 * The interface that manages variable repository.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Repository
public interface VariableRepository extends MongoRepository<Variable, String> {

    /**
     * Returns the {@link Variable} document based on the variable name passed as an
     * argument.
     *
     * @param name The variable name
     * @return The {@link Variable} document
     */
    public Variable findByName(@NonNull String name);
}
