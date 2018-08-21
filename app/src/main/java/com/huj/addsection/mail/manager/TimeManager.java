package com.huj.addsection.mail.manager;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeManager {


//    public static String getTime() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//        return format.format(new Date());
//    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }

    public static String getIMGTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return format.format(new Date());
    }

//    public static String getDayTime(long time) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        return format.format(new Date(time));
//
//    }

    public static String getMailTime(long time) {
        Calendar current = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        if (calendar.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {//同一年
            if (calendar.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) {//今天
                 String string = new SimpleDateFormat("HH:mm").format(time);
                String prefixion="";
               if (Integer.parseInt(string.substring(0,2).trim())<=6){
                   prefixion="凌晨 ";
                }else if (Integer.parseInt(string.substring(0,2).trim())<=12){
                   prefixion="上午 ";
               }else if (Integer.parseInt(string.substring(0,2).trim())<=18){
                    prefixion="下午 ";
                }else if (Integer.parseInt(string.substring(0,2).trim())<=24){
                    prefixion="晚上 ";
                }

                return prefixion+ string;
            }

            return  new SimpleDateFormat("MM-dd").format(time);
        } else {
            calendar.setTime(new Date(time + 24 * 60 * 60 * 1000));
            return  new SimpleDateFormat("yyyy-MM-dd").format(time);
        }
    }


    public static String getWeekTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String week = "";
        switch (dayOfWeek) {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
                break;
        }
        return format.format(new Date(time)) + week;
    }


}
