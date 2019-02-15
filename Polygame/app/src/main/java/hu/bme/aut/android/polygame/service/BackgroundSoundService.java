package hu.bme.aut.android.polygame.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import hu.bme.aut.android.polygame.R;

public class BackgroundSoundService extends Service {

    static MediaPlayer player;

    public BackgroundSoundService() {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.song);
        player.setLooping(true);
        player.setVolume(100,100);
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        player.start();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        player.stop();
        player.release();
    }

    public static void setVolume(float left, float right){
        player.setVolume(left, right);
    }

    public static void pauseMusic(){
        player.pause();
    }

    public static void restartMusic(){
        player.start();
    }

}