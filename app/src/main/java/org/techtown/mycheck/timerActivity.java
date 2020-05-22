package org.techtown.mycheck;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class timerActivity extends AppCompatActivity {
    private TextView timer1_textView;
    private TextView timer2_textView;

    private Button timer1_start;
    private Button timer1_stop;
    private Button timer2_start;
    private Button timer2_stop;

    private SeekBar seekBar1;
    private SeekBar seekBar2;

    private CountDownTimer mCountDownTimer1;
    private CountDownTimer mCountDownTimer2;

    private boolean mTimerRunning1;
    private boolean mTimerRunning2;

    private long mStartTimeInMillis1;
    private long mTimeLeftInMillis1;
    private long mEndTime1;
    private long mStartTimeInMillis2;
    private long mTimeLeftInMillis2;
    private long mEndTime2;


    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private DatabaseReference arduinoDatabase=firebaseDatabase.getReference();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().setTitle("타이머 설정");

        seekBar1=findViewById(R.id.seekBar1);
        timer1_textView=findViewById(R.id.timer1_TextView);
        timer1_start=findViewById(R.id.timer1_start);
        timer1_stop=findViewById(R.id.timer1_stop);

        seekBar2=findViewById(R.id.seekBar2);
        timer2_textView=findViewById(R.id.timer2_TextView);
        timer2_start=findViewById(R.id.timer2_start);
        timer2_stop=findViewById(R.id.timer2_stop);



        arduinoDatabase=firebaseDatabase.getReference("arduino");
        arduinoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot messageData:dataSnapshot.getChildren()){
                    String msg=messageData.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //첫번째 시크바 값을 받아서 텍스트에 넣기
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timer1_textView.setText(String.valueOf(progress));
                int input = seekBar.getProgress();
                if (input == 0) {
                    Toast.makeText(getApplicationContext(), "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(String.valueOf(input)) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime1(millisInput);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //2번 시크바 값을 받아서 텍스트에 넣기
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timer2_textView.setText(String.valueOf(progress));
                int input = seekBar.getProgress();
                if (input == 0) {
                    Toast.makeText(getApplicationContext(), "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(String.valueOf(input)) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime2(millisInput);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //1번 스타트 버튼을 눌렀을 때
        timer1_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning1){
                    pauseTimer1();
                }else{
                    startTimer1();
                }
            }
        });

        //2번 스타트 버튼을 눌렀을 때
        timer2_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning2){
                    pauseTimer2();
                }else{
                    startTimer2();
                }
            }
        });


        //1번 리셋 버튼을 눌렀을 때
        timer1_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer1();
            }
        });

        //2번 리셋 버튼을 눌렀을 때
        timer2_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer2();
            }
        });



       /* arduinoDatabase=firebaseDatabase.getReference("arduino");
        arduinoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot messageData:dataSnapshot.getChildren()){
                   String msg=messageData.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(mTimeLeftInMillis1==0){
            arduinoDatabase.child("firepower1").setValue("w");
        }
        if(mTimeLeftInMillis2==0){
            arduinoDatabase.child("firepower2").setValue("w");
        }*/
    }


    //1번 리셋 기능
    private void setTime1(long milliseconds){
        mStartTimeInMillis1=milliseconds;
        resetTimer1();
    }

    //2번 리셋 기능
    private void setTime2(long milliseconds){
        mStartTimeInMillis2=milliseconds;
        resetTimer2();
    }

    //1번 시작 기능
    private void startTimer1(){
        mEndTime1=System.currentTimeMillis()+mTimeLeftInMillis1;

        mCountDownTimer1=new CountDownTimer(mTimeLeftInMillis1,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis1=millisUntilFinished;
                updateCountDownText1();
            }

            @Override
            public void onFinish() {
                mTimerRunning1=false;
                updateWatchInterface1();
            }
        }.start();
        mTimerRunning1=true;
        updateWatchInterface1();
    }

    //2번 시작 기능
    private void startTimer2(){
        mEndTime2=System.currentTimeMillis()+mTimeLeftInMillis2;

        mCountDownTimer2=new CountDownTimer(mTimeLeftInMillis2,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis2=millisUntilFinished;
                updateCountDownText2();
            }

            @Override
            public void onFinish() {
                mTimerRunning2=false;
                updateWatchInterface2();
            }
        }.start();
        mTimerRunning2=true;
        updateWatchInterface2();
    }

    //1번 일시정지 기능
    private void pauseTimer1(){
        mCountDownTimer1.cancel();
        mTimerRunning1=false;
        updateWatchInterface1();
    }

    //2번 일시정지 기능
    private void pauseTimer2(){
        mCountDownTimer2.cancel();
        mTimerRunning2=false;
        updateWatchInterface2();
    }

    //1번 리셋기능
    private void resetTimer1(){
        mTimeLeftInMillis1=mStartTimeInMillis1;
        updateCountDownText1();
        updateWatchInterface1();
    }

    //2번 리셋기능
    private void resetTimer2(){
        mTimeLeftInMillis2=mStartTimeInMillis2;
        updateCountDownText2();
        updateWatchInterface2();
    }

    //1번 텍스트 값 변경
    private void updateCountDownText1(){
        int hours=(int)(mTimeLeftInMillis1/1000)/3600;
        int minutes=(int)((mTimeLeftInMillis1/1000)%3600)/60;
        int seconds=(int)(mTimeLeftInMillis1/1000)%60;

        String timeLeftFormatted;
        if(hours>0){
            timeLeftFormatted=String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,seconds);
        }else {
            timeLeftFormatted=String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        timer1_textView.setText(timeLeftFormatted);



        //만약 시간이 0이라면 가스벨브 off
        if(hours==0 && minutes==0 && seconds==1){
            arduinoDatabase.child("arduino").setValue("01");
        }

    }

    //2번 텍스트 값 변경
    private void updateCountDownText2(){
        int hours=(int)(mTimeLeftInMillis2/1000)/3600;
        int minutes=(int)((mTimeLeftInMillis2/1000)%3600)/60;
        int seconds=(int)(mTimeLeftInMillis2/1000)%60;

        String timeLeftFormatted;
        if(hours>0){
            timeLeftFormatted=String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,seconds);
        }else {
            timeLeftFormatted=String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        timer2_textView.setText(timeLeftFormatted);

        //만약 시간이 0이라면 가스벨브 off
        if(hours==0 && minutes==0 && seconds==1){
            arduinoDatabase.child("arduino").setValue("02");
        }

    }


    //1번 타이머 관련 버튼 보이기 or 숨기기
    private void updateWatchInterface1(){
        if(mTimerRunning1){
            seekBar1.setVisibility(View.INVISIBLE);
            timer1_stop.setVisibility(View.INVISIBLE);
            timer1_start.setBackgroundResource(R.drawable.ic_pause);
        }else{
            seekBar1.setVisibility(View.VISIBLE);
            timer1_start.setBackgroundResource(R.drawable.ic_play);

            if(mTimeLeftInMillis1<1000){
               timer1_start.setVisibility(View.INVISIBLE);
            }else{
                timer1_start.setVisibility(View.VISIBLE);
            }
            if(mTimeLeftInMillis1<mStartTimeInMillis1){
                timer1_stop.setVisibility(View.VISIBLE);
            }else{
                timer1_stop.setVisibility(View.INVISIBLE);
            }
        }
    }

    //2번 타이머 관련 버튼 보이기 or 숨기기
    private void updateWatchInterface2(){
        if(mTimerRunning2){
            seekBar2.setVisibility(View.INVISIBLE);
            timer2_stop.setVisibility(View.INVISIBLE);
            timer2_start.setBackgroundResource(R.drawable.ic_pause);
        }else{
            seekBar2.setVisibility(View.VISIBLE);
            timer2_start.setBackgroundResource(R.drawable.ic_play);

            if(mTimeLeftInMillis2<1000){
                timer2_start.setVisibility(View.INVISIBLE);
            }else{
                timer2_start.setVisibility(View.VISIBLE);
            }
            if(mTimeLeftInMillis2<mStartTimeInMillis2){
                timer2_stop.setVisibility(View.VISIBLE);
            }else{
                timer2_stop.setVisibility(View.INVISIBLE);
            }
        }
    }

    //시작될때 저장된 값 읽기
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs=getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();

        editor.putLong("startTimelnMillis1",mStartTimeInMillis1);
        editor.putLong("millisLeft1",mTimeLeftInMillis1);
        editor.putBoolean("timerRunning1",mTimerRunning1);
        editor.putLong("endTime1",mEndTime1);

        editor.putLong("startTimelnMillis2",mStartTimeInMillis2);
        editor.putLong("millisLeft2",mTimeLeftInMillis2);
        editor.putBoolean("timerRunning2",mTimerRunning2);
        editor.putLong("endTime2",mEndTime2);

        editor.apply();

        if(mCountDownTimer1!=null){
            mCountDownTimer1.cancel();
        }
        if(mCountDownTimer2!=null){
            mCountDownTimer2.cancel();
        }
    }


    //삭제될때 저장되는 값
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mStartTimeInMillis1=prefs.getLong("startTimelnMillis1",600000);
        mTimeLeftInMillis1=prefs.getLong("millisLeft1",mStartTimeInMillis1);
        mTimerRunning1=prefs.getBoolean("timerRunning1",false);

        mStartTimeInMillis2=prefs.getLong("startTimelnMillis2",600000);
        mTimeLeftInMillis2=prefs.getLong("millisLeft2",mStartTimeInMillis2);
        mTimerRunning2=prefs.getBoolean("timerRunning2",false);

        updateCountDownText1();
        updateWatchInterface1();

        updateCountDownText2();
        updateWatchInterface2();

        if(mTimerRunning1){
            mEndTime1=prefs.getLong("endTime1",0);
            mTimeLeftInMillis1=mEndTime1-System.currentTimeMillis();

            if(mTimeLeftInMillis1<0){
                mTimeLeftInMillis1=0;
                mTimerRunning1=false;
                updateCountDownText1();
                updateWatchInterface1();
            }else{
                startTimer1();
            }
        }

        if(mTimerRunning2) {
            mEndTime2 = prefs.getLong("endTime2", 0);
            mTimeLeftInMillis2 = mEndTime2 - System.currentTimeMillis();

            if (mTimeLeftInMillis2 < 0) {
                mTimeLeftInMillis2 = 0;
                mTimerRunning2 = false;
                updateCountDownText2();
                updateWatchInterface2();
            } else {
                startTimer2();
            }
        }
    }

}
