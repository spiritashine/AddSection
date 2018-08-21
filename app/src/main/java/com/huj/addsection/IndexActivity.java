package com.huj.addsection;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huj.addsection.display.DisplayCutoutDemo;
import com.huj.addsection.mail.MailLogActivity;
import com.huj.addsection.mutiple.MainActivity;

public class IndexActivity extends AppCompatActivity {
    private Context context;
    private DisplayCutoutDemo displayCutoutDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 28) {
            displayCutoutDemo = new DisplayCutoutDemo(this);
            displayCutoutDemo.openFullScreenModel();
        }
        setContentView(R.layout.activity_index);
        if (Build.VERSION.SDK_INT >= 28) {
            displayCutoutDemo.controlView();
        }
    }

    public void goMain(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }

    public void goMail(View view) {
        startActivity(new Intent(this,MailLogActivity.class));
    }
}
