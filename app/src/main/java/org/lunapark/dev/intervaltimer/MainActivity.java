package org.lunapark.dev.intervaltimer;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SoundPool.OnLoadCompleteListener, SetTime.DialogListener {

    private ProgressBar progressBar;
    private Button btnStart, btnStop, btnTime1, btnTime2;
    private TextView tvInterval;

    private Bundle bundle;

    private int timeInterval1 = 20;
    private int timeInterval2 = 10;
    private boolean running = false;

    private SoundPool soundPool;
    private int sndBlip1, sndBlip2;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setEnabled(false);
        btnStop.setOnClickListener(this);

        btnTime1 = (Button) findViewById(R.id.btnTime1);
        btnTime1.setOnClickListener(this);

        btnTime2 = (Button) findViewById(R.id.btnTime2);
        btnTime2.setOnClickListener(this);

        updateIntervals();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        tvInterval = (TextView) findViewById(R.id.tvInterval);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);

        try {
            sndBlip1 = soundPool.load(getAssets().openFd("blip1.ogg"), 1);
            sndBlip2 = soundPool.load(getAssets().openFd("blip2.ogg"), 1);


        } catch (IOException e) {
            e.printStackTrace();
        }


        bundle = new Bundle();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnStart:
                running = true;
                AsyncTimer asyncTimer = new AsyncTimer();
                asyncTimer.execute();
                break;
            case R.id.btnStop:
                running = false;
                break;

            case R.id.btnTime1:
                setTimeDialog(timeInterval1, 1);
                break;
            case R.id.btnTime2:
                setTimeDialog(timeInterval2, 2);
                break;
        }
    }

    private void setTimeDialog(int time, int interval) {
        DialogFragment newFragment = new SetTime();
        bundle.putInt("Min", Math.round(time / 60));
        bundle.putInt("Sec", time % 60);
        bundle.putInt("Interval", interval);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "SetTime");
    }

    @Override
    protected void onDestroy() {
        running = false;
        soundPool.release();
        super.onDestroy();
    }

    private String getTimeString(int input) {
        int min = Math.round(input / 60);
        int sec = input % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int seconds, int interval) {
        if (seconds < 2) seconds = 2;
        switch (interval) {
            case 1:
                timeInterval1 = seconds;
                break;
            case 2:
                timeInterval2 = seconds;
                break;
        }

        updateIntervals();
    }

    private void updateIntervals() {
        btnTime1.setText(getTimeString(timeInterval1));
        btnTime2.setText(getTimeString(timeInterval2));
    }

    class AsyncTimer extends AsyncTask<Void, Integer, Void> {

        private Integer loop = 0;

        @Override
        protected void onPreExecute() {
            btnStart.setEnabled(false);
            btnTime1.setEnabled(false);
            btnTime2.setEnabled(false);
            btnStop.setEnabled(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (running) {
                    loop++;
                    vibrator.vibrate(300);
                    soundPool.play(sndBlip2, 1, 1, 0, 1, 1);
                    for (int i = 0; i < timeInterval1; i++) {
                        if (!running) break;
                        Integer finish = timeInterval1;
                        Integer current = i;
                        publishProgress(finish, current, loop);
                        TimeUnit.SECONDS.sleep(1);
                    }
                    vibrator.vibrate(200);
                    soundPool.play(sndBlip1, 1, 1, 0, 0, 1);

                    for (int i = 0; i < timeInterval2; i++) {
                        if (!running) break;
                        Integer finish = timeInterval2;
                        Integer current = i;
                        publishProgress(finish, current, loop);
                        TimeUnit.SECONDS.sleep(1);
                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            int limit = values[0];
            int result = values[1];
            int progress = Math.round(100 * result / limit);
            progressBar.setProgress(progress);


            tvInterval.setText(values[2] + "\n" + getTimeString(limit - result));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            btnStart.setEnabled(true);
            btnTime1.setEnabled(true);
            btnTime2.setEnabled(true);
            btnStop.setEnabled(false);
        }
    }
}
