package com.huj.addsection.mail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.huj.addsection.R;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.manager.TimeManager;

import java.util.ArrayList;

/**
 * Created by huj on 2017/9/4.
 */

public class MailListAdapter extends RecyclerView.Adapter<MailListAdapter.MyViewHolder>{
    private ArrayList<Mail> mData = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private OnItemClickListener listener;

    public MailListAdapter(Context context ) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<Mail> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_mail_list, null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(mData.get(position).froms);
        holder.title.setText(mData.get(position).subject);
        holder.content.setText(mData.get(position).contentHint);
        String date = !TextUtils.isEmpty(mData.get(position).sendDate) ? TimeManager.getMailTime(Long.parseLong(mData.get(position).sendDate)) : "";
        holder.time.setText(date);
    }

    @Override
    public int getItemCount() {
        Log.e("=========", "getItemCount: "+mData.size());
        return mData.size() > 0 ? mData.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name,title,content,time;
        ImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.item_mail_name);
            title= (TextView) itemView.findViewById(R.id.item_mail_title);
            content= (TextView) itemView.findViewById(R.id.item_mail_content);
            time= (TextView) itemView.findViewById(R.id.item_mail_time);
            icon= (ImageView) itemView.findViewById(R.id.item_mail_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener!=null){
                listener.onItemClick(v);
            }
        }
    }
    public interface OnItemClickListener{
        void onItemClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
