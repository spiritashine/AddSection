package com.huj.addsection.mutiple;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huj.addsection.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;

public class MainActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private int count = 0;
    private ArrayList<HashMap<String,String>> dataList = new ArrayList<>();
    private Button button,next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.container);
        button = findViewById(R.id.add_button);
        next = findViewById(R.id.button_next);
        onAdd();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAdd();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NextActivity.class);
                intent.putExtra("data",dataList);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void onAdd() {
        final int index = count;
        count ++;
        final View inflate = LayoutInflater.from(this).inflate(R.layout.item_add, null);
        TextView title = inflate.findViewById(R.id.title);
        final TextView sexChoose = inflate.findViewById(R.id.sex_choose);
        final TextView delete = inflate.findViewById(R.id.delete);
        final EditText name = inflate.findViewById(R.id.name_edit);

        final HashMap<String,String> map = new HashMap<>();
        map.put("name",name.getText().toString());
        map.put("sex",sexChoose.getText().toString());
        dataList.add(map);

        if (count == 1){
            delete.setVisibility(View.GONE);
        }

        title.setText("topic" + count);
        linearLayout.addView(inflate);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataList.remove(index);
                linearLayout.removeView(inflate);
                for ( int i = 0 ; i < dataList.size(); i++ ){
                    Log.e("=========", "name: "+dataList.get(i).get("name"));
                    Log.e("=========", "sex: "+dataList.get(i).get("sex"));
                    Log.e("=========", "");
                }
            }
        });

        sexChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSexData(sexChoose,map);
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                map.put("name",editable.toString());
            }
        });

    }

    private void setSexData(final TextView sexChoose, final HashMap<String,String> map) {
        final List<String> data = new ArrayList<>();
        data.add("male");
        data.add("female");
        data.add("transgender");
        OptionPicker picker = new OptionPicker(this, data);
        for (int i = 0; i < data.size(); i++) {
            if (sexChoose.getText().toString().equals(data.get(i))) {
                sexChoose.setText(data.get(i));
            }
        }

        picker.setOffset(2);
        picker.setTextSize(16);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                sexChoose.setText(item);
                map.put("sex",item);

            }
        });
        picker.show();
    }

}
