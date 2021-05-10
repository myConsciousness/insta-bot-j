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

package org.thinkit.bot.instagram.batch.notification.http;

import com.google.api.client.http.GenericUrl;

import lombok.NonNull;

public interface Communicator {

    /**
     * Sends a post request to the request URL.
     *
     * @param genericUrl The request url object
     * @param message    The message
     *
     * @exception NullPointerException if {@code null} is passed as an argument
     */
    public void post(@NonNull final GenericUrl genericUrl, @NonNull final String message);
}
