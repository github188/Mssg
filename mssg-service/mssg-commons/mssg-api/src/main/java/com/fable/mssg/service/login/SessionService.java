package com.fable.mssg.service.login;

/**
 * @author: yuhl Created on 10:46 2017/11/3 0003
 */
public interface SessionService {

    /**
     * 保存session信息
     * @param key
     * @param value
     * @param <T>
     */
    <T> void setAttribute(String key, T value);

    /**
     * 获取session信息
     * @param key
     * @param object
     * @param <T>
     * @return
     */
    <T> T getAttribute(String key, Object object);

    /**
     * 删除session信息
     * @param key
     */
    void removeAttribute(String key);

}
