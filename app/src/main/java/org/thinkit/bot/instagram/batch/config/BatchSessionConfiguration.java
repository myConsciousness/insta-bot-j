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

package org.thinkit.bot.instagram.batch.config;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.exception.AvailableUserAccountNotFoundException;
import org.thinkit.bot.instagram.mongo.entity.Session;
import org.thinkit.bot.instagram.mongo.entity.UserAccount;
import org.thinkit.bot.instagram.mongo.repository.SessionRepository;

/**
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
public class BatchSessionConfiguration {

    /**
     * The mongo collection
     */
    @Autowired
    private MongoCollections mongoCollections;

    /**
     * The user account
     */
    @Autowired
    private UserAccount userAccount;

    @Bean
    public UserAccount userAccount() {
        final List<UserAccount> userAccounts = this.mongoCollections.getUserAccountRepository().findAll();
        final SessionRepository sessionRepository = this.mongoCollections.getSessionRepository();

        for (final UserAccount userAccount : userAccounts) {
            Session session = sessionRepository.findByUserName(userAccount.getUserName());

            if (session == null) {
                session = new Session();
                session.setUserName(userAccount.getUserName());
                session = sessionRepository.insert(session);
            }

            if (!session.isRunning()) {
                session.setRunning(true);
                sessionRepository.save(session);

                return userAccount;
            }
        }

        throw new AvailableUserAccountNotFoundException(
                "All registered user accounts are running, or there are no valid user accounts registered.");
    }

    @Scheduled(cron = "${spring.batch.schedule.session.cron}", zone = "${spring.batch.schedule.timezone}")
    public void performScheduledRefreshSession() throws Exception {
        final SessionRepository sessionRepository = this.mongoCollections.getSessionRepository();
        final Session session = sessionRepository.findByUserName(this.userAccount.getUserName());

        session.setRunning(false);
        session.setUpdatedAt(new Date());
        sessionRepository.save(session);

        System.exit(0);
    }
}
