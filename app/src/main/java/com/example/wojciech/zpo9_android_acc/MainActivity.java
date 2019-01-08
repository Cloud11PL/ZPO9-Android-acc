package com.example.wojciech.zpo9_android_acc;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final String TAG = "MainActivity";
    private SensorManager sensor;
    private Sensor accelerometer;
    private boolean isRunning = false;
    private EditText editTextAx;
    private EditText editTextAy;
    private EditText editTextAz;
    private TabLayout tabs;
    ViewPager viewPager;


    private PagerAdapter pagerAdapter;



    private PowerManager powerManager;
    private PowerManager.WakeLock myWayClock;
    private LineGraphSeries mSeriesX;
    private LineGraphSeries mSeriesY;
    private LineGraphSeries mSeriesZ;
    ArrayList OYData = new ArrayList();
    int maxX = 10;
    GraphView graph;
    private double graph2LastXValue = 5d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pagerAdapter = new com.example.wojciech.zpo9_android_acc.PagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupNewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabs = findViewById(R.id.tabs);
        graph = findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(150);
        graph.getViewport().setMinY(-15);
        graph.getViewport().setMaxY(15);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        mSeriesX = new LineGraphSeries<>();
        mSeriesY = new LineGraphSeries<>();
        mSeriesZ = new LineGraphSeries<>();
        mSeriesX.setColor(Color.GREEN);
        mSeriesY.setColor(Color.RED);
        mSeriesZ.setColor(Color.BLUE);
        graph.addSeries(mSeriesX);
        graph.addSeries(mSeriesY);
        graph.addSeries(mSeriesZ);

        editTextAx = findViewById(R.id.sensor_outputX);
        editTextAy = findViewById(R.id.sensor_outputY);
        editTextAz = findViewById(R.id.sensor_outputZ);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        myWayClock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"My:tagxD");

        sensor = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void setupNewPager(ViewPager viewPager){
        com.example.wojciech.zpo9_android_acc.PagerAdapter pagerAdapter = new com.example.wojciech.zpo9_android_acc.PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new AccelerometerFragment(),"Accelerometer Output");
        pagerAdapter.addFragment(new GyroscopeFragment(),"Gyroscope Output");
        viewPager.setAdapter(pagerAdapter);
    }

    public SensorManager sManager(){
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        myWayClock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"My:tagxD");

        sensor = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        return sensor;
    }

    public void onClickAction(View view) {
        isRunning = !isRunning;
        Log.d(TAG,"Button Pressed");
        if (isRunning) {
            myWayClock.acquire();
        } else {
            myWayClock.release();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(isRunning) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float ax = event.values[0]; //Skladowa x wektora przyspieszenia
                float ay = event.values[1]; //Skladowa x wektora przyspieszenia
                float az = event.values[2]; //Skladowa x wektora przyspieszenia
                float timeStamp = event.timestamp; // czas w nano-s
                Log.d(TAG, "aX = " + Float.toString(ax) + "timeStamp = " + Float.toString(timeStamp));
                editTextAx.setText(Float.toString(ax));
                editTextAy.setText(Float.toString(ay));
                editTextAz.setText(Float.toString(az));
                OYData.add(ax);
                int count = OYData.size();
                DataPoint[] values = new DataPoint[count];

                for (int i = 0; i < count; i++) {
                    double x = i;
                    double y = ax;
                    DataPoint v = new DataPoint(x, y);
                    values[i] = v;
                }

                if (count >= maxX) {
                    //graph.getViewport().setMaxX(count);
                    maxX = count;
                }
                //mSeries1 = new LineGraphSeries<>(values);

                mSeriesX.appendData(new DataPoint(graph2LastXValue, ax), true, count);
                mSeriesY.appendData(new DataPoint(graph2LastXValue, ay), true, count);
                mSeriesZ.appendData(new DataPoint(graph2LastXValue, az), true, count);
                graph2LastXValue += 1d;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensorXD, int accuracy) {
        /*
        Checking if sensor has changed accuracy. Output data may change.
         */
        sensor = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        if (sensorXD == accelerometer) {
            switch (accuracy) {
                case 0:
                    System.out.println("Unreliable");
                    sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case 1:
                    System.out.println("Low Accuracy");
                    sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case 2:
                    System.out.println("Medium Accuracy");
                    //sensor.registerListener(this,accelerometer,1000000,1000000);
                    sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case 3:
                    System.out.println("High Accuracy");
                    sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                    break;
            }
        }
    }
}
