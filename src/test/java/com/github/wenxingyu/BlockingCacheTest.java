package com.github.wenxingyu;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class BlockingCacheTest {

    private BlockingCache<String> blockingCache = new BlockingCache<>();

    @Test
    public void when_in_time_put_key_should_get_the_value() throws Exception {

        new Thread(() -> {
            sleep(1);
            blockingCache.put("key", "blocking");
        }).start();

        String value = blockingCache.get("key", 5, TimeUnit.SECONDS);
        assertEquals("blocking", value);
    }

    @Test(expected = IllegalStateException.class)
    public void should_get_exception_when_key_not_found_in_time() {
        blockingCache.get("key", 1, TimeUnit.SECONDS);
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}