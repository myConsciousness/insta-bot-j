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

package org.thinkit.bot.instagram.batch.dto;

import java.io.Serializable;

import org.thinkit.bot.instagram.batch.data.mongo.repository.ActionRecordRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.ActionRestrictionRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.ActionSkipRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.DailyActionTotalRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.ErrorRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowBackExpectableUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowBackPossibilityIndicatorRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowMutualUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowUnmutualUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.FollowedUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.HashtagRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.LastActionRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.LeftUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.LikedPhotoRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.MessageMetaRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.MissingUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.SessionRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.TaskExecutionControlRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.UnfollowedUserRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.UserAccountRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.UserFollowerRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.UserFollowingRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.VariableRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The class that manages collections of MongoDB.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MongoCollections implements Serializable {

    /**
     * The user account repository
     */
    @Getter
    private UserAccountRepository userAccountRepository;

    /**
     * The hashtag repository
     */
    @Getter
    private HashtagRepository hashtagRepository;

    /**
     * The liked photo repository
     */
    @Getter
    private LikedPhotoRepository likedPhotoRepository;

    /**
     * The error repository
     */
    @Getter
    private ErrorRepository errorRepository;

    /**
     * The action restriction repository
     */
    @Getter
    private ActionRestrictionRepository actionRestrictionRepository;

    /**
     * The action record repository
     */
    @Getter
    private ActionRecordRepository actionRecordRepository;

    /**
     * The action skip repository
     */
    @Getter
    private ActionSkipRepository actionSkipRepository;

    /**
     * The last action
     */
    @Getter
    private LastActionRepository lastActionRepository;

    /**
     * The variable repository
     */
    @Getter
    private VariableRepository variableRepository;

    /**
     * The message meta repository
     */
    @Getter
    private MessageMetaRepository messageMetaRepository;

    /**
     * The follow back expectable user repository
     */
    @Getter
    private FollowBackExpectableUserRepository followBackExpectableUserRepository;

    /**
     * The follow back possibility indicator repository
     */
    @Getter
    private FollowBackPossibilityIndicatorRepository followBackPossibilityIndicatorRepository;

    /**
     * The missing user repository
     */
    @Getter
    private MissingUserRepository missingUserRepository;

    /**
     * The followed user repository
     */
    @Getter
    private FollowedUserRepository followedUserRepository;

    /**
     * The unfollowed user repository
     */
    @Getter
    private UnfollowedUserRepository unfollowedUserRepository;

    /**
     * The user follower repository
     */
    @Getter
    private UserFollowerRepository userFollowerRepository;

    /**
     * The user following repository
     */
    @Getter
    private UserFollowingRepository userFollowingRepository;

    /**
     * The left user repository
     */
    @Getter
    private LeftUserRepository leftUserRepository;

    /**
     * The session repository
     */
    @Getter
    private SessionRepository sessionRepository;

    /**
     * The task execution control repository
     */
    @Getter
    private TaskExecutionControlRepository taskExecutionControlRepository;

    /**
     * The follow mutual repository
     */
    @Getter
    private FollowMutualUserRepository followMutualUserRepository;

    /**
     * The follow unmutual repository
     */
    @Getter
    private FollowUnmutualUserRepository followUnmutualUserRepository;

    /**
     * The daily action total repository
     */
    @Getter
    private DailyActionTotalRepository dailyActionTotalRepository;
}
