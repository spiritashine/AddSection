package com.huj.addsection.mail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;


import com.huj.addsection.R;
import com.huj.addsection.mail.bean.ImageBucket;
import com.huj.addsection.mail.bean.ImageItem;
import com.huj.addsection.mail.manager.AlbumHelper;
import com.huj.addsection.mail.base.BaseActivity;
import com.huj.addsection.mail.manager.StringManager;
import com.huj.addsection.mail.fragment.PhotoAlbum1Fragment;
import com.huj.addsection.mail.fragment.PhotoAlbum2Fragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoAlbumActivity extends BaseActivity {
    AlbumHelper helper;
    public List<ImageBucket> dataList = new ArrayList<>();
    public int selectedPosition = 0;
    public static final int SHOW1 = 1;
    public static final int SHOW2 = 2;
    @Bind(R.id.tv_complete_album)
    TextView tvCompleteAlbum;
    public int selectedCountAll;
    private FragmentManager mFragmentManager;
    PhotoAlbum1Fragment photoAlbum1Fragment;
    PhotoAlbum2Fragment photoAlbum2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);
        ButterKnife.bind(this);
        helper = AlbumHelper.getHelper();
        helper.init(this);
        dataList = helper.getImagesBucketList(true);

        initFragment();
    }

    /**
     * 实例化fragment
     */
    private void initFragment() {
        photoAlbum1Fragment = new PhotoAlbum1Fragment();
        photoAlbum2Fragment = new PhotoAlbum2Fragment();
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_album, photoAlbum2Fragment);
        fragmentTransaction.add(R.id.fl_album, photoAlbum1Fragment);
        fragmentTransaction.commit();
        showFragment(SHOW1);
    }

    public void showFragment(int show) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (show) {
            case SHOW1:
                fragmentTransaction.show(photoAlbum1Fragment);
                fragmentTransaction.hide(photoAlbum2Fragment);
                break;

            case SHOW2:
                fragmentTransaction.hide(photoAlbum1Fragment);
                fragmentTransaction.show(photoAlbum2Fragment);
                break;
        }
        fragmentTransaction.commit();
    }


    /**
     * 得到选中的图片的数目
     */
    public int getSelectedCount() {
        selectedCountAll = 0;
        for (int i = 0; i < dataList.size(); i++) {
            selectedCountAll = dataList.get(i).selectedCount + selectedCountAll;
        }
        return selectedCountAll;
    }

    /**
     * 设置选中的图片数量
     */
    public void setCompleteAlbum(int selectedCount) {

        if (selectedCount > 0) {
            tvCompleteAlbum.setTextColor(ContextCompat.getColor(this, R.color.blue));
            tvCompleteAlbum.setClickable(true);
            tvCompleteAlbum.setText("完成(" + selectedCount + ")");
        } else {
            tvCompleteAlbum.setTextColor(ContextCompat.getColor(this, R.color.grey));
            tvCompleteAlbum.setClickable(false);
            tvCompleteAlbum.setText(StringManager.getString(R.string.complete));
        }
    }

    @OnClick(R.id.tv_complete_album)
    public void onClick() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            ArrayList<ImageItem> imageList = dataList.get(i).imageList;
            for (int j = 0; j < imageList.size(); j++) {
                if (imageList.get(j).isSelected) {
                    list.add(imageList.get(j).imagePath);
                }
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("pathes", list);
        setResult(WriteMailActivity.CHOOSE_PICTURE_ATTAC, intent);
        finish();
    }


    @Override
    public void onBackPressed() {
      if (photoAlbum1Fragment.isHidden()){
          showFragment(SHOW1);
      }else {
            finish();
        }
    }
}
