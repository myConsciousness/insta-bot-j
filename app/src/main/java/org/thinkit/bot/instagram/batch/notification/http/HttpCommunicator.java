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

import java.io.IOException;
import java.io.Serializable;

import com.google.api.client.http.GenericUrl;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * The class that provides general-purpose processing for HTTP communication.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public final class HttpCommunicator extends AbstractCommunicator implements Serializable {

    private HttpCommunicator(@NonNull final String token) {
        super(token);
    }

    public static Communicator from(@NonNull final String token) {
        return new HttpCommunicator(token);
    }

    @Override
    public void post(@NonNull GenericUrl genericUrl, @NonNull final String message) {
        try {
            super.postRequest(genericUrl, message);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
