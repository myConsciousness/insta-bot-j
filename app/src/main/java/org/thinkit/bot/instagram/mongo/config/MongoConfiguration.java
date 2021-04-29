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

package org.thinkit.bot.instagram.mongo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.thinkit.bot.instagram.catalog.MongoDatabase;
import org.thinkit.bot.instagram.mongo.MongoCollection;
import org.thinkit.bot.instagram.mongo.repository.ActionRecordRepository;
import org.thinkit.bot.instagram.mongo.repository.ActionRestrictionRepository;
import org.thinkit.bot.instagram.mongo.repository.ErrorRepository;
import org.thinkit.bot.instagram.mongo.repository.FollowBackExpectableUserRepository;
import org.thinkit.bot.instagram.mongo.repository.HashtagRepository;
import org.thinkit.bot.instagram.mongo.repository.LastActionRepository;
import org.thinkit.bot.instagram.mongo.repository.LikedPhotoRepository;
import org.thinkit.bot.instagram.mongo.repository.MessageMetaRepository;
import org.thinkit.bot.instagram.mongo.repository.UserAccountRepository;
import org.thinkit.bot.instagram.mongo.repository.VariableRepository;

/**
 * The configuration class for MongoDB.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    /**
     * The variable repository
     */
    @Autowired
    private VariableRepository variableRepository;

    /**
     * The user account repository
     */
    @Autowired
    private UserAccountRepository userAccountRepository;

    /**
     * The hashtag repository
     */
    @Autowired
    private HashtagRepository hashtagRepository;

    /**
     * The liked photo repository
     */
    @Autowired
    private LikedPhotoRepository likedPhotoRepository;

    /**
     * The error repository
     */
    @Autowired
    private ErrorRepository errorRepository;

    /**
     * The action restriction repository
     */
    @Autowired
    private ActionRestrictionRepository actionRestrictionRepository;

    /**
     * The action record repository
     */
    @Autowired
    private ActionRecordRepository actionRecordRepository;

    /**
     * The last action repository
     */
    @Autowired
    private LastActionRepository lastActionRepository;

    /**
     * The message meta repository
     */
    @Autowired
    private MessageMetaRepository messageMetaRepository;

    /**
     * The follow bacl expectable user repository
     */
    @Autowired
    private FollowBackExpectableUserRepository followBackExpectableUserRepository;

    @Override
    protected String getDatabaseName() {
        return MongoDatabase.INSTAGRAM.getTag();
    }

    @Bean
    public MongoCollection mongoCollection() {

        final MongoCollection.MongoCollectionBuilder mongoCollectionBuilder = MongoCollection.builder();
        mongoCollectionBuilder.userAccountRepository(this.userAccountRepository);
        mongoCollectionBuilder.variableRepository(this.variableRepository);
        mongoCollectionBuilder.hashtagRepository(this.hashtagRepository);
        mongoCollectionBuilder.likedPhotoRepository(this.likedPhotoRepository);
        mongoCollectionBuilder.errorRepository(this.errorRepository);
        mongoCollectionBuilder.actionRestrictionRepository(this.actionRestrictionRepository);
        mongoCollectionBuilder.actionRecordRepository(this.actionRecordRepository);
        mongoCollectionBuilder.lastActionRepository(this.lastActionRepository);
        mongoCollectionBuilder.messageMetaRepository(this.messageMetaRepository);
        mongoCollectionBuilder.followBackExpectableUserRepository(this.followBackExpectableUserRepository);

        return mongoCollectionBuilder.build();
    }
}
