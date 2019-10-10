package com.littlec.sdk.net;

import java.io.IOException;

import com.littlec.sdk.net.callback.ProgressListener;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by zhangguoqiong on 2016/7/21.
 */
class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody,
                                ProgressListener progressListener) {
        this.responseBody = responseBody;
       this.progressListener=progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        long contentLength = 0L;
        try {
            contentLength = responseBody.contentLength();
        } catch (Exception e) {
        }
        return contentLength;
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            try {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            } catch (Exception e) {
            }
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                int progress = (int) (100.0 * totalBytesRead / responseBody.contentLength());
                if(progressListener!=null)
                progressListener.update(progress, bytesRead == -1);
                return bytesRead;
            }
        };
    }
}
