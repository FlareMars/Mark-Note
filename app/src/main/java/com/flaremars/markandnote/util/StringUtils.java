package com.flaremars.markandnote.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by FlareMars on 2016/5/4.
 * 封装字符串相关操作
 */
public enum StringUtils {
    INSTANCE;

    public final static SimpleDateFormat DATE_FORMAT_SIMPLE = new SimpleDateFormat("MM-dd");
    public final static SimpleDateFormat DATE_FORMAT_FULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat DATE_FORMAT_FULL_NO_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat DATE_FORMAT_WITH_HM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final static DecimalFormat DECIMAL_TWO_FORMAT = new DecimalFormat("#0.00");
    public final static DecimalFormat DECIMAL_ONE_FORMAT = new DecimalFormat("#0.0");

    public static String decimalFormat(DecimalFormat formater, BigDecimal bigDecimal) {
        if(bigDecimal == null) {
            return "null";
        } else {
            return formater.format(bigDecimal);
        }
    }

    public boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 将字符串中的反斜杠转为斜杠
     * @param src 源字符串
     * @return 转化后字符串
     */
    public String transformBackSlant(String src) {
        if (src == null) {
            return "";
        }
        return src.replace("\\", "/");
    }

    public boolean validateEmail(String email) {
        final String emailPattern = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(emailPattern);
        return pattern.matcher(email).find();
    }

    public boolean validatePhone(String phone) {

        final String phonePattern = "^\\d{11}$";
        final Pattern pattern = Pattern.compile(phonePattern);
        return pattern.matcher(phone).find();
    }

    public String friendlyTime(Date time) {
        if(time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        //判断是否是同一天
        String curDate = DATE_FORMAT.format(cal.getTime());
        String paramDate = DATE_FORMAT.format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
            if(hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
        } else {
            ftime = DATE_FORMAT_WITH_HM.format(time);
        }

        return ftime;
    }

    public String friendlyTime(String sdate) {
        return friendlyTime(toDate(sdate));
    }

    public Date toDate(String sdate) {
        try {
            return DATE_FORMAT_FULL.parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }
}
