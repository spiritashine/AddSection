package com.huj.addsection.display;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

public class DisplayCutoutDemo {

    private Activity mAc;

    public DisplayCutoutDemo(Activity ac) {
        mAc = ac;
    }


    //在使用LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES的时候，状态栏会显示为白色，这和主内容区域颜色冲突,
    //所以我们要开启沉浸式布局模式，即真正的全屏模式,以实现状态和主体内容背景一致
    public void openFullScreenModel(){
        mAc.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = mAc.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= 28) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        }
        mAc.getWindow().setAttributes(lp);
        View decorView = mAc.getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        systemUiVisibility |= flags;
        mAc.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    //获取状态栏高度
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height","dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("hj","**getStatusBarHeight**" + result);
        return result;
    }

    @RequiresApi(api = 28)
    public void controlView(){
        View decorView = mAc.getWindow().getDecorView();
        if(decorView != null){
            Log.d("hj", "**controlView**" + android.os.Build.VERSION.SDK_INT);
            Log.d("hj", "**controlView**" + android.os.Build.VERSION_CODES.P);
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if(windowInsets != null){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                    //getBoundingRects返回List<Rect>,没一个list表示一个不可显示的区域，即刘海屏，可以遍历这个list中的Rect,
                    //即可以获得每一个刘海屏的坐标位置,当然你也可以用类似getSafeInsetBottom的api
                    Log.d("hj", "**controlView**" + displayCutout.getBoundingRects());
                    Log.d("hj", "**controlView**" + displayCutout.getSafeInsetBottom());
                    Log.d("hj", "**controlView**" + displayCutout.getSafeInsetLeft());
                    Log.d("hj", "**controlView**" + displayCutout.getSafeInsetRight());
                    Log.d("hj", "**controlView**" + displayCutout.getSafeInsetTop());
                }
            }
        }
    }
}