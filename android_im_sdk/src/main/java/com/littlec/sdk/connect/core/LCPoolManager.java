/**
 * Title: LCPoolManager.java
 * Description:
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 上午10:40:16
 * Version: 1.0
 */
package com.littlec.sdk.connect.core;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: LCPoolManager
 * Description:线程池管理实现类
 * Creator: user
 * Date: 2016/7/19 17:24
 */
class LCPoolManager implements ILCPoolManager {
    /**
     * 不同类型的线程池，可以同时管理多个线程池，关闭指定的线程池
     */
    @SuppressLint("UseSparseArrays")
    private final Map<Integer, BaseThreadPool> threadPoolMap = new HashMap<Integer, BaseThreadPool>();

    @Override
    public void addTask(LCBaseTask task) {
        if (task != null) {
            BaseThreadPool threadPool = null;
            synchronized (threadPoolMap) {
                threadPool = threadPoolMap.get(task.getThreadPoolType());
                //指定类型的线程池不存在则创建一个新的
                if (threadPool == null) {
                    threadPool = new BaseThreadPool(LCPoolParams.getInstance(task.getThreadPoolType()));
                    threadPoolMap.put(task.getThreadPoolType(), threadPool);
                }
            }
            threadPool.execute(task);
        }
    }

    @Override
    public BaseThreadPool getThreadPool(int threadPoolType) {
        BaseThreadPool threadPool = null;
        synchronized (threadPoolMap) {
            threadPool = threadPoolMap.get(threadPoolType);
            //指定类型的线程池不存在则创建一个新的
            if (threadPool == null) {
                threadPool = new BaseThreadPool(LCPoolParams.getInstance(threadPoolType));
            }
        }

        return threadPool;
    }
    /** 
     * @Title:  stopGivenThreadPool<br>
     * @Description:  停止给定类型的线程池<br>
     * @param:  threadPoolType 线程池类型<br>
     * @return:  void<br>
     * @throws: 2016/9/7 10:34
     */  
    @Override
    public void stopGivenThreadPool(int threadPoolType){
        if(threadPoolMap!=null){
            BaseThreadPool threadPool=threadPoolMap.get(threadPoolType);
            if(threadPool!=null){
                threadPool.shutdownNow();
                threadPoolMap.remove(threadPool);
            }
        }
    }

    @Override
    public boolean removeTask(LCBaseTask task) {
        BaseThreadPool threadPool = threadPoolMap.get(task.getThreadPoolType());

        if (threadPool != null) {
            return threadPool.remove(task);
        }
        return false;
    }

    @Override
    public void stopAllTask() {
        if (threadPoolMap != null) {
            for (Integer key : threadPoolMap.keySet()) {
                BaseThreadPool threadPool = threadPoolMap.get(key);

                if (threadPool != null) {
                    threadPool.shutdownNow();//试图停止所有正在执行的线程，不再处理还在池队列中等待的任务
                }
            }

            threadPoolMap.clear();
        }
    }
}
