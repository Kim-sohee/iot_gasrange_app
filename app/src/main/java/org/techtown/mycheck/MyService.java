package org.techtown.mycheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyService extends Service {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    NotificationManager notificationManager;
    PendingIntent intent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        databaseReference=firebaseDatabase.getReference("android");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot messageData:dataSnapshot.getChildren()){
                    String msg=messageData.getValue().toString();

                    if(msg.equals("S")){
                        notification_Method1();
                    }
                    else if(msg.equals("D")){
                        notification_Method2();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "loadPost:onCancelled", Toast.LENGTH_SHORT).show();
            }
        });

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void notification_Method1(){
        intent= PendingIntent.getActivity(this,0,new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder=new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.btn_star)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("KITCHEN BOT")
                .setContentText("화재가 발생하였습니다!")
                .setAutoCancel(true)
                .setContentIntent(intent);

        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void notification_Method2(){
        intent= PendingIntent.getActivity(this,0,new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder=new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.btn_star)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("KITCHEN BOT")
                .setContentText("조리가 완료되었습니다.")
                .setAutoCancel(true)
                .setContentIntent(intent);

        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }

}
