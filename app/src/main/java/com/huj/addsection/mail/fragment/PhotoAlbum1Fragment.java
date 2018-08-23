package com.huj.addsection.mail.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import com.huj.addsection.R;
import com.huj.addsection.mail.adapter.ImageBucketAdapter;
import com.huj.addsection.mail.PhotoAlbumActivity;
import com.huj.addsection.mail.base.BaseFragment;
import com.huj.addsection.mail.manager.StringManager;
import com.huj.addsection.mail.manager.TitleManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PhotoAlbum1Fragment extends BaseFragment {
    @Bind(R.id.gv_album1)
    GridView gvAlbum1;

    ImageBucketAdapter imageBucketAdapter;

    @Override
    public View initView() {
        View view = View.inflate(pActivity, R.layout.fragment_photo_album1, null);
        ButterKnife.bind(this, view);

        // 设置标题栏
        TitleManager.showTitle(view, TitleManager.NO_TEXT, StringManager.getString(R.string.photo_album), R.string.cancle);

        imageBucketAdapter = new ImageBucketAdapter(pActivity, pActivity.dataList);
        gvAlbum1.setAdapter(imageBucketAdapter);
        gvAlbum1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pActivity.selectedPosition = position;
                pActivity.showFragment(PhotoAlbumActivity.SHOW2);
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
            pActivity.selectedPosition=0;
            imageBucketAdapter.notifyDataSetChanged();
            pActivity.setCompleteAlbum(pActivity.getSelectedCount());//设置选中的图片数量
        }
    }

    @OnClick(R.id.tv_title_right)
    public void onClick() {
        pActivity.finish();
    }
}
