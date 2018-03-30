package com.qihoo.cache.store.impl;

import com.qihoo.cache.store.ValueHolder;

import java.lang.ref.WeakReference;

/**
 * Created by zhuhailong-dc on 2018/3/29.
 */
public class WeakValueHolder<V> implements ValueHolder<V> {

    public WeakValueHolder(V value) {
        super();
        if (null == value) {
            return;
        }
        this.v = new WeakReference<V>(value);
    }

    private WeakReference<V> v;

    public V value() {
        return this.v.get();
    }

}
