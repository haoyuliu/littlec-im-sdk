/* Project: android_im_sdk
 * 
 * File Created at 2016/8/3
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.net;

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.connect.core.LCPoolFactory;
import com.littlec.sdk.database.api.GetDataFromDB;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.entity.UploadDBEntity;
import com.littlec.sdk.net.callback.BreakCallBack;
import com.littlec.sdk.utils.LCLogger;
import com.squareup.okhttp.OkHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @Type com.littlec.sdk.utils.http
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/8/3
 * @Version
 */

public class UploadManager {
    LCLogger logger = LCLogger.getLogger("UploadManager");
    private OkHttpClient client;

    public Map<String, UploadTask> getCurrentTaskList() {
        return currentTaskList;
    }

    private Map<String, UploadTask> currentTaskList = new HashMap<>();

    public void init(OkHttpClient okHttpClient) {
//        executorService = Executors.newFixedThreadPool(mPoolSize);
        if (okHttpClient != null) {
            this.client = okHttpClient;
        }
    }
    /* public static UploadManager getInstance(OkHttpClient okHttpClient) {
        if (uploadManager == null) {
            uploadManager = new UploadManager(okHttpClient);
        }
        return uploadManager;
    }*/

    public UploadManager(OkHttpClient okHttpClient) {
        init(okHttpClient);
    }

    private UploadManager() {

    }
    /**
     添加上传任务
     */
    public UploadTask addUploadTask(UploadTask task, BreakCallBack callback) {
        UploadTask uploadTask = currentTaskList.get(task.getId());
        if (null != uploadTask
                && uploadTask.getUploadStatus() != HttpTaskStatus.HTTP_STATUS_CANCEL) {
            logger.d("task already exist");
            return uploadTask;
        }
        currentTaskList.put(task.getId(), task);
        task.setUploadStatus(HttpTaskStatus.HTTP_STATUS_PREPARE);
        task.setHttpClient(client);
        task.setCallback(callback);
        if (!getDBTaskById(task.getId())) {
            UploadDBEntity dbEntity = new UploadDBEntity(task.getId(), task.getUuid(),
                    task.getLocalPath(), task.getCount(), task.getCompletedSize(),
                    task.getTotalSize(), task.getUploadStatus());
            DBFactory.getDBManager().getDBUploadService().insertOrReplace(dbEntity);
        }
        LCPoolFactory.getThreadPoolManager().addTask(task);
//        Future future = executorService.submit(task);
//        futureMap.put(task.getId(), future);
        return task;
    }

   /* public UploadTask resume(String taskId) {
        UploadTask uploadTask = getCurrentTaskById(taskId);
        if (uploadTask != null) {
            if (uploadTask.getUploadStatus() == HttpTaskStatus.HTTP_STATUS_PAUSE) {
                Future future = executorService.submit(uploadTask);
                futureMap.put(uploadTask.getId(), future);
            }

        } else {
            uploadTask = getDBTaskById(taskId);
            if (uploadTask != null) {
                currentTaskList.put(taskId, uploadTask);
                Future future = executorService.submit(uploadTask);
                futureMap.put(uploadTask.getId(), future);
            }
        }
        return uploadTask;
    }*/

    //    public DownloadTask addDownloadTask(DownloadTask task) {
    //        return addDownloadTask(task, null);
    //    }



    public void pause(String taskId) {
        UploadTask task = getCurrentTaskById(taskId);
        if (task != null) {
            task.setUploadStatus(HttpTaskStatus.HTTP_STATUS_PAUSE);
           task.cancel();
            currentTaskList.remove(task.getId());
        }
    }
    public void resume(String taskId){
        LCMessage message= GetDataFromDB.getMessageFromDB(taskId);
        if(message!=null)
        HttpPostTask.newBuilder().uploadFile(message);
    }

    public void cancel(UploadTask task) {
        task.cancel();
        currentTaskList.remove(task.getId());
//        futureMap.remove(task.getId());
        DBFactory.getDBManager().getDBUploadService().deleteByKey(task.getId());
    }

    public void cancel(String taskId) {
        UploadTask task = getCurrentTaskById(taskId);
        if (task != null) {
            task.setUploadStatus(HttpTaskStatus.HTTP_STATUS_CANCEL);
            cancel(task);
        }
    }

    public UploadTask getCurrentTaskById(String taskId) {
        return currentTaskList.get(taskId);
    }



    /**
     从数据库获取任务
     */
    public boolean getDBTaskById(String taskId) {
        UploadDBEntity entity = DBFactory.getDBManager().getDBUploadService().load(taskId);
        if (entity != null) {
            return true;
        }
        return false;
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/3 zhangguoqiong creat
 */
