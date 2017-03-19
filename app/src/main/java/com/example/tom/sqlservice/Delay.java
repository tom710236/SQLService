package com.example.tom.sqlservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by TOM on 2017/3/14.
 */
//繼承Service (需在Manifests上宣告此Service)
public class Delay extends Service{
    //宣告
    Runnable runnable;
    Handler handler;
    int i=0;
    //建構子
    public Delay(){
    }
    // startServicec後
    // 每五秒就執行Log.e("delay","timeDelay");和makeNotification();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                Log.e("delay","timeDelay");
                i++;
                makeNotification();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable,5000);
        return super.onStartCommand(intent, flags, startId);
    }
    //stopService後
    //執行Log.e("STOP","STOP") ,handler.removeCallbacks(runnable);
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("STOP","STOP");
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //推播
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void makeNotification() {
        Bitmap bmp = BitmapFactory
                .decodeResource(getResources(), R.drawable.pig64);
        Notification.BigPictureStyle big =
                new Notification.BigPictureStyle();
        big.bigPicture(
                BitmapFactory.decodeResource(getResources(), R.drawable.pig256))
                .setSummaryText("bla bla bla");
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.pig32)
                .setContentTitle("新增")
                .setContentText("新增一筆資料")
                .setContentInfo("第"+i+"資料")
                .setWhen(System.currentTimeMillis())
                .build();
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(i, notification);
        Log.e("INT", String.valueOf(i));
    }
}