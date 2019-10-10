package com.littlec.sdk.net;

//import com.squareup.okhttp.RequestBody;

import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.entity.UploadDBEntity;
import com.littlec.sdk.net.callback.ProgressListener;
import com.littlec.sdk.utils.LCLogger;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by zhangguoqiong on 2016/7/20.
 */
 class ProgressRequestBody extends RequestBody {
    //实际的待包装请求体
    private final RequestBody requestBody;
    //进度回调接口
    private final ProgressListener progressListener;
    //包装完成的BufferedSink
    private BufferedSink bufferedSink;

    private UploadDBEntity uploadDBEntity;

    private String id;
    private LCLogger logger=LCLogger.getLogger("ProgressRequestBody");

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener,
                               String id) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
        this.id = id;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
//        logger.e("任务id"+id);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    /**
     * 写入，回调进度接口
     */
    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度
            long contentLength = 0L;
            long completedSize = 0;
            int progress = 0;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);

                if (completedSize == 0) {
                    if (id!= null)
                        uploadDBEntity =DBFactory.getDBManager().getDBUploadService().load(id);
                    if (uploadDBEntity != null) {
                        completedSize = uploadDBEntity.getCompletedSize();
                        contentLength = uploadDBEntity.getTotalSize();
                        bytesWritten += completedSize;
                    }
                }
                bytesWritten += byteCount;
                if (DBFactory.getDBManager().getDBUploadService() != null && uploadDBEntity != null) {
                    progress = (int) (100.0 * bytesWritten / contentLength);
                } else {
                    progress = (int) (100.0 * bytesWritten / contentLength());
                }
                progressListener.update(progress, bytesWritten == contentLength);
            }
        };
    }
}
