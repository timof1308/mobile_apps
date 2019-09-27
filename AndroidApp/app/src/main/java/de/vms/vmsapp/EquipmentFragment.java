package de.vms.vmsapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EquipmentFragment extends Fragment {

    // UI elements
    private View view;
    private ListView listView;
    private Button newEquipmentButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_equipment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // define listView to render elements
        listView = (ListView) view.findViewById(R.id.equipmentListView);
        newEquipmentButton = (Button) view.findViewById(R.id.newEquipmentButton);

        // load equipment
        getEquipment();
    }

    private void getEquipment() {

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
                    Log.d("data", s);
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

    private void loadIntoListView (String json) throws JSONException{
        JSONArray jsonArray = new JSONArray(json);
        String[] stocks = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            stocks[i] = obj.getString("name");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stocks);
        listView.setAdapter(arrayAdapter);
    }
}
