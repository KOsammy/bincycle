package com.example.bincycle;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtpAuthentication extends AppCompatActivity {

    private EditText code1, code2, code3, code4, code5, code6;
    TextView mobileNumber;
    Button verifyButton;
    String firebaseOtp;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authentication);


        mobileNumber = findViewById(R.id.mobi);
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);
        progressBar = findViewById(R.id.progress);
        verifyButton = findViewById(R.id.Log_btn);

        otpSetUp();

      String mobile = getIntent().getStringExtra("mobile");
       mobileNumber.setText("+233"+ mobile);

        firebaseOtp = getIntent().getStringExtra("otp");

        // Get a reference to the Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

// Get a reference to the specific location in the database where the OTP is stored
        DatabaseReference otpRef = database.getReference("otp");

// Attach a listener to the reference to listen for changes to the OTP value
        otpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the OTP value from the data snapshot
                firebaseOtp = dataSnapshot.getValue(String.class);
                // Do something with the OTP value, such as displaying it or passing it to a method for validation
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(OtpAuthentication.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(code1.getText().toString().trim().isEmpty()
               || code2.getText().toString().trim().isEmpty()
               || code3.getText().toString().trim().isEmpty()
               ||code3.getText().toString().trim().isEmpty()
               || code4.getText().toString().trim().isEmpty()
               || code5.getText().toString().trim().isEmpty()
               || code6.getText().toString().trim().isEmpty()) {
                   Toast.makeText(OtpAuthentication.this, "Please Enter a valid Otp", Toast.LENGTH_SHORT).show();
                   return;
               }
               String enteredCode = code1.getText().toString() +
                                    code2.getText().toString() +
                                    code3.getText().toString() +
                                    code4.getText().toString() +
                                    code5.getText().toString() +
                                    code6.getText().toString();

               if(firebaseOtp !=null) {
                   progressBar.setVisibility(View.VISIBLE);
                   verifyButton.setVisibility(View.INVISIBLE);
                   PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                           firebaseOtp,
                           enteredCode
                   );
                   FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   progressBar.setVisibility(View.GONE);
                                   verifyButton.setVisibility(View.VISIBLE);
                                   if(task.isSuccessful()){
                                       Toast.makeText(OtpAuthentication.this, "Authentication is Successful", Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(getApplicationContext(),Home.class);
                                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                   }
                                   else{
                                       Toast.makeText(OtpAuthentication.this, "The OTP ENTERED IS INCORRECT", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
               }
            }
        });
    }

    private void otpSetUp(){
        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    code6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    }




