package com.example.user.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{


    final Context super_context = this;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int PERMISSION_REQUEST_CODE = 1;
    AnswerCallBroadcastReceiver call;
    int[] threshold = {30,30,80,50,70};
    int[] durationOfCallMinWeek = {20, 5, 120, 20, 17};
    int[] frequenceOfCallMinWeek = {3, 6, 10, 2, 4};
    static String[] numbers = new String[5];

    Boolean isClickedOnSave = false;
    final String nameOfFile = "mobile_final4.txt";
    Button getContactList;
    Button saveInFile;
    Button settings;
    Button clear, clear2;
    TextView text;
    ScrollView scrollView;
    ScrollView contactView;
    LinearLayout ll;
    LinearLayout linLay;
    final String TAG = "TAG";
    Contacts contacts;


    private void getCallDetails() {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, null);
        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
        int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
        int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        sb.append( "Call Details :");
        while ( managedCursor.moveToNext() ) {
            String phNumber = managedCursor.getString( number );
            String callType = managedCursor.getString( type );
            String callDate = managedCursor.getString( date );
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString( duration );
            String dir = null;
            int dircode = Integer.parseInt( callType );
            switch( dircode ) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append( "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );
            sb.append("\n----------------------------------");
        }
        managedCursor.close();

        TextView tv1 = new TextView(getApplicationContext());
        tv1.setText(sb);
        linLay.addView(tv1);

    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   contacts = new Contacts(getApplicationContext());
      //  text = findViewById(R.id.textView);
      //  scrollView = new ScrollView(this);
        getContactList = findViewById(R.id.getContacts);
        clear = findViewById(R.id.clear);
        clear2 = findViewById(R.id.clear2);
        settings = findViewById(R.id.settings);

        call = new AnswerCallBroadcastReceiver(MainActivity.this);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to READ_CALL_LOG - requesting it");
                String[] permissions = {Manifest.permission.WRITE_CALL_LOG};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            boolean permission_to_write = isStoragePermissionGranted();
            boolean permission_to_read = checkPermissionForReadExtertalStorage();
            if(!permission_to_read){
                try {
                    requestPermissionForReadExtertalStorage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(permission_to_write){
/*
                Button getCallDet = findViewById(R.id.getCallInfo);
                getCallDet.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        linLay = new LinearLayout(getApplicationContext());
                        linLay.setOrientation(LinearLayout.VERTICAL);

                        contactView.addView(linLay);

                        //contacts.getDetailOfCalls(linLay, getApplicationContext());
                        getCallDetails();

                        setContentView(contactView);
                    }
                });
*/
               // FROM HERE 06-13

                clear.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){

                        try {
                            TimeUnit.SECONDS.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SpannableString spannableString = new SpannableString("Bluetooth is now ACTIVE");
                        spannableString.setSpan(
                                new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_light)),
                                0,
                                spannableString.length(),
                                0);

                        Toast.makeText(getApplicationContext(), spannableString, Toast.LENGTH_LONG).show();

                    }
                });


                clear2.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){

                        try {
                            TimeUnit.SECONDS.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SpannableString spanS = new SpannableString("Sending BEEP sound ...");
                        spanS.setSpan(
                                new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_light)),
                                0,
                                spanS.length(),
                                0);
                        Toast.makeText(getApplicationContext(), spanS, Toast.LENGTH_LONG).show();

                    }
                });






                settings.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        try {
                            TimeUnit.SECONDS.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SpannableString spannableString = new SpannableString("Silent mode is ACTIVE");
                        spannableString.setSpan(
                                new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_light)),
                                0,
                                spannableString.length(),
                                0);

                        Toast.makeText(getApplicationContext(), spannableString, Toast.LENGTH_LONG).show();
                    }
                });

                getContactList.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        startActivity(new Intent(getApplicationContext(), SetContactPriority.class));
                    }
                });



            }


        }

    }
}
