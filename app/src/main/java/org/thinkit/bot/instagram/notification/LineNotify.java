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

package org.thinkit.bot.instagram.notification;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;

import org.thinkit.bot.instagram.catalog.NotificationApi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class LineNotify implements Notification, Serializable {

    /**
     * The token
     */
    @ToString.Exclude
    private String token;

    @Override
    public boolean sendMessage(String message) {

        HttpURLConnection httpURLConnection = null;

        try {
            final URL url = new URL(NotificationApi.LINE_NOTIFY.getTag());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.addRequestProperty("Authorization", "Bearer " + token);

            try (final OutputStream outputStream = httpURLConnection.getOutputStream();
                    final PrintWriter writer = new PrintWriter(outputStream)) {
                writer.append("message=").append(URLEncoder.encode(message, "UTF-8")).flush();

                try (final InputStream inputStream = httpURLConnection.getInputStream();
                        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String res = bufferedReader.lines().collect(Collectors.joining());

                    if (!res.contains("\"message\":\"ok\"")) {
                    }
                }
            }
        } catch (Exception ignore) {
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return true;
    }
}
