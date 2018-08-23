package com.huj.addsection.mail.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.huj.addsection.R;
import com.huj.addsection.mail.bean.Attach;
import com.huj.addsection.mail.utils.FileUtils;
import com.huj.addsection.mail.manager.MediaFile;
import com.huj.addsection.mail.base.MyBaseAdapter;
import com.huj.addsection.mail.manager.T;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AttachMailDetailsAdapter extends MyBaseAdapter<Attach> {


    public AttachMailDetailsAdapter(Activity context, List<Attach> list) {
        super(context, list);
    }

    @Override
    protected int setLayoutRes() {

        return R.layout.item_attach_mail_details;
    }

    @Override
    protected View getView(final int position, View convertView, ViewGroup parent, ViewHolder holder) {
        final RelativeLayout rlFile = holder.obtainView(convertView, R.id.rl_file_attach_mail_details);
        TextView tvFileName = holder.obtainView(convertView, R.id.tv_file_name_mail_details);
        TextView tvFileSize = holder.obtainView(convertView, R.id.tv_file_size_mail_details);
        final RelativeLayout rlPic = holder.obtainView(convertView, R.id.rl_pic_attach_mail_details);
        final ImageView ivPic = holder.obtainView(convertView, R.id.iv_pic_attach_mail_details);
        final RelativeLayout rlProgress = holder.obtainView(convertView, R.id.rl_progress_attach_mail_details);

        final Attach attach = list.get(position);
        attach.isLoading = false;
        final File file  = new File(attach.path);
        if ( file.exists() && MediaFile.isImageFile(attach.path)) {
            showRl(rlPic);//显示文件下载RelativeLayout
            Glide.with(context).load(attach.path).into(ivPic);
        } else {
            showRl(rlFile);//显示图片RelativeLayout
            tvFileName.setText(attach.name);
            tvFileSize.setText(attach.size);
        }

        rlFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((file != null) && file.exists()) {
                    T.show("附件已下载，路径:" + attach.path);
                } else {
                    if (!attach.isLoading) {//判断是否正在下载
                        showRl(rlProgress);//显示正在下载下载RelativeLayout
                        attach.isLoading = true;
                        @SuppressLint("HandlerLeak") Handler handle = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                attach.isLoading = false;
                                if (msg.what == 1 && MediaFile.isImageFile(attach.path)){//下载成功
                                    showRl(rlPic);//显示图片RelativeLayout
                                    Glide.with(context).load(attach.path).into(ivPic);
                                }else {
                                    showRl(rlFile);//显示文件RelativeLayout
                                }
                            }
                        };
                        loadDownAttach(attach, handle);
                    }
                }
            }
        });

        return convertView;
    }

    public void loadDownAttach(final Attach attach, final Handler handler) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String dirPath = Environment.getExternalStorageDirectory() + "/Xiniuyun/attach/";
                File file = FileUtils.makeFilePath(dirPath,attach.name);
                BufferedOutputStream bos = null;
                BufferedInputStream bis = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    bis = new BufferedInputStream(attach.in);
                    int c;
                    while ((c = bis.read()) != -1) {
                        bos.write(c);
                        bos.flush();
                    }
                    handler.sendEmptyMessage(1);
                } catch (Exception exception) {
                    handler.sendEmptyMessage(0);
                    exception.printStackTrace();
                } finally {
                    try {
                        bos.close();
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 文件RelativeLayout，图片RelativeLayout，下载progress的RelativeLayout三个的展示
     */
    public void showRl(RelativeLayout rl){
        RelativeLayout parent = (RelativeLayout) rl.getParent();
        for (int i = 0;i<parent.getChildCount();i++){
            parent.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        rl.setVisibility(View.VISIBLE);
    }
}
