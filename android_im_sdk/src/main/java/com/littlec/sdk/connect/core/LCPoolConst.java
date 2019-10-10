/**
 * Title: LCPoolConst.java
 * Description:
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 上午10:37:47
 * Version: 1.0
 */
package com.littlec.sdk.connect.core;

/**
 * ClassName: LCPoolConst
 * Description:  线程池相关的常量
 * Creator: user
 * Date: 2016/7/19 17:22
 */
public class LCPoolConst {

    /**
     * http请求线程池
     */
    public static final int THREAD_TYPE_SIMPLE_HTTP = 1;

    /**
     * 消息传输线程池
     */
    public static final int THREAD_TYPE_MESSAGE = 2;

    /**
     * 重发器
     */
    public static final int THREAD_TYPE_REPEATER = 3;

    /**
     * 其他请求池
     */
    public static final int THREAD_TYPE_OTHER = 4;

    /**
     * 空闲线程存活时间,60秒
     */
    public static final long KEEP_ALIVE_TIME = 10;

    /**
     * 默认核心线程池大小
     */
    public final static int DEFAULT_CORE_POOL_SIZE = 5;

    /**
     * 有界队列长度
     */
    public final static int DEFAULT_WORK_QUEUE_SIZE = 10;
}
