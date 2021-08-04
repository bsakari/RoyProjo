package com.roysam.docgari;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.roysam.docgari.models.Session;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.roysam.docgari.R.layout.activity_login;

public class LoginActivity extends AppCompatActivity {
    Button mBtnCustomer, mBtnDhobi;
    TextView mTvResetPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);
        mBtnCustomer = findViewById(R.id.mBtnCustomer);
        mBtnDhobi = findViewById(R.id.mBtnDhobi);
        mTvResetPin = findViewById(R.id.tv__my_reset_pin);

        mBtnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert("LOGIN AS CUSTOMER",0);
            }
        });
        mBtnDhobi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert("LOGIN AS A DHOBI",1);
            }
        });
        mTvResetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),PinnumberActivity.class));
                startActivity(new Intent(getApplicationContext(),MechanicsActivity.class));
            }
        });
    }

    public void alert(String title, Integer privilege){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.login_alert_layout, null);
        builder.setView(dialogView);
        String phone = select_phone();

        EditText mEdtPhone = dialogView.findViewById(R.id.edt_phone);
        mEdtPhone.setText(phone);
        EditText mEdtPassword = dialogView.findViewById(R.id.edt_password);
        builder.setTitle(title);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phone_number = mEdtPhone.getText().toString().trim();
                String password = mEdtPassword.getText().toString().trim();
                if (phone_number.isEmpty()){
                    mEdtPhone.setError("Enter phone number");
                    mEdtPhone.requestFocus();
                }else if (password.isEmpty()){
                    mEdtPassword.setError("Enter password");
                    mEdtPassword.requestFocus();
                }else {
                    loginUser(phone_number,password);
                }
            }
        }).create().show();

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

    @SuppressLint("StringFormatInvalid")
    public void loginUser(String PhoneNumber, String PinNumber){
        //Loading alert
        final SweetAlertDialog loading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loading.setTitleText("Logging in");
        loading.setContentText("Stable internet required...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        //scanner_sound.start();

        //Message alert
        final SweetAlertDialog message_alert = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        message_alert.setTitleText("PAID!").setCancelable(false);

        //Warning alert
        final  SweetAlertDialog error_alert = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        error_alert.setTitleText("FAILED!").setCancelable(false);
        AndroidNetworking.post("https://0bca17a023b2.ngrok.io/api/login_user")
                .addBodyParameter("PhoneNumber",PhoneNumber)
                .addBodyParameter("PinNumber",PinNumber)
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.dismissWithAnimation();
                            //scanner_sound.stop();
                            if (response.getString("value").equals("0")){
                                if (response.getString("privilege").equals("1")){
                                    Toast.makeText(LoginActivity.this, "Customer", Toast.LENGTH_SHORT).show();
                                    Session my_session = new Session(LoginActivity.this);
//                                    my_session.setValue("1");
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("privilege", response.getString("privilege"));
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(LoginActivity.this, "Dhobi", Toast.LENGTH_SHORT).show();
                                    Session my_session = new Session(LoginActivity.this);
//                                    my_session.setValue("1");
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("privilege", response.getString("privilege"));
                                    startActivity(intent);
                                }

                            }else {
                                error_alert.setContentText(response.getString("message")).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        loading.dismiss();
                        error_alert.setContentText(""+anError.getErrorDetail()).show();
                    }
                });
    }
}