package com.roysam.docgari.models;


import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Clearpath {
    public String user_creating_plan,
            user_deleting_plan,
            user_switching_plan_on_or_off,
            user_saving_today,
            user_confirming_payment,
            user_loading_wallet,
            user_witdrawing,
            user_loading_transactions,
            user_deleting_wallet_record,
            user_creating_account,
            user_setting_service_pin,
            user_reseting_service_pin,
            user_confirming_security_answer,
            user_validating_service_pin,
            user_generating_token,
            user_updating_saving_plan,
            user_synchronizing_saving_plans,
            user_encryption_time,
            user_toping_up_wallet;

    private static byte[] key;

    public Clearpath(byte[] key) {
        this.key = key;
    }

    public void path(Context context) {
        SharedPreferences settings = context.getSharedPreferences("my_prefs", MODE_PRIVATE);
        user_creating_plan = settings.getString("user_creating_plan", "");
        user_deleting_plan = settings.getString("user_deleting_plan", "");
        user_switching_plan_on_or_off = settings.getString("user_switching_plan_on_or_off", "");
        user_saving_today = settings.getString("user_saving_today", "");
        user_confirming_payment = settings.getString("user_confirming_payment", "");
        user_loading_wallet = settings.getString("user_loading_wallet", "");
        user_witdrawing = settings.getString("user_witdrawing", "");
        user_loading_transactions = settings.getString("user_loading_transactions", "");
        user_deleting_wallet_record = settings.getString("user_deleting_wallet_record", "");
        user_creating_account = settings.getString("user_creating_account", "");
        user_setting_service_pin = settings.getString("user_setting_service_pin", "");
        user_reseting_service_pin = settings.getString("user_reseting_service_pin", "");
        user_confirming_security_answer = settings.getString("user_confirming_security_answer", "");
        user_validating_service_pin = settings.getString("user_validating_service_pin", "");
        user_generating_token = settings.getString("user_generating_token", "");
        user_updating_saving_plan = settings.getString("user_updating_saving_plan", "");
        user_synchronizing_saving_plans = settings.getString("user_synchronizing_saving_plans", "");
        user_encryption_time = settings.getString("user_encryption_time", "");
        user_toping_up_wallet = settings.getString("user_toping_up_wallet", "");
        final  byte[] keyValue =  key;


        try {
            AESUtils aesUtils = new AESUtils(keyValue);
            user_creating_plan = aesUtils.decrypt(user_creating_plan);
            user_deleting_plan = aesUtils.decrypt(user_deleting_plan);
            user_switching_plan_on_or_off = aesUtils.decrypt(user_switching_plan_on_or_off);
            user_saving_today = aesUtils.decrypt(user_saving_today);
            user_confirming_payment = aesUtils.decrypt(user_confirming_payment);
            user_loading_wallet = aesUtils.decrypt(user_loading_wallet);
            user_witdrawing = aesUtils.decrypt(user_witdrawing);
            user_loading_transactions = aesUtils.decrypt(user_loading_transactions);
            user_deleting_wallet_record = aesUtils.decrypt(user_deleting_wallet_record);
            user_creating_account = aesUtils.decrypt(user_creating_account);
            user_setting_service_pin = aesUtils.decrypt(user_setting_service_pin);
            user_reseting_service_pin = aesUtils.decrypt(user_reseting_service_pin);
            user_confirming_security_answer = aesUtils.decrypt(user_confirming_security_answer);
            user_validating_service_pin = aesUtils.decrypt(user_validating_service_pin);
            user_generating_token = aesUtils.decrypt(user_generating_token);
            user_updating_saving_plan = aesUtils.decrypt(user_updating_saving_plan);
            user_synchronizing_saving_plans = aesUtils.decrypt(user_synchronizing_saving_plans);
            user_encryption_time = aesUtils.decrypt(user_encryption_time);
//            user_toping_up_wallet = aesUtils.decrypt(user_toping_up_wallet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
