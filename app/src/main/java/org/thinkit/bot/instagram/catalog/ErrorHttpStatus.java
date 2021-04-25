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

package org.thinkit.bot.instagram.catalog;

import org.thinkit.api.catalog.BiCatalog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The catalog that manages error http status.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum ErrorHttpStatus implements BiCatalog<ErrorHttpStatus, Integer> {

    /**
     * Bad Request
     */
    BAD_REQUEST(0, 400),

    /**
     * Unauthorized
     */
    UNAUTHORIZED(1, 401),

    /**
     * Forbidden
     */
    FORBIDDEN(2, 403),

    /**
     * Not found
     */
    NOT_FOUND(3, 404),

    /**
     * Not acceptable
     */
    NOT_ACCEPTABLE(4, 406),

    /**
     * Internal server error
     */
    INTERNAL_SERVER_ERROR(5, 500),

    /**
     * Bad gateway
     */
    BAD_GATEWAY(6, 502),

    /**
     * Service unavailable
     */
    SERVICE_UNAVAILABLE(7, 503);

    /**
     * The code
     */
    @Getter
    private final int code;

    /**
     * The tag
     */
    @Getter
    private final Integer tag;
}
