/* Project: android_im_sdk
 * 
 * File Created at 2016/8/2
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

import android.util.Log;

import com.littlec.sdk.biz.chat.entity.body.LCFileMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCImageMessageBody;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.connect.core.LCBaseTask;
import com.littlec.sdk.connect.repeater.ExcTaskManager;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.dao.UploadDBEntityDao;
import com.littlec.sdk.database.entity.UploadDBEntity;

import com.littlec.sdk.net.callback.BreakCallBack;
import com.littlec.sdk.net.callback.ProgressListener;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.ImageUtils;
import com.littlec.sdk.utils.LCLogger;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * @Type com.littlec.sdk.utils.http
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/8/2
 * @Version
 */

class UploadTask extends LCBaseTask {
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType
            .parse("text/x-markdown; charset=utf-8");
    private LCLogger logger = LCLogger.getLogger(UploadTask.class.getName());
    private LCMessage message;
    private MessageEntityDao lcMessagedbDao;
    private BreakCallBack breakCallBack;
    private UploadDBEntity uploadDBEntity;
    private UploadDBEntityDao uploadDao;
    private OkHttpClient client;
    private String uuid;
    private int count;
    private int totalCount;
    private String id;
    private String localPath;
    private File file;
    private String responseStatus;
    private String responseUrl;
    private String type;
    private String fileMd5;
    private long totalSize;
    private long completedSize;
    private int uploadStatus = HttpTaskStatus.HTTP_STATUS_INIT;
    private String fileName;
    //    private List<UploadTaskListener> listeners = new ArrayList<>();
    private ProgressListener progressListener;

    public UploadTask(Object packet, int threadPoolType, String threadTaskName) {
        super(packet, threadPoolType, threadTaskName);
    }

    public UploadTask(LCMessage message, int threadPoolType) {
        super(message, threadPoolType);
        this.message = message;
    }

    @Override
    public void run() {
        lcMessagedbDao = DBFactory.getDBManager().getDBMessageService();
        logger.d("begin upLoad");
        uploadStatus = HttpTaskStatus.HTTP_STATUS_PREPARE;
        try {
            file = new File(((LCFileMessageBody) message.LCMessageBody()).getLocalPath());
            fileName = file.getName();
            fileMd5 = CommonUtils.getFileMD5(file.getAbsolutePath());
            logger.e("md5:" + fileMd5);
            long filelength = file.length();//总字节数
//            if (checkFileMd5())
            if (false)
                return;
            else {
                if (message.contentType() == LCMessage.ContentType.IMAGE) {
                    if (!((LCImageMessageBody) message.LCMessageBody()).getisOrigin()) {
                        if (new File(ImageUtils
                                .localPathToSmallPath(((LCImageMessageBody) message.LCMessageBody())
                                        .getLocalPath())).exists()) {
                            file=new File(ImageUtils
                                    .localPathToSmallPath(((LCImageMessageBody) message.LCMessageBody())
                                            .getLocalPath()));
                            fileName=file.getName();
                            filelength=file.length();
                        }
                    }
                }
                if (filelength > 20 * 1024 * 1024) {//大于20兆断点续传
                    postBigFile(filelength);
                } else {
                    postSmallFile();
                }
            }
        } catch (FileNotFoundException e) {
            logger.d("文件没发现");
            uploadStatus = HttpTaskStatus.HTTP_STATUS_ERROR;
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_POST_FILE_FAIL.getValue(),
                    LCError.MESSAGE_POST_FILE_FAIL.getDesc());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            lcMessagedbDao.update(message.LCMessageEntity());
            if (getTaskTag().equals(TaskTag.ABNORMAL)) {
                logger.e("删除异常重发表");
                //删除缓存中的数据
                ExcTaskManager.getInstance().removeExceptionTask(getTaskID());
                //删除数据库中的数据
                ExcTaskManager.getInstance().removeDBExceptionTask(getTaskID());
            }
            UploadFactory.getUploadManager().cancel(getId());
            return;
        } catch (JSONException e) {
            logger.d("json解析失败");
            uploadStatus = HttpTaskStatus.HTTP_STATUS_ERROR;
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_POST_FILE_FAIL.getValue(),
                    LCError.MESSAGE_POST_FILE_FAIL.getDesc());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            lcMessagedbDao.update(message.LCMessageEntity());
            if (getTaskTag().equals(TaskTag.ABNORMAL)) {
                logger.e("删除异常重发表");
                //删除缓存中的数据
                ExcTaskManager.getInstance().removeExceptionTask(getTaskID());
                //删除数据库中的数据
                ExcTaskManager.getInstance().removeDBExceptionTask(getTaskID());
            }
            UploadFactory.getUploadManager().cancel(getId());
            return;
        } catch (LCException e) {
            logger.e(e.getErrorCode() + e.getDescription());
            DispatchController.getInstance().onError(message, e.getErrorCode(), e.getDescription());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            lcMessagedbDao.update(message.LCMessageEntity());
            if (getTaskTag().equals(TaskTag.ABNORMAL)) {
                logger.e("删除异常重发表");
                //删除缓存中的数据
                ExcTaskManager.getInstance().removeExceptionTask(getTaskID());
                //删除数据库中的数据
                ExcTaskManager.getInstance().removeDBExceptionTask(getTaskID());
            }
            UploadFactory.getUploadManager().cancel(getId());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            logger.d("网络异常,重新上传文件");
            logger.d("异常msgId" + message.getMsgId());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_POST_FILE_FAIL.value());
            lcMessagedbDao.update(message.LCMessageEntity());
            if (retryTimes == 0) {
                logger.d("retryTimes 0");
                ExcTaskManager.getInstance().putExceptionPacket(message);
            }
            else {
                logger.d("retryTimes " + retryTimes);
                resetTask();
            }
        }

    }

    public boolean checkFileMd5() {
        try {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            builder.add("md5", fileMd5);
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder().url(LCChatConfig.ServerConfig.getMd5Address())
                    .post(requestBody).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                logger.d(result);
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.optString("status");
                if (status.equals("600")) {
                    breakCallBack.success(result, message);
                    return true;
                } else
                    return false;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void postBigFile(long fileLength) throws Exception {
        uploadDBEntity = DBFactory.getDBManager().getDBUploadService().load(id);
        if (uploadDBEntity == null)
            logger.d("uoloadDBentity为null" + id);
        uuid = "";
        count = 0;
        totalCount = 0;
        if (uploadDBEntity != null) {
            count = uploadDBEntity.getCount();
            uuid = uploadDBEntity.getUuid();
            completedSize = uploadDBEntity.getCompletedSize();
            uploadDBEntity.setTotalSize(fileLength);
            DBFactory.getDBManager().getDBUploadService().update(uploadDBEntity);
        }
        if (completedSize >= fileLength) {
            uploadStatus = HttpTaskStatus.HTTP_STATUS_COMPLETED;
            logger.d("task have completed");
            return;
        }
        uploadStatus = HttpTaskStatus.HTTP_STATUS_START;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("offset", "");
        jsonObject.put("md5", fileMd5);
        jsonObject.put("type", type);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (fileType.equals("gif")) {
            jsonObject.put("type", "gif");
        }
        jsonObject.put("suffix", fileType);

        FileInputStream fis = new FileInputStream(file);
        FileChannel fch = fis.getChannel();
        long PieceSize = 500 * 1024;//每个小文件大小
        ByteBuffer bb = ByteBuffer.allocate((int) PieceSize);
        for (int i = 0;; i++) {
            if (uploadStatus != HttpTaskStatus.HTTP_STATUS_PAUSE
                    && uploadStatus != HttpTaskStatus.HTTP_STATUS_CANCEL) {
                bb.clear();
                fch.position(completedSize);
                final int bytesRead = fch.read(bb);
                if (bytesRead == -1) {
                    logger.e("file have been uploaded  and break");
                    break;
                }
                byte[] bytes = bb.array();
                //                logger.d("bytesRead :" + bytesRead + ",PIECE_SIZE:" + PieceSize);
                if (bytesRead != PieceSize) {// trim
                    bytes = Arrays.copyOf(bytes, bytesRead);
                }
                count++;
                RequestBody fileBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, bytes);
                if (fileLength - completedSize < PieceSize) {
                    jsonObject.put("offset", 0);
                } else {
                    jsonObject.put("offset", count);
                }
                if (uuid != "") {
                    jsonObject.put("uuid", uuid);
                }
                completedSize = completedSize + bytesRead;
                MultipartBuilder multipartBuilder = new MultipartBuilder();
                multipartBuilder.addFormDataPart("param", jsonObject.toString());
                multipartBuilder.addFormDataPart("file", "file", fileBody);
                RequestBody requestBody = multipartBuilder.build();
                ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody,
                        progressListener, id);
                Request request = new Request.Builder()
                        .url(LCChatConfig.ServerConfig.getBigFileAddress())
                        .post(progressRequestBody).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    logger.d(result);
                    JSONObject jsonObject1 = new JSONObject(result);
                    responseStatus = jsonObject1.optString("status");
                    if (responseStatus != null) {
                        if (responseStatus.equals("700") || responseStatus.equals("600")) {
                            if (jsonObject1.optString("uuid") != null) {
                                uuid = jsonObject1.optString("uuid");
                                uploadDBEntity.setUuid(uuid);
                                uploadDBEntity.setCompletedSize(completedSize);
                                uploadDBEntity.setCount(count);
                                DBFactory.getDBManager().getDBUploadService()
                                        .update(uploadDBEntity);
                            }
                        }
                        if (responseStatus.equals("600")) {
                            this.setResponseUrl(jsonObject1.optString("original_link"));
                            message.LCMessageEntity()
                                    .setStatus(LCMessage.Status.MSG_SEND_PROGRESS.value());
                            lcMessagedbDao.update(message.LCMessageEntity());
                            breakCallBack.success(result, message);
                            if (message.contentType() == LCMessage.ContentType.IMAGE) {
                                if (!((LCImageMessageBody) message.LCMessageBody()).getisOrigin()){
                                    if(file.exists())
                                    file.delete();
                                }
                            }
                            DBFactory.getDBManager().getDBUploadService().deleteByKey(id);
                            if (getTaskTag().equals(TaskTag.ABNORMAL)) {
                                //删除缓存中的数据
                                ExcTaskManager.getInstance().removeExceptionTask(getTaskID());
                                //删除数据库中的数据
                                logger.d(getTaskID());
                                ExcTaskManager.getInstance().removeDBExceptionTask(getTaskID());
                            }
                        }
                        if (responseStatus.equals("670")) {
                            count = 0;
                            completedSize = 0;
                            uuid = "";
                            uploadDBEntity.setUuid(uuid);
                            uploadDBEntity.setCompletedSize(completedSize);
                            uploadDBEntity.setCount(count);
                            DBFactory.getDBManager().getDBUploadService().update(uploadDBEntity);
                            //                            throw new LCException(LCError.MESSAGE_FILE_OVER_DATE);
                        }
                        if (responseStatus.equals("680")) {
                            count = jsonObject1.optInt("message") - 1;
                            completedSize = PieceSize * count;
                            uploadDBEntity.setCount(count);
                            uploadDBEntity.setCompletedSize(completedSize);
                            DBFactory.getDBManager().getDBUploadService().update(uploadDBEntity);

                        }
                        logger.d(responseStatus);
                        if (!responseStatus.equals("700") && !responseStatus.equals("600")
                                && !responseStatus.equals("670") && !responseStatus.equals("680")) {
                            throw new LCException("文件信息错误"+responseStatus);
                        }
                    }
                } else {
                    logger.d("response返回为空");
                    if (uploadDBEntity.getCount() != 0) {
                        throw new Exception();
                    } else {
                        DispatchController.getInstance().onError(message,
                                LCError.MESSAGE_POST_FILE_FAIL.getValue(),
                                LCError.MESSAGE_POST_FILE_FAIL.getDesc());
                        message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
                        lcMessagedbDao.update(message.LCMessageEntity());
                        break;
                    }
                }
            } else {
                break;
            }
        }
        if (fis != null) {
            fis.close();
        }
    }

    private void postSmallFile() throws Exception {
        MultipartBuilder multipartBuilder = null;
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("type", type);
//        jsonObject.put("md5", fileMd5);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (fileType.equals("gif")) {
            jsonObject.put("type", "gif");
        }
        multipartBuilder = new MultipartBuilder().addFormDataPart("param", jsonObject.toString())
                .addFormDataPart("file", "file", fileBody);
        Log.e("1111", "postSmallFile: "+jsonObject.toString());
        RequestBody requestBody = multipartBuilder.build();
        ProgressListener progressListener = new ProgressListener() {
            public void onSuccess() {
            }

            public void onError(String error) {
            }

            @Override
            public void update(int progress, boolean done) {
                DispatchController.getInstance().onProgress(message, progress);
            }
        };
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody,
                progressListener, null);
        Request request = new Request.Builder().url(LCChatConfig.ServerConfig.getSmallFileAddress())
                .post(progressRequestBody).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String result = response.body().string();
            logger.d(result);
            JSONObject jsonObject1 = new JSONObject(result);
            if (jsonObject1.optString("status") != null) {
                responseStatus = jsonObject1.optString("status");
                if (responseStatus.equals("600") || responseStatus.equals("640")) {
                    breakCallBack.success(result, message);
                    if (message.contentType() == LCMessage.ContentType.IMAGE) {
                        if (!((LCImageMessageBody) message.LCMessageBody()).getisOrigin()){
                            if(file.exists())
                            file.delete();
                        }
                    }
                    if (getTaskTag().equals(TaskTag.ABNORMAL)) {
                        //删除缓存中的数据
                        ExcTaskManager.getInstance().removeExceptionTask(getTaskID());
                        //删除数据库中的数据
                        logger.d(getTaskID());
                        ExcTaskManager.getInstance().removeDBExceptionTask(getTaskID());
                    }
                } else {
                    throw new LCException("文件信息错误"+responseStatus);
                }
            }
        } else {
            logger.d("response返回为空");
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_POST_FILE_FAIL.getValue(),
                    LCError.MESSAGE_POST_FILE_FAIL.getDesc());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            lcMessagedbDao.update(message.LCMessageEntity());
        }
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public void setHttpClient(OkHttpClient client) {
        this.client = client;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDbEntity(UploadDBEntity uploadDBEntity) {
        this.uploadDBEntity = uploadDBEntity;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long toolSize) {
        this.totalSize = toolSize;
    }

    public long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public BreakCallBack getCallback() {
        return breakCallBack;
    }

    public void setCallback(BreakCallBack breakCallBack) {
        this.breakCallBack = breakCallBack;
    }

    public void setType(String type) {
        this.type = type;
    }

    /* public static UploadTask parse(UploadDBEntity entity) {
    
        UploadTask task = new UploadTask(message);
        task.setUploadStatus(entity.getUploadStatus());
        task.setId(entity.getUploadId());
        task.setLocalPath(entity.getLocalPath());
        task.setDbEntity(entity);
        task.setUuid(entity.getUuid());
        task.setCount(entity.getCount());
        return task;
    
    }*/
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/2 zhangguoqiong creat
 */
