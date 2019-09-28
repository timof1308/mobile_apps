package de.vms.vmsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import de.vms.vmsapp.Models.Equipment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoomEquipmentFragment extends Fragment {
    // UI elements
    private View view;
    private TextView roomNameTextView;
    private int roomId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            roomId = bundle.getInt("roomId", -1); // Key, default value
        } else {
            // no room id passed
            Toast.makeText(getActivity(), "No room selected", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("entry", "§§FINISH§§");
        // fragment view
        view = inflater.inflate(R.layout.fragment_room_requipment, container, false);
        // define listView to render elements
        roomNameTextView = (TextView) view.findViewById(R.id.roomNameTextView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // load rooms
        getEquipment();
    }

    public void getEquipment() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjYsImlkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiZW1haWwiOiJ2bXMud3dpMTdzY2FAZ21haWwuY29tIiwicGFzc3dvcmQiOiI5MzdlOGQ1ZmJiNDhiZDQ5NDk1MzZjZDY1YjhkMzVjNDI2YjgwZDJmODMwYzVjMzA4ZTJjZGVjNDIyYWUyMjQ0Iiwicm9sZSI6MSwidG9rZW4iOm51bGwsImlhdCI6MTU2OTU5MjI0Mn0.R6bRJ21QNe-Er5GnakGQAY7YK1KPbN79gX67huhfzO4")
                        .url("http://35.184.56.207/api/equipment")
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
                    Log.d("equipment", s);
                    try {
                        // pass to function to create List View elements and render view
                        loadCheckBoxes(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    public void loadCheckBoxes(String json) throws JSONException {
        // convert json string to json object
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        ArrayList<Equipment> equipment = new ArrayList<Equipment>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // create new room
            Equipment e = new Equipment(obj.getInt("id"), obj.getString("name"));
            // add room to array list
            equipment.add(e);
        }
        Toast.makeText(getActivity(), "SHOW EQUIPMENT CHECKBOXES " + roomId, Toast.LENGTH_LONG).show();
        /*
        RoomListAdapter arrayAdapter = new RoomEquipmentListAdapter(getActivity(), equipment);
        listView.setAdapter(arrayAdapter);
        */
    }

}
