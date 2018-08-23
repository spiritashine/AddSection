package com.huj.addsection.mail.utils;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.huj.addsection.App;
import com.huj.addsection.R;
import com.huj.addsection.mail.adapter.AttachAdapter;
import com.huj.addsection.mail.WriteMailActivity;
import com.huj.addsection.mail.bean.Attach;
import com.huj.addsection.mail.bean.Contacts;
import com.huj.addsection.mail.db.DBContacts;
import com.huj.addsection.mail.manager.StringManager;
import com.huj.addsection.mail.manager.TimeManager;
import com.huj.addsection.mail.view.FlowLayout;
import com.huj.addsection.mail.view.ImageSpanTextWatcher;
import com.huj.addsection.mail.view.MyGridView;

import java.io.File;
import java.util.ArrayList;


public class WriteMailUtils {

    public static WriteMailActivity context;
    public static ArrayList<Contacts> tos;
    public static ArrayList<Contacts> ccs;
    public static ArrayList<Contacts> bccs;
    public static int[] selecteds;
    public static EditText etTo;
    public static EditText etCc;
    public static EditText etBcc;
    public static TextView tvCc;
    public static LinearLayout llBcc;
    public static EditText etSubject;
    public static EditText etMailContent;
    public static MyGridView myGridView;

    public static void initWriteMailUtils(WriteMailActivity context1, ArrayList<Contacts> tos1, ArrayList<Contacts> ccs1, ArrayList<Contacts> bccs1,
                                          int[] selecteds1, EditText etTo1, EditText etCc1, EditText etBcc1, TextView tvCc1, LinearLayout llBcc1,
                                          EditText etMailContent1, EditText etSubject1, MyGridView myGridView1) {
        context = context1;
        tos = tos1;
        ccs = ccs1;
        bccs = bccs1;
        selecteds = selecteds1;
        etTo = etTo1;
        etCc = etCc1;
        etBcc = etBcc1;
        tvCc = tvCc1;
        llBcc = llBcc1;
        etMailContent = etMailContent1;
        myGridView = myGridView1;
        etSubject=etSubject1;
    }


    /**
     * 判断需不需要签名，如果需要，就将签名设置到内容输入框后面
     *
     * @param et 内容输入框
     */
    public static void setSign(EditText et) {
        if (App.getApplication().addresser != null) {
            if (App.getApplication().addresser.hasSign) {
                et.setText("\n\n\n\n" + App.getApplication().addresser.sign);
            } else {
                et.setText("\n\n\n\n");
            }
        }
    }

    /**
     * 设置附件的myGridView
     *
     * @param attachs 附件的数据源
     */
    public static void setMyGridView(final ArrayList<Attach> attachs, AttachAdapter attachAdapter) {
        attachs.add(new Attach());
        myGridView.setAdapter(attachAdapter);
        if (attachs.size() < 2) {
            myGridView.setVisibility(View.GONE);
        } else {
            myGridView.setVisibility(View.VISIBLE);
        }
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == attachs.size() - 1) {//添加附件
                    PopupUtils.showPopupWindowAddAttach(context);
                }
            }
        });
    }

    /**
     * etTo,etCc,etBcc的各种监听
     */
    public static void setEditTextListener(final EditText et, final ArrayList<Contacts> list, final int index) {
        //得到EditText的父控件，以后会在里面添加textview
        final FlowLayout flow = (FlowLayout) et.getParent();
        //得到EditText后面的添加按钮
        final View add = ((RelativeLayout) flow.getParent()).getChildAt(1);

        //EditText焦点变化的各种监听
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // “收件人”栏edittext获取焦点和失去焦点的时候的监听。
                if (hasFocus) {
                    add.setVisibility(View.VISIBLE);
                    if (selecteds[index] < 0) {//如果selecteds[index]>=0,此时获得焦点的栏目有被选中的联系人。
                        setAllTextViewNoSelected();
                    }
                } else {
                    add.setVisibility(View.INVISIBLE);
                    String stringEt = StringManager.getStringByTv(et);
                    //  通过edittext中的有效内容来判断“收件人”“抄送”“密送”栏中是否要添加一个textview。
                    setFlowByEditTextString(et, flow, list, stringEt, index);
                }

                switch (v.getId()) {
                    case R.id.et_cc_write_mail:// 抄送
                        if (hasFocus) {
                            llBcc.setVisibility(View.VISIBLE);
                            tvCc.setText(StringManager.getString(R.string.copy_to));
                        }
                    case R.id.et_bcc_write_mail:// 密送
                        // 如果抄送框,密送框都没有文字,且都是去焦点,则隐藏密送框
                        if (!etCc.isFocused() && !etBcc.isFocused() && ccs.isEmpty() && bccs.isEmpty()) {
                            llBcc.setVisibility(View.GONE);
                            tvCc.setText(StringManager.getString(R.string.copy_to_blind_carbon_copy));
                        }
                        break;
                }
            }
        });

        //监听软键盘的删除键
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String string = StringManager.getStringByTv(et);
                    if (TextUtils.isEmpty(string)) {//et中没有字符可以删除，则删除一个联系人
                        if (selecteds[index] < 0) {//selecteds[index] < 0证明没有选中任何联系人
                            if (list.size() > 0) {//没有选中任何联系人，list.size() > 0证明已经有联系人，可以删除最后一个联系人
                                selecteds[index] = list.size() - 1;
                                TextView childText = (TextView) flow.getChildAt(flow.getChildCount() - 2);
                                setSelectedTextView((Contacts) childText.getTag(), childText);//设置被选中的textview
                            }
                        } else {//selecteds[index] > 0此时有选中的联系人
                            flow.removeViewAt(selecteds[index]);
                            list.remove(selecteds[index]);
                            if (list.size() > 0) {
                                StringManager.removeComma((TextView) flow.getChildAt(flow.getChildCount() - 2));
                            }
                            selecteds[index] = -1;
                        }
                    }
                }
                return false;
            }
        });

        //EditText里面内容的变化的各种监听
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //  通过edittext中的有效内容来判断“收件人”“抄送”“密送”栏中是否要添加一个textview。
                String string = s.toString();
                if (string.equals("\n") || string.equals(" ")) {//不可以以换行或者空格开头
                    et.setText("");
                }
                if (string.length() > 1 && (string.endsWith(";") || string.endsWith("\n"))) {
                    setFlowByEditTextString(et, flow, list, string.substring(0, string.length() - 1), index);
                }
            }
        });

        //点击“收件人”“抄送”“密送”栏的空白区域的时候，让里面的edittext获取焦点。
        flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFocus(et);//et获得焦点，并显示软键盘
            }
        });

    }

    /**
     * et获得焦点，并显示软键盘
     */
    public static void getFocus(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();//获取焦点 光标出现
        et.setCursorVisible(true);
        InputUtills.show(context, et);
    }

    /**
     * 一次“收件人”“抄送”“密送”栏的edittext输入结束之后，通过edittext中的有效内容
     * 来判断“收件人”“抄送”“密送”栏中是否要添加一个textview。
     */
    public static void setFlowByEditTextString(EditText et, final FlowLayout flow, final ArrayList<Contacts> list, String stringEt, final int index) {
        //如果edittext里面什么也没有，就不做什么操作,直接返回
        if (TextUtils.isEmpty(stringEt)) {
            return;
        }

        //将stringEt作为关键字，在数据库中通过邮件和名字查询，看是否已经存在符合条件的contacts
        Contacts contacts = DBContacts.getInstance().selectContactsByMailName(stringEt);
        if (contacts == null) {
            contacts = new Contacts(stringEt, stringEt);
        }

        //如果list中包含contacts则什么都不操作。
        if (contains(list, contacts)) {
            et.setText("");
            return;
        }
        //将联系人添加到list里面
        list.add(contacts);

        addFlowChild(et, flow, list, index, contacts);
    }

    /**
     * 实例化一个textviev。里面放contacts，加入到flow，里面
     */
    public static void addFlowChild(final EditText et, FlowLayout flow, final ArrayList<Contacts> list, final int index, final Contacts contacts) {
        //如果edittext里面有内容，则new 一个TextView，并对TextView设置
        final TextView textView = new TextView(context);
        // 第一个参数为宽的设置，第二个参数为高的设置。
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);//居中
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.text_size_normal));
        setTextView(contacts, textView);
        textView.setText(contacts.name);
        textView.setTag(contacts);
        et.setText(""); //将后面的edittext里的内容设为空
        int childCount = flow.getChildCount();
        if (childCount > 1) {//如果再edittext前面有textview，则在前面的textview里面文字后面加“、”
            TextView childTextView = (TextView) flow.getChildAt(childCount - 2);
            StringManager.addComma(childTextView);
        }
        flow.addView(textView, childCount - 1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlowLayout parent = (FlowLayout) textView.getParent();
                EditText etChild = (EditText) parent.getChildAt(parent.getChildCount() - 1);
                getFocus(etChild); //et获得焦点，并显示软键盘
                etChild.setCursorVisible(false);//不显示etChild中的光标
                StringManager.removeComma(textView);//去逗号
                Contacts contactsSelected = (Contacts) textView.getTag();
                int selected = getContactsIndex(list, contactsSelected);  // 通过textview上的文字判断，这是list中的第几个联系人
                if (selecteds[index] == selected) {
                    if (isMail(contactsSelected)) {//输入的是有效的邮箱地址。
                        //得到名字和字母的集合
                        Bundle bundle = new Bundle();
                        Contacts dbContacts = DBContacts.getInstance().selectContactsByMail(contactsSelected.mail);
                        if (dbContacts != null) {
                            bundle.putParcelable("contacts", dbContacts);
                        } else {
                            bundle.putParcelable("contacts", contactsSelected);
                        }
                        bundle.putBoolean("isFromWriteMail", true);
                        InputUtills.hide(context, et);
//                        context.startActivity(bundle, ContactDetailsActivity.class, Activity.RESULT_FIRST_USER);
                    }
                } else {
                    setAllTextViewNoSelected();//将“收件人”“抄送”“密送”栏中所有的textview设置为未选中状态
                    selecteds[index] = selected;
                    setSelectedTextView(contactsSelected, textView);//设置被选中的textview
                }
            }
        });
    }

    /**
     * 设置被选中的textview
     */
    public static void setSelectedTextView(Contacts contactsSelected, TextView textView) {
        StringManager.removeComma(textView);//去逗号
        textView.setPadding(ScreenUtils.dip2px(context, 5), 0, ScreenUtils.dip2px(context, 5), 0);
        if (isMail(contactsSelected)) {
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_space_blue_litter));
        } else {
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_space_red_litter));
        }
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
    }

    /**
     * 如果list中包含contacts则返回contacts的下标，如果不在则返回-1。
     */
    public static int getContactsIndex(ArrayList<Contacts> list, Contacts contacts) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).name.equals(contacts.name) && list.get(i).mail.equals(contacts.mail)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 如果list中包含contacts则返回true，如果不在则返回false。
     */
    public static boolean contains(ArrayList<Contacts> list, Contacts contacts) {
        if (getContactsIndex(list, contacts) == -1) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * 将“收件人”“抄送”“密送”栏中所有的textview设置为未选中状态
     */
    public static void setAllTextViewNoSelected() {
        setChildTextView(etTo, tos);
        setChildTextView(etCc, ccs);
        setChildTextView(etBcc, bccs);
        selecteds[0] = -1;
        selecteds[1] = -1;
        selecteds[2] = -1;
    }

    /**
     * 将“收件人”“抄送”“密送”栏中所有的textview设置为未选中状态
     */
    public static void setChildTextView(EditText et, ArrayList<Contacts> list) {
        for (int i = 0; i < list.size(); i++) {
            Contacts contacts = list.get(i);

            TextView child = (TextView) ((FlowLayout) et.getParent()).getChildAt(i);
            StringManager.addComma(child);
            if (i == list.size() - 1) {
                StringManager.removeComma(child);
            }
            setTextView(contacts, child);
        }
    }

    /**
     * 设置textview的属性
     */
    public static void setTextView(Contacts contacts, TextView tv) {
        tv.setPadding(ScreenUtils.dip2px(context, 5), 0, 0, 0);
        tv.setBackgroundColor(ContextCompat.getColor(context, R.color.background));
        if (isMail(contacts)) {
            tv.setTextColor(ContextCompat.getColor(context, R.color.text_tv_blue));
        } else {
            tv.setTextColor(ContextCompat.getColor(context, R.color.text_tv_red));
        }
    }

    /**
     * 判断该联系人的邮箱是否有效，如果没有效，这说明用户输入的是错的邮箱地址
     */
    private static boolean isMail(Contacts contacts) {
        if (contacts.mail.contains("@")) {
            return true;
        }
        return false;
    }

    /**
     * 从selectContactsActivity界面返回之后，根据返回值来更新相应的Flow
     */
    public static void updataFlowFromSelectContacts(Intent data) {
        int index = data.getIntExtra("index", -1);
        ArrayList<Contacts> receivers = data.getParcelableArrayListExtra("list");

        switch (index) {
            case 0:
                tos.clear();
                tos.addAll(receivers);
                updataFlow(etTo, index, tos);
                break;
            case 1:
                ccs.clear();
                ccs.addAll(receivers);
                updataFlow(etCc, index, ccs);
                break;
            case 2:
                bccs.clear();
                bccs.addAll(receivers);
                updataFlow(etBcc, index, bccs);
                break;
        }
    }

    /**
     * 调用一：从selectContactsActivity界面返回之后，根据返回值来更新相应的Flow
     * 调用二：从MailFragment界面点击 item进入WriteMailActivity界面，根据传过来得时，填充收件人，抄送人，密送人
     */
    public static void updataFlow(EditText et, int index, ArrayList<Contacts> list) {
        //得到EditText的父控件，以后会在里面添加textview
        final FlowLayout flow = (FlowLayout) et.getParent();
        for (int i = flow.getChildCount() - 2; i >= 0; i--) {
            flow.removeViewAt(i);
        }
        for (int j = 0; j < list.size(); j++) {
           addFlowChild(et, flow, list, index, list.get(j));
        }
    }

    /**
     * 从ContactsDetailsActivity界面返回之后，根据返回值来更新相应的Flow
     */
    public static void updataFlowFromContactDetails() {
        Contacts contacts = null;
        if (selecteds[0] != -1) {
            contacts = DBContacts.getInstance().selectContactsByMail(tos.get(selecteds[0]).mail);
            if (contacts != null) {
                tos.set(selecteds[0], contacts);
                WriteMailUtils.updataFlow(etTo, 0, tos);
            }
            selecteds[0] = -1;
        } else if (selecteds[1] != -1) {
            contacts = DBContacts.getInstance().selectContactsByMail(ccs.get(selecteds[1]).mail);
            if (contacts != null) {
                ccs.set(selecteds[1], contacts);
                WriteMailUtils.updataFlow(etCc, 1, ccs);
            }
            selecteds[1] = -1;
        } else if (selecteds[2] != -1) {
            contacts = DBContacts.getInstance().selectContactsByMail(bccs.get(selecteds[2]).mail);
            if (contacts != null) {
                bccs.set(selecteds[2], contacts);
                WriteMailUtils.updataFlow(etBcc, 2, bccs);
            }
            selecteds[2] = -1;
        }
    }

    /**
     * 调用系统相机照相。
     */
    public static void photo(WriteMailActivity activity, int requestCode) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dirFile = new File(Environment.getExternalStorageDirectory(), "xiniuyun");
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(dirFile, "img_" + TimeManager.getIMGTime() + ".jpg");
        WriteMailActivity.takePhotoPath = file.getPath();

        if (android.os.Build.VERSION.SDK_INT < 24) {
            // 从文件中创建uri
            Uri uri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            //兼容android7.0 使用共享文件的形式
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            Uri uri = App.application.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        activity.startActivityForResult(openCameraIntent, requestCode);
    }

    /**
     * 将需要添加的附件添加的附件的数据源中
     *
     * @param path 附件的路径
     */
    public static void addAttach(ArrayList<Attach> attachs, String path) {
        if (!TextUtils.isEmpty(path)) {
            File file1 = new File(path);
            Attach attach = new Attach(file1.getName(), file1.getAbsolutePath(), Formatter.formatFileSize(context, file1.length()));
            attachs.add(attachs.size() - 1, attach);
            if (attachs.size() < 2) {
                myGridView.setVisibility(View.GONE);
            } else {
                myGridView.setVisibility(View.VISIBLE);
            }
            if (StringManager.isEmpty(etSubject)){
                etSubject.setText("发送文件");
            }

        }
    }

    /**
     * 从草稿箱，发件箱来到本界面的时候，将邮件内容转化成Spanned，放入到edittext
     */
    public static void HTML2Spanned(Editable editable, ImageSpanTextWatcher imageSpanTextWatcher) {
        String string = editable.toString();
        if (string.contains("<img src=")) {
            int index = string.indexOf("<");
            String img = string.substring(index, string.indexOf(">") + 1);
            String path = img.substring(img.indexOf(":") + 3, img.indexOf(">") - 1);
            String emoticon = "☠";
            editable.replace(index, index + img.length(), emoticon);
            Drawable drawable = new BitmapDrawable(App.getApplication().getResources(), imageSpanTextWatcher.revitionImageSize(path));
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, path, ImageSpan.ALIGN_BASELINE);
            editable.setSpan(span, index, index + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            HTML2Spanned(editable,imageSpanTextWatcher);
        }
    }

}
