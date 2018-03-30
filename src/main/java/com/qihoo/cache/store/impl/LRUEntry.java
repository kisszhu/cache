package com.qihoo.cache.store.impl;

import com.qihoo.cache.store.ValueHolder;

import java.util.Map;

/**
 * Created by zhuhailong-dc on 2018/3/29.
 */
public class LRUEntry<K, V extends ValueHolder<?>> implements Map.Entry<K, ValueHolder<?>> {

    final K key; // non-null
    ValueHolder<?> v; // non-null

    LRUEntry<K, ValueHolder<?>> pre;
    LRUEntry<K, ValueHolder<?>> next;

    public LRUEntry<K, ValueHolder<?>> getPre() {
        return pre;
    }

    public void setPre(LRUEntry<K, ValueHolder<?>> pre) {
        this.pre = pre;
    }

    public LRUEntry<K, ValueHolder<?>> getNext() {
        return next;
    }

    public void setNext(LRUEntry<K, ValueHolder<?>> next) {
        this.next = next;
    }

    public LRUEntry(K key, V value) {
        super();
        this.key = key;
        this.v = value;
    }

    public K getKey() {
        return key;
    }

    public ValueHolder<?> getValue() {
        return this.v;
    }

    public ValueHolder<?> setValue(ValueHolder<?> value) {
        ValueHolder<?> oldValue = this.v;
        this.v = value;
        return oldValue;
    }
}
