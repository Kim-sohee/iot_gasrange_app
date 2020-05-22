package org.techtown.mycheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class alarmActivity extends AppCompatActivity {

    private Switch sound,vibrate;
    private SoundPool soundPool;
    int tom;
    Vibrator mVib;

    String shared="Nodata";
    String shared2="Nodata";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getSupportActionBar().setTitle("알람설정");

        mVib=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);


        sound=(Switch)findViewById(R.id.sw_sound);
        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //여기에 소리 ON/OFF 동작
                if(buttonView.getId()==R.id.sw_sound){
                    if(sound.isChecked()){
                        soundPool=new SoundPool(1, AudioManager.STREAM_MUSIC,0);
                        tom=soundPool.load(getApplicationContext(),R.raw.ring,1);
                        soundPool.play(tom,1,1,0,1,2);
                        Toast.makeText(getApplicationContext(), "소리: "+sound.getTextOn().toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "소리: "+sound.getTextOff().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        vibrate=(Switch)findViewById(R.id.sw_vibrato);
        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //여기에 진동 ON/OFF 동작
                if(buttonView.getId()==R.id.sw_vibrato){
                    if(vibrate.isChecked()){
                        mVib.vibrate(500);
                        Toast.makeText(getApplicationContext(), "진동: "+vibrate.getTextOn().toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "진동: "+vibrate.getTextOff().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        SharedPreferences sharedPreferences1=getSharedPreferences(shared,MODE_PRIVATE);
        Boolean soundOnOff=sharedPreferences1.getBoolean("sou",false);
        sound.setChecked(soundOnOff);

        SharedPreferences sharedPreferences2=getSharedPreferences(shared2,MODE_PRIVATE);
        Boolean vibratoOnOff=sharedPreferences2.getBoolean("vib",false);
        vibrate.setChecked(vibratoOnOff);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences1=getSharedPreferences(shared,0);
        SharedPreferences.Editor editor1=sharedPreferences1.edit();
        Boolean soundOnOff=sound.isChecked();
        editor1.putBoolean("sou",soundOnOff);
        editor1.commit();

        SharedPreferences sharedPreferences2=getSharedPreferences(shared2,0);
        SharedPreferences.Editor editor2=sharedPreferences2.edit();
        Boolean vibratoOnOff=vibrate.isChecked();
        editor2.putBoolean("vib",vibratoOnOff);
        editor2.commit();
    }
}
