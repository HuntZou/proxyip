package jhinwins.utils;

/**
 * Created by Jhinwins on 2017/7/21  11:28.
 * Desc:
 */
public class StringUtils {
    /**
     * 判断字符串是否为空
     */
    public static boolean isNull(String str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
