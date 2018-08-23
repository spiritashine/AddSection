package com.huj.addsection.mail.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huj.addsection.R;


public class DialogManager {
    private static TextView confirm;
    private static TextView cancle;
    private static ProgressDialog progressDialog;
    private static Dialog dialog;

    /**
     * 显示progressDialog
     */
    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context, message, R.style.dialog_progress);//创建Dialog并设置样式主题
        progressDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        progressDialog.show();
        Window window = progressDialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);//不获得焦点
    }

    /**
     * 显示Dialog
     */
    public static void showDialog(Context context, String title) {
        dialog = new Dialog(context, title, R.style.dialog_progress);//创建Dialog并设置样式主题
        dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        dialog.show();
    }

    /**
     * 显示Dialog
     */
    public static void showDialog(Context context, String title, String confirmString) {
        dialog = new Dialog(context, title, confirmString, R.style.dialog_progress);//创建Dialog并设置样式主题
        dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        dialog.show();
    }
 /**
     * 显示Dialog
     */
    public static void showDialog(Context context, String title, String confirmString, String cancleString) {
        dialog = new Dialog(context, title, confirmString,cancleString, R.style.dialog_progress);//创建Dialog并设置样式主题
        dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        dialog.show();
    }

    /**
     * 得到确定按钮
     */
    public static TextView getConfirm() {
        return confirm;
    }

    /**
     * 得到取消按钮
     */
    public static TextView getCancle() {
        return cancle;
    }

    /**
     * 关闭progressDialog
     */
    public static void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 关闭Dialog
     */
    public static void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    static class ProgressDialog extends AlertDialog {
        String message;

        public ProgressDialog(Context context, String message, int theme) {
            super(context, theme);
            this.message = message;

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_progress);
            TextView tv = (TextView) findViewById(R.id.message_progress_dialog);
            tv.setText(message);

        }
    }

    static class Dialog extends AlertDialog {
        String title;
        String confirmString;
        String cancleString;

        public Dialog(Context context, String title, int theme) {
            super(context);
            this.title = title;
        }

        public Dialog(Context cancle, String title, String confirmString, int theme) {
            this(cancle, title,theme);
            this.confirmString = confirmString;
        }

        public Dialog(Context cancle, String title, String confirmString, String cancleString, int theme) {
            this(cancle, title,confirmString, theme);
            this.cancleString = cancleString;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog);
            ((TextView) findViewById(R.id.title_dialog)).setText(title);
            if (confirmString != null) {
                ((TextView) findViewById(R.id.confirm_dialog)).setText(confirmString);
            }
            if (cancleString != null) {
                ((TextView) findViewById(R.id.cancle_dialog)).setText(cancleString);
            }
            confirm = (TextView) findViewById(R.id.confirm_dialog);
            cancle = (TextView) findViewById(R.id.cancle_dialog);
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDialog();
                }
            });
        }
    }

}
