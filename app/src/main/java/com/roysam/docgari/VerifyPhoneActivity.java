package com.roysam.docgari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.roysam.docgari.models.AESUtils;
import com.roysam.docgari.models.Clearpath;
import com.roysam.docgari.models.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lucifer.org.snackbartest.Icon;
import lucifer.org.snackbartest.MySnack;

public class VerifyPhoneActivity extends AppCompatActivity {

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    //The edittext to input the code
    private EditText editTextCode;

    //firebase auth object
    private FirebaseAuth mAuth;
    String mobile,user_creating_account,user_encryption_time,mySess;
    SQLiteDatabase db;

    StringBuilder phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        checkConnectivity();
        Session mySession = new Session(getApplicationContext());
        mySess = mySession.getAuth();
//        byte[] keyValue  = new byte[]{(byte) mySess.charAt(0),
//                (byte) mySess.charAt(1), (byte) mySess.charAt(2),
//                (byte) mySess.charAt(3), (byte) mySess.charAt(4),
//                (byte) mySess.charAt(5), (byte) mySess.charAt(6),
//                (byte) mySess.charAt(7), (byte) mySess.charAt(8),
//                (byte) mySess.charAt(9), (byte) mySess.charAt(10),
//                (byte) mySess.charAt(11), (byte) mySess.charAt(12),
//                (byte) mySess.charAt(13), (byte) mySess.charAt(14),
//                (byte) mySess.charAt(15)};
//        Clearpath clearpath = new Clearpath(keyValue);
//        clearpath.path(getApplicationContext());
//        user_encryption_time = clearpath.user_encryption_time;
//        AESUtils aesUtils = new AESUtils(keyValue);
//        try {
//            user_encryption_time = aesUtils.decrypt(user_encryption_time);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        byte[] newKeyValue  = new byte[]{(byte) user_encryption_time.charAt(0),
//                (byte) user_encryption_time.charAt(1), (byte) user_encryption_time.charAt(2),
//                (byte) user_encryption_time.charAt(3), (byte) user_encryption_time.charAt(4),
//                (byte) user_encryption_time.charAt(5), (byte) user_encryption_time.charAt(6),
//                (byte) user_encryption_time.charAt(7), (byte) user_encryption_time.charAt(8),
//                (byte) user_encryption_time.charAt(9), (byte) user_encryption_time.charAt(10),
//                (byte) user_encryption_time.charAt(11), (byte) user_encryption_time.charAt(12),
//                (byte) user_encryption_time.charAt(13), (byte) user_encryption_time.charAt(14),
//                (byte) user_encryption_time.charAt(15)};
//
//        Clearpath newClearPath = new Clearpath(newKeyValue);
//        newClearPath.path(getApplicationContext());

//        user_creating_account = newClearPath.user_creating_account;
        db= openOrCreateDatabase("user_phone_number", MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS phonenumber(id VARCHAR,number VARCHAR)");

        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editTextCode);


        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
//        sendVerificationCode(mobile);
        createAccount(mobile);
        phone = new StringBuilder(mobile);
        phone.setCharAt(0,'4');
        phone.toString().trim();


        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }

                //verifying the code entered manually

                if (mAuth.getUid()!=null){
                    verifyVerificationCode(code);
                }else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(VerifyPhoneActivity.this);
                    alert.setTitle("Verification failed");
                    alert.setMessage("No session granted");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
                            finish();
                        }
                    });
                    alert.create().show();
                }
            }
        });

    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+254" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            SweetAlertDialog alert = new SweetAlertDialog(VerifyPhoneActivity.this,SweetAlertDialog.ERROR_TYPE);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
                    finish();
                }
            });
            alert.show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            createAccount("25"+phone);
//                            Toast.makeText(VerifyPhoneActivity.this, "25"+phone, Toast.LENGTH_LONG).show();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
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
                    MySnack.SnackBuilder snackBuilder = new MySnack.SnackBuilder(VerifyPhoneActivity.this);
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
    protected void onStop(){
        Tovuti.from(this).stop();
        super.onStop();
    }

    @SuppressLint("StringFormatInvalid")
    public void createAccount(String PhoneNumber){
        //Loading alert
        final SweetAlertDialog loading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loading.setTitleText("Finalising account creation");
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
        AndroidNetworking.post("https://6f30b4c4266c.ngrok.io/api/register_user")
                .addBodyParameter("phone_number",PhoneNumber)
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
                                Intent intent = new Intent(VerifyPhoneActivity.this, PinnumberActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                db.execSQL("INSERT INTO phonenumber VALUES('"+1+"','"+response.getString("phone")+"')");

                            }else {
                                error_alert.setContentText(response.getString("phone")).show();
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