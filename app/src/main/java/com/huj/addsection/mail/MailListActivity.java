package com.huj.addsection.mail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.huj.addsection.BaseApplication;
import com.huj.addsection.R;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.db.DBAddresser;
import com.huj.addsection.mail.db.DBMail;
import com.huj.addsection.mail.manager.L;
import com.huj.addsection.mail.manager.NetManager;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;


public class MailListActivity extends AppCompatActivity {

    LinearLayout mailBar;
    RecyclerView mailList;
    private MailListAdapter adapter;
    private ArrayList<Mail> mData = new ArrayList<>();
    private Addresser addresser;
    private boolean isLeave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);
        Intent intent = getIntent();
        addresser = intent.getParcelableExtra("addresser");
        new Thread(new Runnable() {
            @Override
            public void run() {
                getMailFromServerTop(addresser,null);
            }
        }).start();
        mailBar = findViewById(R.id.mail_bar);
        mailList = findViewById(R.id.mail_list);
        adapter = new MailListAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mailList.setLayoutManager(manager);
        mailList.setAdapter(adapter);
        mData = DBMail.getInstance().selectInbox(addresser);
        adapter.setData(mData);
        adapter.setOnItemClickListener(new MailListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                 Intent intent = new Intent(MailListActivity.this,MailDetailActivity.class);
                 intent.putExtra("data",mData.get(mailList.getChildAdapterPosition(view)));
                 startActivity(intent);
            }
        });
    }

    public ArrayList<Mail> getMailFromServerTop(Addresser a, Mail m) {
        ArrayList<Mail> list = new ArrayList<>();
        boolean startTop = false;
        //如果没有网络直接返回一个没有数据的list
        //判断有没有网络，如果没有网络，则不进行网络连接
        if (!NetManager.isNetworkConnected(BaseApplication.getApplication()) || isLeave) {
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
            BaseApplication.store = store;
            BaseApplication.folder = folder;
            Message[] messages = folder.getMessages();
            List<Message> listM = new ArrayList<>();
            for (int i = 0; i < messages.length; i++) {
                listM.add(messages[i]);
            }
            Collections.reverse(listM);
            listM.toArray(messages);
            L.i("top messages.length=" + listM.size());
            for (int i = 0; i < listM.size(); i++) {
                //账号切换的时候，如果上次的账号已经切换，即a.account和BaseApplication.addresser.account不相等的时候，不要继续请求
                //下载的时候如果切换到了别的界面，就不要继续下载了
                if (!a.account.equals(addresser.account) || isLeave) {
                    return list;
                }
                String uid = getuid(listM.get(i), folder);
                if (!TextUtils.isEmpty(uid)) {
//                    if (startTop) {
                        Mail mail = new Mail(listM.get(i), uid);
                        list.add(mail);
                        DBMail.getInstance().addMail(a, mail);
//                    } else {
//                        if (m != null) {
//                            startTop = uid.equals(m.uid);
//                        } else {
//                            return list;
//                        }
//                    }
                }
            }
            return list;
        } catch (MessagingException e) {
            Log.i("gao", "连接失败 getPageMailFromServer e= " + e);
            e.printStackTrace();
            return list;
        }

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
}
