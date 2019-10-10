package com.littlec.sdk.connect.core;

import com.littlec.sdk.connect.repeater.ExcTaskManager;
import com.littlec.sdk.utils.CommonUtils;

/**
 * ClassName: LCBaseTask
 * Description:  基本线程任务
 * Creator: user
 * Date: 2016/7/19 16:19
 */
public class LCBaseTask<T> implements Runnable {
    private TaskTag taskTag = TaskTag.NORMAL;

    private String taskID;
    //首次执行时间
    private long firstSendTime;
    /**
     * 线程池类型
     */
    protected int threadPoolType;

    protected String taskName = null;

    protected int retryTimes = 0;

    protected T packet;

    public LCBaseTask(T packet, int threadPoolType, String threadTaskName) {
        initThreadTaskObject(packet, threadPoolType, threadTaskName);
    }

    public LCBaseTask(T packet, int threadPoolType) {
        initThreadTaskObject(packet, threadPoolType, this.toString());
    }

    /**
     * 在默认线程池中执行
     */
    public LCBaseTask(T packet) {
        initThreadTaskObject(packet, LCPoolConst.THREAD_TYPE_MESSAGE, this.toString());
    }

    /**
     * 初始化线程任务
     *
     * @param threadPoolType 线程池类型
     * @param threadTaskName 线程任务名称
     */
    private void initThreadTaskObject(T packet, int threadPoolType, String threadTaskName) {
        this.packet = packet;
//        this.taskID = UUID.randomUUID().toString();
        this.threadPoolType = threadPoolType;
        String name = LCPoolParams.getInstance(threadPoolType).name();
        if (threadTaskName != null) {
            name = name + "_" + threadTaskName;
        }
        this.firstSendTime = CommonUtils.getCurrentTime();

        setTaskName(name);
    }

    public void setTaskTag(TaskTag tag) {
        this.taskTag = tag;
    }

    public TaskTag getTaskTag() {
        return taskTag;
    }

    /**
     * 取得线程池类型
     *
     * @return
     */
    public int getThreadPoolType() {
        return threadPoolType;
    }

    /**
     * 开始任务
     */
    public void start() {
        LCPoolFactory.getThreadPoolManager().addTask(this);
    }

    /**
     * 取消任务
     */
    public void cancel() {
        LCPoolFactory.getThreadPoolManager().removeTask(this);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    protected void resetTask() {
        ExcTaskManager.getInstance().putExceptionTask(this);
    }

    public long getFirstSendTime() {
        return firstSendTime;
    }

    public void setFirstSendTime(long firstSendTime) {
        this.firstSendTime = firstSendTime;
    }

    public int getRetryTimes(){
        return retryTimes;
    }

    public void incrementRetryTimes() {
        this.retryTimes = ++retryTimes;
    }

    public T getPacket() {
        return packet;
    }

    public enum TaskTag {
        NORMAL,
        ABNORMAL
    }

}
