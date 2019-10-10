/* Project: android_im_sdk
 * 
 * File Created at 2016/7/26
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

import com.littlec.sdk.utils.LCLogger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Type com.littlec.sdk.chat.core
 * @User user
 * @Desc 看门狗
 * @Date 2016/7/26
 * @Version
 */
class ExcWatchDog<T> {
    private LCLogger Logger = LCLogger.getLogger("ExcWatchDog");

    private ExcTimerListener listener;
    private Timer watchdogTimer;
    private AtomicBoolean timerRunning=new AtomicBoolean(false);


    public ExcWatchDog(ExcTimerListener listener) {
        this.listener = listener;
        watchdogTimer = new Timer();
    }

    /**
     * @Title: putTaskNextExeTime <br>
     * @Description: 压入任务的下次执行时间,每次单独计时<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/28 10:15
     */
   /* protected void putTaskNextExeTime() {
//        double nextTime = ExcWatchDogUtils.getBackoffTime(nextTimes);
        startTimer(3000);
    }*/

    /**
     * @Title: startTimer <br>
     * @Description: 启动定时器<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/28 9:51
     */
    protected void startTimer(double nextTime) {
        Logger.d("startTimer");
        if (watchdogTimer == null) {
            watchdogTimer=new Timer();
        }
        if(timerRunning.compareAndSet(false,true)) {
            watchdogTimer.scheduleAtFixedRate(new mWatchDogTask(), 0, (long) nextTime);
        }
    }

    /**
     * @Title: stopTimer <br>
     * @Description: 取消定时器 清除任务 时间戳清零<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/28 10:14
     */
    protected void cancelTimer() {
        if(watchdogTimer == null){
            Logger.e(new NullPointerException("The watchdogTimer is Null"));
            return;
        }
        if (timerRunning.compareAndSet(true,false)) {
                watchdogTimer.cancel();
                watchdogTimer=null;
        }
    }
    /** 
     * @Title: getTimerRunningFlag <br>
     * @Description: 获取Timer当前的状态  <br>
     * @param:  <br>   
     * @return:  <br>
     * @throws: 2016/9/14 11:15
     */  
    protected boolean getTimerRunningFlag() {
        if (watchdogTimer == null) {
            return false;
        } else {
            return timerRunning.get();
        }
    }

    /**
     * @ClassName: ExcWatchDog
     * @Description: 定时器任务
     * @author: user
     * @date: 2016/7/28 9:51
     */
    private class mWatchDogTask extends TimerTask {
        @Override
        public void run() {
            //通知重发器需要立即执行的任务id
            listener.onNext();
        }
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/7/26 user creat
 */
