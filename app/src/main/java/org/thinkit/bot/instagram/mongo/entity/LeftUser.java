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

package org.thinkit.bot.instagram.mongo.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * The entity that manages the left user.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Data
@Document("left_user")
public final class LeftUser implements Serializable {

    /**
     * The id
     */
    @Indexed(unique = true)
    private String id;

    /**
     * The user name
     */
    @Indexed(unique = true)
    private String userName;

    /**
     * The created datetime
     */
    private Date createdAt;

    /**
     * The updated datetime
     */
    private Date updatedAt;
}
