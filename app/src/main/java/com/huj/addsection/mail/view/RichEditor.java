package com.huj.addsection.mail.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.huj.addsection.mail.WriteMailActivity;
import com.huj.addsection.mail.bean.Contacts;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.manager.TimeManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class RichEditor extends WebView {
    private static final String CALLBACK_SCHEME = "re-callback://";
    private String mContents;

    public RichEditor(Context context) {
        this(context, null);
    }

    public RichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
                    callback(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    public void loadUrl(Mail mailOld, int type) {
        String string1 = " <div id='editor' contentEditable='true' style='outline: none; -webkit-tap-highlight-color: rgba(255, 255, 255, 0); -webkit-focus-ring-color: rgba(0, 0, 0, 0);'  >";
        String string2 = " </div><script type='text/javascript' src='file:///android_res/raw/rich_editor.js'></script>";
        String content = "";
        if (type == WriteMailActivity.TYPE_REPLY) {
            content = "<div style = 'background-color:EEEEEE;padding-bottom:5px;padding-top:5px;margin-bottom:10px;margin-top:10px'>在" + TimeManager.getMailTime(Long.parseLong(mailOld.sendDate)) + "，<font color='#0066FF'>" + mailOld.fromContacts.name + "</font>写到:</div>" + mailOld.contentWithImage;
        } else if (type == WriteMailActivity.TYPE_FORWARD) {
            StringBuffer sbTo = new StringBuffer();
            for (Contacts key : mailOld.tosList) {
                sbTo.append(key.name + "、");
            }
            String to = sbTo.toString();
            to =to.length()>0? to.substring(0, to.length() - 1):"";
            StringBuffer sbcc = new StringBuffer();
            for (Contacts key : mailOld.ccsList) {
                sbcc.append(key.name + "、");
            }
            String cc = sbcc.toString();
            cc =cc.length()>0? cc.substring(0, cc.length() - 1):"";

            content = "<font color='#757575'  style = 'margin:5px;padding:5px' >-------转发的邮件-----</font>" +
                    "<div style = 'background-color:EEEEEE;margin:5px;padding:5px'>" +
                    "<font color='#757575'>发件人:</font><font color='#0066FF'>" + mailOld.fromContacts.name + "</font>" +
                    "<br/><font color='#757575'>发送日期:</font><font color='#31353B'>" + TimeManager.getMailTime(Long.parseLong(mailOld.sendDate)) + "</font>" +
                    "<br/><font color='#757575'>收件人:</font><font color='#0066FF'>"+to+"</font>" +
                    "<br/><font color='#757575'>抄送人:</font><font color='#0066FF'>"+cc+"</font>" +
                    "<br/><font color='#757575'>主题:</font><font color='#31353B'>" + mailOld.subject + "</font></div>" + mailOld.contentWithImage;
        }
        loadDataWithBaseURL("", string1 + content + string2, "text/html", "UTF-8", "");
        mContents = content;
    }

    public void callback(String text) {
        try {
            String decode = URLDecoder.decode(text, "UTF-8");
            mContents = decode.replaceFirst(CALLBACK_SCHEME, "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getHtml() {
        return mContents;
    }

}