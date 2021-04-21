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

package org.thinkit.bot.instagram.batch.tasklet;

import java.util.Date;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.thinkit.bot.instagram.batch.MongoCollection;
import org.thinkit.bot.instagram.catalog.CommandType;
import org.thinkit.bot.instagram.content.HashtagResourceMapper;
import org.thinkit.bot.instagram.content.entity.HashtagResource;
import org.thinkit.bot.instagram.mongo.entity.Hashtag;
import org.thinkit.bot.instagram.mongo.entity.LastAction;
import org.thinkit.bot.instagram.mongo.repository.HashtagRepository;
import org.thinkit.bot.instagram.mongo.repository.LastActionRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class PrepareAutolikeTasklet implements Tasklet {

    /**
     * The mongo collection
     */
    private MongoCollection mongoCollection;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.debug("START");

        this.reversalEntryHashtag();
        this.updateLastAction();

        log.debug("END");
        return RepeatStatus.FINISHED;
    }

    private void reversalEntryHashtag() {
        log.debug("START");

        final LastActionRepository lastActionRepository = this.mongoCollection.getLastActionRepository();
        LastAction lastAction = lastActionRepository
                .findByCommandTypeCode(CommandType.REVERSAL_ENTRY_HASHTAG.getCode());

        if (lastAction == null) {
            lastAction = new LastAction();
            lastAction.setCommandTypeCode(CommandType.REVERSAL_ENTRY_HASHTAG.getCode());
        }

        lastAction.setStart(new Date());

        this.updateHashtag();

        lastAction.setEnd(new Date());
        lastAction.setUpdatedAt(new Date());

        lastActionRepository.save(lastAction);
        log.debug("Updated last action: {}", lastAction);

        log.debug("END");
    }

    private void updateHashtag() {
        log.debug("START");

        final HashtagRepository hashtagRepository = this.mongoCollection.getHashtagRepository();
        hashtagRepository.deleteAll();

        for (final HashtagResource hashtagResource : HashtagResourceMapper.newInstance().scan()) {
            final Hashtag hashtag = new Hashtag();
            hashtag.setTag(hashtagResource.getTag());
            hashtag.setGroupCode(hashtagResource.getGroupCode());

            hashtagRepository.insert(hashtag);
            log.debug("Inserted hashtag: {}", hashtag);
        }

        log.debug("END");
    }

    private void updateLastAction() {
        log.debug("START");

        final LastActionRepository lastActionRepository = this.mongoCollection.getLastActionRepository();
        LastAction lastAction = lastActionRepository.findByCommandTypeCode(CommandType.AUTO_LIKE.getCode());

        if (lastAction == null) {
            lastAction = new LastAction();
            lastAction.setCommandTypeCode(CommandType.AUTO_LIKE.getCode());
        }

        lastAction.setStart(new Date());
        lastAction.setEnd(null);
        lastAction.setUpdatedAt(new Date());

        lastActionRepository.save(lastAction);
        log.debug("Updated last action: {}", lastAction);

        log.debug("END");
    }
}
