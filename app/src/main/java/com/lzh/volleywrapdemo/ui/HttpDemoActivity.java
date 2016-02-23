package com.lzh.volleywrapdemo.ui;

import com.lzh.volleywrapdemo.R;
import com.lzh.volleywrapdemo.model.WeatherModel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * liuzhenhui 16/2/23.下午8:34
 */
public class HttpDemoActivity extends Activity {
    private static final String TAG = HttpDemoActivity.class.getSimpleName();

    private Button btnGetWeather;
    private TextView tvShowWeather;

    private WeatherModel mWeatherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_httpdemo);

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
                    mWeatherModel.getWeather(weatherCallback);
                    break;
            }
        }
    };
}
