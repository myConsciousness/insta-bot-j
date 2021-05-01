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

package org.thinkit.bot.instagram.batch.strategy.hashtag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.dto.MongoCollections;
import org.thinkit.bot.instagram.mongo.entity.Hashtag;
import org.thinkit.bot.instagram.param.TargetHashtag;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@NoArgsConstructor(staticName = "newInstance")
@Component
public final class DefaultHashtagSelectionStrategy implements HashtagSelectionStrategy {

    /**
     * The mongo collection
     */
    @Autowired
    private MongoCollections mongoCollection;

    @Override
    public List<TargetHashtag> getTargetHashtags(final int groupCode) {
        log.debug("START");

        final List<Hashtag> hashtags = this.mongoCollection.getHashtagRepository().findByGroupCode(groupCode);
        final List<TargetHashtag> targetHashtags = new ArrayList<>(hashtags.size());

        hashtags.forEach(hashtag -> {
            log.debug("The using hashtags: {}", hashtag);
            targetHashtags.add(TargetHashtag.from(hashtag.getTag()));
        });

        Collections.shuffle(targetHashtags);
        log.debug("Shuffled hashtags: {}", targetHashtags);

        log.debug("END");
        return targetHashtags;
    }
}
