package com.customui.sxshi.pathstudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.customui.sxshi.pathstudy.view.BaguaView;
import com.customui.sxshi.pathstudy.view.RotateArrowView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setContentView(new RadarView(this));
//        setContentView(new BaguaView(this));
        RotateArrowView rotateArrowView=new RotateArrowView(this);
        setContentView(rotateArrowView);
        rotateArrowView.startAnimation();
    }
}
