package de.vms.vmsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.vms.vmsapp.Models.Company;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateCompanyDialog extends DialogFragment {
    // UI elements
    private EditText edit_companyname;

    public static CreateCompanyDialog getInstanceFor() {
        CreateCompanyDialog cvd = new CreateCompanyDialog();
        // in case method accepts passed variables pass handling here
        return cvd;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_create_company, null);

        //to display cursor, but doesn't work so far
        edit_companyname = view.findViewById(R.id.edit_companyname);
        edit_companyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == edit_companyname.getId()) {
                    edit_companyname.setCursorVisible(true);
                }
            }
        });

        builder.setView(view)
                .setTitle("Create Company")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createCompany(edit_companyname.getText().toString());
                    }
                });
        return builder.create();
    }

    /**
     * Create a new room
     */
    private void createCompany(final String s) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("name", s)
                        .build();

                // get api url and token from shared pref
                SharedPreferences shared_pref = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
                String url = shared_pref.getString("URL", null);
                String token = shared_pref.getString("token", null);

                // prepare request
                Request request = new Request.Builder()
                        .addHeader("Authorization", token)
                        .url(url + "companies")
                        .post(formBody)
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
                    try {
                        // pass to function to create List View elements and render view
                        parseResponse(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    /**
     * parse create company response from json to object
     *
     * @param json String
     * @throws JSONException
     */
    private void parseResponse(String json) throws JSONException {
        // create json object
        JSONObject obj = new JSONObject(json);
        // create company
        Company company = new Company(obj.getInt("id"), obj.getString("name"));
        Log.d("data", "" + company.getId());
        // get target fragment
        if (getTargetFragment() == null) {
            return;
        }
        // prepare intent
        Intent intent = CompaniesFragment.newIntent(company);
        // send intent from dialog to fragment
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
