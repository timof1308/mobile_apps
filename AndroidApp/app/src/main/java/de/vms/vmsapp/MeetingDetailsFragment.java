package de.vms.vmsapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.vms.vmsapp.Adapters.MeetingVisitorListAdapter;
import de.vms.vmsapp.Adapters.RoomSpinnerAdapter;
import de.vms.vmsapp.Models.Company;
import de.vms.vmsapp.Models.Meeting;
import de.vms.vmsapp.Models.Room;
import de.vms.vmsapp.Models.Visitor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MeetingDetailsFragment extends Fragment {
    // UI elements
    private View view;
    private EditText dateEditText;
    private EditText durationEditText;
    private Spinner roomSpinner;
    private Button addVisitorButton;
    private Button createMeetingButton;
    private ListView listView;
    private final Calendar myCalendar = Calendar.getInstance();
    // form data
    private String date;
    private int duration;
    private Meeting meeting;
    private Room room;
    private ArrayList<Visitor> visitors;
    private ArrayList<Room> rooms;
    private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;
    private static final String EXTRA_MEETING_MESSAGE = "meeting";
    private MeetingVisitorListAdapter arrayAdapter;
    // api params
    private String URL;
    private String TOKEN;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            meeting = bundle.getParcelable("meeting");
            visitors = meeting.getVisitors();
        } else {
            Toast.makeText(getContext(), "No meeting selected", Toast.LENGTH_SHORT).show();
        }

        // get api url and token from shared pref
        SharedPreferences shared_pref = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (View) inflater.inflate(R.layout.fragment_meeting_details, container, false);
        // ui elements
        dateEditText = (EditText) view.findViewById(R.id.dateEditText);
        durationEditText = (EditText) view.findViewById(R.id.durationEditText);
        roomSpinner = (Spinner) view.findViewById(R.id.roomSpinner);
        addVisitorButton = (Button) view.findViewById(R.id.addVisitorButton);
        createMeetingButton = (Button) view.findViewById(R.id.createMeetingButton);
        listView = (ListView) view.findViewById(R.id.visitorsListView);

        // populate data to text view
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMANY);
        String strDate = dateFormat.format(meeting.getDate());
        dateEditText.setText(strDate);
        myCalendar.setTime(meeting.getDate());
        durationEditText.setText(Integer.toString(meeting.getDuration()));
        room = meeting.getRoom();

        getRooms();

        // add visitors to listview by listadapter
        arrayAdapter = new MeetingVisitorListAdapter(getContext(), visitors, MeetingDetailsFragment.this);
        listView.setAdapter(arrayAdapter);

        // open date picker dialog on text edit focus
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                // open date picker
                new DatePickerDialog(getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(year, monthOfYear, dayOfMonth);
                        // open time picker after date has been selected
                        new TimePickerDialog(getContext(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                myCalendar.set(Calendar.MINUTE, minute);
                                // update date input text based on selection
                                updateDateLabel();
                            }
                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                    }
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
            }
        });

        // room spinner on select item event
        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                room = (Room) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
        return view;
    }

    /**
     * Update date edit text input from calendar popup
     */
    private void updateDateLabel() {
        String myFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        // update text edit text
        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    private void getRooms() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
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
                        updateRoomSpinner(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    /**
     * Update room drop down spinner
     *
     * @param json
     * @throws JSONException
     */
    private void updateRoomSpinner(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        rooms = new ArrayList<Room>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // create new room
            Room room = new Room(obj.getInt("id"), obj.getString("name"));
            // add room to array list
            rooms.add(room);
        }
        RoomSpinnerAdapter arrayAdapter = new RoomSpinnerAdapter(getActivity(), rooms);
        roomSpinner.setAdapter(arrayAdapter);
    }
}
