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

package org.thinkit.bot.instagram.http;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.thinkit.api.catalog.BiCatalog;
import org.thinkit.bot.instagram.catalog.ErrorHttpStatus;
import org.thinkit.bot.instagram.exception.http.AccessForbiddenException;
import org.thinkit.bot.instagram.exception.http.BadGatewayException;
import org.thinkit.bot.instagram.exception.http.BadRequestException;
import org.thinkit.bot.instagram.exception.http.InternalServerErrorException;
import org.thinkit.bot.instagram.exception.http.NotAcceptableException;
import org.thinkit.bot.instagram.exception.http.NotFoundException;
import org.thinkit.bot.instagram.exception.http.ServiceUnavailableException;
import org.thinkit.bot.instagram.exception.http.UserUnauthorizedException;
import org.thinkit.bot.instagram.util.SecuritySchemeUtils;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
public abstract class AbstractCommunicator implements Communicator {

    /**
     * The http request factory
     */
    private static final HttpRequestFactory HTTP_REQUEST_FACTORY = (new NetHttpTransport()).createRequestFactory();

    /**
     * The token
     */
    private String token;

    /**
     * Sends a POST request to the URL set in the URL object passed as an argument.
     *
     * @param genericUrl The API URL
     * @return The HTTP response
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     * @throws IOException If an error occurs during HTTP communication
     */
    protected HttpResponse postRequest(@NonNull final GenericUrl genericUrl) throws IOException {
        final HttpRequest httpRequest = HTTP_REQUEST_FACTORY.buildPostRequest(genericUrl, null);
        httpRequest.getHeaders().setAuthorization(SecuritySchemeUtils.bearer(token));
        return this.checkHttpStatus(httpRequest.execute());
    }

    /**
     * Returns the http status message from HTTP response.
     *
     * @param httpResponse The HTTP response
     * @return The HTTP status message
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    protected String getStatusMessage(@NonNull final HttpResponse httpResponse) {
        return httpResponse.getStatusMessage();
    }

    /**
     * Check the status code of the HTTP response.
     *
     * <p>
     * The exception will always be raised at runtime if a status code indicating a
     * client error and a server error is detected. If no error is detected, the
     * HTTP response passed as an argument will be returned as is.
     *
     * @param httpResponse The http response
     * @return The http response passed as an argument
     *
     * @exception NullPointerException If {@code null} is passed as an argument
     */
    private HttpResponse checkHttpStatus(@NonNull final HttpResponse httpResponse) {

        final ErrorHttpStatus errorHttpStatus = BiCatalog.getEnumByTag(ErrorHttpStatus.class,
                httpResponse.getStatusCode());

        if (errorHttpStatus == null) {
            return httpResponse;
        }

        switch (errorHttpStatus) {
        case BAD_REQUEST -> throw new BadRequestException(this.getStatusMessage(httpResponse));
        case UNAUTHORIZED -> throw new UserUnauthorizedException(this.getStatusMessage(httpResponse));
        case FORBIDDEN -> throw new AccessForbiddenException(this.getStatusMessage(httpResponse));
        case NOT_FOUND -> throw new NotFoundException(this.getStatusMessage(httpResponse));
        case NOT_ACCEPTABLE -> throw new NotAcceptableException(this.getStatusMessage(httpResponse));
        case INTERNAL_SERVER_ERROR -> throw new InternalServerErrorException(this.getStatusMessage(httpResponse));
        case BAD_GATEWAY -> throw new BadGatewayException(this.getStatusMessage(httpResponse));
        case SERVICE_UNAVAILABLE -> throw new ServiceUnavailableException(this.getStatusMessage(httpResponse));
        default -> throw new IllegalStateException(); // It will never happen
        }
    }
}
