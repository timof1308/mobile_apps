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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResetActivity extends AppCompatActivity {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private Toolbar mToolbar;
    private TextInputEditText text_input_email;
    private TextInputEditText text_input_password;
    private TextInputEditText text_input_password_confirm;
    private TextInputEditText text_input_token;
    private Button resetBtn;
    private String input_email;
    private String input_password;
    private String input_password_confirm;
    private String input_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        text_input_email = (TextInputEditText) findViewById(R.id.text_input_email);
        text_input_password = (TextInputEditText) findViewById(R.id.text_input_password);
        text_input_password_confirm = (TextInputEditText) findViewById(R.id.text_input_password_confirm);
        text_input_token = (TextInputEditText) findViewById(R.id.text_input_token);
        resetBtn = (Button) findViewById(R.id.reset);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data
                input_email = text_input_email.getText().toString();
                input_password = text_input_password.getText().toString();
                input_password_confirm = text_input_password_confirm.getText().toString();
                input_token = text_input_token.getText().toString();

                resetPassword();
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


    private void resetPassword() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare json as string
                String json = "{\"email\": \"" + input_email + "\", \"password\": \"" + input_password + "\", \"password_confirmation\": \"" + input_password_confirm + "\", \"token\": \"" + input_token + "\"}";
                Log.d("JSON BODY", json);

                // parse json string to body
                RequestBody body = RequestBody.create(json, JSON); // new

                // get api url from shared pref
                SharedPreferences shared_pref = getSharedPreferences("app", MODE_PRIVATE);
                String url = shared_pref.getString("URL_AUTH", null);
                // request
                Request request = new Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .url(url + "auth/reset")
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

                // autorisiert
                Toast.makeText(ResetActivity.this, R.string.reset_success, Toast.LENGTH_SHORT).show();

                Log.d("RESET", "GO TO LOGIN");
                // redirect to new intent
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        };

        asyncTask.execute();
    }
}
