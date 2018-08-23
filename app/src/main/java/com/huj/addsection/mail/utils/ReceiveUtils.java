package com.huj.addsection.mail.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.huj.addsection.App;
import com.huj.addsection.mail.MailDetailActivity;
import com.huj.addsection.mail.MailLogActivity;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.db.DBMail;
import com.huj.addsection.mail.manager.DialogManager;
import com.huj.addsection.mail.manager.L;
import com.huj.addsection.mail.manager.NetManager;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * 接收邮件的类
 */
public class ReceiveUtils {
    public boolean isLeave = false;//是否离开mail界面
    public static Thread getPageMailBottomThread;
    public static Thread getPageMailTopThread;

    /**
     * 上滑加载更多
     */
    public void getPageMailBottom(final Addresser a, final int total, final Mail startMail, final Handler handler) {
        isLeave = false;
        getPageMailBottomThread = new Thread() {
            @Override
            public void run() {
                super.run();
                //先去数据库中取出total个mail
                ArrayList<Mail> pageMailFromDb = DBMail.getInstance().getPageMailFromDbIndex(a, startMail, total);
                L.i("Bottom pageMailFromDb.size()=" + pageMailFromDb.size());
                //如果数据库中取出的不够total个mail，那就再去服务器请求。
                if (pageMailFromDb.size() < total) {
                    //再去网上请求。
                    ArrayList<Mail> mailsFromServer = getMailFromServerBottom(a, DBMail.getInstance().getMinSendDateMailIndex(a), total - pageMailFromDb.size());
                    //账号切换的时候，如果上次的账号已经切换，即a.account和BaseApplication.addresser.account不相等的时候，则不需要handle在回传来更新界面
                    if (!a.account.equals(App.addresser.account)) {
                        return;
                    }
//                    if (MailFragment.mailFolderType == Mail.MAIL_TYPE_INBOX) {
                        //将服务器中请求的数据添加到数据库请求的数据中，然后装进BaseApplication.inbox
                        pageMailFromDb.addAll(mailsFromServer);
                        L.i("Bottom mailsFromServer.size()=" + mailsFromServer.size());
//                    } else {
//                        pageMailFromDb.clear();
//                    }
                }
                App.inbox.addAll(pageMailFromDb);
                android.os.Message msg = android.os.Message.obtain();
                msg.what = 1;
                msg.obj = pageMailFromDb;
                handler.sendMessage(msg);
            }
        };
        getPageMailBottomThread.start();
    }

    /**
     * 下拉刷新
     */
    public void getPageMailTop(final Addresser a, final Handler handler) {
        isLeave = false;
        getPageMailTopThread = new Thread() {
            @Override
            public void run() {
                super.run();
                android.os.Message msg = android.os.Message.obtain();
                msg.what = 2;
                //取出数据库中最近的一个mail,作为标志位去服务器请求
                ArrayList<Mail> pageMailFromServer = getMailFromServerTop(a, DBMail.getInstance().getMaxSendDateMailIndex(a));
                //账号切换的时候，如果上次的账号已经切换，即a.account和BaseApplication.addresser.account不相等的时候，则不需要handle在回传来更新界面
                if (!a.account.equals(App.addresser.account)) {
                    return;
                }
                if (isLeave) { //下载的时候如果切换到了别的界面，就不要继续下载了
                    handler.sendMessage(msg);
                    return;
                }
                L.i("top pageMailFromServer.size() = " + pageMailFromServer.size());
//                if (MailFragment.mailFolderType == Mail.MAIL_TYPE_INBOX) {
                    for (int i = 0; i < pageMailFromServer.size(); i++) {
                        App.inbox.add(0, pageMailFromServer.get(i));
                    }
//                }
                handler.sendMessage(msg);
            }
        };
        getPageMailTopThread.start();
    }

    public ArrayList<Mail> getMailFromServerTop(Addresser a, Mail m) {
        ArrayList<Mail> list = new ArrayList<>();
        boolean startTop = false;
        //如果没有网络直接返回一个没有数据的list
        //判断有没有网络，如果没有网络，则不进行网络连接
        if (!NetManager.isNetworkConnected(App.getApplication()) || isLeave) {
            return list;
        }
        try {
            Session session = MailLogActivity.getSessionPOP3orIMAP(a.receiveProtocol);
            if (isLeave || session == null) { //下载的时候如果切换到了别的界面，就不要继续下载了
                return list;
            }
            Store store = session.getStore(a.receiveProtocol.name);//设置通讯协议
            if (isLeave) { //下载的时候如果切换到了别的界面，就不要继续下载了
                return list;
            }
            store.connect(a.account, a.password);//连接
            if (isLeave) { //下载的时候如果切换到了别的界面，就不要继续下载了
                return list;
            }
            Folder folder = store.getFolder("INBOX");
            if (isLeave) { //下载的时候如果切换到了别的界面，就不要继续下载了
                return list;
            }
            folder.open(Folder.READ_WRITE);// 设置仅读READ_ONLY   READ_WRITE
            if (isLeave) { //下载的时候如果切换到了别的界面，就不要继续下载了
                return list;
            }
            if (!folder.isOpen()) {//如果没有开启，则在开启一遍。
                folder.open(Folder.READ_WRITE);
            }
            if (!folder.isOpen() || isLeave) {//如果第二遍仍然没有开启，则返回请求数据为空。
                return list;
            }
            App.store = store;
            App.folder = folder;
            Message[] messages = folder.getMessages();
            L.i("top messages.length=" + messages.length);
            for (int i = 0; i < messages.length; i++) {
                //账号切换的时候，如果上次的账号已经切换，即a.account和BaseApplication.addresser.account不相等的时候，不要继续请求
                //下载的时候如果切换到了别的界面，就不要继续下载了
                if (!a.account.equals(App.addresser.account) || isLeave) {
                    return list;
                }
                String uid = getuid(messages[i], folder);
                if (!TextUtils.isEmpty(uid)) {
                    if (startTop) {
                        Mail mail = new Mail(messages[i], uid);
                        list.add(mail);
                        DBMail.getInstance().addMail(a, mail);
                    } else {
                        if (m != null) {
                            startTop = uid.equals(m.uid);
                        } else {
                            return list;
                        }
                    }
                }
            }
            return list;
        } catch (MessagingException e) {
            Log.i("gao", "连接失败 getPageMailFromServer e= " + e);
            e.printStackTrace();
            return list;
        }

    }


    public ArrayList<Mail> getMailFromServerBottom(Addresser a, Mail m, int several) {
        ArrayList<Mail> list = new ArrayList<>();
        boolean startBottom = false;
        //如果没有网络直接返回一个没有数据的list
        if (!NetManager.isNetworkConnected(App.getApplication())) {//判断有没有网络，如果没有网络，则不进行网络连接
            return list;
        }
        if (m == null) {//等于空证明第一次登录该账号，直接下载。
            startBottom = true;
        }
        try {
            Session session = MailLogActivity.getSessionPOP3orIMAP(a.receiveProtocol);
            Store store = session.getStore(a.receiveProtocol.name);//设置通讯协议
            store.connect(a.account, a.password);//连接
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);// 设置仅读READ_ONLY   READ_WRITE
            if (!folder.isOpen()) {//如果没有开启，则在开启一遍。
                folder.open(Folder.READ_WRITE);
            }
            if (!folder.isOpen()) {//如果第二遍仍然没有开启，则返回请求数据为空。
                return list;
            }
            Message[] messages = folder.getMessages();
            L.i(" messages.length=" + messages.length);
            for (int i = messages.length - 1; i > -1; i--) {
                //账号切换的时候，如果上次的账号已经切换，即a.account和BaseApplication.addresser.account
                //不相等的时候，不要继续请求 //下载的时候如果切换到了别的界面，就不要继续下载了
                if (!a.account.equals(App.addresser.account) || isLeave) {
                    return list;
                }
                String uid = getuid(messages[i], folder);
                if (!TextUtils.isEmpty(uid)) {
                    if (startBottom) {
                        Mail mail = new Mail(messages[i], uid);
                        list.add(mail);
                        DBMail.getInstance().addMail(a, mail);
                    } else {
                        if (m != null) {
                            startBottom = uid.equals(m.uid);
                        } else {
                            return list;
                        }
                    }
                }
                //下载的mail够一页的时候，停止下载
                if (list.size() == several) {
                    return list;
                }
            }
        } catch (MessagingException e) {
            Log.i("gao", "连接失败 getMailFromServerBottom  e= " + e);
            e.printStackTrace();
            return list;
        }
        return list;
    }

    /**
     * 得到uid
     */
    public String getuid(Message message, Folder folder) {
        String uid = "";
        try {
            if (folder instanceof POP3Folder) {
                POP3Folder inbox = (POP3Folder) folder;
                uid = inbox.getUID(message);
            } else if (folder instanceof IMAPFolder) {
                IMAPFolder inbox = (IMAPFolder) folder;
                uid = Long.toString(inbox.getUID(message));
            }
            return uid;
        } catch (MessagingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获得邮件的详情
     */
    public static void getMailDetail2(Addresser a, Mail m, Handler handler) {
        try {
            if (m.message == null) {
                if (App.folder == null || !App.folder.isOpen()) {
                    if (App.store == null || !App.store.isConnected()) {
                        Session session = MailLogActivity.getSessionPOP3orIMAP(a.receiveProtocol);
                        App.store = session.getStore(a.receiveProtocol.name);//设置通讯协议
                        App.store.connect(a.account, a.password);//连接
                    }
                    App.folder = App.store.getFolder("INBOX");
                    App.folder.open(Folder.READ_WRITE);// 设置仅读READ_ONLY   READ_WRITE
                    if (!App.folder.isOpen()) {//如果没有开启，则在开启一遍。
                        App.folder.open(Folder.READ_WRITE);
                    }
                    if (!App.folder.isOpen()) {//如果第二遍仍然没有开启，则返回请求数据为空。
                        handler.sendEmptyMessage(MailDetailActivity.NO_DETAILS);
                        return;
                    }
                }
                Message[] messages = App.folder.getMessages();
                for (int i = 0; i < messages.length; i++) {
                    String uid = "";
                    if (App.folder instanceof POP3Folder) {
                        POP3Folder inbox = (POP3Folder) App.folder;
                        uid = inbox.getUID(messages[i]);
                    } else if (App.folder instanceof IMAPFolder) {
                        IMAPFolder inbox = (IMAPFolder) App.folder;
                        uid = Long.toString(inbox.getUID(messages[i]));
                    }
                    if (uid.equals(m.uid)) {
                        m.message = messages[i];
                        break;
                    }
                }
            }

            //得到附件的列表
            m.getAttachMent(m.message);
            //如果无图内容为空，下载无图内容。如果无图内容不为空，直接下载有图内容
            if (TextUtils.isEmpty(m.contentNoImage)) {
                m.contentNoImage = m.getHTMLContentNoImage(m.message, new StringBuffer());
                if (!TextUtils.isEmpty(m.contentNoImage)) {
                    handler.sendEmptyMessage(MailDetailActivity.NOIMG_DETAILS);
                } else {//没有内容
                    handler.sendEmptyMessage(MailDetailActivity.NO_DETAILS);
                    return;
                }
            }
            m.contentWithImage = m.getHTMLContentWithImage(m.message, new StringBuffer());
            if (!TextUtils.isEmpty(m.contentWithImage)) {
                handler.sendEmptyMessage(MailDetailActivity.WITHIMG_DETAILS);
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            L.i("e = "+e);
            handler.sendEmptyMessage(MailDetailActivity.NO_DETAILS);
        } catch (MessagingException e) {
            e.printStackTrace();
            L.i("e = "+e);
            handler.sendEmptyMessage(MailDetailActivity.NO_DETAILS);
        } catch (Exception e) {
            e.printStackTrace();
            L.i("e = "+e);
            handler.sendEmptyMessage(MailDetailActivity.NO_DETAILS);
        }


    }

    /**
     * 获得邮件的详情
     */
    public static void getMailDetail(Context context, final Addresser a, final Mail m, final Handler handler) {
        L.i("加载中...");
        DialogManager.showProgressDialog(context, "加载中...");
        new Thread() {
            @Override
            public void run() {
                super.run();
                ReceiveUtils.getMailDetail2(a, m, handler);
            }
        }.start();
    }

}
