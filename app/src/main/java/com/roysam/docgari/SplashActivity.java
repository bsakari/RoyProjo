package com.roysam.docgari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.roysam.docgari.models.Session;

public class SplashActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        db = openOrCreateDatabase("user_phone_number", MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS phonenumber(id VARCHAR,number VARCHAR)");
        cursor = db.rawQuery("SELECT * FROM phonenumber",null);
//        db.execSQL("DELETE FROM phonenumber");
        Thread splash = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    if (cursor.getCount()==0){
                        startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                        finish();
                    }else {
                        Session session = new Session(getApplicationContext());
                        if (session.getValue().isEmpty()){
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }else {
                            startActivity(new Intent(getApplicationContext(), PinnumberActivity.class));
                            finish();
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splash.start();
    }
}