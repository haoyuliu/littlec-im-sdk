package com.littlec.sdk.connect.core;

/**
 * ClassName: ILCPoolManager
 * Description:  线程池管理接口
 * Creator: user
 * Date: 2016/7/19 16:45
 */
public interface ILCPoolManager {
    //新增任务，注意这里如果不指定线程池类型，会将线程放在默认线程池中运行
    void addTask(LCBaseTask task);

    //获取线程池
    BaseThreadPool getThreadPool(int threadPoolType);

    //移出任务
    boolean removeTask(LCBaseTask task);

    //停止所有任务
    void stopAllTask();

    void stopGivenThreadPool(int threadPoolType);

}
