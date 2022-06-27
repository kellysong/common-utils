package com.sjl.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.sjl.arrowstepview.R;
import com.sjl.util.LogUtils;


public class CommonUtilsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_utils_activity);
        LogUtils.init("SIMPLE_LOGGER", true);
        LogUtils.i("CommonUtilsActivity");
    }


}
