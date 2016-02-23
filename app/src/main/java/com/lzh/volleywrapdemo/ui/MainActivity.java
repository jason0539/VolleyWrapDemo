package com.lzh.volleywrapdemo.ui;

import com.lzh.volleywrapdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_btn_httpdemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(HttpDemoActivity.class);
            }
        });
        findViewById(R.id.main_btn_imagedemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(ImageDemoActivity.class);
            }
        });
    }

    private void goActivity(Class c) {
        Intent intent = new Intent(MainActivity.this, c);
        startActivity(intent);
    }
}
