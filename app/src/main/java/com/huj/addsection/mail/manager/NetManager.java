package com.huj.addsection.mail.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetManager {

	/**
	 * 检测网络是否可用
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context == null)
			return false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		boolean flag= ni != null && ni.isConnectedOrConnecting();
		return flag;
	}
	
}
