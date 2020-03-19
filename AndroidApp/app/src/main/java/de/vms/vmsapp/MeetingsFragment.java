package de.vms.vmsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import de.vms.vmsapp.Adapters.MeetingListAdapter;
import de.vms.vmsapp.Models.Company;
import de.vms.vmsapp.Models.Meeting;
import de.vms.vmsapp.Models.Room;
import de.vms.vmsapp.Models.User;
import de.vms.vmsapp.Models.Visitor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingsFragment extends Fragment {
    // UI elements
    private View view;
    private ListView listView;
    private MeetingListAdapter arrayAdapter;
    private Button newMeetingButton;
    // api params
    private String URL;
    private String TOKEN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meetings, container, false);
        listView = (ListView) view.findViewById(R.id.meetingsListView);
        newMeetingButton = (Button) view.findViewById(R.id.newMeetingButton);

        // get api url and token from shared pref
        SharedPreferences shared_pref = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMeetings();

        newMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateMeetingBundle();
            }
        });
    }

    /**
     * open create meeting bundle fragment
     */
    private void openCreateMeetingBundle() {
        // New Fragment
        MeetingBundleFragment meeting_bundle_fragment = new MeetingBundleFragment();
        // Create new fragment and transaction
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, meeting_bundle_fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * get meetings for logged in user
     */
    private void getMeetings() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                User user;
                try {
                    // get user from jwt
                    user = JwtController.decodeJwt(TOKEN);

                    // prepare request
                    Request request = new Request.Builder()
                            .addHeader("Authorization", TOKEN)
                            .url(URL + "users/" + user.getId() + "/meetings")
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    // LOG response
                    Log.d("meetings", s);
                    try {
                        // pass to function to display data and render view
                        loadMeetingListView(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    public void loadMeetingListView(String json) throws JSONException, ParseException {
        // convert json string to json object
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        ArrayList<Meeting> meetings = new ArrayList<Meeting>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // parse json
            Meeting meeting = new Meeting();
            meeting.setId(obj.getInt("id"));
            meeting.setDate(obj.getString("date"));
            meeting.setDuration(obj.getInt("duration"));

            // get nested json objects
            JSONObject roomJson = new JSONObject(obj.getString("room"));
            JSONArray visitorsArray = new JSONArray(obj.getString("visitors"));

            // meeting -> room
            Room r = new Room();
            r.setId(roomJson.getInt("id"));
            r.setName(roomJson.getString("name"));
            // assign room to meeting
            meeting.setRoom(r);
            // meeting -> visitors
            ArrayList<Visitor> visitors = new ArrayList<Visitor>();
            for (int j = 0; j < visitorsArray.length(); j++) {
                // get json object from array
                JSONObject visitorJson = visitorsArray.getJSONObject(j);

                Visitor v = new Visitor();
                v.setId(visitorJson.getInt("id"));
                v.setName(visitorJson.getString("name"));
                v.setEmail(visitorJson.getString("email"));
                v.setTel(visitorJson.getString("tel"));
                if (!visitorJson.getString(("check_in")).equals("null")) {
                    v.setCheck_in(visitorJson.getString("check_in"));
                }
                if (!visitorJson.getString(("check_out")).equals("null")) {
                    v.setCheck_out(visitorJson.getString("check_out"));
                }
                // meeting -> visitor -> company
                JSONObject companyJson = new JSONObject(visitorJson.getString("company"));
                Company c = new Company();
                c.setId(companyJson.getInt("id"));
                c.setName(companyJson.getString("name"));
                v.setCompany(c);

                // add visitor to visitors array list
                visitors.add(v);
            }

            // meeting -> visitors
            meeting.setVisitors(visitors);

            // add room to array list
            meetings.add(meeting);
        }

        MeetingListAdapter arrayAdapter = new MeetingListAdapter(getActivity(), meetings);
        listView.setAdapter(arrayAdapter);
    }
}
