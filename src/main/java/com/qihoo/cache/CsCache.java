package com.qihoo.cache;

import com.qihoo.cache.store.DataStore;
import com.qihoo.cache.store.StoreAccessException;
import com.qihoo.cache.store.ValueHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhuhailong-dc on 2018/3/30.
 */
public class CsCache<K, V> {
    private final DataStore<K, V> store;
    private static Logger logger = LoggerFactory.getLogger(CsCache.class);

    public CsCache(final DataStore<K, V> dataStore) {
        store = dataStore;
    }

    public V get(final K key) {
        try {
            ValueHolder<V> value = store.get(key);
            if (null == value) {
                return null;
            }
            return value.value();
        } catch (StoreAccessException e) {
            logger.error("store access error : ", e.getMessage());
            logger.error(e.getStackTrace().toString());
            return null;
        }
    }

    public void put(final K key, final V value) {
        try {
            store.put(key, value);
        } catch (StoreAccessException e) {
            logger.error("store access error : ", e.getMessage());
            logger.error(e.getStackTrace().toString());
        }
    }

    public V remove(K key) {
        try {
            ValueHolder<V> value = store.remove(key);
            return value.value();
        } catch (StoreAccessException e) {
            logger.error("store access error : ", e.getMessage());
            logger.error(e.getStackTrace().toString());
            return null;
        }
    }

    public void clear() {
        try {
            store.clear();
        } catch (StoreAccessException e) {
            logger.error("store access error : ", e.getMessage());
            logger.error(e.getStackTrace().toString());
        }
    }
}
