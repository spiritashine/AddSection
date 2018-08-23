package com.huj.addsection;

import android.app.Application;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Mail;

import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Store;

public class App extends Application {
    public static App application;
    public static Addresser addresser;
    public static ArrayList<Mail> inbox = new ArrayList<>();
    public static Store store;
    public static Folder folder;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    /**
     * 得到BaseApplication
     */
    public static App getApplication() {
        return application;
    }



}
