package com.github.wenxingyu;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SyncService {

    private static Logger logger = LoggerFactory.getLogger(SyncService.class);

    private LoadingCache<String, CountDownLatch> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, CountDownLatch>() {
        @Override
        public CountDownLatch load(@Nonnull String key) throws Exception {
            return new CountDownLatch(1);
        }
    });

    public void await(String key, long timeout, TimeUnit unit) {
        try {
            boolean result = cache.getUnchecked(key).await(timeout, unit);
            if (!result) {
                logger.error("waiting for key [{}] timeout", key);
                throw new IllegalStateException("timeout");
            }
        } catch (InterruptedException e) {
            logger.error("while waiting for key: [{}], thread was interrupted", key);
        } finally {
            cache.invalidate(key);
        }
    }

    public void notify(String key) {
        logger.info("incoming key: [{}]", key);
        cache.getUnchecked(key).countDown();
    }

}
