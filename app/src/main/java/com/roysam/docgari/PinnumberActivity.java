package com.roysam.docgari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roysam.docgari.models.AESUtils;
import com.roysam.docgari.models.Clearpath;
import com.roysam.docgari.models.Session;
import com.roysam.docgari.models.User;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lucifer.org.snackbartest.Icon;
import lucifer.org.snackbartest.MySnack;

public class PinnumberActivity extends AppCompatActivity {
    private EditText mEdtPin, mEdtConfirmPin, mEdtFullName,mEdtRecoveryEmail;
    private Spinner mSpinnerPrivilege;
    private Button mBtnContinue;
    private String phone_number;
    private boolean pin_number;
    int privilege = 0;

    SweetAlertDialog confirm_response_message,success_message,error_message;
    SweetAlertDialog message_alert,error_alert;
    private TextView mTvResetPin;
    View myLayout;
    String alert_answer,user_setting_service_pin,user_reseting_service_pin,user_confirming_security_answer,user_encryption_time,mySess;
    TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinnumber);
        checkConnectivity();
        Session mySession = new Session(getApplicationContext());
        mySess = mySession.getAuth();

        mEdtPin = findViewById(R.id.edt_pin);
        mEdtConfirmPin = findViewById(R.id.edt_confirm_pin);
        mEdtFullName = findViewById(R.id.edt_ful_name);
        mBtnContinue = findViewById(R.id.buttonContinue);
        mEdtRecoveryEmail = findViewById(R.id.edt_recovery_email);
        mTvResetPin = findViewById(R.id.tv_reset_pin);
        mSpinnerPrivilege = findViewById(R.id.spinner_priviledge);
        phone_number = select_phone();
        pin_number = select_pin();
        Session session = new Session(getApplicationContext());
        if (!session.getValue().isEmpty()){
            if (!phone_number.isEmpty() && pin_number == true){
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }


        //Message alert
        message_alert = new SweetAlertDialog(PinnumberActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        message_alert.setTitleText("SUCCESS!").setCancelable(false);
        message_alert.setCanceledOnTouchOutside(false);
        message_alert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                message_alert.dismiss();
//                finish();
            }
        });

        //Warning alert
        error_alert = new SweetAlertDialog(PinnumberActivity.this, SweetAlertDialog.ERROR_TYPE);
        error_alert.setTitleText("FAILED!")
                .setCancelable(false);
        error_alert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                error_alert.dismiss();
//                finish();
            }
        });
        mSpinnerPrivilege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                privilege = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                privilege = 0;
            }
        });



        AndroidNetworking.initialize(PinnumberActivity.this);


        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin_number = mEdtPin.getText().toString().trim();
                String confirm_pin_number = mEdtConfirmPin.getText().toString().trim();
                String full_name = mEdtFullName.getText().toString().trim();
                String recovery_email = mEdtRecoveryEmail.getText().toString().trim();


                if (pin_number.isEmpty()){
                    mEdtPin.setError("Please enter pin");
                    mEdtPin.requestFocus();
                }else if (confirm_pin_number.length() < 4){
                    mEdtPin.setError("Pin too short");
                    mEdtPin.requestFocus();
                }else if (confirm_pin_number.isEmpty()){
                    mEdtConfirmPin.setError("Please confirm pin");
                    mEdtConfirmPin.requestFocus();
                }else if (confirm_pin_number.length() < 4){
                    mEdtConfirmPin.setError("Confirm pin too short");
                    mEdtConfirmPin.requestFocus();
                }else if (!pin_number.equals(confirm_pin_number)){
                    mEdtConfirmPin.setError("Pin and confirm pin don't match");
                    mEdtConfirmPin.requestFocus();
                }else if (full_name.isEmpty()){
                    mEdtFullName.setError("Please enter name");
                    mEdtFullName.requestFocus();
                }else if (recovery_email.isEmpty()){
                    mEdtRecoveryEmail.setError("Please enter pin");
                    mEdtRecoveryEmail.requestFocus();
                }else {
                    String time = String.valueOf(System.currentTimeMillis());
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users/"+time);
                    User user = new User(full_name,recovery_email,phone_number,String.valueOf(privilege),"0.00","0.00",time);
                    ProgressDialog dialog = new ProgressDialog(PinnumberActivity.this);
                    dialog.setTitle("Loading");
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()){
                                set_pin_number(phone_number,pin_number,full_name,recovery_email,String.valueOf(privilege));
                            }else {
                                alert("ERROR","Failed. Please check data connection and try again");
                            }
                        }
                    });

                }

            }
        });



    }

    public String select_phone(){
        SQLiteDatabase db;
        Cursor cursor;
        db= openOrCreateDatabase("user_phone_number", MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS phonenumber(id VARCHAR,number VARCHAR)");
        cursor = db.rawQuery("SELECT * FROM phonenumber",null);
        if (cursor.getCount()==0){
            return  "";
        }else {
            StringBuffer buffer = new StringBuffer();
            while (cursor.moveToNext()){
                buffer.append(cursor.getString(1));
            }
            String phone = buffer.toString();
            return  phone;
        }
    }

    public boolean select_pin(){
        SQLiteDatabase db;
        Cursor cursor;
        db= openOrCreateDatabase("user_pin_number", MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS pinnumber(id VARCHAR,pinnumber VARCHAR)");
        cursor = db.rawQuery("SELECT * FROM pinnumber",null);
        if (cursor.getCount()==0){
            return  false;
        }else {
            return  true;
        }
    }

    public void create_pin(){
        SQLiteDatabase db;
        db= openOrCreateDatabase("user_pin_number", MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS pinnumber(id VARCHAR,pinnumber VARCHAR)");
        db.execSQL("INSERT INTO pinnumber VALUES('"+1+"','"+ "1" +"')");
    }

    public void set_pin_number(String phoneNumber, String PinNumber, String FullName, String RecoveryEmail,String Privilege){
        final SweetAlertDialog pDialog;
        pDialog = new SweetAlertDialog(PinnumberActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setContentText("Please wait...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();


        success_message = new SweetAlertDialog(PinnumberActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        error_message = new SweetAlertDialog(PinnumberActivity.this, SweetAlertDialog.ERROR_TYPE);
        Log.d("my_phone", "set_pin_number: "+phone_number);

        AndroidNetworking.post("https://6f30b4c4266c.ngrok.io/api/set_service_pin")
                .addBodyParameter("phoneNumber",phoneNumber)
                .addBodyParameter("PinNumber",PinNumber)
                .addBodyParameter("FullName",FullName)
                .addBodyParameter("Privilege",Privilege)
                .addBodyParameter("RecoveryEmail",RecoveryEmail.trim())
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pDialog.dismiss();
                            if (response.getString("value").equals("0")){
                                Log.d("it_worked", "onResponse: "+"it_worked");
                                if (response.getString("privilege").equals("0")){
                                    Toast.makeText(PinnumberActivity.this, "Logging in as customer", Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder my_alert = new AlertDialog.Builder(PinnumberActivity.this);
                                    my_alert.setTitle("Message");
                                    my_alert.setMessage(response.getString("message"));
                                    my_alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            create_pin();
                                            Session session = new Session(getApplicationContext());
                                            session.setValue(phone_number);
                                            finish();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        }
                                    }).create().show();
                                }else {
                                    Toast.makeText(PinnumberActivity.this, "Logging in as admin", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                error_message.setTitleText(response.getString("title"))
                                        .setContentText(response.getString("message")).setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        error_message.dismissWithAnimation();
                                    }
                                }).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        error_alert.setTitle("ERROR");
                    }
                });
    }


    public void checkConnectivity(){
        Tovuti.from(getApplicationContext()).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                String is_connected;
                if (isConnected == false){
                    is_connected = "Looks like you're disconnected.";
                    MySnack.SnackBuilder snackBuilder = new MySnack.SnackBuilder(PinnumberActivity.this);
                    snackBuilder .setText(is_connected)
                            .setTextColor("#ffffff")   //optional
                            .setTextSize(2)           //optional
                            .setBgColor(String.valueOf(R.color.gray))      //optional
                            .setDurationInSeconds(10)  //will display for 10 seconds
                            .setActionBtnColor("#f44336") //optional

                            .setIcon(Icon.WARNING)
                            //or  .setIcon(R.drawable.ic_info_black_24dp)

                            .setActionListener("fix", new View.OnClickListener() {  //optional
                                @Override
                                public void onClick(View view) {

                                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            })
                            .build();
                }

            }
        });
    }

    @Override
    public void onStop(){
//        Tovuti.from(PinnumberActivity.this).stop();
        super.onStop();
    }
    public void clear_plans(){
        SQLiteDatabase db;
        db= openOrCreateDatabase("alarm_database", MODE_PRIVATE,null);
        db.execSQL("DELETE FROM alarm_table");
    }
    public void delete_phone(){
        SQLiteDatabase db;
        db= openOrCreateDatabase("user_phone_number", MODE_PRIVATE,null);
        db.execSQL("DELETE FROM phonenumber");
    }
    public void delete_pin(){
        SQLiteDatabase db;
        db= openOrCreateDatabase("user_pin_number", MODE_PRIVATE,null);
        db.execSQL("DELETE FROM pinnumber");
    }

    public void alert(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(PinnumberActivity.this);
        builder.setTitle(title);
        builder.setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }
}
