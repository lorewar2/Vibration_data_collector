package com.example.Test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.content.Context;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.widget.RelativeLayout;
import java.util.Calendar;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import android.os.Environment;



public class MainActivity extends AppCompatActivity {
    private SensorManager sensormanager;
    private Sensor accelmeter;
    private static final String TAG = "myApp";

    private Long startTime = Calendar.getInstance().getTimeInMillis();
    private SimpleDrawingView paintview_x;
    private SimpleDrawingView paintview_y;
    private SimpleDrawingView paintview_z;
    private RelativeLayout relativeLayout;
    ArrayList<Long> record_time = new ArrayList<Long>();
    ArrayList<Float> record_x = new ArrayList<Float>();
    ArrayList<Float> record_y = new ArrayList<Float>();
    ArrayList<Float> record_z = new ArrayList<Float>();
    private boolean recording = false;
    private boolean vibrating = false;
    private String textToWrite = "";

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            TextView text_x = (TextView) findViewById(R.id.textx);
            TextView text_y = (TextView) findViewById(R.id.texty);
            TextView text_z = (TextView) findViewById(R.id.textz);
            text_x.setText("x: " + Float.toString(sensorEvent.values[0]));
            text_y.setText("y: " + Float.toString(sensorEvent.values[1]));
            text_z.setText("z: " + Float.toString(sensorEvent.values[2]));
            paintview_x = (SimpleDrawingView) findViewById(R.id.canvas_x_id);
            paintview_x.x = (sensorEvent.values[0] * 500);
            paintview_x.invalidate();
            paintview_y = (SimpleDrawingView) findViewById(R.id.canvas_y_id);
            paintview_y.x = (sensorEvent.values[1] * 500);
            paintview_y.invalidate();
            paintview_z = (SimpleDrawingView) findViewById(R.id.canvas_z_id);
            paintview_z.x = (sensorEvent.values[2] * 500);
            paintview_z.invalidate();
            if(recording == true) {
                record_time.add(Calendar.getInstance().getTimeInMillis() - startTime);
                record_x.add(sensorEvent.values[0]);
                record_y.add(sensorEvent.values[1]);
                record_z.add(sensorEvent.values[2]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelmeter = sensormanager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensormanager.registerListener(listener, accelmeter, SensorManager.SENSOR_DELAY_GAME);

    }
    public void update_text(View view) {

    }
    public void vibrate(View view) {
        EditText amp_text = (EditText) findViewById(R.id.text_input_amp);
        int amplitude = Integer.parseInt(amp_text.getText().toString());
        amplitude = amplitude % 256;
        // do something
        final VibrationEffect vibrationEffect;
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Button button = (Button)findViewById(R.id.button_vibrate);
        button.setText("Vibrating");
        vibrationEffect = VibrationEffect.createOneShot(30 * 60 * 1000, amplitude);
        // it is safe to cancel other vibrations currently taking place
        vibrator.cancel();
        vibrator.vibrate(vibrationEffect);
        vibrating = true;
        set_status(view);
    }
    public void stop_vibrating(View view) {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
        Button button = (Button)findViewById(R.id.button_vibrate);
        button.setText("Vibrate");
        vibrating = false;
        set_status(view);
    }
    public void start_recording(View view) {
        recording = true;
        startTime = Calendar.getInstance().getTimeInMillis();
        record_time.clear();
        record_x.clear();
        record_y.clear();
        record_z.clear();
        set_status(view);
    }
    public void stop_recording(View view) {
        recording = false;
        set_status(view);
        writeFileExternalStorage();
    }
    public void set_status(View view){
        TextView text = (TextView)findViewById(R.id.status);
        if(vibrating && recording) {
            text.setText("Recording, Vibrating");
        }
        else if (vibrating) {
            text.setText("Not Recording, Vibrating");
        }
        else if (recording) {
            text.setText("Recording, Not Vibrating");
        }
        else {
            text.setText("Not Recording, Not Vibrating");
        }
    }
    public void share_file(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToWrite);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
    public void writeFileExternalStorage() {
        //Text of the Document
        textToWrite = "\nStarted at " + Long.toString(startTime) + "\nindex\ttime\tx\ty\tz";

        for (int i = 0; i < record_time.size(); i++){
            textToWrite = textToWrite + "\n" + Integer.toString(i) + "\t" + Long.toString(record_time.get(i)) + "\t" + Float.toString(record_x.get(i)) + "\t" + Float.toString(record_y.get(i)) + "\t" + Float.toString(record_z.get(i));
        }

        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }

        //Create a new file that points to the root directory, with the given name:
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "vibration_output.txt");

        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(textToWrite.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
