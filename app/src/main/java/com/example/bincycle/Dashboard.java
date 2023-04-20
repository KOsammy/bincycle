package com.example.bincycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity {

    EditText username, phone;
    Button next;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String mobile, fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Hooks
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        next = findViewById(R.id.Next_btn);
        progressBar = findViewById(R.id.prog_bar);
        fullname = username.getText().toString();
        mobile = phone.getText().toString();

        if(getSupportActionBar()!= null){
            getSupportActionBar().hide();
        }

        next.setOnClickListener(view -> {
            if (phone.getText().toString().trim().isEmpty()) {
                Toast.makeText(Dashboard.this, "Enter Your phone Number", Toast.LENGTH_SHORT).show();
            } else if (username.getText().toString().trim().isEmpty()) {
                Toast.makeText(Dashboard.this, "Enter Your Username", Toast.LENGTH_SHORT).show();
                return;
            }
            // check for correct phone format
            else if (!isValidPhoneNumber(phone.getText().toString())) {
                Toast.makeText(Dashboard.this, "Phone is valid",Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+233" + phone.getText().toString(),
                    60,
                    TimeUnit.SECONDS,
                    Dashboard.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            progressBar.setVisibility(View.GONE);
                            next.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            progressBar.setVisibility(View.GONE);
                            next.setVisibility(View.VISIBLE);
                            Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            progressBar.setVisibility(View.GONE);
                            next.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(Dashboard.this, OtpAuthentication.class);
                            intent.putExtra("username", username.getText().toString());
                            intent.putExtra("phone", phone.getText().toString());
                            intent.putExtra("otp",s);
                            startActivity(intent);
                        }
                    });
        });
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Regular expression to check if the phone number is in the format +1234567890
        return phoneNumber.matches("[0-9]{10}");
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential firebaseOtp) {
        mAuth.signInWithCredential(firebaseOtp)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Toast.makeText(Dashboard.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Dashboard.this, Home.class));
                        } else {
                            Toast.makeText(Dashboard.this, "Login failed", Toast.LENGTH_SHORT).show();
                            // Sign in failure
                        }
                    }
                });
    }

}


