package com.littlec.sdk.connect;

import android.text.TextUtils;
import android.util.Log;

import com.littlec.sdk.utils.LCLogger;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.okhttp.OkHttpChannelBuilder;

/**
 * @Type com.cmri.ercs.tech.net.grpc.utils.RpcChannelManager
 * @User dinglai
 * @Desc manaage the channel instance
 * @Date 2017-1-7
 * @Version
 */

public final class RpcChannelManager {
    private static final String TAG = "RpcChannelManager";
    private static RpcChannelManager _instance = new RpcChannelManager();

    private ManagedChannel _channel;
    private String baseAddress;
    private boolean isUsePlainTex;

    public static RpcChannelManager getInstance() {
        return _instance;
    }

    private RpcChannelManager() {
    }


    //初始化，如果已经初始化过了，则直接返回，只维护一个channel
    public synchronized ManagedChannel initChannel(String baseAddress, boolean isUsePlainTex) {
        LCLogger.getLogger(TAG).d("RpcChannelManager initChannel address:" + baseAddress + "  isUsePlainTex:" + isUsePlainTex);
        if (TextUtils.isEmpty(baseAddress)) {
            throw new IllegalArgumentException("initChannel failed: address is empty!");
        }
        if (_channel != null && !_channel.isShutdown() && isUsePlainTex == this.isUsePlainTex && baseAddress.equals(this.baseAddress)) {
            LCLogger.getLogger(TAG).e("initChannel channel avaliable then return it");
            return _channel;
        }
        this.baseAddress = baseAddress;
        this.isUsePlainTex = isUsePlainTex;
        if (_channel != null && !_channel.isShutdown()) {
            LCLogger.getLogger(TAG).e(" initChannel shutdown, to recreate");
            _channel.shutdownNow();
            _channel = null;
        }
        try {
            int port = 80;
            StringBuilder host = new StringBuilder(baseAddress);
            int index = baseAddress.lastIndexOf(":");
            if (index != -1) {
                port = Integer.parseInt(baseAddress.substring(index + 1));
                host.delete(index, host.length());
                if (host.toString().startsWith("[")) {
                    host.deleteCharAt(0);
                    host.deleteCharAt(host.length() - 1);
                }
            }
            _channel = OkHttpChannelBuilder.forAddress(host.toString(), port).usePlaintext(this.isUsePlainTex).build();
            LCLogger.getLogger(TAG).d(" initChannel, OkHttpChannelBuilder build");
        } catch (Exception e) {
            LCLogger.getLogger(TAG).e(" initChannel failed:" + Log.getStackTraceString(e));
        }
        return _channel;
    }


    public ManagedChannel getChannel() {
        if (_channel == null) {
            LCLogger.getLogger(TAG).e("channel is null ");
        }
        return _channel;
    }

    public String getBaseAddress() {
        return baseAddress;
    }


    public boolean isUsePlainTex() {
        return isUsePlainTex;
    }


    public synchronized void shutdownChannel() {
        LCLogger.getLogger(TAG).e("shutdownChannel");
        if (_channel != null) {
            LCLogger.getLogger(TAG).d("channel isShutdown:" + _channel.isShutdown());
            LCLogger.getLogger(TAG).d("channel isTerminated:" + _channel.isTerminated());
            if (!_channel.isShutdown()) {
                try {
                    _channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
                    LCLogger.getLogger(TAG).d("after shutdown ,isShutdown:" + _channel.isShutdown());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!_channel.isTerminated()) {
                _channel.shutdownNow();
                LCLogger.getLogger(TAG).d("not terminated，after shutdownNow, isTerminated:" + _channel.isTerminated());
            }
            baseAddress = null;
            _channel = null;
        }
    }
}
