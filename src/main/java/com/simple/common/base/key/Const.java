package com.simple.common.base.key;

import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * @author 高振中
 * @summary 常量接口
 * @date 2024-05-10 21:45:31
 **/
public interface Const {

 

    /**
     * 【主键类型】常量
     **/
    interface IdType {
        String SNOW = "snow";/* 雪花算法分布式主键 */
        String AUTO = "auto";/* 数据库自增主键 */
        String UUID = "uuid";/* UUID主键 */
        String CUSTOM = "custom";/* 程序中给定主键 */
    }

    interface Sql {
        String ALIAS = " t";
        String SELECT = "SELECT ";
        String DELETE = "DELETE ";
        String INSERT = "INSERT ";
        String REPLACE = "REPLACE ";
        String INTO = "INTO ";
        String UPDATE = "UPDATE ";
        String REPLACE_INTO = "REPLACE INTO ";
        String INSERT_INTO = "INSERT INTO ";
        String FROM = " FROM ";
        String EQQ = "=?";
        String EQQ_COMMA = "=?,";
        String IN = " IN ";
        String WHERE = " WHERE ";
        String WHERE_T = " WHERE t.";
        String T_SET = " t SET ";
        String T = "t.";
        String COMMA = ",";
        String COLON = ":";
        String NULL = "NULL";
        String QUOT = "'";
        String LEFT_BRACKET = " (";
        String R_BRACKET = ")";
        String VALUES = " VALUES (";
        String R_VALUES = ") VALUES (";
        String WRAP = "\r\n";
        String SEMICOLON_WRAP = ";\r\n";
        String COUNT1 = "COUNT(1)";
        String DR_EQ = "dr=1";
        String CREATE_TIME = "createTime";
        String CREATE_BY = "createBy";
        String UPDATE_TIME = "updateTime";
        String UPDATE_BY = "updateBy";
        String DR = "dr";
        Set<String> NO_UPDATE = Set.of(CREATE_TIME, CREATE_BY);
    }

    DateTimeFormatter FORMAT_ALL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter FORMAT_HMS = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter FORMAT_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter FORMAT_SHORT_YMD = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter FORMAT_YEAR = DateTimeFormatter.ofPattern("yyyy");
    DateTimeFormatter FORMAT_ALL_SHORT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    String UNDER_LINE = "_";//下划线
    String EMPTY = "";//空串
    String BLANK = " ";//空格
    String M_LINE = "-";//中划线(减号)

    // 全局byte常量
    byte BYTE_0 = 0;
    byte BYTE_1 = 1;
    byte BYTE_2 = 2;
    byte BYTE_3 = 3;

    // 全局INT常量
    int INT_0 = 0;
    int INT_1 = 1;
    int INT_2 = 2;
    int INT_3 = 3;
    int INT_10 = 10;

    int INT_20 = 20;
    int INT_100 = 100;
    int INT_1000 = 1000;
    int INT_2000 = 2000;
    long ADMIN = 10000;
    long LONG_0 = 0;
    long LONG_1 = 1;
    long LONG_10 = 10;

    double DOUBLE_0 = 0.0;

    interface Result {
        int OK = 200;
        int ERROR = 500;
        String SUCCESS = "成功";
        String FAILURE = "失败";
    }
}
