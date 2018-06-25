package com.example.user.myapplication;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.example.user.myapplication.Contacts.contactData;

public class AnswerCallBroadcastReceiver extends BroadcastReceiver {

    Context context;
    AnswerCallBroadcastReceiver(){}
    public AnswerCallBroadcastReceiver(Context con){
        this.context = con;
    }

    String inComingNumber = "";
    static public int[] numberOfCalls = {0,0,0,0,0};
    public void setIncommingNumber(String str){
        inComingNumber = str;
    }
    public String getIncomingNumber(){
        return inComingNumber;
    }
    String TAG = "TAG";

/*
    private void requestMutePermissions(Context cont) {
        try {
            if (Build.VERSION.SDK_INT < 23) {
                AudioManager audioManager = (AudioManager)cont.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if( Build.VERSION.SDK_INT >= 23 ) {
                this.requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp();
            }
        } catch ( SecurityException e ) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp(Context cont) {

        NotificationManager notificationManager = (NotificationManager) cont.getSystemService(Context.NOTIFICATION_SERVICE);
        // if user granted access else ask for permission
        if ( notificationManager.isNotificationPolicyAccessGranted()) {
            AudioManager audioManager = (AudioManager) cont.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else{
            // Open Setting screen to ask for permisssion
           // Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
           // startActivityForResult( intent, ON_DO_NOT_DISTURB_CALLBACK_CODE );
        }
    }

    public void silentRingtone(Context cont){

        requestMutePermissions(cont);

        MediaPlayer catSoundMediaPlayer = new MediaPlayer();
        AudioManager am  = (AudioManager) cont.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        am.setRingerMode(0x00000000);

        try {
            if (catSoundMediaPlayer.isPlaying()) {
                catSoundMediaPlayer.stop();
                catSoundMediaPlayer.release();
                catSoundMediaPlayer = MediaPlayer.create(context, R.raw.beep);
            } catSoundMediaPlayer.start();
        } catch(Exception e) { e.printStackTrace(); }

    }
*/
    public void handlingIncomingCall(Context cont){
        String num = getIncomingNumber();
        for(int i=0; i<5; i++){
          //  Log.e(TAG, contactData.get(i).number);
            if(contactData.get(i).number.equals(num)){
                numberOfCalls[i]++;
                if(numberOfCalls[i] >= 3){
                    Log.e(TAG, "Called 3 times ");

                  //  silentRingtone(cont);
                    //am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                   //catSoundMediaPlayer.start();
                }

            }
            //break;
        }

    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {

        if(arg1.getAction().equals("android.intent.action.PHONE_STATE")){

            String state = arg1.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Log.d(TAG, "Inside Extra state off hook");
                String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                setIncommingNumber(number);
                Toast.makeText(arg0, "CALL FROM"+number, Toast.LENGTH_LONG).show();
                Log.e(TAG, "outgoing number : " + number);
            }

            else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Log.e(TAG, "Inside EXTRA_STATE_RINGING");
                String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                setIncommingNumber(number);
                Toast.makeText(arg0, "CALL FROM: "+number, Toast.LENGTH_LONG).show();

                setIncommingNumber(number);

                handlingIncomingCall(arg0);

                Log.e(TAG, "incoming number : " + number);
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Log.d(TAG, "Inside EXTRA_STATE_IDLE");
            }
        }
    }
}