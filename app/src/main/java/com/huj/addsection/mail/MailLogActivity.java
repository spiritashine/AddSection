package com.huj.addsection.mail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.huj.addsection.App;
import com.huj.addsection.R;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.MyAuthenticator;
import com.huj.addsection.mail.bean.Protocol;
import com.huj.addsection.mail.db.DBAddresser;
import com.huj.addsection.mail.db.DBMail;
import com.huj.addsection.mail.utils.LoginUtils;
import com.huj.addsection.mail.manager.NetManager;
import com.huj.addsection.mail.manager.PreferenceManager;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

public class MailLogActivity extends AppCompatActivity {
    private Addresser addresser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_log);
        addresser = new Addresser("huj@xingyuanauto.com","spiritashine,.897392");
        setProtocol();
    }

    public void submit(View view) {
        LoginUtils.login(this, addresser, handler2);
    }

    private void setProtocol() {
        Protocol receiveProtocol = new Protocol();
        Protocol sendProtocol = new Protocol();
        receiveProtocol.name = "pop3";
        receiveProtocol.host = "pop3.mxhichina.com";
        receiveProtocol.port = "110";
        receiveProtocol.ssl = false;
        sendProtocol.name = "smtp";
        sendProtocol.host = "smtp.mxhichina.com";
        sendProtocol.port = "25";
        sendProtocol.ssl = false;
        addresser.receiveProtocol = receiveProtocol;
        addresser.sendProtocol = sendProtocol;
    }

    /**
     * 登录
     */
    public static boolean loginPOP3orIMAP(String account, String password, Protocol p, Addresser addresser) {
        try {
            Session session = getSessionPOP3orIMAP(p);//得到session
            Log.e("gao", p.name + "开始连接");
            Store store = session.getStore(p.name);//设置通讯协议
            store.connect(account, password);//连接
            Log.e("gao", p.name + "连接完成");
            addresser.setReceiveProtocol(p);
        } catch (MessagingException e) {
            Log.e("gao", p.name + "连接失败 e= " + e);
            e.printStackTrace();
            return false;
        }
        Log.e("gao", p.name + "连接成功");
        return true;
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

    /**
     * 登录
     */
    public static boolean loginSMTP(String account, String password, Protocol p, Addresser addresser) {
        try {
            Session session = getSessionSMTP(p,account, password);  //得到session
            Log.e("gao", p.name + "开始连接");
            Transport transport = session.getTransport(p.name);//设置通讯协议
            transport.connect(account, password);//连接
            Log.e("gao", p.name + "连接完成");
            addresser.setSendProtocol(p);
        } catch (MessagingException e) {
            Log.e("gao", p.name + "连接失败 e= " + e);
            e.printStackTrace();
            return false;
        }
        Log.e("gao", p.name + "连接成功");
        return true;
    }


    /**
     * 得到Session
     */
    public static Session getSessionSMTP(Protocol p,String account, String password) {
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
                Boolean isLoginReceive = loginPOP3orIMAP(addresser.account, addresser.password, addresser.receiveProtocol, addresser);
                Boolean isLoginSend = loginSMTP(addresser.account, addresser.password, addresser.sendProtocol, addresser);
                Log.e("========","isLoginReceive = " + isLoginReceive);
                Log.e("========","isLoginSend = " + isLoginSend);
                Message message = Message.obtain();
                message.obj = isLoginReceive && isLoginSend;
                handler.sendMessage(message);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ((Boolean) msg.obj) {
                Toast.makeText(MailLogActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                DBAddresser.getInstance().addAddresser(addresser);
                //作为当前邮箱账号保存起来。
                PreferenceManager.setString(MailLogActivity.this, "addresser", addresser.account);
                //有邮箱的账号做表名，在数据库中建一个存储mial的表
                DBMail.getInstance().createTable(addresser);
                Intent intent = new Intent(MailLogActivity.this, MailListActivity.class);
                intent.putExtra("addresser",addresser);
                startActivity(intent);
                App.addresser = addresser;
                finish();
            } else {
                Toast.makeText(MailLogActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if ((Boolean) msg.obj) {
                Toast.makeText(MailLogActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                    DBAddresser.getInstance().addAddresser(addresser);
                    Intent intent = new Intent(MailLogActivity.this, MailListActivity.class);
                    intent.putExtra("addresser",addresser);
                    startActivity(intent);
                    //作为当前邮箱账号保存起来。
                    PreferenceManager.setString(MailLogActivity.this, "addresser", addresser.account);
                    //有邮箱的账号做表名，在数据库中建一个存储mial的表
                    DBMail.getInstance().createTable(addresser);
            } else {
                Toast.makeText(MailLogActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
            }

        }
    };

}
