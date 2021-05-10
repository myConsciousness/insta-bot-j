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

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;

/**
 * The entity that manages follow back possibility indicator.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Data
@Document("follow_back_possibility_indicator")
public final class FollowBackPossibilityIndicator implements Serializable {

    /**
     * The id
     */
    @Getter
    @Indexed(unique = true)
    private String id;

    /**
     * The highest indicator
     */
    @Getter
    private int highestIndicator;

    /**
     * The high indicator
     */
    @Getter
    private int highIndicator;

    /**
     * The middle indicator
     */
    @Getter
    private int middleIndicator;

    /**
     * The low indicator
     */
    @Getter
    private int lowIndicator;

    /**
     * The lowest indicator
     */
    @Getter
    private int lowestIndicator;

    /**
     * The created datetime
     */
    @Getter
    private Date createdAt = new Date();

    /**
     * The updated datetime
     */
    @Getter
    private Date updatedAt = new Date();
}
