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

import java.util.Collections;
import java.util.List;

import org.thinkit.bot.instagram.mongo.entity.Hashtag;
import org.thinkit.bot.instagram.mongo.repository.HashtagRepository;
import org.thinkit.bot.instagram.param.TargetHashtag;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@NoArgsConstructor(staticName = "newInstance")
public final class FocusHashtagSelectionStrategy implements HashtagSelectionStrategy {

    @Override
    public List<TargetHashtag> getTargetHashtags(@NonNull final HashtagRepository hashtagRepository,
            @NonNull final String chargeUserName, final int groupCode) {
        log.debug("START");

        final List<Hashtag> hashtags = hashtagRepository.findByChargeUserNameAndGroupCode(chargeUserName, groupCode);

        Collections.shuffle(hashtags);
        log.debug("Shuffled hashtags: {}", hashtags);

        log.debug("END");
        return List.of(TargetHashtag.from(hashtags.get(0).getTag()));
    }
}
