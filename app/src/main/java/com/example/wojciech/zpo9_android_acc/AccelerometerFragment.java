package com.example.wojciech.zpo9_android_acc;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AccelerometerFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "accelerometer_fragment";

    private SensorManager sensor;

    private Sensor accelerometer;
    private boolean isRunning = false;
    private EditText editTextAx;
    private EditText editTextAy;
    private EditText editTextAz;

    private PowerManager powerManager;
    private PowerManager.WakeLock myWayClock;
    private LineGraphSeries mSeriesX;
    private LineGraphSeries mSeriesY;
    private LineGraphSeries mSeriesZ;
    ArrayList OYData = new ArrayList();
    int maxX = 10;
    GraphView graph;
    private double graph2LastXValue = 5d;
    Button button;
    private ArrayList<pointData> dataToSave = new ArrayList<>();
    SimpleDateFormat sp = new SimpleDateFormat("HH:mm:ss");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accelometer_fragment,container,false);

        graph = view.findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    return sp.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
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

        button = (Button) view.findViewById(R.id.buttonA);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isRunning = !isRunning;
                Log.d(TAG,"Button Pressed");
                if (isRunning) {
                    myWayClock.acquire();
                    button.setText(getResources().getString(R.string.button_stop));
                } else {
                    myWayClock.release();
                    button.setText(getResources().getString(R.string.button_start));
                }
            }
        });
        powerManager = (PowerManager) this.getActivity().getSystemService(Context.POWER_SERVICE);
        myWayClock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"My:tagxD");

        sensor = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(isRunning) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float ax = event.values[0]; //Skladowa x wektora przyspieszenia
                float ay = event.values[1]; //Skladowa x wektora przyspieszenia
                float az = event.values[2]; //Skladowa x wektora przyspieszenia
                if(!Float.valueOf(ax).isNaN()){
                    pointData pointDataXD = new pointData(Float.toString(event.values[0]),Float.toString(event.values[1]),Float.toString(event.values[2]));
                    System.out.println(pointDataXD.getX());
                    dataToSave.add(pointDataXD);
                }
                float timeStamp = event.timestamp; // czas w nano-s
                Date actual = new Date(event.timestamp);
                System.out.println(actual.toString());
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
                    maxX = count;
                }
                Long actualDate = new Date().getTime();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND,10);
                Date dateMaX = calendar.getTime();
                calendar.add(Calendar.SECOND,-5);
                Date dateMiN = calendar.getTime();

                graph.getViewport().setMinX(dateMiN.getTime());
                graph.getViewport().setMaxX(dateMaX.getTime());
                graph.getViewport().setXAxisBoundsManual(true);
                mSeriesX.appendData(new DataPoint(actualDate, ax), true, count);
                mSeriesY.appendData(new DataPoint(actualDate, ay), true, count);
                mSeriesZ.appendData(new DataPoint(actualDate, az), true, count);


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

    public void writeDataToDeviceA() {
        if (dataToSave.size() > 0) {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), "Output Sensor Data");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "AccData.txt");
                FileWriter writer = new FileWriter(gpxfile,true);
                writer.append(String.valueOf(dataToSave));
                writer.flush();
                writer.close();
                Toast.makeText(getContext(), "Acc Data Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
        }
    }
}
