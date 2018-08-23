package com.huj.addsection.mail.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.MyAuthenticator;
import com.huj.addsection.mail.bean.Protocol;
import com.huj.addsection.mail.manager.L;
import com.huj.addsection.mail.manager.NetManager;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

/**
 * 登录服务器相关工具类
 */
public class LoginUtils {

    /**
     * 登录
     */
    public static boolean loginPOP3orIMAP(String account, String password, Protocol p, Addresser addresser) {
        try {
            Session session = getSessionPOP3orIMAP(p);//得到session
            Log.i("gao", p.name + "开始连接");
            Store store = session.getStore(p.name);//设置通讯协议
            store.connect(account, password);//连接
            Log.i("gao", p.name + "连接完成");
            addresser.setReceiveProtocol(p);
        } catch (MessagingException e) {
            Log.i("gao", p.name + "连接失败 e= " + e);
            e.printStackTrace();
            return false;
        }
        Log.i("gao", p.name + "连接成功");
        return true;
    }

    /**
     * 登录
     */
    public static boolean loginSMTP(String account, String password, Protocol p, Addresser addresser) {
        try {
            Session session = getSessionSMTP(p,account, password);  //得到session
            Log.i("gao", p.name + "开始连接");
            Transport transport = session.getTransport(p.name);//设置通讯协议
            transport.connect(account, password);//连接
            Log.i("gao", p.name + "连接完成");
            addresser.setSendProtocol(p);
        } catch (MessagingException e) {
            Log.i("gao", p.name + "连接失败 e= " + e);
            e.printStackTrace();
            return false;
        }
        Log.i("gao", p.name + "连接成功");
        return true;
    }



    /**
     * 服务器设置界面用的登录(设置方法)方法
     */
    public static void login(final Context context, final Addresser addresser, final Handler handler) {
        if (!NetManager.isNetworkConnected(context)) {//判断有没有网络，如果没有网络，则不进行网络连接
            return;
        }
        //登录
        new Thread() {
            @Override
            public void run() {
                super.run();
                Boolean isLoginReceive = LoginUtils.loginPOP3orIMAP(addresser.account, addresser.password, addresser.receiveProtocol, addresser);
                Boolean isLoginSend = LoginUtils.loginSMTP(addresser.account, addresser.password, addresser.sendProtocol, addresser);
                L.i("isLoginReceive = " + isLoginReceive);
                L.i("isLoginSend = " + isLoginSend);
                Message message = Message.obtain();
                message.obj = isLoginReceive && isLoginSend;
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 得到Session
     */
    public static Session getSessionSMTP(Protocol p, String account, String password) {
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.auth", "true");// 服务器需要认证
        props.setProperty("mail.transport.protocol", p.port);// 声明发送邮件使用的端口
        props.setProperty("mail.smtp.host", p.host); //设置服务器
        props.setProperty("mail.smtp.timeout", "20000"); //设置网络连接时间
        if (p.ssl) { //如果开启了ssl加密则需如下设置，未开启ssl加密则不需要。
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //使用JSSE的SSL socketfactory来取代默认的socketfactory
            props.setProperty("mail.smtp.socketFactory.fallback", "false");  // 只处理SSL的连接,对于非SSL的连接不做处理
            props.setProperty("mail.smtp.socketFactory.port", p.port);  //设置SSL连接的端口号
        }
        return Session.getInstance(props, new MyAuthenticator(account, password));
    }

  /**
     * 得到Session
     */
    public static Session getSessionPOP3orIMAP(Protocol p) {
        Properties props = System.getProperties();
        props.setProperty("mail." + p.name + ".host", p.host); //设置服务器
        props.setProperty("mail." + p.name + ".port", p.port); //设置端口号
        props.setProperty("mail." + p.name + ".timeout", "20000"); //设置网络连接时间
        if (p.ssl) { //如果开启了ssl加密则需如下设置，未开启ssl加密则不需要。
            props.setProperty("mail." + p.name + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //使用JSSE的SSL socketfactory来取代默认的socketfactory
            props.setProperty("mail." + p.name + ".socketFactory.fallback", "false");  // 只处理SSL的连接,对于非SSL的连接不做处理
            props.setProperty("mail." + p.name + ".socketFactory.port", p.port);  //设置SSL连接的端口号
        }
        return Session.getDefaultInstance(props);
    }

}
