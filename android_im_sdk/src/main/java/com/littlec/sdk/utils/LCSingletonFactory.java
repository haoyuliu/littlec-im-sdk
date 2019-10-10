package com.littlec.sdk.utils;

import com.littlec.sdk.connect.LCGrpcManager;
import com.littlec.sdk.connect.RpcChannelManager;
import com.littlec.sdk.config.LCChatConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: LCSingletonFactory
 * Description:  所有单列模式的工厂类
 * Creator: user
 * Date: 2016/7/20 10:32
 */
public class LCSingletonFactory {
    private static final String TAG = "LCSingletonFactory";
    private static LCLogger Logger = LCLogger.getLogger(TAG);

    private static class SingletonFactoryHolder {
        private static Map<String, Object> objectCache = Collections.synchronizedMap(new HashMap());
    }

    /**
     * MethodName: getInstance <br>
     * Description: 获取指定类的实列，不存在则创建实列 <br>
     * Creator: user<br>
     * Param:  <br>
     * Return:  <br>
     * Date: 2016/7/20 10:37
     */
    public static <T> T getInstance(Class<T> clazz) {
        T result = (T) SingletonFactoryHolder.objectCache.get(clazz.getName());
        synchronized (LCSingletonFactory.class) {
            if (result == null) {
                result = createInstance(clazz);
                if (result != null) {
                    SingletonFactoryHolder.objectCache.put(clazz.getName(), result);
                }
            }
        }
        return result;
    }

    private static <T> T createInstance(Class<T> clazz) {
        T t = null;
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            t = (T) constructor.newInstance();
        } catch (InstantiationException e) {
            Logger.e(e);
        } catch (IllegalAccessException e) {
            Logger.e(e);
        } catch (NoSuchMethodException e) {
            Logger.e(e);
        } catch (InvocationTargetException e) {
            Logger.e(e);
        }
        return t;
    }

    private static <T> T createInstance(Class<T> clazz, Class<?>[] parameterTypes,
                                        Object[] paramValues) {
        T t = null;
        try {
            t = clazz.getConstructor(parameterTypes).newInstance(paramValues);
        } catch (InstantiationException e) {
            Logger.e(e);
        } catch (Exception e) {
            Logger.e(e);
        }
        return t;
    }

    public static void releaseCache() {
        try {
            if (RpcChannelManager.getInstance().getChannel() != null) {//有可能还没有登陆就直接调用退出接口
                LCSingletonFactory.getInstance(LCGrpcManager.class).onDestroy();
            }
            NetworkMonitor.unRegisterReceiver(LCChatConfig.LCChatGlobalStorage.getInstance().getContext());
            if (SingletonFactoryHolder.objectCache != null) {
                SingletonFactoryHolder.objectCache.clear();
            }
        } catch (Exception e) {

        }
    }

    protected void finalize() throws Throwable {
        releaseCache();
        super.finalize();
    }
}
