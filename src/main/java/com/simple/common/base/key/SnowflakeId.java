package com.simple.common.base.key;


/**
 * @author 高振中
 * @summary 【雪花算法】主键生成
 * @date 2024-05-10 21:45:31
 **/
public class SnowflakeId {
    private final static long START_TIME = 898608000000L;
    //15661422735631L
    private final static byte MAX_WORKER_ID = 31;
    private final static byte MAX_DATA_CENTER_ID = 31;
    private final static long WORKER_ID_MOVE_BITS = 12;
    private final static long DATA_CENTER_ID_MOVE_BITS = 17;
    private final static byte TIMESTAMP_MOVE_BITS = 22;
    private final static short SEQUENCE_MASK = 4095;
    private static long workerId;
    private static long dataCenterId;
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    public static void setDataCenterId(long dataCenterId) {
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("DataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        SnowflakeId.dataCenterId = dataCenterId;
    }

    public static void setWorkerId(long workerId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        SnowflakeId.workerId = workerId;
    }

    /**
     * 线程安全的获得下一个 ID 的方法
     */
    public static synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = blockTillNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - START_TIME) << TIMESTAMP_MOVE_BITS) | (dataCenterId << DATA_CENTER_ID_MOVE_BITS) | (workerId << WORKER_ID_MOVE_BITS) | sequence;
    }

    /**
     * id 逆转为毫秒时间戳
     *
     * @param id ID
     * @return Long
     */
    public static Long reverseId(Long id) {
        return (id >> TIMESTAMP_MOVE_BITS) + START_TIME;
    }

    /**
     * 阻塞到下一个毫秒 即 直到获得新的时间戳
     */
    protected static long blockTillNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 9; i ++) {
            System.out.println(nextId());
        }
    }
}
