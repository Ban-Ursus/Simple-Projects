package com.example.ban.frame_animation;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private AnimationDrawable drawable;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ba = (Button)findViewById(R.id.buttonA);
        Button bj = (Button)findViewById(R.id.buttonJ);
        Button br = (Button)findViewById(R.id.buttonR);
        iv = (ImageView)findViewById(R.id.imageView);

        drawable = (AnimationDrawable)iv.getDrawable();

        drawable.stop();

        View.OnClickListener mce = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(drawable.isRunning()) drawable.stop();

                switch (v.getId()) {
                    case R.id.buttonA:
                        iv.setImageResource(R.drawable.pink_attack);

                        drawable = (AnimationDrawable)iv.getDrawable();

                        drawable.start();

                        break;
                    case R.id.buttonJ:
                        iv.setImageResource(R.drawable.pink_jump);

                        drawable = (AnimationDrawable)iv.getDrawable();

                        drawable.start();

                        break;
                    case R.id.buttonR:
                        iv.setImageResource(R.drawable.pink_run);

                        drawable = (AnimationDrawable)iv.getDrawable();

                        drawable.start();

                        break;
                }
            }
        };
        ba.setOnClickListener(mce);
        bj.setOnClickListener(mce);
        br.setOnClickListener(mce);

    }
}
