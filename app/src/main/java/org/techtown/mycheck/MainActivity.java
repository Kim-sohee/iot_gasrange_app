package org.techtown.mycheck;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.akaita.android.circularseekbar.CircularSeekBar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String[] listItems;

    Vibrator mainVib;

    private SoundPool soundPool;
    int sound_beep;


    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference=firebaseDatabase.getReference();

    private DatabaseReference arduinoDatabase=firebaseDatabase.getReference();

    private ChildEventListener mChildEventListener;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainVib=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        soundPool=new SoundPool(5,AudioManager.STREAM_MUSIC,0);
        sound_beep=soundPool.load(this,R.raw.ring,1);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flMain,new homeFragment());
        ft.commit();

        navigationView.setCheckedItem(R.id.nav_home);

        //firebase에서 데이터 읽어서 알림창 띄우기
        databaseReference=firebaseDatabase.getReference("android");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot messageData:dataSnapshot.getChildren()){
                    msg=messageData.getValue().toString();


                    if(msg.equals("S")){
                        showDialog();
                    }
                    else if(msg.equals("D")){
                        showMessage();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        arduinoDatabase=firebaseDatabase.getReference("arduino");
        arduinoDatabase.addValueEventListener(new ValueEventListener() {
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



    }

    public void settingActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),"로봇 제어 시작",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain,new homeFragment());
            ft.commit();
        } else if (id == R.id.nav_timer) {
            Intent i=new Intent(MainActivity.this,timerActivity.class);
            startActivity(i);

        /*} else if (id == R.id.nav_alarm) {
            Intent i=new Intent(MainActivity.this,alarmActivity.class);
            startActivity(i);*/

        } else if (id == R.id.nav_setting) {
            Intent i=new Intent(MainActivity.this,settingActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //디텍션 알림창 메소드
    public void showMessage(){
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ditection);

        Button notifibutton=dialog.findViewById(R.id.notification_no);
        Button notifibutton2=dialog.findViewById(R.id.notification_yes);

        //soundPool.play(sound_beep,1f,1f,0,-1,2f);

        notifibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("android").setValue("null");
                //soundPool.stop(sound_beep);
                dialog.dismiss();
            }
        });

        notifibutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("android").setValue("null");
                arduinoDatabase.child("fireball").setValue("0");
                arduinoDatabase.child("arduino").setValue("01");
                //soundPool.stop(sound_beep);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //화재경보 알림창 메소드
    public void showDialog(){
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.error);

        //soundPool.play(sound_beep,1f,1f,0,-1,2f);

        //Button dialogbtn=dialog.findViewById(R.id.errorButton);
        Button dialogbtn2=dialog.findViewById(R.id.errorButton2);
        /*dialogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("android").setValue("a");
                dialog.dismiss();
            }
        });
*/
        dialogbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("android").setValue("null");
                arduinoDatabase.child("arduino").setValue("01");
                //soundPool.stop(sound_beep);
                dialog.dismiss();
            }
        });

        //mainVib.vibrate(new long[]{500,1000,500,1000},0);
        dialog.show();
    }



    public void timerSetting(){
        listItems=new String[]{"5분","10분","15분","20분"};
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("시간을 선택하세요");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"선택 됨: "+listItems[which],Toast.LENGTH_SHORT).show();

                if(which==0){
                    //5분 타이머
                }else if(which==1){
                    //10분 타이머
                }else if(which==2){
                    //15분 타이머
                }else{
                    //20분 타이머
                }

                dialog.dismiss();
            }
        });
        mBuilder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMessage();
            }
        });
        AlertDialog mDialog=mBuilder.create();
        mDialog.show();
    }

    public void calling(View view){
        Intent intent=new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:119"));
        startActivity(intent);

    }
}
