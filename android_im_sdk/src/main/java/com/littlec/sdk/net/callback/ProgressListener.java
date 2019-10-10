package com.littlec.sdk.net.callback;

/**
 * Created by zhangguoqiong on 2016/7/20.
 */
public interface ProgressListener {
    void onSuccess();
    void update(int progress, boolean done);
    void onError(String error);
}
