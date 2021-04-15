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

package org.thinkit.bot.instagram.repository.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;

import lombok.Data;

/**
 * The entity that manages liked photo.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Data
public final class LikedPhoto implements Serializable {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 239353027201994251L;

    /**
     * The id
     */
    @Id
    private int id;

    /**
     * The user name
     */
    private String userName;

    /**
     * The url
     */
    private String url;

    /**
     * The created by
     */
    private String createdBy;

    /**
     * The created at
     */
    private Timestamp createdAt;

    /**
     * The updated by
     */
    private String updatedBy;

    /**
     * The updated by
     */
    private Timestamp updatedAt;
}
