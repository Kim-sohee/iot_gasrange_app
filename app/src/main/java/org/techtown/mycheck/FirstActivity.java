package org.techtown.mycheck;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.PixelCopy;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        Window window=getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        StartAnimations();

        //startService(new Intent(this, MyService.class));


        //splash화면 status bar 없애기
        if (Build.VERSION.SDK_INT >= 19){
            getWindow().getDecorView().setSystemUiVisibility
                    ( View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN );

        }
        else {

            // getWindow().getDecorView().setSystemUiVisibility(View.GONE);

            getWindow().getDecorView().setSystemUiVisibility
                    ( View.SYSTEM_UI_FLAG_LOW_PROFILE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

        }

    }

    private void StartAnimations(){
        splashTread=new Thread(){

            @Override
            public void run() {
                try {
                    int waited=0;
                    while(waited<2000){
                        sleep(100);
                        waited+=100;
                    }
                    Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                    startActivity(intent);
                    FirstActivity.this.finish();
                }catch (InterruptedException e){
                }finally {
                    FirstActivity.this.finish();
                }
            }
        };
        splashTread.start();
    }
}

