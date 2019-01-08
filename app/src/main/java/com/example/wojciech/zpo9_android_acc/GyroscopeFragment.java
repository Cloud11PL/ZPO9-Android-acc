package com.example.wojciech.zpo9_android_acc;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class GyroscopeFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "gyroscope_fragment";

    private SensorManager sensor;

    private Sensor accelerometer;
    private boolean isRunning = false;
    private EditText editTextAx;
    private EditText editTextAy;
    private EditText editTextAz;
    private TabLayout tabs;
    ViewPager viewPager;

    private PowerManager powerManager;
    private PowerManager.WakeLock myWayClock;
    private LineGraphSeries mSeriesX;
    private LineGraphSeries mSeriesY;
    private LineGraphSeries mSeriesZ;
    ArrayList OYData = new ArrayList();
    int maxX = 10;
    GraphView graph;
    private double graph2LastXValue = 5d;

    //MainActivity mA = new MainActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accelometer_fragment,container,false);

        graph = view.findViewById(R.id.graph);
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

        editTextAx = view.findViewById(R.id.sensor_outputX);
        editTextAy = view.findViewById(R.id.sensor_outputY);
        editTextAz = view.findViewById(R.id.sensor_outputZ);


        powerManager = (PowerManager) this.getActivity().getSystemService(Context.POWER_SERVICE);
        myWayClock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"My:tagxD");

        sensor = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensor.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        return view;
    }

    public void onClickActionG(View view) {
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
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
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
        sensor = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
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
