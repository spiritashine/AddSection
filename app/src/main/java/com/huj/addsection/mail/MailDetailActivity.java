package com.huj.addsection.mail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huj.addsection.App;
import com.huj.addsection.R;
import com.huj.addsection.mail.adapter.AttachMailDetailsAdapter;
import com.huj.addsection.mail.bean.Contacts;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.db.DBMail;
import com.huj.addsection.mail.base.BaseActivity;
import com.huj.addsection.mail.manager.DialogManager;
import com.huj.addsection.mail.manager.L;
import com.huj.addsection.mail.view.MyGridView;
import com.huj.addsection.mail.view.MyScrollView;
import com.huj.addsection.mail.utils.ReceiveUtils;
import com.huj.addsection.mail.manager.StringManager;
import com.huj.addsection.mail.manager.T;
import com.huj.addsection.mail.view.TextCircleView;
import com.huj.addsection.mail.manager.TimeManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MailDetailActivity extends BaseActivity {

    @Bind(R.id.tv_subject_details)
    TextView tvSubjectDetails;
    @Bind(R.id.ctv_details)
    TextCircleView ctvDetails;
    @Bind(R.id.from_details)
    TextView fromDetails;
    @Bind(R.id.date_details)
    TextView dateDetails;
    @Bind(R.id.to_details)
    TextView toDetails;
    @Bind(R.id.webv_content_details)
    WebView webvContentDetails;
    @Bind(R.id.rl_title2_detail)
    LinearLayout rlTitle2Detail;
    @Bind(R.id.msv_details)
    MyScrollView msvDetails;
    @Bind(R.id.rl_title1_details)
    RelativeLayout rlTitle1Details;
    @Bind(R.id.ll_top_details)
    LinearLayout llTopDetails;
    @Bind(R.id.rr_title2_detail)
    RelativeLayout rrTitle2Detail;
    @Bind(R.id.iv_iscontainattch_details)
    ImageView ivIscontainattchDetails;
    @Bind(R.id.gv_item_attach_details)
    MyGridView gv;
    @Bind(R.id.ll_gv)
    LinearLayout llGv;
    public final static int WITHIMG_DETAILS = 0;
    public final static int NOIMG_DETAILS = 1;
    public final static int NO_DETAILS = 2;
    public final static int REPLY = 3;
    public final static int PRE = 7;
    public final static int NEXT = 8;
    public int position;
    public Mail mail;
    boolean isFromMain;
    int rlTitle1DetailsHeight;
    int rrTitle2DetailTop;
    int tvSubjectDetailsBottom;
    public  boolean isSendding = false;//是否正在发送邮件
    public ArrayList<Contacts> replyTos = new ArrayList<>();//如果回复邮件，这个为收件人。
    public  ArrayList<Contacts> replyCcs = new ArrayList<>();//如果回复邮件，这个为抄送人。
    @SuppressLint("HandlerLeak")
    public  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NOIMG_DETAILS:
                    L.i("NOIMG_DETAILS");
                    if (TextUtils.isEmpty(mail.contentWithImage)) {
                        webvContentDetails.loadDataWithBaseURL("about:blank", mail.contentNoImage, "text/html", "utf-8", null);
                    }
                    break;
                case WITHIMG_DETAILS:
                    DialogManager.closeProgressDialog();
                    L.i("WITHIMG_DETAILS");
                    webvContentDetails.loadDataWithBaseURL("about:blank", mail.contentWithImage, "text/html", "utf-8", null);
                    setAttachGv();//设置附件所在的gridview
                    break;
                case NO_DETAILS:
                    L.i("NO_DETAILS");
                    DialogManager.closeProgressDialog();
                    setAttachGv();//设置附件所在的gridview
                    break;
                case REPLY:
                    Mail newMail = (Mail) msg.obj;
                    newMail.type =  msg.arg1;
                    if (newMail.type == Mail.MAIL_TYPE_OUT) {
                        T.show("发送成功");
                    } else if (newMail.type == Mail.MAIL_TYPE_SEND) {
                        T.show("发送失败");
                    }
                    if (DBMail.getInstance().exist(App.addresser, newMail.uid)) {
                        DBMail.getInstance().updateMail(App.addresser, newMail);
                    } else {
                        DBMail.getInstance().addMail(App.addresser, newMail);
                    }
                    isSendding = false;//将正在发送邮件的标志位，置为false
                    setResult(MailListActivity.RESULTCODE_MAILDETAILS, new Intent());
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_detail);
        ButterKnife.bind(this);

        //给控件填充数据
        initView();

        //判断数据库中有没有带图片的html和附件的详细信息，如果没有就去下载，如果有直接展示
        getDetails();

        //监听ScrollListener的滑动，随着滑动rlTitle1Details位置和透明度发生变化，rlTitle2Detail的位置发生变化
        setOnScrollListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次进入该界面都要更新的内容，将收件人，发件人，抄送，密送
        setContacts();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            rlTitle1DetailsHeight = rlTitle1Details.getHeight();
            rrTitle2DetailTop = rrTitle2Detail.getTop();
            tvSubjectDetailsBottom = tvSubjectDetails.getBottom();
        }
    }

    @OnClick({R.id.back_details})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.back_details:
//                setResult(MailDetailActivity.RESULTCODE_MAILDETAILS, new Intent());
                finish();
                break;
        }
    }

    /**
     * 给控件填充数据
     */
    private void initView() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        isFromMain = intent.getBooleanExtra("isFromMain", false);

        mail = App.inbox.get(position);
        if (!mail.isSeen) {
            mail.isSeen = true;
            DBMail.getInstance().updateMailRead(App.addresser, mail);
        }
        tvSubjectDetails.setText(mail.subject);
        dateDetails.setText(TimeManager.getMailTime(Long.parseLong(mail.sendDate)));
        if (mail.isContainAttch) {
            ivIscontainattchDetails.setVisibility(View.VISIBLE);
        } else {
            ivIscontainattchDetails.setVisibility(View.GONE);
        }
        StringManager.setTextCircleViewColor(ctvDetails, mail.textColor);
    }

    /**
     * 判断数据库中有没有带图片的html和附件的详细信息，如果没有就去下载，如果有直接展示
     */
    private void getDetails() {
        if (!TextUtils.isEmpty(mail.contentWithImage)) {//有图内容不为空时，直接显示有图内容
            L.i("mail.contentWithImage=" + mail.contentWithImage);
            webvContentDetails.loadDataWithBaseURL("about:blank", mail.contentWithImage, "text/html", "utf-8", null);
            setAttachGv();//设置附件所在的gridview
        } else if (!TextUtils.isEmpty(mail.contentNoImage)) {//有图内容为空，无图内容不为空时，显示无图内容，并且下载有图内容
            L.i("mail.contentNoImage=" + mail.contentNoImage);
            webvContentDetails.loadDataWithBaseURL("about:blank", mail.contentNoImage, "text/html", "utf-8", null);
            ReceiveUtils.getMailDetail(MailDetailActivity.this, App.addresser, mail, handler);
        } else if (TextUtils.isEmpty(mail.contentNoImage)) {//有图内容为空且无图内容为空时，暂时不显示，下载无图和有图内容
            L.i("TextUtils.isEmpty(mail.contentNoImage" );
            ReceiveUtils.getMailDetail(MailDetailActivity.this, App.addresser, mail, handler);
        }
    }

    /**
     * 每次进入该界面都要更新的内容，将收件人，发件人，抄送，密送
     */
    private void setContacts() {
        //将收件人，发件人，抄送，密送转换成Contacts
        mail.fromContacts = StringManager.conversionToContacts(mail.froms);
        mail.tosList = StringManager.conversionToContactses(mail.tos);
        mail.ccsList = StringManager.conversionToContactses(mail.ccs);
        mail.bccsList = StringManager.conversionToContactses(mail.bccs);
        //给控件填充数据
        fromDetails.setText(mail.fromContacts.name);
        ctvDetails.setText(StringManager.getCtv(mail.fromContacts.name));
        StringBuffer sb = new StringBuffer("发至");
        replyCcs.clear();
        for (int i = 0; i < mail.tosList.size(); i++) {
            Contacts contacts = mail.tosList.get(i);
            if (!StringManager.containsContacts(contacts, replyCcs)) {
                sb.append(mail.tosList.get(i).name + "、");
                replyCcs.add(contacts);
            }
        }
        for (int i = 0; i < mail.ccsList.size(); i++) {
            Contacts contacts = mail.ccsList.get(i);
            if (!StringManager.containsContacts(contacts, replyCcs)) {
                sb.append(mail.ccsList.get(i).name + "、");
                replyCcs.add(contacts);
            }
        }
        toDetails.setText(sb.toString());
        StringManager.removeComma(toDetails);
        //去掉replyCcs里的本身
        for (int i = 0; i < replyCcs.size(); i++) {
            if (replyCcs.get(i).mail.equals(App.addresser.account)) {
                replyCcs.remove(i);
            }
        }
        replyTos.clear();
        replyTos.add(mail.fromContacts);
    }

    /**
     * 监听ScrollListener的滑动，
     * 随着滑动rlTitle1Details位置和透明度发生变化，rlTitle2Detail的位置发生变化
     */
    private void setOnScrollListener() {
        msvDetails.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY, int oldscrollY, boolean isUp) {

                //rlTitle1Details的位置变化
                setRlTitleDetailsPosition(scrollY, oldscrollY, isUp);

                //rlTitle1Details的透明度变化
                setRlTitleAlpha(scrollY);

                //rlTitle2Detail的位置变化
                setRlTitle2DetailPosition(scrollY);
            }
        });
    }

    /**
     * rlTitle1Details的位置变化
     */
    private void setRlTitleDetailsPosition(int scrollY, int oldscrollY, boolean isUp) {
        int rlTitle1DetailsTop = 0;
        if (rlTitle1Details.getTop() <= -rlTitle1DetailsHeight) {
            if (isUp) {
                rlTitle1DetailsTop = -rlTitle1DetailsHeight;
            } else {
                rlTitle1DetailsTop = rlTitle1Details.getTop() - (scrollY - oldscrollY);
            }
        } else if (rlTitle1Details.getTop() > -rlTitle1DetailsHeight && rlTitle1Details.getTop() < 0) {
            rlTitle1DetailsTop = rlTitle1Details.getTop() - (scrollY - oldscrollY);
        } else if (rlTitle1Details.getTop() >= 0) {
            if (!isUp) {
                rlTitle1DetailsTop = 0;
            } else {
                rlTitle1DetailsTop = rlTitle1Details.getTop() - (scrollY - oldscrollY);
            }
        }
        if (scrollY <= 0) {
            rlTitle1DetailsTop = 0;
        } else if (scrollY >= tvSubjectDetailsBottom) {
            rlTitle1DetailsTop = -rlTitle1DetailsHeight;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, rlTitle1DetailsHeight);
        params.setMargins(0, rlTitle1DetailsTop, 0, 0);
        rlTitle1Details.setLayoutParams(params);
    }

    /**
     * 点击最下面的输入框，做了回复或者转发，回到本界面title所在的位置有可能会错乱，将这些位置设置为最开始的位置
     */
    public void setTitleBack(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                msvDetails.fullScroll(ScrollView.FOCUS_UP);
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, rlTitle1DetailsHeight);
        params.setMargins(0, 0, 0, 0);
        rlTitle1Details.setLayoutParams(params);
        if (rlTitle2Detail.getParent() != llTopDetails) {
            rrTitle2Detail.removeView(rlTitle2Detail);
            llTopDetails.addView(rlTitle2Detail);
            rlTitle2Detail.setBackgroundResource(R.drawable.boder_bottom);
        }
    }

    /**
     * rlTitle1Details的透明度变化
     */
    private void setRlTitleAlpha(int scrollY) {
        if (scrollY >= 0 && scrollY <= rlTitle1DetailsHeight) {//设置透明度
            rlTitle1Details.setAlpha(1f - scrollY * 0.1f / rlTitle1DetailsHeight);
        } else {
            rlTitle1Details.setAlpha(1f);
        }
    }

    /**
     * rlTitle2Detail的位置变化
     */
    private void setRlTitle2DetailPosition(int scrollY) {
        if (scrollY >= rrTitle2DetailTop) {
            if (rlTitle2Detail.getParent() != llTopDetails) {
                rrTitle2Detail.removeView(rlTitle2Detail);
                llTopDetails.addView(rlTitle2Detail);
                rlTitle2Detail.setBackgroundResource(R.drawable.boder_bottom);
            }
        } else {
            if (rlTitle2Detail.getParent() != rrTitle2Detail) {
                llTopDetails.removeView(rlTitle2Detail);
                rrTitle2Detail.addView(rlTitle2Detail);
                rlTitle2Detail.setBackgroundColor(ContextCompat.getColor(MailDetailActivity.this, R.color.background));
            }
        }
    }

    /**
     * 点击返回键
     */
    @Override
    public void onBackPressed() {
        setResult(MailListActivity.RESULTCODE_MAILDETAILS, new Intent());
        finish();
    }

    /**
     * 设置附件所在的gridview
     */
    private void setAttachGv() {
        if (!mail.attchsList.isEmpty()) {
            llGv.setVisibility(View.VISIBLE);
            AttachMailDetailsAdapter adapter = new AttachMailDetailsAdapter(MailDetailActivity.this, mail.attchsList);
            gv.setAdapter(adapter);
        } else {
            llGv.setVisibility(View.GONE);
        }
    }
}
