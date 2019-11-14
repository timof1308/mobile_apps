package de.vms.vmsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vms.vmsapp.Adapters.CompanySpinnerAdapter;
import de.vms.vmsapp.Models.Company;
import de.vms.vmsapp.Models.Visitor;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditVisitorFragment extends Fragment {
    private View view;
    private Button updateButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText telEditText;
    private TextView hostEditText;
    private TextView roomEditText;
    private Spinner companySpinner;
    private Company company;
    private ArrayList<Company> companies;
    private Visitor visitor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            visitor = bundle.getParcelable("visitor");
            company = visitor.getCompany();
        } else {
            Toast.makeText(getContext(), "No visitor selected", Toast.LENGTH_SHORT).show();
        }

        getCompanies();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (View) inflater.inflate(R.layout.fragment_edit_visitor, container, false);
        updateButton = (Button) view.findViewById(R.id.updateButton);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        telEditText = (EditText) view.findViewById(R.id.telEditText);
        hostEditText = (TextView) view.findViewById(R.id.hostTextView);
        roomEditText = (TextView) view.findViewById(R.id.roomTextView);
        companySpinner = (Spinner) view.findViewById(R.id.companySpinner);

        // fill data
        nameEditText.setText(visitor.getName());
        emailEditText.setText(visitor.getEmail());
        telEditText.setText(visitor.getTel());
        hostEditText.setText(getActivity().getResources().getString(R.string.host_label) + ": " + visitor.getMeeting().getUser().getName());
        roomEditText.setText(getActivity().getResources().getString(R.string.room_label) + ": " + visitor.getMeeting().getRoom().getName());

        // company spinner on select item event
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                company = (Company) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVisitor();
                Toast.makeText(getActivity(), "Visitor has been updated", Toast.LENGTH_SHORT).show();
                // "go back" to meetings fragment
                getFragmentManager().popBackStack();
            }
        });

        Log.d("visitor passed", "" + visitor.getId());

        return view;
    }

    /**
     * update visitor data
     */
    private void updateVisitor() {
        // prepare data
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String tel = telEditText.getText().toString();
        int company_id = company.getId();

        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("name", name)
                        .add("email", email)
                        .add("tel", tel)
                        .add("meeting_id", "" + visitor.getMeeting().getId())
                        .add("company_id", "" + company_id)
                        .build();

                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjYsImlkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiZW1haWwiOiJ2bXMud3dpMTdzY2FAZ21haWwuY29tIiwicGFzc3dvcmQiOiI5MzdlOGQ1ZmJiNDhiZDQ5NDk1MzZjZDY1YjhkMzVjNDI2YjgwZDJmODMwYzVjMzA4ZTJjZGVjNDIyYWUyMjQ0Iiwicm9sZSI6MSwidG9rZW4iOm51bGwsImlhdCI6MTU2OTU5MjI0Mn0.R6bRJ21QNe-Er5GnakGQAY7YK1KPbN79gX67huhfzO4")
                        .url("http://35.184.56.207/api/visitors/" + visitor.getId())
                        .put(formBody)
                        .build();

                // run request
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response.body().string());

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
                    Log.d("update visitor", s);
                }
            }
        };

        asyncTask.execute();
    }

    /**
     * Get all companies
     */
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
                        parseCompanies(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    /**
     * parse companies from string to json
     *
     * @param json
     * @throws JSONException
     */
    private void parseCompanies(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        companies = new ArrayList<Company>();
        int position = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // create new room
            Company c = new Company(obj.getInt("id"), obj.getString("name"));
            // check if company matches visitor company
            if (c.getId() == company.getId()) {
                position = i;
            }
            // add room to array list
            companies.add(c);
        }

        CompanySpinnerAdapter arrayAdapter = new CompanySpinnerAdapter(getContext(), companies);
        companySpinner.setAdapter(arrayAdapter);
        // set selection to visitor company
        companySpinner.setSelection(position);
    }
}
