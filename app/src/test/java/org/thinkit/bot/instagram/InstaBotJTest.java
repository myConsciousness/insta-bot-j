package org.thinkit.bot.instagram;

import org.junit.jupiter.api.Test;

public class InstaBotJTest {

    @Test
    void test() {

        InstaBotJ.from("test", "testPassword").executeAutoLikes();
    }
}
