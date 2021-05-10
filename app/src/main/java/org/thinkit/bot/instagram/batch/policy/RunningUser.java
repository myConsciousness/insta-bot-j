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

package org.thinkit.bot.instagram.batch.policy;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.exception.SessionInconsistencyFoundException;
import org.thinkit.bot.instagram.mongo.entity.Session;
import org.thinkit.bot.instagram.mongo.entity.UserAccount;
import org.thinkit.bot.instagram.mongo.repository.SessionRepository;
import org.thinkit.bot.instagram.mongo.repository.UserAccountRepository;
import org.thinkit.bot.instagram.util.RuntimeMxUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@Component
public final class RunningUser implements Serializable {

    /**
     * The session
     */
    @Getter
    private final Session session;

    /**
     * The user account
     */
    @Getter
    private final UserAccount userAccount;

    /**
     * The user acccount repository
     */
    @Autowired
    private UserAccountRepository userAccountRepository;

    /**
     * The session repository
     */
    @Autowired
    private SessionRepository sessionRepository;

    private RunningUser() {
        log.debug("START");

        final List<UserAccount> userAccounts = this.userAccountRepository.findAll();

        for (final UserAccount userAccount : userAccounts) {
            Session session = sessionRepository.findByUserName(userAccount.getUserName());

            if (session == null) {
                session = new Session();
                session.setUserName(userAccount.getUserName());
                session = sessionRepository.insert(session);
            }

            if (!session.isRunning()) {
                session.setRunning(true);
                session.setPid(RuntimeMxUtils.getPid());
                session.setJvmName(RuntimeMxUtils.getJvmName());
                session.setVmName(RuntimeMxUtils.getVmName());
                session.setVmVersion(RuntimeMxUtils.getVmVersion());
                session.setVmVendor(RuntimeMxUtils.getVmVendor());
                session.setSpecName(RuntimeMxUtils.getSpecName());
                session.setSpecVersion(RuntimeMxUtils.getSpecVersion());
                session.setManagementSpecVersion(RuntimeMxUtils.getManagementSpecVersion());
                session.setInputArgs(RuntimeMxUtils.getInputArgs());
                session.setClassPath(RuntimeMxUtils.getClassPath());
                session.setLibraryPath(RuntimeMxUtils.getLibraryPath());
                session.setUpdatedAt(new Date());
                sessionRepository.save(session);

                this.session = session;
                this.userAccount = userAccount;

                log.info("Successed to create session.");
                log.debug("The running session: {}", session);
                log.debug("The running user account", userAccount);
                log.debug("END");
                return;
            }
        }

        // When there is no available user
        this.session = null;
        this.userAccount = null;

        log.info("Failed to create session.");
        log.debug("END");
    }

    public static RunningUser newInstance() {
        return new RunningUser();
    }

    public void closeSession() {
        log.debug("START");

        if (this.session == null) {
            throw new SessionInconsistencyFoundException(
                    String.format("Could not find the session linked to the user [%s].", this.userAccount));
        }

        this.clearSession(session);

        sessionRepository.save(session);
        log.debug("Updated session: {}", session);

        log.info("The session was closed successfully.");
        log.debug("END");
    }

    public boolean isAvailable() {
        return this.session != null && this.userAccount != null;
    }

    public String getUserName() {
        return this.userAccount.getUserName();
    }

    public String getPassword() {
        return this.userAccount.getPassword();
    }

    private void clearSession(@NonNull final Session session) {
        log.debug("START");

        session.setRunning(false);
        session.setPid(0L);
        session.setJvmName("");
        session.setVmName("");
        session.setVmVersion("");
        session.setVmVendor("");
        session.setSpecName("");
        session.setSpecVersion("");
        session.setManagementSpecVersion("");
        session.setInputArgs("");
        session.setClassPath("");
        session.setLibraryPath("");
        session.setUpdatedAt(new Date());

        log.debug("END");
    }
}
