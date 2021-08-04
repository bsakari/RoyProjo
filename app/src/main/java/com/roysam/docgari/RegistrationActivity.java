package com.roysam.docgari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;

import lucifer.org.snackbartest.Icon;
import lucifer.org.snackbartest.MySnack;

public class RegistrationActivity extends AppCompatActivity {
    private EditText editTextMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        checkConnectivity();

        editTextMobile = findViewById(R.id.editTextMobile);

        findViewById(R.id.buttonContinue).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                String mobile = editTextMobile.getText().toString().trim();

                if(mobile.isEmpty() || mobile.length() < 10){
                    editTextMobile.setError("Enter a valid phone number");
                    editTextMobile.requestFocus();
                    return;
                }

                Intent intent = new Intent(RegistrationActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_login).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void checkConnectivity(){
        Tovuti.from(this).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                String is_connected;
                if (isConnected == false){
                    is_connected = "Looks like you're disconnected.";
                    MySnack.SnackBuilder snackBuilder = new MySnack.SnackBuilder(RegistrationActivity.this);
                    snackBuilder .setText(is_connected)
                            .setTextColor("#ffffff")   //optional
                            .setTextSize(2)           //optional
                            .setBgColor(String.valueOf(R.color.gray))      //optional
                            .setDurationInSeconds(10)  //will display for 10 seconds
                            .setActionBtnColor("#f44336") //optional

                            .setIcon(Icon.WARNING)
                            .setActionListener("fix", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }).build();
                }

            }
        });
    }

    @Override
    protected void onStop(){
        Tovuti.from(this).stop();
        super.onStop();
    }

}
