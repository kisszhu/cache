package com.qihoo.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zhuhailong-dc on 2018/3/29.
 * CacheManager：定义了创建、配置。获取、管理和控制多个唯一命名的Cache，这些
 * Cache存在于CacheManager的上下文中。
 * 一个CacheManager仅被一个CachingProvider所拥有。
 */
public class SoCacheManager implements CacheManager {

    private final SoCachingProvider soCachingProvider;
    private final ClassLoader classLoader;
    private final URI uri;
    private final Properties props;
    private volatile boolean isClosed;

    private static Logger logger = LoggerFactory.getLogger(CacheManager.class);

    // 用于管理cache
    private final ConcurrentMap<String, SoCache<?, ?>> caches = new ConcurrentHashMap<>();

    public SoCacheManager(SoCachingProvider soCachingProvider, Properties props, ClassLoader classLoader, URI uri) {
        this.soCachingProvider = soCachingProvider;
        this.classLoader = classLoader;
        this.uri = uri;
        this.props = props;
    }


    @Override
    synchronized public void close() {
        if (!isClosed) {
            soCachingProvider.releaseCacheManager(getURI(), getClassLoader());
            isClosed = true;

            ArrayList<Cache<?, ?>> cacheList = new ArrayList(caches.values());
            // TODO 这块为什么先 clear 然后在 close
            caches.clear();

            for (Cache<?, ?> cache : cacheList) {
                try {
                    cache.close();
                } catch (Exception e) {
                    logger.warn("cannot close cache : " + cache, e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    synchronized public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration)
            throws IllegalArgumentException {
        if (isClosed) {
            throw new IllegalStateException();
        }

        checkNotNull(cacheName, "cacheName");
        checkNotNull(configuration, "configuration");

        SoCache<?, ?> cache = caches.get(cacheName);

        if (cache == null) {
            cache = new SoCache<K, V>(this, cacheName, configuration);
            caches.put(cache.getName(), cache);

            return (Cache<K, V>) cache;
        } else {
            throw new CacheException("A cache named " + cacheName + " already exists.");
        }
    }

    @Override
    synchronized public void destroyCache(String cacheName) {
        if (isClosed) {
            throw new IllegalStateException();
        }
        checkNotNull(cacheName, "cacheName");

        Cache<?, ?> cache = caches.get(cacheName);
        if (cache != null) {
            cache.close();
        }
    }

    private void checkNotNull(Object object, String name) {
        if (object == null) {
            throw new NullPointerException(name + " can not be null");
        }
    }

    @Override
    public void enableManagement(String arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void enableStatistics(String arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return (Cache<K, V>) getCache(cacheName, Object.class, Object.class);
    }

    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyClazz, Class<V> valueClazz) {
        if (isClosed) {
            throw new IllegalStateException();
        }

        checkNotNull(keyClazz, "keyType");
        checkNotNull(valueClazz, "valueType");

        SoCache<K, V> cache = (SoCache<K, V>) caches.get(cacheName);

        if (cache == null) {
            return null;
        } else {
            Configuration<?, ?> configuration = cache.getConfiguration(Configuration.class);

            if (configuration.getKeyType() != null && configuration.getKeyType().equals(keyClazz)) {
                if (configuration.getValueType() != null && configuration.getValueType().equals(valueClazz)) {
                    return cache;
                } else {
                    throw new ClassCastException("Incompatible cache value types specified, expected "
                            + configuration.getValueType() + " but " + keyClazz + " was specified");
                }
            } else {
                throw new ClassCastException("Incompatible cache key types specified, expected "
                        + configuration.getKeyType() + " but " + valueClazz + " was specified");
            }
        }
    }

    @Override
    public Iterable<String> getCacheNames() {
        if (isClosed()) {
            throw new IllegalStateException();
        }

        return Collections.unmodifiableList(new ArrayList<String>(caches.keySet()));
    }

    @Override
    public CachingProvider getCachingProvider() {
        return soCachingProvider;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public Properties getProperties() {
        return this.props;
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(getClass())) {
            return clazz.cast(this);
        }

        throw new IllegalArgumentException("Unwapping to " + clazz + " is not a supported by this implementation");
    }

    synchronized public void releaseCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException();
        }

        caches.remove(cacheName);
    }
}