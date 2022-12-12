package com.project4.simpletimer;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText timeEt;
    private Button startBtn;
    private boolean started;
    private boolean alarmStopped;
    private CountDownTimer countDownTimer;
    private static final Handler HANDLER = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeEt = findViewById(R.id.timeEt);
        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(view -> {
            if (started) {
                started = false;
                startBtn.setText(R.string.start);
                timeEt.setCursorVisible(true);
                countDownTimer.cancel(); // cancel the countdown timer execution when user click on the stop button
            } else {
                started = true;
                startBtn.setText(R.string.stop);
                timeEt.setCursorVisible(false);
                alarmStopped = false;

                //get raw time entered by user at min:sec format
                String rawTime = timeEt.getText().toString();

                long time = 0;

                //try to parse the time entered by user
                try {
                    time = (Integer.parseInt(rawTime)) * 1000L; //convert seconds to millis
                } catch (Exception e) {
                    timeEt.setText(R.string.default_time); // default time is set if error
                }

                countDownTimer = new CountDownTimer(time, 100) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //update the counter during the execution
                        long seconds = millisUntilFinished / 1000;
                        timeEt.setText( "" + (seconds < 10 ? "0" + seconds : seconds));
                    }

                    @Override
                    public void onFinish() {
                        // execution is finished, we set default values
                        timeEt.setText(R.string.start_time);
                        startBtn.setText(R.string.start);
                    MediaPlayer beepSound = playDefaultNotification();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage(R.string.timer_done + "\n Tap to stop alarm" );


                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Handle positive button click
                                beepSound.stop();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        beepSound.setLooping(true);
                        beepSound.start();
                    }
                };
                countDownTimer.start();
            }
        });
    }
    /** Plays the default notification sound.
     * @return*/
    protected MediaPlayer playDefaultNotification() {
        final Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final MediaPlayer mediaPlayer = new MediaPlayer();
        final Context context = getApplicationContext();

        try {
            mediaPlayer.setDataSource(context, defaultRingtoneUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
            }
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return mediaPlayer;
    }
}