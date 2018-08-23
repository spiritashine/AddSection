package com.huj.addsection.mail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.huj.addsection.App;
import com.huj.addsection.R;
import com.huj.addsection.mail.adapter.MailListAdapter;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.db.DBAddresser;
import com.huj.addsection.mail.db.DBMail;
import com.huj.addsection.mail.manager.L;
import com.huj.addsection.mail.manager.PreferenceManager;
import com.huj.addsection.mail.utils.ReceiveUtils;
import com.huj.addsection.mail.manager.T;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;


public class MailListActivity extends AppCompatActivity {

    ImageView ivAdd;
    XRecyclerView mailList;
    private MailListAdapter adapter;
    private ArrayList<Mail> mData = new ArrayList<>();
    private Addresser addresser;
    public boolean isLeave = false;
    public static boolean isLoadingMail = false;
    public ReceiveUtils receiveUtils;
    public static final int GETMAIL_DB = 0;
    public static final int GETMAIL_BOTTOM = 1;
    public static final int GETMAIL_TOP = 2;
    public static final int SWITCH_ACCOUNT = 3;
    public static final int RESULTCODE_MAILDETAILS = 1;
    public static final int RESULTCODE_WRITEMAIL = 2;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETMAIL_BOTTOM:
                    Log.e("=========", "GETMAIL_BOTTOM: ");
                    ArrayList<Mail> getMails = (ArrayList<Mail>) msg.obj;
                    Log.e("=========", "handleMessage: "+getMails);
                    mailList.loadMoreComplete();
                    mData.addAll(getMails);
                    adapter.setData(mData);
                    isLoadingMail = false;
//                    adapter.setData(getMails);
//                    L.i("getMails = " + getMails.size());
                    //有新邮件是，更新抽屉里面“收件箱”的未读mail数和mail列表上方的mail数
                    break;
                case GETMAIL_TOP:
                    Log.e("=========", "GETMAIL_TOP: ");
                    adapter.notifyDataSetChanged();
                    mailList.refreshComplete();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);
        addresser = new Addresser();
        addresser.account = PreferenceManager.getString(this,"addresser");
        addresser = DBAddresser.getInstance().selectAddresser(addresser.account);
        App.addresser = addresser;
        App.inbox = DBMail.getInstance().selectInbox(App.addresser);

        //判断是否正在下载邮件，如果正在下载邮件，则不在开启下载邮件线程，如果没有正在下载则开始下载邮件。
        if (!isLoadingMail) {
            isLoadingMail = true;
            if (receiveUtils != null) {
                receiveUtils.isLeave = true;
            }
            receiveUtils = new ReceiveUtils();
            if (App.inbox.size() == 0) {//证明是第一次登入该账号，开始下载。
                receiveUtils.getPageMailBottom(App.addresser, Mail.PAGE_COUNT, null, handler);
            } else {//不是第一次进入该账号，向上下载，
                receiveUtils.getPageMailTop(App.addresser, handler);
            }
            isLoadingMail = false;
        }else {
            isLoadingMail = false;
        }
        ivAdd = findViewById(R.id.mail_add);
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
                 intent.putExtra("position", mailList.getChildAdapterPosition(view) - 1);
                 intent.putExtra("isFromMain", true);
                 startActivityForResult(intent, RESULTCODE_MAILDETAILS);
            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MailListActivity.this,WriteMailActivity.class));
            }
        });

        mailList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (!isLoadingMail) {
                    isLoadingMail = true;
                    if (receiveUtils != null) {
                        receiveUtils.isLeave = true;
                    }
                    receiveUtils = new ReceiveUtils();
                    receiveUtils.getPageMailTop(App.addresser, handler);
                } else {
                    T.show("正在收邮件.....");
                    mailList.refreshComplete();
                }
            }

            @Override
            public void onLoadMore() {
                if (!isLoadingMail) {
                    L.i("上滑加载更多 isLoadingMail = " + isLoadingMail);
                    isLoadingMail = true;
                    if (receiveUtils != null) {
                        receiveUtils.isLeave = true;
                    }
                    receiveUtils = new ReceiveUtils();
                    receiveUtils.getPageMailBottom(App.addresser, Mail.PAGE_COUNT ,
                            App.inbox.get(App.inbox.size() - 1), handler);

                } else {
                    T.show("正在收邮件.....");
                    mailList.loadMoreComplete();
                }
            }
        });
    }

}
