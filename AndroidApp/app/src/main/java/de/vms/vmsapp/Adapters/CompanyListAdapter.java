package de.vms.vmsapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;

import de.vms.vmsapp.Models.Company;
import de.vms.vmsapp.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompanyListAdapter extends ArrayAdapter {

    private ArrayList<Company> companies;
    // api params
    private String URL;
    private String TOKEN;

    /**
     * Constructer to override constructer of parent class
     *
     * @param context   Context
     * @param companies ArrayList for rooms
     */
    public CompanyListAdapter(Context context, ArrayList<Company> companies) {
        super(context, 0, companies);
        this.companies = companies;

        // get api url and token from shared pref
        SharedPreferences shared_pref = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Company company = getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.company_list_item, parent, false);
        }

        // data population view lookup
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
        // populate data to text view
        nameTextView.setText(company.getName());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                        .setTitle(getContext().getString(R.string.company_delete_title))
                        .setMessage(getContext().getString(R.string.company_delete_text))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // send request to delete company
                                deleteCompany(company.getId(), position);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        // return completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return this.companies.size();
    }

    /**
     * Get room on position
     *
     * @param position int that has been clicked
     * @return Room model
     */
    public Company getItem(int position) {
        return this.companies.get(position);
    }

    /**
     * delete company with id and remove company from array list by position
     *
     * @param companyId
     * @param position
     */
    private void deleteCompany(int companyId, int position) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "companies/" + companyId)
                        .delete()
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
                    Log.d("delete", s);
                    removeCompanyFromList(position);
                }
            }
        };

        asyncTask.execute();
    }

    /**
     * Remove object on position and update view
     *
     * @param position int position to be removed
     */
    public void removeCompanyFromList(int position) {
        this.companies.remove(position);
        Toast.makeText(getContext(), "Company has been deleted", Toast.LENGTH_SHORT).show();
        this.notifyDataSetChanged();
    }

    /**
     * add company to update list view
     *
     * @param company Company
     */
    public void addCompany(Company company) {
        this.companies.add(company);
        Toast.makeText(getContext(), "Company successfully created", Toast.LENGTH_SHORT).show();
        this.notifyDataSetChanged();
    }
}
