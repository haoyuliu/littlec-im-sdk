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

import com.fingo.littlec.proto.css.CssErrorCode;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.connect.core.LCBaseTask;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.entity.ExcTaskDBEntity;
import com.fingo.littlec.proto.css.Msg;
import com.littlec.sdk.net.HttpPostTask;
import com.littlec.sdk.net.UploadFactory;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.LCLogger;

/**
 * @Type com.littlec.sdk.chat.core
 * @User user
 * @Desc 异常任务栈管理
 * @Date 2016/7/28
 * @Version
 */
public class ExcTaskManager {
    private static final String TAG = "ExcTaskManager";
    private static final LCLogger Logger = LCLogger.getLogger(TAG);

    //    private ExcTaskListener listener;
    private ExcTaskMsgRepeater mRepeater;

    private ExcTaskManager() {
        mRepeater = new ExcTaskMsgRepeater();
    }

    public static ExcTaskManager getInstance() {
        return LCSingletonFactory.getInstance(ExcTaskManager.class);
    }

    public void setExcTimeOut(long time) {
        if (time <= 0) {
            return;
        }
        mRepeater.setTimeOut(time);
    }

    private enum ExtType {
        MESSAGE_EXC_TYPE(0),
        FILE_EXC_TYPE(1);
        int value = 0;

        ExtType(int value) {
            this.value = value;
        }
    }

    /**
     * @Title: putExceptionTask <br>
     * @Description: 保存异常任务<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/28 15:10
     */
    public synchronized <T> void putExceptionPacket(T packet) {
        LCBaseTask task;
        //存入重发表
        if (packet instanceof LCMessage) {
            LCMessage message = (LCMessage) packet;
            Logger.d("putExceptionPacket," + message.toString());
            ExcTaskDBEntity entity = new ExcTaskDBEntity();
            switch (((LCMessage) packet).LCMessageEntity().getContentType()) {
                case Msg.EMsgContentType.AUDIO_VALUE:
                case Msg.EMsgContentType.IMAGE_VALUE:
                case Msg.EMsgContentType.VIDEO_VALUE:
                case Msg.EMsgContentType.FILE_VALUE:
                case Msg.EMsgContentType.LOCATION_VALUE:
                    entity.setExcType(ExtType.FILE_EXC_TYPE.value);
                    /*********************需要先判断一下文件上传的状态********************/
                    if (message.LCMessageEntity().getStatus() == LCMessage.Status.MSG_POST_FILE_FAIL.value()) {
                        task = HttpPostTask.newBuilder().parseMessageToTask(message);
                        break;
                    }
                default:
                    task = new ExcMsgTask(message);
                    entity.setExcType(ExtType.MESSAGE_EXC_TYPE.value);
                    break;
            }
            /***********************将异常任务主键设置为msgId,便于去重************************/
            task.setTaskID(message.getMsgId());
            /*************************设置task的起始时间***********************************/
            task.setFirstSendTime(CommonUtils.getCurrentTime());
            /******************异常模块的task，设置tag标识**********************************/
            task.setTaskTag(LCBaseTask.TaskTag.ABNORMAL);
            /****************upload task 里面根据重发次数判断添加excStack的方式****************/
            task.incrementRetryTimes();
            mRepeater.putExceptionTask(task);
            entity.setFirstSendTime(task.getFirstSendTime());
//            entity.setMessageEntity(message.LCMessageEntity());
            entity.setTaskId(task.getTaskID());
            DBFactory.getDBManager().getDBExcTaskService().insertOrReplace(entity);
        }
        mRepeater.restoreWatchDog();
    }

    /**
     * @Title: putExceptionTask <br>
     * @Description: 添加异常任务 <br>
     * @param: LCBaseTask <br>
     * @return: <br>
     * @throws: 2016/9/14 10:35
     */
    public synchronized void putExceptionTask(LCBaseTask task) {
        /****************upload task 里面根据重发次数判断添加exctask的方式********************/
        task.incrementRetryTimes();
        mRepeater.putExceptionTask(task);
        mRepeater.restoreWatchDog();
    }

    /**
     * @Title: startExcModule <br>
     * @Description: 登录成功后， 启动一次异常模块,从数据库加载所有异常消息,启动定时器<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/19 15:53
     */
    public void startExcModule() {
        if (!mRepeater.getAllTaskRunning()) {
            //从数据库加载
            mRepeater.loadAllTaskFromDB();
            //启动定时器
            mRepeater.restoreWatchDog();
            //执行所有异常任务
            mRepeater.startAllExceptionTask();
        }
    }


    /**
     * @Title:stopReapter <br>
     * @Description: 停止正在运行的异常任务并清除缓存和数据库数据, 在退出登录时调用<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/17 18:37
     */
    public void stopRepeater() {
        Logger.d("stopRepeater");
        mRepeater.stopAllTask();
        mRepeater.stopWatchDog();
        mRepeater.clearCache();
    }

    /**
     * @Title: restoreReapter<br>
     * @Description: 恢复所有异常任务<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/17 18:37
     */
    public void restoreRepeater() {
        Logger.d("restoreRepeater");
        mRepeater.restoreWatchDog();
    }

    /**
     * @Title: removeExceptionTask <br>
     * @Description: 移除对应异常任务<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/28 16:55
     */
    public void removeExceptionTask(String taskId) {
        mRepeater.removeExceptionTask(taskId);
    }

    /**
     * @Title: removeDBExceptionTask <br>
     * @Description: 从数据库删除任务 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/14 11:26
     */
    public void removeDBExceptionTask(String taskId) {
        Logger.d("removeDBExceptionTask id:" + taskId);
        DBFactory.getDBManager().getDBExcTaskService().deleteByKey(taskId);
    }


    public static void handleSendError(String taskId, LCMessage message, CssErrorCode.ErrorCode errorCode) {
        //从缓存中删除
        ExcTaskManager.getInstance().removeExceptionTask(taskId);
        //从数据库中删除
        ExcTaskManager.getInstance().removeDBExceptionTask(taskId);

        UploadFactory.getUploadManager().cancel(taskId);
        //回调错误异常
        message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
        DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        Logger.d("handleSendError send error,messageid:" + message.getMsgId());
        DispatchController.getInstance().onError(message, errorCode.getNumber(),
               errorCode.name());
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
