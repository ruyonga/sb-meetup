package com.sb.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneNumberLogin extends AppCompatActivity {

    @BindView(R.id.phonenumber)
    EditText phonenumber;
    @BindView(R.id.login_button)
    Button login_button;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(v -> {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phonenumber.getText().toString().trim(),
                    120,
                    TimeUnit.SECONDS,
                    PhoneNumberLogin.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(PhoneNumberLogin.this, "Invalid phone number or code", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                            // The SMS verification code has been sent to the provided phone number, we
                            // now need to ask the user to enter the code and then construct a credential
                            // by combining the code with a verification ID.
                            Log.d("TAG", "onCodeSent:" + verificationId);

                            // Save verification ID and resending token so we can use them later

                            showVerificationForm(verificationId);

                        }
                    });


        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = task.getResult().getUser();
                        switchActivity();

                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(PhoneNumberLogin.this, "Invalid code, try again", Toast.LENGTH_LONG).show();
                            //showVerificationForm();
                        }
                    }
                });
    }

    private void showVerificationForm(String phoneVerificationId) {
        View dialogView = getLayoutInflater().inflate(R.layout.verification_code, null);
        BottomSheetDialog dialog = new BottomSheetDialog(PhoneNumberLogin.this);
        EditText code = dialogView.findViewById(R.id.code);
        Button verify = dialogView.findViewById(R.id.verify);
        dialog.setContentView(dialogView);
        dialog.show();

        try {
            verify.setOnClickListener(v -> {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code.getText().toString().trim());
                if (credential != null) {
                    signInWithPhoneAuthCredential(credential);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void switchActivity() {

        startActivity(new Intent(this, MainActivity.class));
    }
}
