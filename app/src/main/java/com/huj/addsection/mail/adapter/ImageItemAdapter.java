package com.huj.addsection.mail.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huj.addsection.R;
import com.huj.addsection.mail.bean.ImageItem;

import java.util.List;

public class ImageItemAdapter extends MyBaseAdapter<ImageItem> {

    public ImageItemAdapter(Activity context, List<ImageItem> list) {
        super(context, list);
    }

    @Override
    protected int setLayoutRes() {

        return R.layout.item_image_attach;
    }

    @Override
    protected View getView(int position, View convertView, ViewGroup parent, ViewHolder holder) {

        ImageView ivAttach = holder.obtainView(convertView, R.id.iv_item_image_attach);
        ImageView ivAttachSelected = holder.obtainView(convertView, R.id.iv_item_image_attach_selected);

        ImageItem imageItem = list.get(position);
        Glide.with(context).load(TextUtils.isEmpty(imageItem.thumbnailPath) ? imageItem.imagePath : imageItem.thumbnailPath).into(ivAttach);
        if (imageItem.isSelected) {
            ivAttachSelected.setVisibility(View.VISIBLE);
        } else {
            ivAttachSelected.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}
