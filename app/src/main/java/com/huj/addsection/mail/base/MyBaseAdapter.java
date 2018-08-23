package com.huj.addsection.mail.base;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Adapter基类、实现getView以外的方法
 * Created by yxl on 2016/5/18.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    protected Activity context;
    protected List<T> list;
    protected LayoutInflater inflater;

    public MyBaseAdapter(Activity context, List<T> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public void addData(T t){
        this.list.add(t);
        notifyDataSetChanged();
    }

    public void addData(List<T> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void refreshData(List<T> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public List<T> getData(){
        return list;
    }

    public void removeData(List<T> list){
        this.list.removeAll(list);
        notifyDataSetChanged();
    }


    public void clearAdapter(){
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    /**  
     * 布局资源
     */  
    protected abstract int setLayoutRes();  
    
    /**  
     * 重写getView方法
     */  
    protected abstract View getView(int position, View convertView,
                                    ViewGroup parent, ViewHolder holder);
  
    /**  
     * 每个控件的缓存  
     */  
    protected class ViewHolder {  
        public SparseArray<View> view = new SparseArray<View>();
  
        /**  
         * 指定resId和类型即可获取到相应的view
         */  
        public <T extends View> T obtainView(View convertView, int resId) {
            View v = view.get(resId);
            if (null == v) {  
                v = convertView.findViewById(resId);  
                view.put(resId, v);  
            }  
            return (T) v;  
        }  
  
    }  
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;  
        if (null == convertView) {  
            holder = new ViewHolder();  
            convertView = LayoutInflater.from(context).inflate(setLayoutRes(),
                    null);  
            convertView.setTag(holder);  
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }  
        return getView(position, convertView, parent, holder);  
    }  
    
    


}
