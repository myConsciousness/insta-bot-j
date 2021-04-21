package org.thinkit.bot.instagram.batch.tasklet;

import com.mongodb.lang.NonNull;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.thinkit.bot.instagram.batch.MongoCollection;
import org.thinkit.bot.instagram.catalog.TaskType;
import org.thinkit.bot.instagram.content.HashtagResourceMapper;
import org.thinkit.bot.instagram.content.entity.HashtagResource;
import org.thinkit.bot.instagram.mongo.entity.Hashtag;
import org.thinkit.bot.instagram.mongo.repository.HashtagRepository;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
public final class ReversalEntryHashtagTasklet extends AbstractTasklet {

    /**
     * The mongo collection
     */
    private MongoCollection mongoCollection;

    public ReversalEntryHashtagTasklet(@NonNull final MongoCollection mongoCollection) {
        super(TaskType.REVERSAL_ENTRY_HASHTAG, mongoCollection.getLastActionRepository());
        this.mongoCollection = mongoCollection;
    }

    @Override
    protected RepeatStatus executeTask(StepContribution contribution, ChunkContext chunkContext) {
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
        return RepeatStatus.FINISHED;
    }
}
