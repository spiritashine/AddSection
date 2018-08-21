package com.huj.addsection;

import android.app.Activity;
import android.app.Application;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Mail;

import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Store;

public class BaseApplication extends Application {
    public ArrayList<Activity> activities = new ArrayList<Activity>();
    public static BaseApplication application;
    public static Addresser addresser;
    public static ArrayList<Mail> inbox = new ArrayList<>();
    public static Store store;
    public static Folder folder;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // 打印错误日志
//        CustomCrashHandler mCustomCrashHandler = CustomCrashHandler.getInstance();
//        mCustomCrashHandler.setCustomCrashHanler(getApplicationContext());
        application = this;

        //每次打开程序的时候，删除上次保存在本地的图片。
//        FileUtils.deleteDir(Environment.getExternalStorageDirectory() + "/Xiniuyunimage");



    }


    /**
     * 得到BaseApplication
     */
    public static BaseApplication getApplication() {
        return application;
    }

    /**
     * 得到装activity的集合
     */
    public ArrayList<Activity> getActivities() {
        return activities;
    }

    /**
     * 添加activity
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 删除activity
     */
    public void deleteActivity(Activity activity) {
        if (activities.contains(activity)) {
            if (activity.isFinishing()) {
                activity.finish();
            }
            activities.remove(activity);
        }
    }

    /**
     * 清空activities
     */
    public void clearActivity() {
        for (int i = 0; i < activities.size(); i++) {
            if (!activities.get(i).isFinishing()) {
                activities.get(i).finish();
            }
        }

        activities.clear();
    }


}
