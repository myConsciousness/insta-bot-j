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

package org.thinkit.bot.instagram.batch.strategy.report;

import java.io.Serializable;

import com.mongodb.lang.NonNull;

import org.thinkit.bot.instagram.batch.data.mongo.entity.Session;
import org.thinkit.bot.instagram.batch.data.mongo.repository.SessionRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class ReportStartSessionStrategy implements ReportStrategy, Serializable {

    /**
     * The running user name
     */
    private String runningUserName;

    /**
     * The session repository
     */
    private SessionRepository sessionRepository;

    @Override
    public String buildReport() {
        return this.createMessage(this.sessionRepository.findByUserName(this.runningUserName));
    }

    private String createMessage(@NonNull final Session session) {
        return """
                Initialized session.
                -------------------
                Running User
                -------------------
                Name: [%s]
                PID: [%s]
                JVM Name: [%s]
                VM Name: [%s]
                VM Version: [%s]
                VM vendor: [%s]
                Spec Name: [%s]
                Spec Version: [%s]
                        """.formatted(session.getUserName(), session.getPid(), session.getJvmName(),
                session.getVmName(), session.getVmVersion(), session.getVmVendor(), session.getSpecName(),
                session.getSpecVersion());
    }
}
