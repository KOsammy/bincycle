package com.example.bincycle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //Variables
    Animation topanim, bottomanim;
    TextView logo, coder, sponser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //to remove the action bar on the top
        if(getSupportActionBar()!= null){
            getSupportActionBar().hide();
        }


        //Animation
        topanim= AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottomanim= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //Hooks
        logo = findViewById(R.id.textView2);
        coder = findViewById(R.id.textView4);
        sponser = findViewById(R.id.textView3);

        logo.setAnimation(topanim);
        coder.setAnimation(bottomanim);
        sponser.setAnimation(bottomanim);


            int SPLASH_SCREEN = 5000;

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Intent intent2 = new Intent(MainActivity.this, Dashboard.class);
                    startActivity(intent2);
                    finishAffinity();
                }

            },SPLASH_SCREEN);


                }
            }








//25091998
