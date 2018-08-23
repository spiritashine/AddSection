package com.huj.addsection.mail.manager;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huj.addsection.R;


/**
 * 标题栏管理类 Created by yxl on 2016/5/4.
 */
public class TitleManager {

    public static final int BACK = 0;// 左边返回按钮，右侧什么都不显示
    public static final int IMG_IMG = 1;// 左边右侧都显示img
    public static final int TEXTBACK_TEXT = 2;//左边右侧都显示汉字
    public static final int TEXT_TEXT = 3;//左边右侧都显示汉字
    public static final int IMG_TEXT = 4;// 返回，右侧文字
    public static final int BACKTEXT = 5;//  右侧文字返回
    public static final int BACK_TEXT = 6;//  左边返回按钮图片，右边文字
    public static final int NO_TEXT = 7;//  左边什么都没有，右边汉字

    /**
     * @param activity  所在 activity
     * @param what      右边图标的显示方式
     * @param title     中间标题的文字
     * @param resources 右边图标的显示资源
     */
    public static void showTitle(final Activity activity, int what, String title,
                                 int... resources) {
        // 设置标题
        TextView tvTitle = (TextView) activity.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        // 左侧文字
        TextView tvLeft = (TextView) activity.findViewById(R.id.tv_title_left);
        // 左侧图片
        ImageView ivLeft = (ImageView) activity.findViewById(R.id.imgv_title_left);
        // 右侧文字
        TextView tvRight = (TextView) activity.findViewById(R.id.tv_title_right);
        // 右侧图片
        ImageView ivRight = (ImageView) activity.findViewById(R.id.imgv_title_right);

        switch (what) {
            case BACK:
                setBack(activity, ivLeft);
                break;
            case TEXT_TEXT:
                tvLeft.setVisibility(View.VISIBLE);
                tvRight.setVisibility(View.VISIBLE);
                ivLeft.setVisibility(View.INVISIBLE);
                tvLeft.setText(StringManager.getString(resources[0]));
                tvRight.setText(StringManager.getString(resources[1]));
                tvRight.setTextColor(ContextCompat.getColor(activity, R.color.text_tv_blue));
//                setBack(activity, tvLeft);
                break;
            case TEXTBACK_TEXT:
                tvLeft.setVisibility(View.VISIBLE);
                tvRight.setVisibility(View.VISIBLE);
                ivLeft.setVisibility(View.INVISIBLE);
                tvLeft.setText(StringManager.getString(resources[0]));
                tvRight.setText(StringManager.getString(resources[1]));
                tvRight.setTextColor(ContextCompat.getColor(activity, R.color.text_tv_blue));
                setBack(activity, tvLeft);
                break;
            case IMG_TEXT:
                tvRight.setVisibility(View.VISIBLE);
                if (resources.length>0){
                    tvRight.setText(StringManager.getString(resources[0]));
                }
                break;
            case BACKTEXT:
                ivLeft.setVisibility(View.INVISIBLE);
                tvLeft.setVisibility(View.VISIBLE);
                setBack(activity, tvLeft);
                break;
            case BACK_TEXT:
                ivLeft.setVisibility(View.VISIBLE);
                tvRight.setVisibility(View.VISIBLE);
                tvRight.setText(StringManager.getString(resources[0]));
                setBack(activity, ivLeft);
                break;
//            case NO_TEXT:
//                ivLeft.setVisibility(View.GONE);
//                tvRight.setVisibility(View.VISIBLE);
//                tvRight.setText(StringManager.getString(resources[0]));
//                break;
        }

    }

    /**
     * @param what      右边图标的显示方式
     * @param title     中间标题的文字
     * @param resources 右边图标的显示资源
     */
    public static void showTitle(final View view, int what, String title,
                                 int... resources) {
        // 设置标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        // 左侧文字
        TextView tvLeft = (TextView) view.findViewById(R.id.tv_title_left);
        // 左侧图片
        ImageView ivLeft = (ImageView) view.findViewById(R.id.imgv_title_left);
        // 右侧文字
        TextView tvRight = (TextView) view.findViewById(R.id.tv_title_right);
        // 右侧图片
        ImageView ivRight = (ImageView) view.findViewById(R.id.imgv_title_right);

        switch (what) {
            case IMG_IMG:
                ivRight.setVisibility(View.VISIBLE);
                ivLeft.setImageResource(resources[0]);
                ivRight.setImageResource(resources[1]);
                break;
            case IMG_TEXT:
                tvRight.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(StringManager.getString(resources[0]))){
                    tvRight.setText(StringManager.getString(resources[0]));
                }
                break;
            case NO_TEXT:
                ivLeft.setVisibility(View.GONE);
                tvRight.setVisibility(View.VISIBLE);
                tvRight.setText(StringManager.getString(resources[0]));
                break;
        }

    }

    /**
     * 得到标题栏左边的ImageView,以便设置点击事件
     */
    public static View getLeftImg(Activity activity) {
        return activity.findViewById(R.id.imgv_title_left);
    }

    /**
     * 得到标题栏左边的ImageView,以便设置点击事件
     */
    public static View getLeftImg(View view) {
        return view.findViewById(R.id.imgv_title_left);
    }

    /**
     * 得到标题栏右边的ImageView,以便设置点击事件
     */
    public static View getRighttImg(View view) {
        return view.findViewById(R.id.imgv_title_right);
    }

    /**
     * 得到标题栏边的ImageView,以便设置点击事件
     */
    public static View getIvRight(Activity activity) {
        return activity.findViewById(R.id.imgv_title_right);
    }


    /**
     * 得到标题栏右边的TextView,以便设置点击事件
     *
     * @param activity
     * @return
     */
    public static View getTvRight(Activity activity) {
        return activity.findViewById(R.id.tv_title_right);
    }


    /**
     * 设置返回点击事件
     */
    public static void setBack(final Activity activity, View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();// 返回
            }
        });
    }

}
