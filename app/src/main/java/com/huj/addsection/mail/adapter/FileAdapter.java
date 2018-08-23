package com.huj.addsection.mail.adapter;

import android.app.Activity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.huj.addsection.R;
import com.huj.addsection.mail.manager.MediaFile;
import com.huj.addsection.mail.manager.TimeManager;

import java.io.File;
import java.util.List;

public class FileAdapter extends MyBaseAdapter<File> {

    public FileAdapter(Activity context, List<File> list) {
        super(context, list);

    }

    @Override
    protected int setLayoutRes() {

        return R.layout.item_file;
    }

    @Override
    protected View getView(int position, View convertView, ViewGroup parent,
                           ViewHolder holder) {
        TextView tvName = holder.obtainView(convertView, R.id.name_item_file);
        TextView tvSize = holder.obtainView(convertView, R.id.size_item_file);
        ImageView ivIcon = holder.obtainView(convertView, R.id.iv_file);

        File file = list.get(position);
        tvName.setText(file.getName());

        int id ;
        if (file.isDirectory()) {
            id = R.mipmap.file_icon_folder;
            tvSize.setVisibility(View.GONE);
        } else {

            tvSize.setText(Formatter.formatFileSize(context, file.length()) + "," + TimeManager.getTime(file.lastModified()));
            tvSize.setVisibility(View.VISIBLE);
            if (MediaFile.isImageFile(file.getAbsolutePath())) {
                id = R.mipmap.attach_file_icon_image;
            } else {
                id = R.mipmap.attach_file_icon_default;
            }
        }
       ivIcon.setImageResource(id);

        return convertView;
    }

}
