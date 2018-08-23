package com.huj.addsection.mail.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.huj.addsection.R;
import com.huj.addsection.mail.bean.ImageBucket;
import com.huj.addsection.mail.bean.ImageItem;

import java.util.List;

public class ImageBucketAdapter extends MyBaseAdapter<ImageBucket> {

    public ImageBucketAdapter(Activity context, List<ImageBucket> list) {
        super(context, list);
    }

    @Override
    protected int setLayoutRes() {

        return R.layout.item_image_bucket;
    }


    @Override
    protected View getView(int position, View convertView, ViewGroup parent, ViewHolder holder) {

        ImageView ivBucket = holder.obtainView(convertView, R.id.iv_image_bucket);
        RelativeLayout rlSelected = holder.obtainView(convertView, R.id.rl_image_bucket_selected);
        TextView tvSelected = holder.obtainView(convertView, R.id.tv_image_bucket_selected);
        TextView tvName = holder.obtainView(convertView, R.id.name_image_bucket);
        TextView tvCount = holder.obtainView(convertView, R.id.count_image_bucket);

        ImageBucket imageBucket = list.get(position);
        List<ImageItem> imageList = imageBucket.imageList;
        ImageItem imageItem = imageList.get(0);
        int selected = 0;
        for (int i = 0; i < imageList.size(); i++) {
            if (imageList.get(i).isSelected) {
                selected++;
            }
        }
        Glide.with(context).load(TextUtils.isEmpty(imageItem.thumbnailPath) ? imageItem.imagePath : imageItem.thumbnailPath).into(ivBucket);
        if (selected > 0) {
            rlSelected.setVisibility(View.VISIBLE);
            tvSelected.setText(selected + "");
        } else {
            rlSelected.setVisibility(View.INVISIBLE);
        }
        tvName.setText(imageBucket.bucketName);
        tvCount.setText(imageBucket.count + "");
        return convertView;
    }

}
