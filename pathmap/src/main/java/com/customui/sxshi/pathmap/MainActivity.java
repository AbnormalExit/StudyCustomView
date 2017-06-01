package com.customui.sxshi.pathmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private CityMapView detialMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detialMapView= (CityMapView) findViewById(R.id.svg_map);
        detialMapView.setOnMapClickListener(new CityMapView.OnMapClickListener() {
            @Override
            public void onClick(CityItem cityItem) {
                Toast.makeText(MainActivity.this, cityItem.getCityName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
