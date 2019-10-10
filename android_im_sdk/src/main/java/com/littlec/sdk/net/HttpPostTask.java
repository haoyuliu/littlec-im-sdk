package com.littlec.sdk.net;

import com.littlec.sdk.biz.chat.entity.body.LCAudioMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCFileMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCImageMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCLocationMessageBody;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCVideoMessageBody;
import com.littlec.sdk.connect.core.LCPoolConst;
import com.littlec.sdk.connect.core.LCPoolFactory;
import com.littlec.sdk.connect.core.LCCmdServiceFactory;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.entity.MediaEntity;
import com.fingo.littlec.proto.css.Msg;
import com.littlec.sdk.net.callback.BreakCallBack;
import com.littlec.sdk.net.callback.ProgressListener;
import com.littlec.sdk.utils.LCLogger;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: HttpPostTask
 * Description:  实现post请求
 * Creator: zhangguoqiong
 * Date: 2016/7/17 22:26
 */
public class HttpPostTask extends HttpBaseTask {
    //    private LCMessagedbDao lcMessagedbDao;
    private LCLogger logger = LCLogger.getLogger("HttpPostTask");

    public HttpPostTask(String url) {
        setUrl(url);
    }

    public HttpPostTask() {

    }

    public static HttpPostTask newBuilder() {
        return LCSingletonFactory.getInstance(HttpPostTask.class);
        //        return new HttpPostTask();
    }

    @Override
    protected Request buildRequest() {
        Request request = new Request.Builder().url(getUrl()).post(getRequestBody()).build();
        return request;
    }

    @Override
    public Response doSynsExcute() {
        Response response = null;
        try {
            response = client.newCall(buildRequest()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void doAynsExcute() {
        client.newCall(buildRequest()).enqueue(getCallBack());
    }

    public void checkFileMd5(String fileMd5,Callback callback) {
        try {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            builder.add("md5", fileMd5);
            RequestBody requestBody = builder.build();
            setRequestBody(requestBody);
            setUrl(LCChatConfig.ServerConfig.getMd5Address());
            setCallback(callback);
            doAynsExcute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void uploadAvatar(String imagePath, Callback callback) {
        try {
            File file = new File(imagePath);
            String fileName = file.getName();
            MultipartBuilder multipartBuilder = null;
            RequestBody fileBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fileName", fileName);
            jsonObject.put("type", "image");
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (fileType.equals("gif")) {
                jsonObject.put("type", "gif");
            }
            multipartBuilder = new MultipartBuilder()
                    .addFormDataPart("param", jsonObject.toString())
                    .addFormDataPart("file", "file", fileBody);
            RequestBody requestBody = multipartBuilder.build();
            setRequestBody(requestBody);
            setUrl(LCChatConfig.ServerConfig.getSmallFileAddress());
            setCallback(callback);
            doAynsExcute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void uploadCrashTask() {
        File file = new File(LCChatConfig.LCChatGlobalStorage.LC_CRASH_LOG_PATH);
        File[] files = file.listFiles();
        if (files != null) {
            UploadCrashTask uploadCrashTask = new UploadCrashTask(files,
                    LCPoolConst.THREAD_TYPE_SIMPLE_HTTP);
            client.setConnectTimeout(50, TimeUnit.SECONDS);
            client.setReadTimeout(600, TimeUnit.SECONDS);
            client.setWriteTimeout(600, TimeUnit.SECONDS);
            uploadCrashTask.setHttpClient(client);
            LCPoolFactory.getThreadPoolManager().addTask(uploadCrashTask);
        }
    }

    public UploadTask parseMessageToTask(final LCMessage message) {
        UploadTask uploadTask = new UploadTask(message, LCPoolConst.THREAD_TYPE_SIMPLE_HTTP);
        uploadTask.setId(message.LCMessageEntity().getMsgId());
        client.setConnectTimeout(50, TimeUnit.SECONDS);
        client.setReadTimeout(600, TimeUnit.SECONDS);
        client.setWriteTimeout(600, TimeUnit.SECONDS);
        uploadTask.setHttpClient(client);
        if (message != null) {
            switch (message.LCMessageEntity().getContentType()) {
                case Msg.EMsgContentType.IMAGE_VALUE:
                    uploadTask.setType("image");
                    uploadTask.setCallback(postImageFileBreakCallBack);
                    break;
                case Msg.EMsgContentType.AUDIO_VALUE:
                    uploadTask.setType("audio");
                    uploadTask.setCallback(postAudioFileBreakCallBack);
                    break;
                case Msg.EMsgContentType.VIDEO_VALUE:
                    uploadTask.setType("video");
                    uploadTask.setCallback(postVideoFileBreakCallBack);
                    break;
                case Msg.EMsgContentType.LOCATION_VALUE:
                    uploadTask.setType("location");
                    uploadTask.setCallback(postLocationBreakCallBack);

            }
        }
        ProgressListener progressListener = new ProgressListener() {
            public void onSuccess() {
            }

            public void onError(String error) {
            }

            @Override
            public void update(int progress, boolean done) {
                DispatchController.getInstance().onProgress(message,
                            progress);
            }
        };
        uploadTask.setProgressListener(progressListener);
        return uploadTask;
    }

    public void uploadFile(final LCMessage message) {
        client.setConnectTimeout(50, TimeUnit.SECONDS);
        client.setReadTimeout(600, TimeUnit.SECONDS);
        client.setWriteTimeout(600, TimeUnit.SECONDS);
        UploadManager uploadManager = UploadFactory.getUploadManager();
        uploadManager.init(client);
        ProgressListener progressListener = new ProgressListener() {
            public void onSuccess() {
            }

            public void onError(String error) {
            }

            @Override
            public void update(int progress, boolean done) {
                   DispatchController.getInstance().onProgress(message,
                            progress);
            }
        };
        UploadTask uploadTask = new UploadTask(message, LCPoolConst.THREAD_TYPE_SIMPLE_HTTP);
        uploadTask.setId(message.LCMessageEntity().getMsgId());
        uploadTask.setProgressListener(progressListener);
        if (message != null) {
            switch (message.LCMessageEntity().getContentType()) {
                case Msg.EMsgContentType.IMAGE_VALUE:
                    uploadTask.setType("image");
                    uploadManager.addUploadTask(uploadTask, postImageFileBreakCallBack);
                    break;
                case Msg.EMsgContentType.AUDIO_VALUE:
                    uploadTask.setType("audio");
                    uploadManager.addUploadTask(uploadTask, postAudioFileBreakCallBack);
                    break;
                case Msg.EMsgContentType.VIDEO_VALUE:
                    uploadTask.setType("video");
                    uploadManager.addUploadTask(uploadTask, postVideoFileBreakCallBack);
                    break;
                case Msg.EMsgContentType.LOCATION_VALUE:
                    uploadTask.setType("location");
                    uploadManager.addUploadTask(uploadTask, postLocationBreakCallBack);
                    break;
                case Msg.EMsgContentType.FILE_VALUE:
                    uploadTask.setType("other");
                    uploadManager.addUploadTask(uploadTask, postFileBreakCallBack);
                    break;
                default:
                    break;

            }
        }
    }

    private BreakCallBack postImageFileBreakCallBack = new BreakCallBack() {
        @Override
        public void success(String result, LCMessage message) {
            if (result != null) {
                System.out.println(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int small_width = 0;
                    int small_height = 0;
                    int middle_width = 0;
                    int middle_height = 0;
                    String original_link = jsonObject.optString("original_link","");
                    String small_link = jsonObject.optString("small_link","");
                    String middle_link = jsonObject.optString("middle_link","");
                    String large_link = jsonObject.optString("large_link","");
                    if (!small_link.isEmpty()) {
                        small_width = Integer.parseInt(small_link.substring(
                                small_link.lastIndexOf("_") + 1, small_link.lastIndexOf("x")));
                        small_height = Integer.parseInt(small_link.substring(
                                small_link.lastIndexOf("x") + 1, small_link.lastIndexOf(".")));
                    }
                    if (!middle_link.isEmpty()) {
                        middle_width = Integer.parseInt(middle_link.substring(
                                middle_link.lastIndexOf("_") + 1, middle_link.lastIndexOf("x")));
                        middle_height = Integer.parseInt(middle_link.substring(
                                middle_link.lastIndexOf("x") + 1, middle_link.lastIndexOf(".")));
                    }
                    MediaEntity fileMessageExtention = message.LCMessageEntity().getMediaEntity();
                    fileMessageExtention.setOriginalLink(original_link);
                    fileMessageExtention.setSmallLink(small_link);
                    fileMessageExtention.setMiddleLink(middle_link);
                    fileMessageExtention.setLargeLink(large_link);
                    DBFactory.getDBManager().getDBMediaService().update(fileMessageExtention);
                    LCImageMessageBody body = (LCImageMessageBody) message.LCMessageBody();
                    body.setSmall_height(small_height);
                    body.setSmall_width(small_width);
                    if (middle_height != 0 && middle_width != 0) {
                        body.setMiddle_width(middle_width);
                        body.setMiddle_height(middle_height);
                    }
                    body.setOriginalUri(original_link);
                    body.setSmallUri(small_link);
                    body.setMiddleUri(middle_link);
                    body.setLargeUri(large_link);
                    LCCmdServiceFactory.getMessageService().sendPacket(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private BreakCallBack postAudioFileBreakCallBack = new BreakCallBack() {
        @Override
        public void success(String result, LCMessage message) {
            if (result != null) {
                System.out.println(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String original_link = jsonObject.optString("original_link");
                    //                    long fileLength = jsonObject.optLong("size");
                    //                    int duration = jsonObject.optInt("duration");
                    LCAudioMessageBody body = (LCAudioMessageBody) message.LCMessageBody();
                    body.setOriginalUri(original_link);
                    //                    body.setFileLength(fileLength);
                    //                    body.setDuration(duration);
                    MediaEntity fileMessageExtention = message.LCMessageEntity().getMediaEntity();
                    fileMessageExtention.setOriginalLink(original_link);
//                    fileMessageExtention.setDuration(body.getDuration());
                    DBFactory.getDBManager().getDBMediaService().update(fileMessageExtention);
                    LCCmdServiceFactory.getMessageService().sendPacket(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private BreakCallBack postVideoFileBreakCallBack = new BreakCallBack() {
        @Override
        public void success(String result, LCMessage message) {
            System.out.println(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String original_link = jsonObject.optString("original_link");
                String small_link = jsonObject.optString("cover_link");
                int small_width = Integer.parseInt(small_link
                        .substring(small_link.lastIndexOf("_") + 1, small_link.lastIndexOf("x")));
                int small_height = Integer.parseInt(small_link
                        .substring(small_link.lastIndexOf("x") + 1, small_link.lastIndexOf(".")));
                //                int duration = jsonObject.optInt("duration");
                LCVideoMessageBody body = (LCVideoMessageBody) message.LCMessageBody();
                MediaEntity fileMessageExtention = message.LCMessageEntity().getMediaEntity();
                fileMessageExtention.setOriginalLink(original_link);
                fileMessageExtention.setSmallLink(small_link);
                fileMessageExtention.setDuration(body.getDuration());
                fileMessageExtention.setThumbPath(body.getThumbPath());
                DBFactory.getDBManager().getDBMediaService().update(fileMessageExtention);
                body.setOriginalUri(original_link);
                body.setThumbnailUrl(small_link);
                //                body.setDuration(duration);
                body.setHeight(small_height);
                body.setWidth(small_width);
                LCCmdServiceFactory.getMessageService().sendPacket(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BreakCallBack postLocationBreakCallBack = new BreakCallBack() {
        @Override
        public void success(String result, LCMessage message) {
            System.out.println(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String small_link = jsonObject.optString("small_link");
                int small_width = Integer.parseInt(small_link
                        .substring(small_link.lastIndexOf("_") + 1, small_link.lastIndexOf("x")));
                int small_height = Integer.parseInt(small_link
                        .substring(small_link.lastIndexOf("x") + 1, small_link.lastIndexOf(".")));
                MediaEntity fileMessageExtention = message.LCMessageEntity().getMediaEntity();
                fileMessageExtention.setOriginalLink(small_link);
                DBFactory.getDBManager().getDBMediaService().update(fileMessageExtention);
                LCLocationMessageBody body = (LCLocationMessageBody) message.LCMessageBody();
                body.setOriginalUri(small_link);
                body.setHeight(small_height);
                body.setWidth(small_width);
                LCCmdServiceFactory.getMessageService().sendPacket(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private BreakCallBack postFileBreakCallBack = new BreakCallBack() {
        @Override
        public void success(String result, LCMessage message) {
            if (result != null) {
                System.out.println(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String original_link = jsonObject.optString("original_link");
                    LCFileMessageBody body = (LCFileMessageBody) message.LCMessageBody();
                    body.setOriginalUri(original_link);
                    MediaEntity fileMessageExtention = message.LCMessageEntity().getMediaEntity();
                    fileMessageExtention.setOriginalLink(original_link);
                    DBFactory.getDBManager().getDBMediaService().update(fileMessageExtention);
                    LCCmdServiceFactory.getMessageService().sendPacket(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
