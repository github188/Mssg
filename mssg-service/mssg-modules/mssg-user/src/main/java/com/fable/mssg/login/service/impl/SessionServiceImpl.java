package com.fable.mssg.login.service.impl;


import com.fable.mssg.service.login.SessionService;
import com.fable.mssg.utils.login.LoginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * @author: yuhl Created on 11:05 2017/11/3 0003
 */
@Service
public class SessionServiceImpl implements SessionService {

    @Autowired(required = false)
    public CacheManager cacheManager;

    /**
     * 保存session信息
     *
     * @param key
     * @param value
     */
    @Override
    public <T> void setAttribute(String key, T value) {
        Cache cache = cacheManager.getCache(LoginUtils.LOGIN_CACHE_NAME);
        cache.put(key, value);
    }

    /**
     * 获取session信息
     *
     * @param key
     * @param object
     * @return
     */
    @Override
    public <T> T getAttribute(String key, Object object) {
        Cache cache = cacheManager.getCache(LoginUtils.LOGIN_CACHE_NAME);
        if (null != cache) {
            if (null != cache.get(key)) {
                return (T) cache.get(key, (Class<T>) object);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 删除session信息
     *
     * @param key
     */
    @Override
    public void removeAttribute(String key) {
        Cache cache = cacheManager.getCache(LoginUtils.LOGIN_CACHE_NAME);
        if (null != cache) { // 存在缓存则删除
            cache.evict(key);
        }
    }

}
