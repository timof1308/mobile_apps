package de.vms.vmsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.vms.vmsapp.Models.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

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

        // load users
        //getUsers();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToolbar.setTitle(R.string.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean loginSucceed = false;
                String email = text_input_username.getText().toString();
                String password = text_input_password.getText().toString();

                Log.d(email, password);
                //check fields not empty

                // request users from DB


                User userObject = new User();
                userObject.setEmail(email);
                userObject.setPassword(password);

                checkUser(userObject);
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


    private void checkUser(User enteredUser) {
        AsyncTask<User, Void, User> asyncTask = new AsyncTask<User, Void, User>() {
            @Override
            protected User doInBackground(User... users) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                if (users.length != 1) {
                    return null;
                }
                User enteredUser = users[0];

                // prepare json as string
                String json = "{\"email\":\"" + enteredUser.getEmail() + "\",\"password\":\"" + enteredUser.getPassword() + "\"}";

                // parse json string to body
                RequestBody body = RequestBody.create(json, JSON); // new

                // get api url from shared pref
                SharedPreferences shared_pref = getSharedPreferences("app", MODE_PRIVATE);
                String url = shared_pref.getString("URL_AUTH", null);
                // request
                Request request = new Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .url(url + "auth/login")
                        .post(body)
                        .build();

                // run request
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        // authorized
                        String responseBody = response.body().string();
                        Log.d("responseBody", responseBody);

                        JSONObject jsonObject = new JSONObject(responseBody);
                        String token = jsonObject.getString("token");

                        Log.d("token", token);

                        enteredUser.setToken(token);

                        return enteredUser;
                    } else {
                        // not authorized
                        return null;
                    }

                    // return response as string to "onPostExecute"
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(User s) {
                super.onPostExecute(s);
                if (s == null) {
                    // unautorisiert
                    Log.d("Login", "Failed");
                    text_input_username.setError("Please check your Username");
                    text_input_password.setError("Please please check your Password");
                } else {
                    // autorisiert
                    Log.d("Login", "Success");

                    // save token from login in SharedPreferences
                    SharedPreferences shared_pref = getSharedPreferences("app", MODE_PRIVATE);
                    SharedPreferences.Editor shared_pref_edit = shared_pref.edit();
                    shared_pref_edit.putString("token", s.getToken());
                    shared_pref_edit.apply();

                    // redirect to new intent
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        asyncTask.execute(enteredUser);
    }

}
