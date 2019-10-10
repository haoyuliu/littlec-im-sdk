package com.littlec.sdk.connect.repeater;

import android.text.TextUtils;

import com.fingo.littlec.proto.css.CssErrorCode;
import com.littlec.sdk.biz.chat.entity.body.LCATMessageBody;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCMessageBody;
import com.littlec.sdk.connect.core.LCBaseTask;
import com.littlec.sdk.connect.core.LCPoolConst;
import com.littlec.sdk.connect.core.LCPoolFactory;
import com.littlec.sdk.biz.chat.utils.ConvertMessageBodyUtils;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.entity.ExcTaskDBEntity;
import com.littlec.sdk.database.entity.MediaEntity;
import com.littlec.sdk.database.entity.MessageEntity;
import com.littlec.sdk.utils.sp.UserInfoSP;
import com.fingo.littlec.proto.css.Msg;
import com.littlec.sdk.net.HttpPostTask;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.LCLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClassName: ExcTaskMsgRepeater
 * Description:  重发器
 * Creator: user
 * Date: 2016/7/20 14:42
 */
class ExcTaskMsgRepeater implements ExcTimerListener<String> {
    private static final String TAG = "ExcTaskMsgRepeater";
    private static final LCLogger Logger = LCLogger.getLogger(TAG);
    private static final int TIMER_FIXED_DELAYED = 8 * 1000;
    private static int delayedTimes = 0;

    private static long excTimeout = 3 * 60 * 1000;

    private volatile ExcTaskStack<String, LCBaseTask> excTaskStack;

    //    private BaseThreadPool pool;
    private ExcWatchDog<String> watchDog;
    private AtomicBoolean startAllTaskDone = new AtomicBoolean(false);

    //实列化异常任务栈
    public ExcTaskMsgRepeater() {
        watchDog = new ExcWatchDog<>(this);
        excTaskStack = new ExcTaskStack<>(0);
    }

    public void setTimeOut(long time) {
        excTimeout = time;
    }

    /**
     * @Title: putExceptionTask <br>
     * @Description: 添加异常任务 <br>
     * @param: LCBaseTask <br>
     * @return: <br>
     * @throws: 2016/9/14 10:35
     */
    public void putExceptionTask(LCBaseTask task) {
        if (task == null) {
            return;
        }
        Logger.d("putExceptionTask，before exist size:" + excTaskStack.size());
        excTaskStack.put(task.getTaskID(), task);
        Logger.d("putExceptionTask，id:" + task.getTaskID() + ",exist size:" + excTaskStack.size());
    }


    /**
     * @Title: removeExceptionTask <br>
     * @Description: 移除对应异常任务<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/28 16:55
     */
    public void removeExceptionTask(String taskId) {
        if (excTaskStack != null && !TextUtils.isEmpty(taskId)) {
            excTaskStack.remove(taskId);
            Logger.d("removeExceptionTask，id:" + taskId + ",exist size:" + excTaskStack.size());
        }
    }

    /**
     * @Title: getAllTaskRunning <br>
     * @Description: 获取数据库任务是否已经有全部加载<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/11/8 17:26
     */
    public boolean getAllTaskRunning() {
        return startAllTaskDone.get();
    }

    /**
     * @Title: loadAllTaskFromDB <br>
     * @Description: 实现异常任务本地加载 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/28 15:08
     */
    protected void loadAllTaskFromDB() {
        Logger.d("loadAllTaskFromDB");
        if (excTaskStack == null) {
            Logger.e("excTaskStack is null");
            return;
        }
        excTaskStack.clear();
        Logger.d("excTaskStack clear");
        List<ExcTaskDBEntity> excTaskDBEntityList = DBFactory.getDBManager().getDBExcTaskService().loadAll();
        if (excTaskDBEntityList != null) {
            LCBaseTask task;
            Logger.d("excTaskDBEntityList size:" + excTaskDBEntityList.size());
            for (ExcTaskDBEntity taskDBEntity : excTaskDBEntityList) {
                List<MessageEntity> messageEntityList = DBFactory.getDBManager().getDBMessageService().queryBuilder()
                        .where(MessageEntityDao.Properties.MsgId.eq(taskDBEntity.getTaskId()))
                        .list();
                if (messageEntityList == null || messageEntityList.isEmpty()) {
                    Logger.e("messageEntity is null");
                    continue;
                }
                if (messageEntityList.size() > 1) {
                    Logger.e("!!! found many messageEntity by taskId:" + taskDBEntity.getTaskId());
                }
                MessageEntity messageEntity = messageEntityList.get(0);
                MediaEntity mediaEntity = DBFactory.getDBManager().getDBMediaService().load(messageEntity.getMediaId());
                LCMessageBody messageBody = null;
                if (messageEntity.getContentType() == LCMessage.ContentType.AT.value()) {
                    try {
                        List<String> atMembers = new ArrayList<>();
                        String result = messageEntity.getExtra();
                        JSONObject jsonObject = new JSONObject(result);
                        String memberString = jsonObject.optString("members");
                        String[] members = memberString.split(",");
                        for (String s : members) {
                            atMembers.add(s);
                        }
                        messageBody = new LCATMessageBody(jsonObject.optString("content"), atMembers);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    messageBody = ConvertMessageBodyUtils.fileExtentionToMessageBody(messageEntity.getMsgId(),
                            messageEntity.getContentType(), mediaEntity);
                }
                LCMessage message = new LCMessage(messageEntity, messageBody);
                switch (message.LCMessageEntity().getContentType()) {
                    case Msg.EMsgContentType.AUDIO_VALUE:
                    case Msg.EMsgContentType.IMAGE_VALUE:
                    case Msg.EMsgContentType.VIDEO_VALUE:
                    case Msg.EMsgContentType.FILE_VALUE:
                    case Msg.EMsgContentType.LOCATION_VALUE:
                        if (message.LCMessageEntity().getStatus() == LCMessage.Status.MSG_POST_FILE_FAIL.value()) {
                            task = HttpPostTask.newBuilder().parseMessageToTask(message);
                            break;
                        }
//                        task = HttpPostTask.newBuilder().parseMessageToTask(message);
//                        break;
                    default:
                        task = new ExcMsgTask(message);
                        break;
                }
                task.setTaskID(taskDBEntity.getTaskId());
                //                task.setRetryTimes(task.getRetryTimes() + 1);
                putExceptionTask(task);
            }
        }
    }

    /**
     * @Title: loadAllExceptionTask <br>
     * @Description: 加载所有的异常任务<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/28 15:39
     */
    public List<LCBaseTask> loadAllExcTaskFromCache() {
        Logger.d("loadAllExcTaskFromCache, cache size:" + excTaskStack.size());
        List<LCBaseTask> list = new ArrayList<>();
        synchronized (excTaskStack) {
            Iterator<Map.Entry<String, LCBaseTask>> it = excTaskStack.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, LCBaseTask> entry = it.next();
                list.add(entry.getValue());
            }
            /********************加载完，就清空堆栈*****************/
            excTaskStack.clear();
            Logger.d("excTaskStack clear");
        }
        return list;
    }

    /**
     * MethodName: offerTask <br>
     * Description: 第一次启动加载所有的异常任务 <br>
     * Creator: user<br>
     * Param:  <br>
     * Return:  <br>
     * Date: 2016/7/20 15:30
     */
    public void startAllExceptionTask() {
        Logger.d("startAllExceptionTask");
        if (startAllTaskDone.compareAndSet(false, true)) {
            //            loadAllTaskFromDB();
            List<LCBaseTask> taskList = loadAllExcTaskFromCache();
            for (LCBaseTask task : taskList) {
                Logger.d("add task to threadpool," + task.getTaskID());
                LCPoolFactory.getThreadPoolManager().addTask(task);
            }
        }
    }

    /**
     * @Title: recoverWatchDog <br>
     * @Description: 恢复定时器，并且判断有没有完全加载过<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/14 11:18
     */
    public void restoreWatchDog() {
        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
            return;
        }
        if (!watchDog.getTimerRunningFlag()) {
            watchDog.startTimer(TIMER_FIXED_DELAYED);
        }
    }

    /**
     * MethodName: stopAllTask <br>
     * Description: 停止所有正在运行的task
     * 当网络monitor 监听到网络断开，停止重发器所有任务，清空计时器
     * Creator: user<br>
     * Param:  <br>
     * Return:  <br>
     * Date: 2016/7/20 16:07
     */
    public void stopAllTask() {
        Logger.d("stopAllTask");
        if (startAllTaskDone.compareAndSet(true, false)) {
            LCPoolFactory.getThreadPoolManager()
                    .stopGivenThreadPool(LCPoolConst.THREAD_TYPE_REPEATER);
        }
    }

    /**
     * @Title: stopWatchDog  <br>
     * @Description: 停止看门狗<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/11/3 16:37
     */
    public void stopWatchDog() {
        if (watchDog != null && watchDog.getTimerRunningFlag()) {
            watchDog.cancelTimer();
        }
    }

    /**
     * @Title: clearCache <br>
     * @Description: 清除缓存和数据库数据<br>
     */
    public void clearCache() {
        Logger.d("clearCache");
        List<LCBaseTask> taskList = loadAllExcTaskFromCache();
        for (LCBaseTask task : taskList) {
            //从缓存中删除
            removeExceptionTask(task.getTaskID());
            //从数据库中删除
            ExcTaskManager.getInstance().removeDBExceptionTask(task.getTaskID());
            //回调错误异常
            LCMessage message = (LCMessage) task.getPacket();
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
            Logger.d("send error,messageid:" + message.getMsgId() + ",exc task out of time ,stop send!");
            DispatchController.getInstance().onError(message, LCError.MESSAGE_SEND_ERROR.getValue(),
                    LCError.MESSAGE_SEND_ERROR.getDesc());
        }
    }

    /**
     * @Title: onNext <br>
     * @Description: 定时器固定轮询堆栈中的异常任务<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/11/3 16:40
     */
    @Override
    public void onNext() {
        //判断时间有没有超时,超时的移除
        //在异常队列为空的情况下,不执行后续操作流程
        Logger.d("onNext");
        if (excTaskStack.size() < 1) {
            Logger.e("ExcTaskStack empty,please stop Timer");
            //停止定时器
            if (delayedTimes == 10) {
                delayedTimes = 0;
                if (watchDog != null) {
                    watchDog.cancelTimer();
                }
            } else {
                delayedTimes++;
            }
            return;
        }
        List<LCBaseTask> taskList = loadAllExcTaskFromCache();
        for (LCBaseTask task : taskList) {
            long nowTime = CommonUtils.getCurrentTime();
            if (nowTime - task.getFirstSendTime() > excTimeout) {
                Logger.d("send error,taskid:" + task.getTaskID() + ",exc task out of time ,stop send!");
                ExcTaskManager.handleSendError(task.getTaskID(), (LCMessage) task.getPacket(), CssErrorCode.ErrorCode.SERVER_INNER_ERROR);
                continue;
            } else {
                Logger.d("start exc task:FirstSendTime:"+task.getFirstSendTime());
                LCPoolFactory.getThreadPoolManager().addTask(task);
            }
        }
    }

}
