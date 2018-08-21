package com.huj.addsection.mutiple;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.huj.addsection.R;

import java.util.ArrayList;
import java.util.HashMap;

public class NextActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Intent intent = getIntent();
        ArrayList<HashMap<String,String>> data = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("data");
        listView = findViewById(R.id.list);
        for ( int i = 0 ; i < data.size(); i++ ){
            list.add("name > " + data.get(i).get("name"));
            list.add("sex > " + data.get(i).get("sex"));
            list.add("============");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(arrayAdapter);
    }
}
