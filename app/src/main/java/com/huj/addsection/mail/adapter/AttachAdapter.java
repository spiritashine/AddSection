package com.huj.addsection.mail.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huj.addsection.R;
import com.huj.addsection.mail.bean.Attach;
import com.huj.addsection.mail.manager.MediaFile;
import com.huj.addsection.mail.base.MyBaseAdapter;
import com.huj.addsection.mail.view.MyGridView;

import java.util.List;

public class AttachAdapter extends MyBaseAdapter<Attach> {
    MyGridView myGridView;

    public AttachAdapter(Activity context, List<Attach> list, MyGridView myGridView) {
        super(context, list);
        this.myGridView = myGridView;
    }

    @Override
    protected int setLayoutRes() {

        return R.layout.item_attach;
    }

    @Override
    protected View getView(final int position, View convertView, ViewGroup parent,
                           ViewHolder holder) {
        RelativeLayout rlFile = holder.obtainView(convertView, R.id.rl_file_attach);
        TextView tvFileName = holder.obtainView(convertView, R.id.tv_file_name);
        TextView tvFileSize = holder.obtainView(convertView, R.id.tv_file_size);
        RelativeLayout rlPic = holder.obtainView(convertView, R.id.rl_pic_attach);
        ImageView ivPic = holder.obtainView(convertView, R.id.iv_pic_attach);
        TextView tvPicSize = holder.obtainView(convertView, R.id.tv_pic_size);

        ImageView ivDelete = holder.obtainView(convertView, R.id.iv_delete_attach);
        RelativeLayout rlAdd = holder.obtainView(convertView, R.id.rl_add_attach);

        if (position == list.size() - 1) {//最后一个现实添加按钮
            rlFile.setVisibility(View.INVISIBLE);
            rlPic.setVisibility(View.INVISIBLE);
            rlAdd.setVisibility(View.VISIBLE);
        } else {
            rlAdd.setVisibility(View.INVISIBLE);
            Attach attach = list.get(position);
            if (MediaFile.isImageFile(attach.path)) {//附件为图片
                rlFile.setVisibility(View.INVISIBLE);
                rlPic.setVisibility(View.VISIBLE);
                Glide.with(context).load(attach.path).into(ivPic);
                tvPicSize.setText(attach.size);
            } else {//附件不为图片
                rlFile.setVisibility(View.VISIBLE);
                rlPic.setVisibility(View.INVISIBLE);
                tvFileName.setText(attach.name);
                tvFileSize.setText(attach.size);
            }
        }

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
                if (list.size() < 2) {
                    myGridView.setVisibility(View.GONE);
                } else {
                    myGridView.setVisibility(View.VISIBLE);
                }
            }
        });


        return convertView;
    }

}
