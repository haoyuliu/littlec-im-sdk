package com.littlec.sdk.connect.core;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: BaseThreadPool
 * Description:  基础线程池
 * Creator: user
 * Date: 2016/7/19 16:39
 */
public class BaseThreadPool extends ThreadPoolExecutor {
    public BaseThreadPool(LCPoolParams threadPoolParamter) {
        super(threadPoolParamter.getCorePoolSize(), threadPoolParamter.getMaxNumPoolSize(),
                threadPoolParamter.getKeepAliveTime(), TimeUnit.SECONDS, //线程池维护线程所允许的空闲时间的单位
                threadPoolParamter.getPoolQueueSize() <= 0 ? new SynchronousQueue<Runnable>()
                        : new LinkedBlockingDeque<Runnable>(threadPoolParamter.getPoolQueueSize()), //线程池所使用的缓冲队列
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
