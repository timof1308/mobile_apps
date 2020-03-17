package de.vms.vmsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import de.vms.vmsapp.Models.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationActivity extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    Toolbar mToolbar;
    Button registerBtn;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mToolbar = findViewById(R.id.toolbar);
        registerBtn = findViewById(R.id.register);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textInputEmail = findViewById(R.id.text_input_email);
        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputConfirmPassword = findViewById(R.id.text_input_confirm_password);
    }

    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Please enter a valid email address");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validateUsername() {
        String usernameInput = textInputUsername.getEditText().getText().toString().trim();

        if (usernameInput.isEmpty()) {
            textInputUsername.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 30) {
            textInputUsername.setError("Username too long");
            return false;
        } else {
            textInputUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        String confirmpassword = textInputConfirmPassword.getEditText().getText().toString();
        String password = textInputPassword.getEditText().getText().toString();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputPassword.setError("Password too weak");
            return false;
        } else if (!password.equals(confirmpassword)) {
            textInputConfirmPassword.setError("Passwords not equal");
            textInputPassword.setError("Passwords not equal");
            return false;
        } else {
            textInputPassword.setError(null);
            textInputConfirmPassword.setError(null);
            return true;
        }
    }


    public void confirmInput(View v) {
        if (!validateEmail() | !validateUsername() | !validatePassword()) {
            return;
        }

        String input = "Email: " + textInputEmail.getEditText().getText().toString();
        input += "\n";
        input += "Username: " + textInputUsername.getEditText().getText().toString();
        input += "\n";
        input += "Password: " + textInputPassword.getEditText().getText().toString();

        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToolbar.setTitle(R.string.registration);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textInputEmail.getEditText().getText().toString();
                String password = textInputPassword.getEditText().getText().toString();
                String username = textInputUsername.getEditText().getText().toString();
                int role = 0;


                //check fields not empty

                // request users from DB


                User userObject = new User();
                userObject.setEmail(email);
                userObject.setPassword(password);
                userObject.setName(username);
                userObject.setRole(role);

                register(userObject);
            }
        });

    }

    /**
     * Register
     */

    private void register(User enteredUser) {
        AsyncTask<User, Void, User> asyncTask = new AsyncTask<User, Void, User>() {
            @Override
            protected User doInBackground(User... users) {
                OkHttpClient client = new OkHttpClient();

                if (users.length != 1) {
                    return null;
                }
                User enteredUser = users[0];

                // prepare json as String
                String json = "{" +
                        "\"name\" : \"" + enteredUser.getName() + "\"," +
                        "\"email\" : \"" + enteredUser.getEmail() + "\"," +
                        "\"password\" : \"" + enteredUser.getPassword() + "\"," +
                        "\"role\" : \"" + enteredUser.getRole() + "\"" +
                        "}";

                // parse string to body
                RequestBody body = RequestBody.create(json, JSON); // new

                // get api url from shared pref
                SharedPreferences shared_pref = getSharedPreferences("app", MODE_PRIVATE);
                String url = shared_pref.getString("URL_AUTH", null);

                // prepare request
                Request request = new Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .url(url + "auth/register")
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

                        /*
                        for (int i = 0; i < jsonArray.length(); i++) {
                            // get json object from array
                            JSONObject obj = jsonArray.getJSONObject(i);
                            token = obj.getString("token");
                        }*/

                        Log.d("token", "token");

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
                    //textInputUsername.setError("Please enter a valid Username or Password");
                    //textInputPassword.setError("Please enter a valid Username or Password");


                } else {
                    // autorisiert
                    Log.d("Login", "Success");

                    // Speichern in Lokalem Speicher für weitere Service Aufrufe
                    // s.getToken()

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        asyncTask.execute(enteredUser);
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
