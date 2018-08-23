package com.huj.addsection.mail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.huj.addsection.R;
import com.huj.addsection.mail.adapter.FileAdapter;
import com.huj.addsection.mail.base.BaseActivity;
import com.huj.addsection.mail.manager.StringManager;
import com.huj.addsection.mail.manager.TitleManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.ll_back_previous_level)
    LinearLayout llBackPreviousLevel;
    @Bind(R.id.lv_file)
    ListView lvFile;
    ArrayList<File> list = new ArrayList<>();
    FileAdapter fileAdapter;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ButterKnife.bind(this);

        // 设置标题栏
        TitleManager.showTitle(this, TitleManager.BACKTEXT, "", R.string.cancle);

        file = Environment.getExternalStorageDirectory();
        fileAdapter = new FileAdapter(this, list);
        lvFile.setAdapter(fileAdapter);

        //得到数据
        initData();

        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file1 = list.get(position);
                if (file1.isDirectory()) {
                    file = file1;
                    initData();
                } else {
                   Intent intent = new Intent();
                    intent.putExtra("path", file1.getAbsolutePath());
                    setResult(WriteMailActivity.RESULTCODE_FILE_ACTIVITY, intent);
                    FileActivity.this.finish();
                }
            }
        });
    }

    @OnClick({R.id.ll_back_previous_level})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back_previous_level: //返回上一行
                //得到数据
                file = file.getParentFile();
                initData();
                break;
        }
    }


    private void initData() {
        //如果是根目录则隐藏“返回上一行”
        if (file.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            llBackPreviousLevel.setVisibility(View.GONE);
            tvTitle.setText(StringManager.getString(R.string.sdcard0));//设置标题中间的文字
        } else {
            llBackPreviousLevel.setVisibility(View.VISIBLE);
            tvTitle.setText(file.getName());//设置标题中间的文字
        }
        //获取
        File[] files = file.listFiles();
        list.clear();
        for (File f : files) {
            if (!f.getName().startsWith(".")){
                list.add(f);
            }
        }
        //给文件排序
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.isDirectory() && rhs.isFile())
                    return -1;
                if (lhs.isFile() && rhs.isDirectory())
                    return 1;
                return lhs.getName().toLowerCase().compareTo( rhs.getName().toLowerCase());
            }
        });
        fileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (file.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {//根目录
           finish();
        } else {
            file = file.getParentFile();
            initData();
        }
    }
}
