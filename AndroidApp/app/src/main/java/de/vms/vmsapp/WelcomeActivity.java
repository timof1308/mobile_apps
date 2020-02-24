package de.vms.vmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private Button login;
    private Button register;

    private Button testMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);



        login = (Button) findViewById(R.id.openLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        register = (Button) findViewById(R.id.openRegistration);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });

        testMenu = (Button) findViewById(R.id.testMenu);
        testMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTestMenuActivity();
            }


        });

        SharedPreferences sp1=getSharedPreferences("Login", MODE_PRIVATE);
        String token = sp1.getString("token", null);

        if (token != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void openTestMenuActivity() { //TEST
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
