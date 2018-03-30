package com.qihoo.cache;

import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zhuhailong-dc on 2018/3/29.
 * CachingProvider：定义了创建、配置、获取、管理和控制多个CacheManager
 * 一个应用可以在运行期间访问多个CachingProvider
 */
public class SoCachingProvider implements CachingProvider {

    private static final String DEFAULT_URI_STRING = "";

    private static final URI URI_DEFAULT;

    // TODO 为什么要使用 ClassLoader URI作为key
    private final Map<ClassLoader, ConcurrentMap<URI, CacheManager>> cacheManagers = new WeakHashMap<>();

    static {
        try {
            URI_DEFAULT = new URI(DEFAULT_URI_STRING);
        } catch (URISyntaxException e) {
            throw new javax.cache.CacheException(e);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void close(ClassLoader classLoader) {

    }

    @Override
    public void close(URI uri, ClassLoader classLoader) {

    }

    @Override
    public CacheManager getCacheManager() {
        return getCacheManager(getDefaultURI(), getDefaultClassLoader(), null);
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        return getCacheManager(uri, classLoader, getDefaultProperties());
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        uri = uri == null ? getDefaultURI() : uri;
        classLoader = classLoader == null ? getDefaultClassLoader() : classLoader;
        properties = properties == null ? new Properties() : cloneProperties(properties);

        ConcurrentMap<URI, CacheManager> cacheManagersByURI = cacheManagers.get(classLoader);

        if (cacheManagersByURI == null) {
            cacheManagersByURI = new ConcurrentHashMap<>();
        }

        CacheManager cacheManager = cacheManagersByURI.get(uri);

        if (cacheManager == null) {
            cacheManager = new SoCacheManager(this, properties, classLoader, uri);
            cacheManagersByURI.put(uri, cacheManager);
        }

        if (!cacheManagers.containsKey(classLoader)) {
            cacheManagers.put(classLoader, cacheManagersByURI);
        }

        return cacheManager;
    }

    /**
     * 复制Properties中的元素，相互修改不影响
     *
     * @param properties
     * @return
     */
    private static Properties cloneProperties(Properties properties) {
        Properties clone = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            clone.put(entry.getKey(), entry.getValue());
        }
        return clone;
    }

    @Override
    public ClassLoader getDefaultClassLoader() {
        return getClass().getClassLoader();
    }

    @Override
    public Properties getDefaultProperties() {
        return new Properties();
    }

    @Override
    public URI getDefaultURI() {
        return URI_DEFAULT;
    }

    @Override
    public boolean isSupported(OptionalFeature optionalFeature) {
        return false;
    }

    public void releaseCacheManager(URI uri, ClassLoader classLoader) {
        if (uri == null || classLoader == null) {
            throw new NullPointerException("uri or classLoader should not be null");
        }
        ConcurrentMap<URI, CacheManager> cacheManagersByURI = cacheManagers.get(classLoader);
        // TODO 这段代码需要验证是否有问题，为什么一个Map只移除了一次
        if (cacheManagersByURI != null) {
            cacheManagersByURI.remove(uri);
            if (cacheManagersByURI.size() == 0) {
                cacheManagers.remove(classLoader);
            }
        }
    }
}
