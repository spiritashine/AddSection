package com.huj.addsection.mail.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;


import com.huj.addsection.R;
import com.huj.addsection.mail.FileActivity;
import com.huj.addsection.mail.PhotoAlbumActivity;
import com.huj.addsection.mail.WriteMailActivity;
import com.huj.addsection.mail.bean.Contacts;

import java.util.ArrayList;

public class PopupUtils {
    public static PopupWindow mPopupWindowContactsHint;
    public static ListView mWriteMailContactsHintListView;
    public static ArrayList<Contacts> contactses = new ArrayList<>();


    /**
     * WriteMailActivity中 ,添加附件弹出的PopupWindow
     */
    public static void showPopupWindowAddAttach(final WriteMailActivity activity) {
        InputUtills.hide(activity);
        View contentView = View.inflate(activity, R.layout.popup_add_attach, null);
        ImageView choosePhoto = (ImageView) contentView.findViewById(R.id.iv_choosephoto_addattach);
        ImageView takePhoto = (ImageView) contentView.findViewById(R.id.iv_takephoto_addattach);
        ImageView chooseFile = (ImageView) contentView.findViewById(R.id.iv_choosefile_addattach);
        final PopupWindow mPopWindow = getAndSetPopWindow(activity, contentView, ScreenUtils.getScreenWidth(activity) - 2 * ScreenUtils.dip2px(activity, 10), ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupwindow的显示位置
        mPopWindow.showAtLocation(View.inflate(activity, R.layout.activity_main, null), Gravity.BOTTOM, 0, ScreenUtils.dip2px(activity, 10));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_choosephoto_addattach:
                        activity.startActivity(null, PhotoAlbumActivity.class, WriteMailActivity.ATTAC);
                        break;

                    case R.id.iv_takephoto_addattach:
                        WriteMailUtils.photo(activity, WriteMailActivity.ATTAC);
                        break;

                    case R.id.iv_choosefile_addattach:
                        activity.startActivity(null, FileActivity.class, WriteMailActivity.ATTAC);
                        break;
                }
                mPopWindow.dismiss();
            }
        };
        choosePhoto.setOnClickListener(onClickListener);
        takePhoto.setOnClickListener(onClickListener);
        chooseFile.setOnClickListener(onClickListener);
    }

    /**
     * WriteMailActivity中 ,写邮件的时候，添加图片
     */
    public static void showPopupWindowInsertImg(final WriteMailActivity activity) {
        InputUtills.hide(activity);
        View contentView = View.inflate(activity, R.layout.popup_insert_img, null);
        ImageView choosePhoto = (ImageView) contentView.findViewById(R.id.iv_choosephoto_insertimg);
        ImageView takePhoto = (ImageView) contentView.findViewById(R.id.iv_takephoto_insertimg);
        final PopupWindow mPopWindow = getAndSetPopWindow(activity, contentView, ScreenUtils.getScreenWidth(activity) - 2 * ScreenUtils.dip2px(activity, 10), ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupwindow的显示位置
        mPopWindow.showAtLocation(View.inflate(activity, R.layout.activity_main, null), Gravity.BOTTOM, 0, ScreenUtils.dip2px(activity, 10));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_choosephoto_insertimg:
                        activity.startActivity(null, PhotoAlbumActivity.class, WriteMailActivity.INSERT_IMG);
                        break;

                    case R.id.iv_takephoto_insertimg:
                        WriteMailUtils.photo(activity, WriteMailActivity.INSERT_IMG);
                        break;
                }
                mPopWindow.dismiss();
            }
        };
        choosePhoto.setOnClickListener(onClickListener);
        takePhoto.setOnClickListener(onClickListener);
    }

    /**
     * 得到PopupWindow，并对PopupWindow进行基本设置
     */
    public static PopupWindow getAndSetPopWindow(final Activity context, View contentView, int w, int h) {
        final PopupWindow popWnd = new PopupWindow(contentView, w, h);
        popWnd.setFocusable(true);
//        mPopWindowPlus.setOutsideTouchable(true);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popWnd.setBackgroundDrawable(new ColorDrawable(0xff0000));

        // PopupWindow弹出窗体弹出的时候背景变暗
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.5f; // 0.0-1.0f
        context.getWindow().setAttributes(lp);

        // 监听PopupWindow窗口消失
        popWnd.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // PopupWindow窗口消失的时候，背景恢复
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
            }
        });
        return popWnd;
    }





}
