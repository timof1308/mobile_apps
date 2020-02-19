package de.vms.vmsapp;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    Toolbar mToolbar;
    TextInputEditText text_input_username;
    TextInputEditText text_input_password;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar = findViewById(R.id.toolbar);
        text_input_username = findViewById(R.id.text_input_username);
        text_input_password = findViewById(R.id.text_input_password);
        loginBtn = findViewById(R.id.login);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToolbar.setTitle(R.string.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = text_input_username.getText().toString();
                String password = text_input_password.getText().toString();
                Log.d(username, password);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Noch nicht perfekt, aber besser als vorher

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        return false;
    }
}
