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

package org.thinkit.bot.instagram;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.thinkit.bot.instagram.tag.HashTag;
import org.thinkit.bot.instagram.user.InstagramUser;

/**
 * The class that manages test case of {@link InstaBotJ} .
 *
 * @author Kato Shinya
 * @since 1.0.0
 */
public final class InstaBotJTest {

    /**
     * The user name
     */
    private static final String USER_NAME = System.getenv("INSTAGRAM_USER_NAME");

    /**
     * The password
     */
    private static final String PASSWORD = System.getenv("INSTAGRAM_PASSWORD");

    @Test
    void test() {

        List<HashTag> hashTags = new ArrayList<>();
        hashTags.add(HashTag.from("likesforlikes"));
        hashTags.add(HashTag.from("follow"));
        hashTags.add(HashTag.from("travel"));
        hashTags.add(HashTag.from("love"));

        InstaBotJ.from(InstagramUser.from(USER_NAME, PASSWORD)).executeAutoLikes(hashTags);
    }
}
