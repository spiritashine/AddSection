package com.huj.addsection.mail.manager;

import android.content.Context;
import android.widget.Toast;

import com.huj.addsection.App;

public class T {

    public static void show(Object text) {
        show(App.getApplication(), text, 0);
    }

    public static void show(Context context, Object text, int duration) {
        if (text instanceof String) {
            Toast.makeText(context, (String) text, duration).show();
            return;
        }
        if (text instanceof Integer) {

            Toast.makeText(context, (Integer) text, duration).show();
            return;
        }

    }
}
