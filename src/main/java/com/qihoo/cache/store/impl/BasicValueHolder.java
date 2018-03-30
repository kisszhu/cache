package com.qihoo.cache.store.impl;

import com.qihoo.cache.store.ValueHolder;

/**
 * Created by zhuhailong-dc on 2018/3/30.
 */
public class BasicValueHolder<V> implements ValueHolder<V> {
    private final V refValue;

    public BasicValueHolder(final V value) {
        refValue = value;
    }

    public V value() {
        return refValue;
    }

}
