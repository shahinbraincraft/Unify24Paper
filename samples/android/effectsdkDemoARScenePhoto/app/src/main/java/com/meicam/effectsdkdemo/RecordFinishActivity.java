package com.meicam.effectsdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.meicam.effectsdkdemo.R;

public class RecordFinishActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_finish);
        TextView textView = findViewById(R.id.tv_result);
        String resultPath = "file path:" + getIntent().getStringExtra("file_path");
        textView.setText(resultPath);
    }
}
