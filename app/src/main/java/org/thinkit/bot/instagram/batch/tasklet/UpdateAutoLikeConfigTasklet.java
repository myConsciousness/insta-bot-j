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

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;
import org.thinkit.bot.instagram.batch.catalog.VariableName;
import org.thinkit.bot.instagram.batch.data.content.entity.HashtagGroup;
import org.thinkit.bot.instagram.batch.data.content.entity.HashtagResource;
import org.thinkit.bot.instagram.batch.data.content.mapper.HashtagGroupMapper;
import org.thinkit.bot.instagram.batch.data.content.mapper.HashtagResourceMapper;
import org.thinkit.bot.instagram.batch.data.mongo.entity.Hashtag;
import org.thinkit.bot.instagram.batch.data.mongo.entity.Variable;
import org.thinkit.bot.instagram.batch.data.mongo.repository.HashtagRepository;
import org.thinkit.bot.instagram.batch.data.mongo.repository.VariableRepository;
import org.thinkit.bot.instagram.batch.result.BatchTaskResult;
import org.thinkit.bot.instagram.catalog.TaskType;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
@Component
public final class UpdateAutoLikeConfigTasklet extends AbstractTasklet {

    private UpdateAutoLikeConfigTasklet() {
        super(TaskType.UPDATE_AUTO_LIKE_CONFIG);
    }

    public static Tasklet newInstance() {
        return new UpdateAutoLikeConfigTasklet();
    }

    @Override
    protected BatchTaskResult executeTask(StepContribution contribution, ChunkContext chunkContext) {
        log.debug("START");

        this.updateHashtag();
        this.updateHashtagGroupCount();

        log.debug("END");
        return BatchTaskResult.builder().actionCount(1).build();
    }

    private void updateHashtag() {
        log.debug("START");

        final String chargeUserName = super.getRunningUserName();
        final HashtagRepository hashtagRepository = super.getMongoCollections().getHashtagRepository();
        hashtagRepository.deleteByChargeUserName(chargeUserName);

        for (final HashtagResource hashtagResource : HashtagResourceMapper.from(chargeUserName).scan()) {
            final Hashtag hashtag = new Hashtag();
            hashtag.setChargeUserName(chargeUserName);
            hashtag.setTag(hashtagResource.getTag());
            hashtag.setGroupCode(hashtagResource.getGroupCode());

            hashtagRepository.insert(hashtag);
            log.debug("Inserted hashtag: {}", hashtag);
        }

        log.debug("END");
    }

    private void updateHashtagGroupCount() {
        log.debug("START");

        final VariableRepository variableRepository = super.getMongoCollections().getVariableRepository();
        Variable variable = variableRepository.findByName(VariableName.HASHTAG_GROUP_COUNT.getTag());

        if (variable == null) {
            variable = new Variable();
            variable.setName(VariableName.HASHTAG_GROUP_COUNT.getTag());
        }

        for (final HashtagGroup hashtagGroup : HashtagGroupMapper.newInstance().scan()) {
            // This iteration should be always done only once
            variable.setValue(String.valueOf(hashtagGroup.getCount()));
        }

        variableRepository.save(variable);
        log.debug("Updated variable: {}", variable);

        log.debug("END");
    }
}
