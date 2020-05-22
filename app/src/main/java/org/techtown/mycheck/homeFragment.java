package org.techtown.mycheck;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.goodiebag.protractorview.ProtractorView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.CookieManager;
import java.net.Socket;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private DatabaseReference androidDatabase=firebaseDatabase.getReference();
    private DatabaseReference arduinoDatabase=firebaseDatabase.getReference();
    private DatabaseReference rasDatabase=firebaseDatabase.getReference();

    private ChildEventListener mChildEventListener;
    String msg;
    String shared="Nodata";

    private ToggleButton VideoButton;

    private ToggleButton fireballButton;

    //ProtractorView protractorView1=new ProtractorView(getContext());
    private ProtractorView protractorView1;
    private ProtractorView protractorView2;

    private Animation fab_open,fab_close;
    private Boolean isFabOpen=false;
    private FloatingActionButton fab,fab0,fab1,fab2;

    public homeFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).settingActionBarTitle("홈");

        View v=inflater.inflate(R.layout.fragment_home, container, false);

        initDatabase();

        fab_open= AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fab_close=AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);

        fab=(FloatingActionButton)v.findViewById(R.id.fab);
        fab0=(FloatingActionButton)v.findViewById(R.id.fab0);
        fab1=(FloatingActionButton)v.findViewById(R.id.fab1);
        fab2=(FloatingActionButton)v.findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabOpen) {
                    button_close();
                } else {
                    button_open();
                }
            }
        });

        fab0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arduinoDatabase.child("fireball").setValue("3");
                Vibrator myVib;
                myVib=(Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                myVib.vibrate(50);
                button_close();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arduinoDatabase.child("fireball").setValue("1");
                Vibrator myVib;
                myVib=(Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                myVib.vibrate(50);
                button_close();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arduinoDatabase.child("fireball").setValue("2");
                Vibrator myVib;
                myVib=(Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                myVib.vibrate(50);
                button_close();
            }
        });


        //final TextView TEST_EditText=(TextView)v.findViewById(R.id.TEST_TextView);

        //firebase에서 android 폴더 안 데이터 읽기
        androidDatabase=firebaseDatabase.getReference("android");
        androidDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot messageData:dataSnapshot.getChildren()){
                    msg=messageData.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //firebase에서 arduino 폴더 안 데이터 읽기
        arduinoDatabase=firebaseDatabase.getReference("arduino");
        arduinoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot messageData:dataSnapshot.getChildren()){
                    msg=messageData.getValue().toString();

                    if(msg.equals("01")){
                        protractorView1.setAngle(0);
                        arduinoDatabase.child("firepower1").setValue("W");
                        arduinoDatabase.child("arduino").setValue("null");
                    }
                    if(msg.equals("02")){
                        protractorView2.setAngle(0);
                        arduinoDatabase.child("firepower2").setValue("W");
                        arduinoDatabase.child("arduino").setValue("null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //firebase에서 ras 폴더 안 데이터 읽기
        rasDatabase=firebaseDatabase.getReference("ras");
        rasDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot messageData:dataSnapshot.getChildren()){
                    msg=messageData.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //가스벨브 버튼 선택 제어
        fireballButton=(ToggleButton)v.findViewById(R.id.fireball_button);
        fireballButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    protractorView1.setVisibility(View.VISIBLE);
                    protractorView2.setVisibility(View.INVISIBLE);
                    fireballButton.setSelected(true);
                }
                else{
                    protractorView1.setVisibility(View.INVISIBLE);
                    protractorView2.setVisibility(View.VISIBLE);
                    fireballButton.setSelected(false);
                }
            }
        });


        //가스벨브 1번 제어
        protractorView1=(ProtractorView)v.findViewById(R.id.protractorView1);
        protractorView1.setOnProtractorViewChangeListener(new ProtractorView.OnProtractorViewChangeListener() {
            @Override
            public void onProgressChanged(ProtractorView protractorView, int i, boolean b) {
                if(i>0 && i<30){
                    arduinoDatabase.child("firepower1").setValue("W");
                }
                if(i>=30 && i<90){
                    arduinoDatabase.child("firepower1").setValue("X");
                }
                if(i>=90 && i<150){
                    arduinoDatabase.child("firepower1").setValue("Y");
                }
                if(i>=150 && i<=180){
                    arduinoDatabase.child("firepower1").setValue("Z");
                }

                if(i==2|i==60||i==120||i==179){
                    Vibrator myVib;
                    myVib=(Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    myVib.vibrate(30);
                }

            }

            @Override
            public void onStartTrackingTouch(ProtractorView protractorView) {

            }

            @Override
            public void onStopTrackingTouch(ProtractorView protractorView) {

            }
        });

        //가스벨브 2번 제어
        protractorView2=(ProtractorView)v.findViewById(R.id.protractorView2);
        protractorView2.setOnProtractorViewChangeListener(new ProtractorView.OnProtractorViewChangeListener() {
            @Override
            public void onProgressChanged(ProtractorView protractorView, int i, boolean b) {

                if(i>0 && i<30){
                    arduinoDatabase.child("firepower2").setValue("W");
                }
                if(i>=30 && i<90){
                    arduinoDatabase.child("firepower2").setValue("X");
                }
                if(i>=90 && i<150){
                    arduinoDatabase.child("firepower2").setValue("Y");
                }
                if(i>=150 && i<=180){
                    arduinoDatabase.child("firepower2").setValue("Z");
                }

                if(i==2|i==60||i==120||i==179){
                    Vibrator myVib;
                    myVib=(Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    myVib.vibrate(30);
                }

            }

            @Override
            public void onStartTrackingTouch(ProtractorView protractorView) {

            }

            @Override
            public void onStopTrackingTouch(ProtractorView protractorView) {

            }
        });


        SharedPreferences prefs=this.getActivity().getSharedPreferences("firepower",MODE_PRIVATE);
        int protra1=prefs.getInt("protra1",0);
        int protra2=prefs.getInt("protra2",0);
        protractorView1.setAngle(protra1);
        protractorView2.setAngle(protra2);

        Boolean check=prefs.getBoolean("check",true);
        fireballButton.setChecked(check);


        //비디오 재생 & 전원버튼
        final WebView VideoView=(WebView)v.findViewById(R.id.videoView);
        VideoButton=(ToggleButton)v.findViewById(R.id.powerButton);
        VideoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    //getActivity().startService(new Intent(getActivity(),MyService.class));

                    rasDatabase.child("ras").setValue("O");

                    //비디오 스트리밍
                    VideoView.getSettings().setJavaScriptEnabled(true);
                    VideoView.getSettings().setLoadWithOverviewMode(true);
                    VideoView.getSettings().setUseWideViewPort(true);

                    VideoView.setInitialScale(370);

                    VideoView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

                    VideoView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                    VideoView.setScrollbarFadingEnabled(true);


                    String url="http://223.194.171.105:8090/javascript_simple.html";
                    VideoView.loadUrl(url);

                    //줌 인 or 줌 아웃
                    VideoView.getSettings().setBuiltInZoomControls(true);
                    VideoView.getSettings().setSupportZoom(true);

                    Toast.makeText(getContext(),"ON",Toast.LENGTH_SHORT).show();
                    VideoButton.setSelected(true);


                }else{

                    arduinoDatabase.child("fireball").setValue("0");
                    arduinoDatabase.child("firepower1").setValue("W");
                    arduinoDatabase.child("firepower2").setValue("W");
                    rasDatabase.child("ras").setValue("0");

                    VideoView.getSettings().setJavaScriptEnabled(false);
                    VideoView.getSettings().setLoadWithOverviewMode(false);
                    VideoView.getSettings().setUseWideViewPort(false);

                    String url1="about:blank";
                    VideoView.loadUrl(url1);

                    Toast.makeText(getContext(),"OFF",Toast.LENGTH_SHORT).show();
                    VideoButton.setSelected(false);

                    //getActivity().stopService(new Intent(getActivity().getApplicationContext(),MyService.class));
                }
            }
        });

        //기억생성
        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences(shared,Context.MODE_PRIVATE);
        Boolean memory=sharedPreferences.getBoolean("Video",false);
        VideoButton.setChecked(memory);

        return v;

    }

    private void initDatabase(){
       /* firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("user");
        databaseReference.child("user").setValue("check");*/

        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        androidDatabase.addChildEventListener(mChildEventListener);
    }

    public void button_open(){
        fab0.startAnimation(fab_open);
        fab1.startAnimation(fab_open);
        fab2.startAnimation(fab_open);
        fab1.setClickable(true);
        fab1.setClickable(true);
        fab2.setClickable(true);
        isFabOpen = true;
    }
    public void button_close(){
        fab0.startAnimation(fab_close);
        fab1.startAnimation(fab_close);
        fab2.startAnimation(fab_close);
        fab1.setClickable(false);
        fab1.setClickable(false);
        fab2.setClickable(false);
        isFabOpen = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //기억 하기
        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences(shared,0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Boolean memory=VideoButton.isChecked();
        editor.putBoolean("Video",memory);

        SharedPreferences prefs=this.getActivity().getSharedPreferences("firepower",MODE_PRIVATE);
        SharedPreferences.Editor editor1=prefs.edit();
        int protra1=protractorView1.getAngle();
        editor1.putInt("protra1",protra1);
        int protra2=protractorView2.getAngle();
        editor1.putInt("protra2",protra2);

        Boolean check=fireballButton.isChecked();
        editor1.putBoolean("check",check);


        editor.commit();
        editor1.commit();


        getActivity().startService(new Intent(getActivity(),MyService.class));


    }
}
