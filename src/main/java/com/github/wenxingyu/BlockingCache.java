package com.github.wenxingyu;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class BlockingCache<V> {

    private SyncService syncService = new SyncService();

    private Cache<String, V> cache = CacheBuilder.newBuilder().build();

    public V get(String key, int timeout, TimeUnit unit) {
        try {
            syncService.await(key, timeout, unit);
            return cache.getIfPresent(key);
        } finally {
            cache.invalidate(key);
        }
    }

    public void put(String key, V value) {
        cache.put(key, value);
        syncService.notify(key);
    }

}
