package com.huj.addsection.mail.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/*
 * ScrollView并没有实现滚动监听，所以我们必须自行实现对ScrollView的监听，
 * 我们很自然的想到在onTouchEvent()方法中实现对滚动Y轴进行监听
 * ScrollView的滚动Y值进行监听
 *
 * 用在mail的详情界面，和webview一起使用
 */
public class MyScrollView extends ScrollView {
    private OnScrollListener listener;

    public void setOnScrollListener(OnScrollListener listener) {
        this.listener = listener;
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnScrollListener{
        void onScroll(int scrollY, int oldscrollY, boolean isUp);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (oldt > t ) {// 向下
            if(listener != null){
                listener.onScroll(t ,oldt,false);
            }
        } else if (oldt < t) {// 向上
            if(listener != null){
                listener.onScroll(t,oldt,true);
            }
        }

    }

}
