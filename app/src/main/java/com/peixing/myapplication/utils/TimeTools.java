package com.peixing.myapplication.utils;

/**
 * Author :leilei on 2016/11/11 2326.
 */
public class TimeTools {

    //毫秒换成00:00:00
    public static String getCountTimeByLong(long finishTime) {
        int totalTime = (int) (finishTime / 1000);//秒
        int ms = (int) (finishTime % 1000);
        int day = 0, hour = 0, minute = 0, second = 0;


        if (86400 <= totalTime) {
            day = totalTime / 86400;
            totalTime = totalTime - day * 86400;
        }

        if (3600 <= totalTime) {
            hour = totalTime / 3600;
            totalTime = totalTime - 3600 * hour;
        }
        if (60 <= totalTime) {
            minute = totalTime / 60;
            totalTime = totalTime - 60 * minute;
        }
        if (0 <= totalTime) {
            second = totalTime;
        }
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            if (day < 10) {
                sb.append("0").append(day).append(" 天 ");
            } else {
                sb.append(day).append(" 天 ");
            }
        }
        if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (minute < 10) {
            sb.append("0").append(minute).append(":");
        } else {
            sb.append(minute).append(":");
        }
        if (second < 10) {
            sb.append("0").append(second).append(":");
        } else {
            sb.append(second).append(":");
        }
        sb.append(ms / 10);
        return sb.toString();
    }
}
