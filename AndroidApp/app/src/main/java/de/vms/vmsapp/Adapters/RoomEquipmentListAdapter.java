package de.vms.vmsapp.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vms.vmsapp.Models.Equipment;
import de.vms.vmsapp.Models.Room;
import de.vms.vmsapp.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RoomEquipmentListAdapter extends ArrayAdapter<Equipment> {
    private ArrayList<Equipment> equipment;
    private Room room;
    private View itemView;

    public RoomEquipmentListAdapter(Context context, ArrayList<Equipment> equipment, Room room) {
        super(context, 0, equipment);
        this.equipment = equipment;
        this.room = room;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Equipment e = getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.room_equipment_item, parent, false);
        }
        // data population view lookup
        CheckBox roomEquipmentCheckBox = (CheckBox) convertView.findViewById(R.id.roomEquipmentCheckBox);
        TextView roomEquipmentTextView = (TextView) convertView.findViewById(R.id.roomEquipmentTextView);
        // set value
        roomEquipmentCheckBox.setChecked(false); // false = default
        roomEquipmentCheckBox.setText(e.getName());

        // click event listener for check box
        roomEquipmentCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roomEquipmentCheckBox.isChecked()) {
                    postRoomEquipment(e.getId());
                    Log.d("checkbox", "post" + e.getId());
                } else {
                    // get value
                    int roomEquipmentId = Integer.parseInt(roomEquipmentTextView.getText().toString());
                    deleteRoomEquipment(roomEquipmentId);
                    Log.d("checkbox", "delete" + e.getId());
                }
            }
        });

        // get view of item for later manipulation
        itemView = convertView;
        return convertView;
    }

    /**
     * Get room on position
     *
     * @param position int that has been clicked
     * @return Room model
     */
    public Equipment getItem(int position) {
        return this.equipment.get(position);
    }

    private void postRoomEquipment(int equipment_id) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                RequestBody body = null;
                try {
                    JSONObject json = new JSONObject();
                    json.put("equipment_id", equipment_id);
                    MediaType JSON_FORMAT = MediaType.parse("application/json; charset=utf-8");
                    body = RequestBody.create(JSON_FORMAT, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjYsImlkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiZW1haWwiOiJ2bXMud3dpMTdzY2FAZ21haWwuY29tIiwicGFzc3dvcmQiOiI5MzdlOGQ1ZmJiNDhiZDQ5NDk1MzZjZDY1YjhkMzVjNDI2YjgwZDJmODMwYzVjMzA4ZTJjZGVjNDIyYWUyMjQ0Iiwicm9sZSI6MSwidG9rZW4iOm51bGwsImlhdCI6MTU2OTU5MjI0Mn0.R6bRJ21QNe-Er5GnakGQAY7YK1KPbN79gX67huhfzO4")
                        .url("http://35.184.56.207/api/rooms/" + room.getId() + "/equipment")
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
                    Log.d("room equipment", s);
                    try {
                        // parse response from post
                        JSONObject json = new JSONObject(s);
                        // update room equipment id in text view
                        TextView itemTextView = (TextView) itemView.findViewById(R.id.roomEquipmentTextView);
                        itemTextView.setText(Integer.toString(json.getInt("id")));
                        itemTextView.setVisibility(View.INVISIBLE); // make text view invisible again
                        // show toast message
                        Toast.makeText(getContext(), "Equipment has been added to room", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    private void deleteRoomEquipment(int equipment_id) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjYsImlkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiZW1haWwiOiJ2bXMud3dpMTdzY2FAZ21haWwuY29tIiwicGFzc3dvcmQiOiI5MzdlOGQ1ZmJiNDhiZDQ5NDk1MzZjZDY1YjhkMzVjNDI2YjgwZDJmODMwYzVjMzA4ZTJjZGVjNDIyYWUyMjQ0Iiwicm9sZSI6MSwidG9rZW4iOm51bGwsImlhdCI6MTU2OTU5MjI0Mn0.R6bRJ21QNe-Er5GnakGQAY7YK1KPbN79gX67huhfzO4")
                        .url("http://35.184.56.207/api/rooms/" + room.getId() + "/equipment/" + equipment_id)
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
                    Log.d("room equipment", s);
                    // show toast message
                    Toast.makeText(getContext(), "Equipment has been removed from room", Toast.LENGTH_LONG).show();
                }
            }
        };

        asyncTask.execute();
    }
}
