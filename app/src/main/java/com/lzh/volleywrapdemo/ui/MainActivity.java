package com.lzh.volleywrapdemo.ui;

import com.lzh.volleywrapdemo.R;
import com.lzh.volleywrapdemo.model.WeatherModel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button btnGetWeather;
    private TextView tvShowWeather;

    private WeatherModel mWeatherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetWeather = (Button) findViewById(R.id.main_btn_get_weather);
        tvShowWeather = (TextView) findViewById(R.id.main_tv_weather);
        btnGetWeather.setOnClickListener(onClickListener);

        mWeatherModel = new WeatherModel();
    }

    WeatherModel.WeatherCallback weatherCallback = new WeatherModel.WeatherCallback() {
        @Override
        public void fail(String msg) {
            tvShowWeather.setText(msg);
        }

        @Override
        public void success(String msg) {
            tvShowWeather.setText(msg);
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_btn_get_weather:
                    mWeatherModel.getWeather("beijing", weatherCallback);
                    break;
            }
        }
    };
}
