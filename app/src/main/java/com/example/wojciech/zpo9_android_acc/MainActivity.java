package com.example.wojciech.zpo9_android_acc;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    GraphView graph;
    AccelerometerFragment acc;
    GyroscopeFragment gyro;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pagerAdapter = new com.example.wojciech.zpo9_android_acc.PagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupNewPager(viewPager);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item1){
            acc.writeDataToDeviceA();
        }
        if(id == R.id.item2){
            gyro.writeDataToDeviceA();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupNewPager(ViewPager viewPager){
        com.example.wojciech.zpo9_android_acc.PagerAdapter pagerAdapter = new com.example.wojciech.zpo9_android_acc.PagerAdapter(getSupportFragmentManager());
        acc = new AccelerometerFragment();
        gyro = new GyroscopeFragment();
        pagerAdapter.addFragment(acc,"Accelerometer Output");
        pagerAdapter.addFragment(gyro,"Gyroscope Output");
        viewPager.setAdapter(pagerAdapter);

    }
}
