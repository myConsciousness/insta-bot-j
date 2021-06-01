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

package org.thinkit.bot.instagram.batch.data.mongo.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * The entity that manages daily action total.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Data
@Document("daily_action_total")
public final class DailyActionTotal implements Serializable {

    /**
     * The id
     */
    @Id
    @Indexed(unique = true)
    private String id;

    /**
     * The task type code
     */
    @Indexed
    private int taskTypeCode;

    /**
     * The total
     */
    private int total;

    /**
     * The year
     */
    private int year;

    /**
     * The month
     */
    private int month;

    /**
     * The day
     */
    private int day;

    /**
     * The created datetime
     */
    private Date createdAt = new Date();

    /**
     * The updated datetime
     */
    private Date updatedAt = new Date();
}
