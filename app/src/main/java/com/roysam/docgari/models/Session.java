package com.roysam.docgari.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import static android.content.Context.MODE_PRIVATE;

public class Session {

    private SharedPreferences prefs;
    Context context;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        context = cntx;
    }

    public void setValue(String value) {
        prefs.edit().putString("username", value).commit();
    }

    public String getValue() {
        String value = prefs.getString("username","");
        return value;
    }

    public String getAuth() {
        SharedPreferences settings = context.getSharedPreferences("my_prefs", MODE_PRIVATE);
        String value = settings.getString("user_getting_auth","");
        return value;
    }
}