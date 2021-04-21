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

package org.thinkit.bot.instagram.param;

import java.io.Serializable;

import org.thinkit.bot.instagram.catalog.TagSymbol;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * The entity that manages target hashtag.
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TargetHashtag implements Serializable {

    /**
     * The serial version ID
     */
    private static final long serialVersionUID = -2228837120836511893L;

    /**
     * The tag
     */
    @Getter
    private String tag;

    /**
     * The hash tag
     */
    @Getter
    private String hashTag;

    /**
     * The constructor.
     *
     * @param tag The tag
     *
     * @exception NullPointerException If {@code null} is passed as an argument.
     */
    private TargetHashtag(@NonNull final String tag) {
        this.tag = tag;
        this.hashTag = new StringBuilder(TagSymbol.HASH.getTag()).append(tag).toString();
    }

    /**
     * Returns the new instance of {@link TargetHashtag} based on the argument.
     *
     * @param tag The tag
     * @return The new instance of {@link TargetHashtag}
     *
     * @exception NullPointerException If {@code null} is passed as an argument.
     */
    public static TargetHashtag from(@NonNull final String tag) {
        return new TargetHashtag(tag);
    }
}
