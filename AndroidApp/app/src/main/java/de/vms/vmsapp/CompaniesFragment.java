package de.vms.vmsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vms.vmsapp.Adapters.CompanyListAdapter;
import de.vms.vmsapp.Models.Company;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompaniesFragment extends Fragment {
    // UI elements
    private View view;
    private ListView listView;
    private Button newCompanyButton;
    private CompanyListAdapter arrayAdapter;
    private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;
    private static final String EXTRA_COMPANY_MESSAGE = "company";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment view
        view = inflater.inflate(R.layout.fragment_companies, container, false);
        listView = (ListView) view.findViewById(R.id.companiesListView);
        newCompanyButton = (Button) view.findViewById(R.id.newCompanyButton);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        newCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateCompanyDialog();
            }
        });

        getCompanies();
    }

    private void getCompanies() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjYsImlkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiZW1haWwiOiJ2bXMud3dpMTdzY2FAZ21haWwuY29tIiwicGFzc3dvcmQiOiI5MzdlOGQ1ZmJiNDhiZDQ5NDk1MzZjZDY1YjhkMzVjNDI2YjgwZDJmODMwYzVjMzA4ZTJjZGVjNDIyYWUyMjQ0Iiwicm9sZSI6MSwidG9rZW4iOm51bGwsImlhdCI6MTU2OTU5MjI0Mn0.R6bRJ21QNe-Er5GnakGQAY7YK1KPbN79gX67huhfzO4")
                        .url("http://35.184.56.207/api/companies")
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
                    Log.d("companies", s);
                    try {
                        // pass to function to create List View elements and render view
                        loadIntoListView(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        ArrayList<Company> companies = new ArrayList<Company>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // create new room
            Company company = new Company(obj.getInt("id"), obj.getString("name"));
            // add room to array list
            companies.add(company);
        }
        arrayAdapter = new CompanyListAdapter(getActivity(), companies);
        listView.setAdapter(arrayAdapter);
    }

    private void openCreateCompanyDialog() {
        // call getInstanceFor method
        CreateCompanyDialog createCompanyDialog = CreateCompanyDialog.getInstanceFor();
        // set target fragment to get this fragment from other fragment
        createCompanyDialog.setTargetFragment(CompaniesFragment.this, TARGET_FRAGMENT_REQUEST_CODE);
        createCompanyDialog.show(getFragmentManager(), "create company"); //different from tut
    }

    public static Intent newIntent(Company company) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_COMPANY_MESSAGE, company);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == TARGET_FRAGMENT_REQUEST_CODE) {
            Company c = data.getExtras().getParcelable(EXTRA_COMPANY_MESSAGE);
            Log.d("company returned", c.getName());
            // @TODO: HANDLE AND SAVE VISITOR IN FRAGMENT TO SEND REQUEST
            arrayAdapter.addCompany(c);
        }
    }
}
