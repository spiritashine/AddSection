package com.huj.addsection.mail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huj.addsection.App;
import com.huj.addsection.R;
import com.huj.addsection.mail.adapter.AttachAdapter;
import com.huj.addsection.mail.bean.Attach;
import com.huj.addsection.mail.bean.Contacts;
import com.huj.addsection.mail.bean.Image;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.db.DBMail;
import com.huj.addsection.mail.utils.AppUtils;
import com.huj.addsection.mail.base.BaseActivity;
import com.huj.addsection.mail.manager.DialogManager;
import com.huj.addsection.mail.view.ImageSpanTextWatcher;
import com.huj.addsection.mail.utils.InputUtills;
import com.huj.addsection.mail.view.MyGridView;
import com.huj.addsection.mail.utils.PopupUtils;
import com.huj.addsection.mail.view.RichEditor;
import com.huj.addsection.mail.utils.SendUtils;
import com.huj.addsection.mail.manager.StringManager;
import com.huj.addsection.mail.manager.T;
import com.huj.addsection.mail.manager.TitleManager;
import com.huj.addsection.mail.utils.WriteMailUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 写邮件
 */
public class WriteMailActivity extends BaseActivity {
    @Bind(R.id.et_to_write_mail)
    EditText etTo;
    @Bind(R.id.et_cc_write_mail)
    EditText etCc;
    @Bind(R.id.et_bcc_write_mail)
    EditText etBcc;
    @Bind(R.id.et_subject)
    EditText etSubject;
    @Bind(R.id.add_subject)
    ImageView addSubject;
    @Bind(R.id.tv_subject_count)
    TextView tvSubjectCount;
    @Bind(R.id.et_mail_content)
    EditText etMailContent;
    @Bind(R.id.tv_cc_write_mail)
    TextView tvCc;
    @Bind(R.id.ll_bcc_write_mail)
    LinearLayout llBcc;
    @Bind(R.id.insert_img)
    ImageView insertImg;
    @Bind(R.id.gv_item_bcc)
    MyGridView myGridView;
    @Bind(R.id.editor_write_mail)
    RichEditor richEditor;

    ImageSpanTextWatcher mImageSpanTextWatcher;
    WriteMailActivity context = this;
    public static final int RESULTCODE_SELECTCONTACTS = 1;
    public static final int RESULTCODE_FILE_ACTIVITY = 2;
    public static final int TAKE_PICTURE_ATTAC = 3;
    public static final int CHOOSE_PICTURE_ATTAC = 4;
    public static final int ATTAC = 5;
    public static final int INSERT_IMG = 6;

    public static final int TYPE_WRITEMAIL = 1;//写邮件
    public static final int TYPE_REPLY = 2;//回复邮件
    public static final int TYPE_FORWARD = 3;//回复邮件
    //    public static final int TYPE_TYPE_DRAFT = 4;//草稿箱
//    public static final int TYPE_TYPE_SEND = 5;//发件箱
    public int type = TYPE_WRITEMAIL;
    public int position;
    public static ArrayList<Contacts> tos = new ArrayList<>();
    public static ArrayList<Contacts> ccs = new ArrayList<>();
    public static ArrayList<Contacts> bccs = new ArrayList<>();
    int[] selecteds = {-1, -1, -1};
    public ArrayList<Attach> attachs = new ArrayList<>();
    public ArrayList<Image> images = new ArrayList<>();
    AttachAdapter attachAdapter;
    public static String takePhotoPath;
    Mail mail;//当前写的邮件
    boolean isSendding = false;//是否正在发送邮件
    Mail mailOld;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            mail.type = (int) msg.obj;
            if (mail.type == Mail.MAIL_TYPE_OUT) {
                T.show("发送成功");
            } else if (mail.type == Mail.MAIL_TYPE_SEND) {
                T.show("发送失败");
            }
            if (DBMail.getInstance().exist(App.addresser, mail.uid)) {
                DBMail.getInstance().updateMail(App.addresser, mail);
            } else {
                DBMail.getInstance().addMail(App.addresser, mail);
            }
            DialogManager.closeDialog();//确定保存邮件之后，关闭弹出的对话框
            DialogManager.closeProgressDialog();//发邮件之后候，关闭圈圈
            isSendding = false;//将正在发送邮件的标志位，置为false
            setResult(MailListActivity.RESULTCODE_WRITEMAIL);
            WriteMailActivity.this.finish();
        }
    };

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_mail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = TYPE_WRITEMAIL;

        // 设置标题栏
        TitleManager.showTitle(this, TitleManager.TEXT_TEXT, StringManager.getString(R.string.write_mail), R.string.cancle, R.string.send);

        //实例化WriteMailUtils工具类需要的各种参数
        WriteMailUtils.initWriteMailUtils(context, tos, ccs, bccs, selecteds, etTo, etCc, etBcc, tvCc, llBcc, etMailContent, etSubject, myGridView);

        //给内容的EditText添加TextWatcher监听，以便添加图片
        mImageSpanTextWatcher = new ImageSpanTextWatcher(etMailContent, images);

        if (type == TYPE_WRITEMAIL) {//从主界面写邮件跳转过来
            //判断需不需要签名，如果需要，就将签名设置到内容输入框后面
            WriteMailUtils.setSign(etMailContent);
        } else if (type == TYPE_REPLY) {//回复
            position = intent.getIntExtra("position", -1);
            mailOld = App.inbox.get(position);
            ArrayList<Contacts> replyTos = intent.getParcelableArrayListExtra("replyTos");
            ArrayList<Contacts> replyCcs = intent.getParcelableArrayListExtra("replyCcs");
            //将收件人，抄送人，密送人数据转移到本activity
            tos.addAll(replyTos);
            ccs.addAll(replyCcs);
            //根据抄送人和密送人判断是否隐藏密送人栏
            if (ccs.isEmpty() && bccs.isEmpty()) {
                llBcc.setVisibility(View.GONE);
                tvCc.setText(R.string.copy_to_blind_carbon_copy);
            } else {
                llBcc.setVisibility(View.VISIBLE);
                tvCc.setText(R.string.copy_to);
            }
            //将收件人，抄送人，密送人填充到界面上
            WriteMailUtils.updataFlow(etTo, 0, tos);
            WriteMailUtils.updataFlow(etCc, 1, ccs);
            WriteMailUtils.updataFlow(etBcc, 2, bccs);
            //设置标题
            etSubject.setText(StringManager.getString(R.string.reply1)+ mailOld.subject);
            richEditor.loadUrl(mailOld,TYPE_REPLY);
        } else if (type == TYPE_FORWARD) {//转发
            position = intent.getIntExtra("position", -1);
            mailOld = App.inbox.get(position);
            etSubject.setText(StringManager.getString(R.string.forward1) + mailOld.subject);//设置标题
            attachs.addAll(mailOld.attchsList); //附件转移到本activity
            richEditor.loadUrl(mailOld,TYPE_FORWARD);
        } else {
            position = intent.getIntExtra("position", -1);
            mail = App.inbox.get(position);
            //将收件人，抄送人，密送人数据转移到本activity
            tos.addAll(mail.tosList);
            ccs.addAll(mail.ccsList);
            bccs.addAll(mail.bccsList);
            //根据抄送人和密送人判断是否隐藏密送人栏
            if (ccs.isEmpty() && bccs.isEmpty()) {
                llBcc.setVisibility(View.GONE);
                tvCc.setText(R.string.copy_to_blind_carbon_copy);
            } else {
                llBcc.setVisibility(View.VISIBLE);
                tvCc.setText(R.string.copy_to);
            }
            //将收件人，抄送人，密送人填充到界面上
            WriteMailUtils.updataFlow(etTo, 0, tos);
            WriteMailUtils.updataFlow(etCc, 1, ccs);
            WriteMailUtils.updataFlow(etBcc, 2, bccs);

            //附件转移到本activity
            attachs.addAll(mail.attchsList);
            //设置标题
            etSubject.setText(mail.subject);
            String contentWithImage = mail.contentWithImage.replaceAll("<br/>", "\n").replaceAll("&nbsp;", " ");
            Editable editable = new SpannableStringBuilder(contentWithImage);
            WriteMailUtils.HTML2Spanned(editable, mImageSpanTextWatcher);
            etMailContent.setText(editable);
        }

        //etTo,etCc,etBcc的各种监听
        WriteMailUtils.setEditTextListener(etTo, tos, 0);
        WriteMailUtils.setEditTextListener(etCc, ccs, 1);
        WriteMailUtils.setEditTextListener(etBcc, bccs, 2);

        //设置附件的myGridView
        attachAdapter = new AttachAdapter(context, attachs, myGridView);
        WriteMailUtils.setMyGridView(attachs, attachAdapter);

        etMailContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//etMailContent得到焦点，显示添加图片按钮，失去焦点隐藏添加图片按钮
                    insertImg.setVisibility(View.VISIBLE);
                } else {
                    insertImg.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.add_subject, R.id.insert_img, R.id.tv_title_right, R.id.tv_title_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_subject://添加附件
                if (AppUtils.isGrantExternalRW(this)) {//7.0以上版本需要先申请sdcard读写权限
                    PopupUtils.showPopupWindowAddAttach(this);
                } else {
                    T.show(StringManager.getString(R.string.inspect_permission));
                }
                break;

            case R.id.insert_img://添加图片
                if (AppUtils.isGrantExternalRW(this)) {//7.0以上版本需要先申请sdcard读写权限
                    PopupUtils.showPopupWindowInsertImg(this);
                } else {
                    T.show(StringManager.getString(R.string.inspect_permission));
                }
                break;

            case R.id.tv_title_right://发送邮件
                if (tos.isEmpty() && ccs.isEmpty() && bccs.isEmpty()) {
                    T.show(R.string.fill_in_receiver);
                    return;
                }
                if (isSendding) {
                    T.show(R.string.please_email_me_later);
                    return;
                }
                //发送邮件的时候，转圈圈
                DialogManager.showProgressDialog(WriteMailActivity.this, StringManager.getString(R.string.please_email_me_later));
                isSendding = true;//将正在发送邮件的标志位，置为true
                InputUtills.hide(WriteMailActivity.this);//如果软键盘在现实中，则先关闭

                new Thread() {
                    public void run() {
                        super.run();
                        getMail();
                        android.os.Message message = android.os.Message.obtain();
                        message.obj = new SendUtils(App.addresser, mail, images).send();
                        handler.sendMessage(message);
                    }
                }.start();
                break;

            case R.id.tv_title_left://取消
                if (type == TYPE_WRITEMAIL) {//写邮件界面
                    DialogManager.showDialog(this, StringManager.getString(R.string.is_save_draft),
                            StringManager.getString(R.string.save_draft), StringManager.getString(R.string.delete_draft));
                } else {//草稿箱或者发件箱进入该界面
                    DialogManager.showDialog(this, StringManager.getString(R.string.is_save_draft_revision),
                            StringManager.getString(R.string.save_revision), StringManager.getString(R.string.abandon_revision));
                }
                TextView confirm = DialogManager.getConfirm();
                TextView cancle = DialogManager.getCancle();
                cancle.setTextColor(ContextCompat.getColor(this, R.color.text_tv_red));
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread() {
                            public void run() {
                                super.run();
                                getMail();
                                android.os.Message message = android.os.Message.obtain();
                                message.obj = Mail.MAIL_TYPE_DRAFT;
                                handler.sendMessage(message);
                            }
                        }.start();
                    }
                });
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogManager.closeDialog();
                        setResult(MailListActivity.RESULTCODE_WRITEMAIL);
                        WriteMailActivity.this.finish();
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULTCODE_SELECTCONTACTS) {//从selectContactsActivity界面返回之后，根据返回值来更新相应的Flow
            WriteMailUtils.updataFlowFromSelectContacts(data);

        } else if (resultCode == Activity.RESULT_CANCELED) { //从ContactsDetailsActivity界面返回之后，根据返回值来更新相应的Flow
            WriteMailUtils.updataFlowFromContactDetails();

        } else if (requestCode == ATTAC) {//附件
            if (resultCode == RESULTCODE_FILE_ACTIVITY) {//选择文件
                WriteMailUtils.addAttach(attachs, data.getStringExtra("path"));

            } else if (resultCode == CHOOSE_PICTURE_ATTAC) {//选择图片
                ArrayList<String> pathes = data.getStringArrayListExtra("pathes");
                for (int i = 0; i < pathes.size(); i++) {
                    WriteMailUtils.addAttach(attachs, pathes.get(i));
                }
            } else {//拍摄照片
                WriteMailUtils.addAttach(attachs, takePhotoPath);
            }
            attachAdapter.notifyDataSetChanged();
        } else if (requestCode == INSERT_IMG) {//插入图片
            if (resultCode == CHOOSE_PICTURE_ATTAC) {//选择图片
                ArrayList<String> pathes = data.getStringArrayListExtra("pathes");
                for (int i = 0; i < pathes.size(); i++) {
                    mImageSpanTextWatcher.insert(pathes.get(i), -1);
                }
            } else {//拍摄照片
                mImageSpanTextWatcher.insert(takePhotoPath, -1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //消亡该界面时，将这三个静态的集合置空。
        tos.clear();
        ccs.clear();
        bccs.clear();
    }

    private void getMail() {
        if (mail == null) {
            mail = new Mail();
            mail.uid = Long.toString(new Date().getTime());
            mail.fromContacts = new Contacts(App.addresser.name, App.addresser.account);
            mail.froms = mail.fromContacts.name + "<" + mail.fromContacts.mail + ">";
            mail.isSeen = true;
            mail.textColor = (int) (Math.random() * 5);
            mail.sendDate = Long.toString(new Date().getTime());
        }

        mail.subject = StringManager.isEmpty(etSubject) ? StringManager.getString(R.string.nosubject) : StringManager.getStringByTv(etSubject);
        String content = StringManager.getStringByTv(etMailContent).replaceAll("</?[^>]+>|\r|\n|☠| ", "").trim(); //去掉空格换行，特殊字符
        if (type == TYPE_REPLY||type == TYPE_FORWARD) {
            String contentWithImage = StringManager.convertSpannedToRichText(etMailContent.getEditableText(), true);
            String contentNoImage = StringManager.convertSpannedToRichText(etMailContent.getEditableText(), false);
            mail.contentWithImage = contentWithImage + richEditor.getHtml();
            mail.contentNoImage = contentNoImage +StringManager.withImage2NoImage(richEditor.getHtml(),mailOld.imagesList,images); //转换
            mail.contentHint =StringManager.removeHtmlTag(mail.contentNoImage);
        } else {
            mail.contentHint = content.length() <= 30 ? content : content.substring(0, 30);
            mail.contentWithImage = StringManager.convertSpannedToRichText(etMailContent.getEditableText(), true);
            mail.contentNoImage = StringManager.convertSpannedToRichText(etMailContent.getEditableText(), false);
        }
        mail.tosList = new ArrayList<>();
        mail.tosList.addAll(tos);
        mail.ccsList = new ArrayList<>();
        mail.ccsList.addAll(ccs);
        mail.bccsList = new ArrayList<>();
        mail.bccsList.addAll(bccs);
        mail.tos = StringManager.conversionToContactsString(mail.tosList);
        mail.ccs = StringManager.conversionToContactsString(mail.ccsList);
        mail.bccs = StringManager.conversionToContactsString(mail.bccsList);
        mail.isContainAttch = attachs.size() > 1;
        mail.attchsList = attachs;
        mail.attchsList.remove(mail.attchsList.size() - 1);//因为attachs的最后一个附件是“+”，不是真的附件。
        mail.attchs = StringManager.conversionToAttachsString(attachs);
    }
}
