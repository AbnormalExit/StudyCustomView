package com.customui.sxshi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.customui.sxshi.customui.WaveView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        WaveView waveView=new WaveView(this);
        setContentView(waveView);
        waveView.startAnimation();
    }
}
