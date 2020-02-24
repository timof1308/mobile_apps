package de.vms.vmsapp;

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

import java.io.IOException;
import java.util.regex.Pattern;

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

                register(email,password,username);
            }
        });

    }

    /**
     * Register
     */

    private void register(final String email, final String password, final String username){
        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                OkHttpClient client = new OkHttpClient();


                String json2 = "{\n" +
                        "\t\"name\" : \""+ "NicoTest" +"\",\n" +
                        "\t\"email\" : \""+ "nico@test.de" +"\",\n" +
                        "\t\"password\" : \""+ "NicoTest123!" + "\"\n" +
                        "\t\"role\" : \""+ 1 +"\",\n" +
                        "}";

                String json = "{\n" +
                        "\t\"name\" : \""+ username +"\",\n" +
                        "\t\"email\" : \""+ email +"\",\n" +
                        "\t\"password\" : \""+ password + "\"\n" +
                        "\t\"role\" : \""+ 1 +"\",\n" +
                        "}";

                RequestBody body = RequestBody.create(json2, JSON); // new

                /***********
                RequestBody formBody = new FormBody.Builder()
                        .add("email", email)
                        .add("password", password)
                        .add("username", username)
                        .build();

                 **********/

                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjEsImlkIjoxLCJuYW1lIjoiQWRtaW4iLCJlbWFpbCI6InZtcy53d2kxN3NjYUBnbWFpbC5jb20iLCJwYXNzd29yZCI6ImQ5YjVmNThmMGIzODE5ODI5Mzk3MTg2NWExNDA3NGY1OWViYTNlODI1OTViZWNiZTg2YWU1MWYxZDlmMWY2NWUiLCJyb2xlIjoxLCJ0b2tlbiI6bnVsbCwiaWF0IjoxNTgyMjc3ODM1fQ.U9k0Oykk3rGBRKgQpuc7xgSFSeWaUzk9p3dDMCqVDro")
                        //.addHeader("Content-Type", "application/json" )
                        .url("http://35.223.244.220/auth/register")
                        .post(body)
                        .build();


                // run request
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    // return response as string to "onPostExecute"
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    // LOG response
                    Log.d("data", s);
//                    try {
//                        // pass to function to create List View elements and render view
//                        loadIntoListView(s);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        };

        asyncTask.execute();
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
