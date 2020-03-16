package de.vms.vmsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vms.vmsapp.Adapters.RoomListAdapter;
import de.vms.vmsapp.Models.Room;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoomsFragment extends Fragment {
    // UI elements
    private View view;
    private ListView listView;
    private Button newRoomButton;
    // api params
    private String URL;
    private String TOKEN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment view
        view = inflater.inflate(R.layout.fragment_rooms, container, false);
        // define listView to render elements
        listView = (ListView) view.findViewById(R.id.roomsListView);
        // create new room button
        newRoomButton = (Button) view.findViewById(R.id.newRoomButton);

        // get api url and token from shared pref
        SharedPreferences shared_pref = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);

        // list view on click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {

                // get selected room object
                Room room = (Room) listView.getItemAtPosition(position);

                Bundle bundle = new Bundle();
                bundle.putInt("roomId", room.getId());
                bundle.putString("roomName", room.getName());

                RoomEquipmentFragment room_equipment_fragment = new RoomEquipmentFragment();
                room_equipment_fragment.setArguments(bundle);

                // Create new fragment and transaction
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
                transaction.replace(R.id.fragment_container, room_equipment_fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // define listView to render elements
        listView = (ListView) view.findViewById(R.id.roomsListView);
        newRoomButton = (Button) view.findViewById(R.id.newRoomButton);
        newRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateRoomDialog();
            }
        });

        // load rooms
        getRooms();
    }

    /**
     * Get all rooms
     */
    private void getRooms() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "rooms")
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
                    Log.d("rooms", s);
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

    /**
     * Build list view elements and display in fragment
     *
     * @param json String to create JSON for
     * @throws JSONException
     */

    public void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        ArrayList<Room> rooms = new ArrayList<Room>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // create new room
            Room room = new Room(obj.getInt("id"), obj.getString("name"));
            // add room to array list
            rooms.add(room);
        }
        RoomListAdapter arrayAdapter = new RoomListAdapter(getActivity(), rooms);
        listView.setAdapter(arrayAdapter);
    }

    private void openCreateRoomDialog() {
        CreateRoomDialog createRoomDialog = new CreateRoomDialog();
        createRoomDialog.show(this.getFragmentManager(), "create room"); //different from tut
    }
}
