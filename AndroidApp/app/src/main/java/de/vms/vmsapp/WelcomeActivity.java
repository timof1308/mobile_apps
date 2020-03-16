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
    private final String API_URL = "http://35.223.244.220/";

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

        // get shared preferences
        SharedPreferences shared_pref = getSharedPreferences("app", MODE_PRIVATE);
        // store API_URL in shared preferences
        SharedPreferences.Editor shared_pref_edit = shared_pref.edit();
        shared_pref_edit.putString("URL_AUTH", this.API_URL);
        shared_pref_edit.putString("URL", this.API_URL + "api/");
        shared_pref_edit.apply();

        // get stored token
        String token = shared_pref.getString("token", null);
        // check if token is set
        if (token != null) { // token is set --> MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else { // missing token --> LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
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
