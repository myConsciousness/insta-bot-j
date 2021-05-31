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
import org.thinkit.bot.instagram.batch.data.mongo.entity.DailyActionTotal;

/**
 * The interface that manages daily action total repository.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Repository
public interface DailyActionTotalRepository extends MongoRepository<DailyActionTotal, String> {

    /**
     * Returns the daily action total linked to the {@code taskTypeCode} ,
     * {@code year} , {@code month} and {@code day} passed as arguments.
     *
     * @param taskTypeCode The task type code
     * @param year         The year
     * @param month        The month
     * @param day          The day
     * @return The daily action total
     */
    public DailyActionTotal findByTaskTypeCodeAndYearAndMonthAndDay(int taskTypeCode, int year, int month, int day);
}
