package com.littlec.sdk.connect.core;

/**
 * ClassName: LCPoolParams
 * Description:  线程池参数配置
 * Creator: user
 * Date: 2016/7/19 16:20
 */
enum LCPoolParams {
    /**
     * 网络请求池
     */
    httpThreadPool(LCPoolConst.THREAD_TYPE_SIMPLE_HTTP, LCPoolConst.DEFAULT_CORE_POOL_SIZE, 40, LCPoolConst.KEEP_ALIVE_TIME, 10, true),
    /**
     * 消息请求池
     */
    messageThreadPool(LCPoolConst.THREAD_TYPE_MESSAGE, LCPoolConst.DEFAULT_CORE_POOL_SIZE, Integer.MAX_VALUE, LCPoolConst.KEEP_ALIVE_TIME, 0, false),
    /**
     * 重发器请求池  核心线程池10条  缓存队列数量20
     */
    repeaterThreadPool(LCPoolConst.THREAD_TYPE_REPEATER, 20, 40, LCPoolConst.KEEP_ALIVE_TIME, 20, true),

    /**
     * 其他请求池
     */
    otherThreadPool(LCPoolConst.THREAD_TYPE_OTHER, LCPoolConst.DEFAULT_CORE_POOL_SIZE, Integer.MAX_VALUE, LCPoolConst.KEEP_ALIVE_TIME, 0, false);

    /**
     * 核心线程大小:线程池中存在的线程数，包括空闲线程(就是还在存活时间内，没有干活，等着任务的线程)
     */
    private int corePoolSize = 0;

    /**
     * 线程池维护线程的最大数量,当基础线程池以及队列都满了的情况继续创建新线程
     */
    private int maxnumPoolSize = 0;

    /**
     * 线程池维护线程所允许的空闲时间,空闲时间超出该值则移除
     */
    private long keepAliveTime = 0;

    /**
     * 线程池类型
     */
    private int type = 0;

    /**
     * 线程池所使用的缓冲队列
     */
    private int poolQueueSize = 0;

    /**
     * 是否可以超时
     */
    private boolean allowCoreThreadTimeOut = true;

    LCPoolParams(int type, int corePoolSize, int maximumPoolSize, long keepAliveTime,
                 int poolQueueSize, boolean allowCoreThreadTimeOut) {
        this.type = type;
        this.corePoolSize = corePoolSize;
        this.maxnumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.poolQueueSize = poolQueueSize;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public static LCPoolParams getInstance(int type) {
        for (LCPoolParams params : LCPoolParams.values()) {
            if (type == params.getType()) {
                return params;
            }
        }
        return LCPoolParams.otherThreadPool;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxNumPoolSize() {
        return maxnumPoolSize;
    }

    public int getType() {
        return type;
    }

    public int getPoolQueueSize() {
        return poolQueueSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public boolean getAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

}
