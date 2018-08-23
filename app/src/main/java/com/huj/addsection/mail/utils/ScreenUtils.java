package com.huj.addsection.mail.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 1、获取屏幕宽高 2、dp、px、sp之间转换 Created by yxl on 2016/5/3.
 */
public class ScreenUtils {

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 *            context
	 * @return 屏幕宽
	 */
	public static int getScreenWidth(Context context) {
		if (null == context)
			return 0;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 *            context
	 * @return 屏幕高
	 */
	public static int getScreenHeight(Context context) {
		if (null == context)
			return 0;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 有些界面在版本大于19的时候，需要将状态栏设为沉浸式
	 * 
	 */
	public static void setTranslucentStatus(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = activity.getWindow();

			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			
		}
	}

	/**
	 * 有些界面在版本大于19的时候，将状态栏设为沉浸式,将界面的父布局设置10dp的padding
	 * 
	 */
	public static void setViewPadding(Activity activity, View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			view.setPadding(0, ScreenUtils.dip2px(activity, 15), 0, 0);
		}
	}

	/**
	 * 有些界面在版本大于19的时候，将状态栏设为沉浸式,将界面的父布局设置10dp的padding
	 * 
	 */
	public static void setViewVisible(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			view.setVisibility(View.VISIBLE);
		}
	}

}
