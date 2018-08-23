package com.huj.addsection.mail.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.huj.addsection.R;
import com.huj.addsection.mail.adapter.ImageItemAdapter;
import com.huj.addsection.mail.PhotoAlbumActivity;
import com.huj.addsection.mail.base.BaseFragment;
import com.huj.addsection.mail.bean.ImageBucket;
import com.huj.addsection.mail.bean.ImageItem;
import com.huj.addsection.mail.manager.StringManager;
import com.huj.addsection.mail.manager.TitleManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PhotoAlbum2Fragment extends BaseFragment {
    @Bind(R.id.gv_album2)
    GridView gvAlbum2;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    ArrayList<ImageItem> imageList = new ArrayList<>();
    ImageItemAdapter imageItemAdapter;
    ImageBucket imageBucket;

    @Override
    public View initView() {
        View view = View.inflate(pActivity, R.layout.fragment_photo_album2, null);
        ButterKnife.bind(this, view);

        // 设置标题栏
        TitleManager.showTitle(view, TitleManager.IMG_TEXT, StringManager.getString(R.string.photo_album), R.string.cancle);

        imageItemAdapter = new ImageItemAdapter(pActivity, imageList);
        gvAlbum2.setAdapter(imageItemAdapter);
        gvAlbum2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imageList.get(position).isSelected = !imageList.get(position).isSelected;
                imageItemAdapter.notifyDataSetChanged();
                if (imageList.get(position).isSelected) {
                    imageBucket.selectedCount++;
                    pActivity.selectedCountAll++;
                } else {
                    imageBucket.selectedCount--;
                    pActivity.selectedCountAll--;
                }
                pActivity.setCompleteAlbum(pActivity.selectedCountAll);//修改下面“完成”的按钮
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {//展示时调用的方法
            //得到该页显示的imageBucket
            imageBucket = pActivity.dataList.get(pActivity.selectedPosition);
            tvTitle.setText(imageBucket.bucketName);//设置标题栏的内容
            imageList.clear();
            imageList.addAll(imageBucket.imageList);
            imageItemAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.imgv_title_left, R.id.tv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgv_title_left:
                pActivity.showFragment(PhotoAlbumActivity.SHOW1);
                break;

            case R.id.tv_title_right:
                pActivity.finish();
                break;
        }

    }
}
