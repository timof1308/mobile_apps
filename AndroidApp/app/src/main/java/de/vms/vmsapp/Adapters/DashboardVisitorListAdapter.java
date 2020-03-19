package de.vms.vmsapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vms.vmsapp.DashboardFragment;
import de.vms.vmsapp.EditVisitorFragment;
import de.vms.vmsapp.Models.Visitor;
import de.vms.vmsapp.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardVisitorListAdapter extends ArrayAdapter<Visitor> {
    private ArrayList<Visitor> visitors;
    private Button editButton;
    private Button actionButton;
    private Button deleteButton;
    private DashboardFragment dashboard_fragment;
    // api params
    private String URL;
    private String TOKEN;

    public DashboardVisitorListAdapter(Context context, ArrayList<Visitor> visitors, DashboardFragment df) {
        super(context, 0, visitors);
        this.visitors = visitors;
        this.dashboard_fragment = df;

        // get api url and token from shared pref
        SharedPreferences shared_pref = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Visitor visitor = this.visitors.get(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_visitor_list_item, parent, false);
        }

        // data population view lookup
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView companyTextView = (TextView) convertView.findViewById(R.id.companyTextView);
        TextView hostTextView = (TextView) convertView.findViewById(R.id.hostTextView);
        editButton = (Button) convertView.findViewById(R.id.editButton);
        actionButton = (Button) convertView.findViewById(R.id.actionButton);
        deleteButton = (Button) convertView.findViewById(R.id.deleteButton);

        // populate data to text view
        nameTextView.setText(visitor.getName());
        companyTextView.setText(visitor.getCompany().getName());
        hostTextView.setText(visitor.getMeeting().getUser().getName() + " >> " + visitor.getMeeting().getRoom().getName());

        styleVisitorButtons(visitor);

        // click event listener for edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("visitor", visitor);

                EditVisitorFragment edit_visitor_fragment = new EditVisitorFragment();
                edit_visitor_fragment.setArguments(bundle);

                // Create new fragment and transaction
                FragmentTransaction transaction = dashboard_fragment.getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
                transaction.replace(R.id.fragment_container, edit_visitor_fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // click event listener for action button
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visitor.isChecked_In()) {
                    // visitor is not checked in -> check in
                    checkInVisitor(visitor.getId(), position);
                } else {
                    // visitor is checked in -> check out
                    checkOutVisitor(visitor.getId(), position);
                }
                // update stats
                dashboard_fragment.getDashboardData();
            }
        });

        // click event listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visitor.isChecked_In() && !visitor.isChecked_Out()) {
                    // visitor is not checked in && not checked out -> delete
                    // show confirmation prompt
                    new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                            .setTitle(getContext().getString(R.string.dashboard_delete_title))
                            .setMessage(getContext().getString(R.string.dashboard_delete_text))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // send request to delete visitor
                                    deleteVisitor(visitor.getId(), position);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.dashboard_delete_forbidden), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // return completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return this.visitors.size();
    }

    public Visitor getItem(int position) {
        return this.visitors.get(position);
    }

    /**
     * style buttons based on state
     *
     * @param visitor
     */
    private void styleVisitorButtons(Visitor visitor) {
        // visitor has not checked in yet
        if (!visitor.isChecked_In()) {
            // action button -> check in
            actionButton.setBackground(getContext().getDrawable(R.drawable.ic_add));
        } else if (!visitor.isChecked_Out()) { // visitor has not checked out yet
            // action button -> check out
            actionButton.setBackground(getContext().getDrawable(R.drawable.ic_minus));
        } else { // visitor has checked out
            actionButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        // visitor has already checked out
        if (visitor.isChecked_Out()) {
            // action button -> disable
            actionButton.setEnabled(false);
        }
    }

    private void checkInVisitor(int visitorId, int position) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "visitors/" + visitorId + "/check_in")
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
                    Log.d("checkin", s);
                    try {
                        // pass to function to display data and render view
                        updateVisitorList(s, position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    private void checkOutVisitor(int visitorId, int position) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "visitors/" + visitorId + "/check_out")
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
                    Log.d("checkout", s);
                    try {
                        // pass to function to display data and render view
                        updateVisitorList(s, position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    public void updateVisitorList(String json, int position) throws JSONException {
        JSONObject obj = new JSONObject(json);
        // get visitor object on position
        Visitor visitor = getItem(position);
        if (!obj.getString(("check_in")).equals("null")) {
            visitor.setCheck_in(obj.getString("check_in"));
        }
        if (!obj.getString(("check_out")).equals("null")) {
            visitor.setCheck_out(obj.getString("check_out"));
        }

        Toast.makeText(getContext(), "Visitor has been updated", Toast.LENGTH_SHORT).show();

        styleVisitorButtons(visitor);
        this.notifyDataSetChanged();
    }

    private void deleteVisitor(int visitorId, int positon) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "visitors/" + visitorId)
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
                    removeVisitorFromList(positon);
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
    public void removeVisitorFromList(int position) {
        this.visitors.remove(position);
        Toast.makeText(getContext(), "Visitor has been deleted", Toast.LENGTH_SHORT).show();
        this.notifyDataSetChanged();

        // update stats
        dashboard_fragment.getDashboardData();
    }
}
