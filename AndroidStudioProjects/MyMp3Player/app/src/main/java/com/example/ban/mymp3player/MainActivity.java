package com.example.ban.mymp3player;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private boolean isPlaying;
    private int position;
    private SeekBar seekbar;
    private Handler handler = new Handler();
    private int totalTimeMinute;
    private int totalTimeSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStart = (Button)findViewById(R.id.buttonStart);
        Button buttonPrevious = (Button)findViewById(R.id.buttonPrevious);
        Button buttonNext = (Button)findViewById(R.id.buttonNext);
        final TextView tv = (TextView)findViewById(R.id.textView1);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.flower_dance);

        seekbar = (SeekBar)findViewById(R.id.seekBar1);
        seekbar.setMax(mp.getDuration());
        totalTimeMinute = mp.getDuration()/1000/60;
        totalTimeSecond = mp.getDuration()/1000%60;

        tv.setText(totalTimeMinute +"분 "+totalTimeSecond+"초");

        class MyThead extends Thread {
            @Override
            public void run() {
                while(isPlaying){
                    position = mp.getCurrentPosition();
                    seekbar.setProgress(position);

                    handler.post(new Runnable() { // 시간이 약간 안맞음
                        @Override
                        public void run() {
                            totalTimeMinute = (mp.getDuration()-position)/1000/60;
                            totalTimeSecond = (mp.getDuration()-position)/1000%60;
                            tv.setText(totalTimeMinute +"분 "+totalTimeSecond+"초");
                        }
                    });
                }
            }
        }

        OnClickListener mce = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button)v;
                String state = b.getTag().toString();
                if("start".equals(state)) { // 시작
                    mp.seekTo(position);
                    b.setText("정지");
                    b.setTag("pause".toString());
                    isPlaying = true;
                    mp.start();
                    new MyThead().start();
                }else if("pause".equals(state)) { // 일시 정지
                    mp.pause();
                    b.setText("시작");
                    b.setTag("start".toString());
                    isPlaying = false;
                }else if("next".equals(state)) { // 다음 곡으로

                }else if("previous".equals(state)) { // 처음부터 시작

                }

            }
        };

        buttonStart.setOnClickListener(mce);

    } // end of onCreate
} // end of class
