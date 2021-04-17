package org.thinkit.bot.instagram.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageTasklet implements Tasklet {

    private final String message;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("Message: " + message);
        return RepeatStatus.FINISHED;
    }
}
