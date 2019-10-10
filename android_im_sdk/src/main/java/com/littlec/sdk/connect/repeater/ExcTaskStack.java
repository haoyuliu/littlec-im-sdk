/* Project: android_im_sdk
 *
 * File Created at 2016/7/28
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.repeater;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Type com.littlec.sdk.chat.core
 * @User user
 * @Desc异常任务栈,支持删除指定taskId对应的任务，支持更新对应taskId对应的重发次数
 * @Date 2016/7/28
 * @Version
 */
class ExcTaskStack<K, V> extends LinkedHashMap<K, V> {

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private final Lock lock = new ReentrantLock();

    public ExcTaskStack(int initialCapacity) {
        super(initialCapacity, DEFAULT_LOAD_FACTOR, true);
    }

    @Override
    public V get(Object key) {
        try {
            lock.lock();
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            lock.lock();
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        try {
            lock.lock();
            return super.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        try {
            lock.lock();
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        try {
            lock.lock();
            super.clear();
        } finally {
            lock.unlock();
        }
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/7/28 user creat
 */
