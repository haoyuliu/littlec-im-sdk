package com.littlec.sdk.utils;

/**
 * @Type IdWorker.java
 * @Desc Snowflake id generater. 
 * This class can generate an unique orderd 64 bit id, 
 * if assign an unique worker id, this class can generate an unique id in multi-node cluster.
 * @author wangshenghz
 * @date 2016年8月12日 下午3:46:09
 * @version 
 */

public class IdWorker {

    private static IdWorker instance;

    static {
        instance = new IdWorker(0);
    }

    private final long twepoch = 1468976307657L; // 2016/7/20 8:58:27.657
    private final long workerIdBits = 10L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * Provides singleton access to an instance of the IdWorker class.
     * @return an IdWorker instance
     */
    public static IdWorker getInstance() {
        return instance;
    }

    /**
     * IdWorker constructor
     * @param workerId The worker id is a assigned value between 0 and 1023 which is used to differentiate 
     *        different snowflakes when used in a multi-node cluster.
     */
    public IdWorker(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * restore guid to timestamp
     * @param guid The id generated by IdWorker
     * @return timestamp
     */
    public long restore(long guid) {
        return (guid >> timestampLeftShift) + twepoch;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public static void main(String[] args) {
        IdWorker idWorker = IdWorker.getInstance();
        for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();
            System.out.println("id:" + id);
            long timeStamp = idWorker.restore(id);
            System.out.println("timeStamp:" + timeStamp);
        }
    }
}

/**
 * Revision history
 * -------------------------------------------------------------------------
 * 
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016年8月12日 wangshenghz create
 */
