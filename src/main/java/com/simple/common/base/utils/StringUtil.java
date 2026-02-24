package com.simple.common.base.utils;

import static com.simple.common.base.key.Const.INT_0;
import static com.simple.common.base.key.Const.INT_1;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.simple.common.base.key.Const;

/**
 * @author 高振中
 * @summary 字符串工具
 * @date 2024-05-10 21:45:31
 **/
public final class StringUtil {
    private StringUtil() {
    }// Cannot be constructed

    /**
     * 驼峰-->下划线
     */
    public static String toLine(final String word) {
        StringBuilder sb = new StringBuilder();
        char[] chars = word.toCharArray();
        sb.append(Character.toLowerCase(chars[INT_0]));// 第1个字符转小写
        for (int i = INT_1; i < chars.length; i++) { // 从第2个字符开始:遇到大写 前面加_字符转成小写 否则直接拼上
            sb.append(Character.isUpperCase(chars[i]) ? Const.UNDER_LINE + Character.toLowerCase(chars[i]) : chars[i]);
        }
        return sb.toString();
    }
    /**
     * 下划线-->首字母小写驼峰
     */
    public static String toLowerCamel(final String word) {
        StringBuilder sb = new StringBuilder(word);
        Matcher mc = Pattern.compile(Const.UNDER_LINE).matcher(word);
        for (int i = INT_0; mc.find(); i++) {// 每次替换_x为X,同时因为长度减少1偏移量调整1
            sb.replace(mc.end() - i - INT_1, mc.end() - i + INT_1, sb.substring(mc.end() - i, mc.end() - i + INT_1).toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 产生一个32位的UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll(Const.M_LINE, Const.EMPTY);
    }
}
