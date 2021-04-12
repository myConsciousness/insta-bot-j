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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The entity that manages liked photo.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class LikedPhoto implements Serializable {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 239353027201994251L;

    /**
     * The user name
     */
    @Getter
    private String userName;

    /**
     * The url
     */
    @Getter
    private String url;

    /**
     * The created by
     */
    @Getter
    private String createdBy;

    /**
     * The created at
     */
    @Getter
    private Timestamp createdAt;

    /**
     * The updated by
     */
    @Getter
    private String updatedBy;

    /**
     * The updated by
     */
    @Getter
    private Timestamp updatedAt;
}
