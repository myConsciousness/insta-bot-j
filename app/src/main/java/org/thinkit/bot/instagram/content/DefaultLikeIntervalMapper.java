package org.thinkit.bot.instagram.content;

import org.thinkit.bot.instagram.content.entity.DefaultLikeInterval;
import org.thinkit.zenna.mapper.ContentMapper;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The mapper class that manages the content {@code "DefaultLikeInterval"} .
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(staticName = "newInstance")
public class DefaultLikeIntervalMapper extends ContentMapper<DefaultLikeInterval> {
}
