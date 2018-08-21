package com.huj.addsection.mail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.huj.addsection.BaseApplication;
import com.huj.addsection.R;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Contacts;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.manager.L;
import com.huj.addsection.mail.manager.TextCircleView;
import com.huj.addsection.mail.manager.TimeManager;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;


public class MailDetailActivity extends AppCompatActivity {
    private TextView from,date,receive;
    private WebView webView;
    private ImageView back;
    private TextCircleView icon;
    private Mail mail;
    public final static int WITHIMG_DETAILS = 0;
    public final static int NOIMG_DETAILS = 1;
    public final static int NO_DETAILS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_detail);
        from = findViewById(R.id.mail_detail_from);
        date = findViewById(R.id.mail_detail_date);
        receive = findViewById(R.id.mail_detail_receive);
        webView = findViewById(R.id.mail_detail_web);
        back = findViewById(R.id.mail_detail_back);
        icon = findViewById(R.id.mail_detail_icon);

        Intent intent = getIntent();
        Mail mail = intent.getParcelableExtra("data");

        from.setText(mail.froms);
        String dateString = !TextUtils.isEmpty(mail.sendDate) ? TimeManager.getMailTime(Long.parseLong(mail.sendDate)) : "";
        date.setText(dateString);

        StringBuffer sb = new StringBuffer("发至  ");
        for (int i = 0; i < mail.tosList.size(); i++) {
            Contacts contacts = mail.tosList.get(i);
                sb.append(mail.tosList.get(i).name + "、");
        }
        for (int i = 0; i < mail.ccsList.size(); i++) {
            Contacts contacts = mail.ccsList.get(i);
                sb.append(mail.ccsList.get(i).name + "、");
        }
        receive.setText(sb.toString());
        getDetails();
    }

    /**
     * 判断数据库中有没有带图片的html和附件的详细信息，如果没有就去下载，如果有直接展示
     */
    private void getDetails() {
        if (!TextUtils.isEmpty(mail.contentWithImage)) {//有图内容不为空时，直接显示有图内容
            L.i("mail.contentWithImage=" + mail.contentWithImage);
            webView.loadDataWithBaseURL("about:blank", mail.contentWithImage, "text/html", "utf-8", null);
        } else if (!TextUtils.isEmpty(mail.contentNoImage)) {//有图内容为空，无图内容不为空时，显示无图内容，并且下载有图内容
            L.i("mail.contentNoImage=" + mail.contentNoImage);
            webView.loadDataWithBaseURL("about:blank", mail.contentNoImage, "text/html", "utf-8", null);
            getMailDetail(MailDetailActivity.this, BaseApplication.addresser, mail, handler);
        } else if (TextUtils.isEmpty(mail.contentNoImage)) {//有图内容为空且无图内容为空时，暂时不显示，下载无图和有图内容
            L.i("TextUtils.isEmpty(mail.contentNoImage" );
            getMailDetail(MailDetailActivity.this, BaseApplication.addresser, mail, handler);
        }
    }

    /**
     * 获得邮件的详情
     */
    public static void getMailDetail(Context context, final Addresser a, final Mail m, final Handler handler) {
        L.i("加载中...");
        new Thread() {
            @Override
            public void run() {
                super.run();
                getMailDetail2(a, m, handler);
            }
        }.start();
    }

    /**
     * 获得邮件的详情
     */
    public static void getMailDetail2(Addresser a, Mail m, Handler handler) {
        try {
            if (m.message == null) {
                if (BaseApplication.folder == null || !BaseApplication.folder.isOpen()) {
                    if (BaseApplication.store == null || !BaseApplication.store.isConnected()) {
                        Session session = MailLogActivity.getSessionPOP3orIMAP(a.receiveProtocol);
                        BaseApplication.store = session.getStore(a.receiveProtocol.name);//设置通讯协议
                        BaseApplication.store.connect(a.account, a.password);//连接
                    }
                    BaseApplication.folder = BaseApplication.store.getFolder("INBOX");
                    BaseApplication.folder.open(Folder.READ_WRITE);// 设置仅读READ_ONLY   READ_WRITE
                    if (!BaseApplication.folder.isOpen()) {//如果没有开启，则在开启一遍。
                        BaseApplication.folder.open(Folder.READ_WRITE);
                    }
                    if (!BaseApplication.folder.isOpen()) {//如果第二遍仍然没有开启，则返回请求数据为空。
                        handler.sendEmptyMessage(NO_DETAILS);
                        return;
                    }
                }
                Message[] messages = BaseApplication.folder.getMessages();
                for (int i = 0; i < messages.length; i++) {
                    String uid = "";
                    if (BaseApplication.folder instanceof POP3Folder) {
                        POP3Folder inbox = (POP3Folder) BaseApplication.folder;
                        uid = inbox.getUID(messages[i]);
                    } else if (BaseApplication.folder instanceof IMAPFolder) {
                        IMAPFolder inbox = (IMAPFolder) BaseApplication.folder;
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
                    handler.sendEmptyMessage(NOIMG_DETAILS);
                } else {//没有内容
                    handler.sendEmptyMessage(NO_DETAILS);
                    return;
                }
            }
            m.contentWithImage = m.getHTMLContentWithImage(m.message, new StringBuffer());
            if (!TextUtils.isEmpty(m.contentWithImage)) {
                handler.sendEmptyMessage(WITHIMG_DETAILS);
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            L.i("e = "+e);
            handler.sendEmptyMessage(NO_DETAILS);
        } catch (MessagingException e) {
            e.printStackTrace();
            L.i("e = "+e);
            handler.sendEmptyMessage(NO_DETAILS);
        } catch (Exception e) {
            e.printStackTrace();
            L.i("e = "+e);
            handler.sendEmptyMessage(NO_DETAILS);
        }
    }

    @SuppressLint("HandlerLeak")
    public  Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NOIMG_DETAILS:
                    L.i("NOIMG_DETAILS");
                    if (TextUtils.isEmpty(mail.contentWithImage)) {
                        webView.loadDataWithBaseURL("about:blank", mail.contentNoImage, "text/html", "utf-8", null);
                    }
                    break;
                case WITHIMG_DETAILS:
                    L.i("WITHIMG_DETAILS");
                    webView.loadDataWithBaseURL("about:blank", mail.contentWithImage, "text/html", "utf-8", null);
                    break;
                case NO_DETAILS:
                    L.i("NO_DETAILS");
                    break;
            }
        }
    };

}
