package com.qihoo.cache.store.impl;

import com.qihoo.cache.store.DataStore;
import com.qihoo.cache.store.StoreAccessException;
import com.qihoo.cache.store.ValueHolder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuhailong-dc on 2018/3/30.
 */
public class BasicDataStore<K, V> implements DataStore<K, V> {

    ConcurrentHashMap<K, ValueHolder<V>> map = new ConcurrentHashMap<K, ValueHolder<V>>();

    public ValueHolder<V> get(K key) throws StoreAccessException {
        return map.get(key);
    }

    public PutStatus put(K key, V value) throws StoreAccessException {
        ValueHolder<V> v = new BasicValueHolder<V>(value);
        map.put(key, v);
        return PutStatus.PUT;
    }

    public ValueHolder<V> remove(K key) throws StoreAccessException {
        return map.remove(key);
    }

    public void clear() throws StoreAccessException {
        map.clear();
    }
}
