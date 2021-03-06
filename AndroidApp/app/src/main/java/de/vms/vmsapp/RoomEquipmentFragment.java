package de.vms.vmsapp;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vms.vmsapp.Adapters.RoomEquipmentListAdapter;
import de.vms.vmsapp.Models.Equipment;
import de.vms.vmsapp.Models.Room;
import de.vms.vmsapp.Models.RoomEquipment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoomEquipmentFragment extends Fragment {
    // UI elements
    private View view;
    private TextView roomNameTextView;
    private ListView listView;
    private int roomId;
    private String roomName;
    private Button deleteRoomButton;
    private Button btn_delete_room;
    // api params
    private String URL;
    private String TOKEN;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            roomId = bundle.getInt("roomId", -1); // Key, default value
            roomName = bundle.getString("roomName", "Room Details"); // Key, default value
        } else {
            // no room id passed
            Toast.makeText(getActivity(), "No room selected", Toast.LENGTH_LONG).show();
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment view
        view = inflater.inflate(R.layout.fragment_room_equipment, container, false);
        // define listView to render elements
        listView = (ListView) view.findViewById(R.id.roomEquipmentListView);
        roomNameTextView = (TextView) view.findViewById(R.id.roomNameTextView);
        deleteRoomButton = (Button) view.findViewById(R.id.deleteRoomButton);
        // set fragment top name
        roomNameTextView.setText(roomName);

        btn_delete_room = (Button) view.findViewById(R.id.deleteRoomButton);
        btn_delete_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRoom();
            }
        });

        // get api url and token from shared pref
        SharedPreferences shared_pref = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        deleteRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                        .setTitle(getContext().getString(R.string.delete_room_title))
                        .setMessage(getContext().getString(R.string.delete_room_text))
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .setIcon(R.drawable.ic_dialog_alert2)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // send request to delete visitor
                                deleteRoom();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

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
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "equipment")
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
                        loadListView(s);
                        getRoomEquipment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    public void getRoomEquipment() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "rooms/" + roomId + "/equipment")
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
                        // pass to function to create List View elements and render view
                        updateListView(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    public void loadListView(String json) throws JSONException {
        // convert json string to json object
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        ArrayList<Equipment> equipment = new ArrayList<Equipment>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // create new equipment
            Equipment e = new Equipment(obj.getInt("id"), obj.getString("name"));
            // add room to array list
            equipment.add(e);
        }
        RoomEquipmentListAdapter arrayAdapter = new RoomEquipmentListAdapter(getActivity(), equipment, new Room(roomId, roomName));
        listView.setAdapter(arrayAdapter);
    }

    public void updateListView(String json) throws JSONException {
        // convert json string to json object
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        ArrayList<RoomEquipment> roomEquipment = new ArrayList<RoomEquipment>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // get nested elements
            JSONObject jsonRoom = (JSONObject) obj.get("room");
            JSONObject jsonEquipment = (JSONObject) obj.get("equipment");
            // create new room equipment
            Room room = new Room(jsonRoom.getInt("id"), jsonRoom.getString("name"));
            Equipment equipment = new Equipment(jsonEquipment.getInt("id"), jsonEquipment.getString("name"));
            RoomEquipment re = new RoomEquipment(obj.getInt("id"), room, equipment);
            // add room to array list
            roomEquipment.add(re);
        }

        // for each item in list view
        for (int i = 0; i < listView.getCount(); i++) {
            // get equipment
            Equipment equipment = (Equipment) listView.getItemAtPosition(i);
            // for each room equipment from call
            for (RoomEquipment re : roomEquipment) {
                // check if loop (list view) matches room equipment
                if (re.getEquipment().getId() == equipment.getId()) {
                    // update checkbox -> mark checked
                    CheckBox itemCheckBox = listView.getChildAt(i).findViewById(R.id.roomEquipmentCheckBox);
                    itemCheckBox.setChecked(true); // mark checked
                    // get text view for room request id
                    TextView itemTextView = listView.getChildAt(i).findViewById(R.id.roomEquipmentTextView);
                    // set id to text view
                    itemTextView.setText(Integer.toString(re.getId()));
                    // make text view invisible again
                    itemTextView.setVisibility(View.INVISIBLE);
                    continue;
                }
            }
        }
    }

    public void deleteRoom() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "rooms/" + roomId)
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

                    RoomsFragment room_fragment = new RoomsFragment();

                    // Create new fragment and transaction
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
                    transaction.replace(R.id.fragment_container, room_fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    Toast.makeText(getActivity(), getContext().getString(R.string.delete_room_success), Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute();
    }
}
