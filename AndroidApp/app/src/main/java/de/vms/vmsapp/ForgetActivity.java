package de.vms.vmsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import de.vms.vmsapp.Models.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgetActivity extends AppCompatActivity {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private Toolbar mToolbar;
    private TextInputEditText text_input_email;
    private Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        text_input_email = (TextInputEditText) findViewById(R.id.text_input_email);
        resetBtn = (Button) findViewById(R.id.reset);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToolbar.setTitle(R.string.reset);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get data
                String email = text_input_email.getText().toString();

                // create custom user object
                User userObject = new User();
                userObject.setEmail(email);

                // form check
                loginUser(userObject);
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


    private void loginUser(User enteredUser) {
        AsyncTask<User, Void, User> asyncTask = new AsyncTask<User, Void, User>() {
            @Override
            protected User doInBackground(User... users) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                if (users.length != 1) {
                    return null;
                }
                User enteredUser = users[0];

                // prepare json as string
                String json = "{\"email\":\"" + enteredUser.getEmail() + "\"}";

                // parse json string to body
                RequestBody body = RequestBody.create(json, JSON); // new

                // get api url from shared pref
                SharedPreferences shared_pref = getSharedPreferences("app", MODE_PRIVATE);
                String url = shared_pref.getString("URL_AUTH", null);
                // request
                Request request = new Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .url(url + "auth/forget")
                        .post(body)
                        .build();

                // run request
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        // authorized
                        String responseBody = response.body().string();

                        return enteredUser;
                    } else {
                        // authorized
                        String responseBody = response.body().string();
                        Log.d("FAIL responseBody", responseBody);
                        // not authorized
                        return null;
                    }

                    // return response as string to "onPostExecute"
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(User s) {
                super.onPostExecute(s);
                if (s == null) {
                    // unautorisiert
                    Log.d("Forget", "Failed");
                    text_input_email.setError("Please check your Email");
                } else {
                    // autorisiert
                    Log.d("Reset", "Success");

                    Toast.makeText(ForgetActivity.this, R.string.forget_success, Toast.LENGTH_SHORT).show();

                    // redirect to new intent
                    Intent intent = new Intent(getApplicationContext(), ResetActivity.class);
                    startActivity(intent);
                }
            }
        };

        asyncTask.execute(enteredUser);
    }
}
