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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.vms.vmsapp.Adapters.BundleVisitorListAdapter;
import de.vms.vmsapp.Adapters.RoomSpinnerAdapter;
import de.vms.vmsapp.Models.Company;
import de.vms.vmsapp.Models.Room;
import de.vms.vmsapp.Models.User;
import de.vms.vmsapp.Models.Visitor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MeetingBundleFragment extends Fragment {
    // UI elements
    private View view;
    private EditText dateEditText;
    private EditText durationEditText;
    private Spinner roomSpinner;
    private Button addVisitorButton;
    private Button createMeetingButton;
    private ListView visitorsListView;
    private final Calendar myCalendar = Calendar.getInstance();
    // form data
    private String date;
    private int duration;
    private Room room;
    private ArrayList<Room> rooms;
    private ArrayList<Company> companies;
    private ArrayList<Visitor> visitors = new ArrayList<Visitor>();
    private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;
    private static final String EXTRA_VISITOR_MESSAGE = "visitor";
    private BundleVisitorListAdapter arrayAdapter;
    // api params
    private String URL;
    private String TOKEN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meeting_bundle, container, false);
        // ui elements
        dateEditText = (EditText) view.findViewById(R.id.dateEditText);
        durationEditText = (EditText) view.findViewById(R.id.durationEditText);
        roomSpinner = (Spinner) view.findViewById(R.id.roomSpinner);
        addVisitorButton = (Button) view.findViewById(R.id.addVisitorButton);
        createMeetingButton = (Button) view.findViewById(R.id.createMeetingButton);
        visitorsListView = (ListView) view.findViewById(R.id.visitorsListView);

        // get api url and token from shared pref
        SharedPreferences shared_pref = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

        addVisitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open alert dialog for visitor input
                CreateVisitorDialog dialog = CreateVisitorDialog.getInstanceFor(companies);
                dialog.setTargetFragment(MeetingBundleFragment.this, TARGET_FRAGMENT_REQUEST_CODE);
                dialog.show(getFragmentManager(), "CreateVisitorDialog");
            }
        });

        createMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prepare and get data for body
                visitors = arrayAdapter.getVisitors();
                String date = dateEditText.getText().toString();
                int duration;
                if (!durationEditText.getText().toString().equals("")) {
                    duration = Integer.parseInt(durationEditText.getText().toString());
                } else {
                    duration = 0;
                }
                int room_id = room.getId();
                int user_id = 0;
                try {
                    User user = JwtController.decodeJwt(TOKEN);
                    user_id = user.getId();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (visitors.size() == 0 || date == null || duration < 1) {
                    Toast.makeText(getContext(), "Please check the fields above and add at least one visitor for your meeting", Toast.LENGTH_SHORT).show();
                    return;
                }
                createMeeting(user_id, room_id, date, duration);
            }
        });

        arrayAdapter = new BundleVisitorListAdapter(getActivity(), visitors);
        visitorsListView.setAdapter(arrayAdapter);

        getRooms();
        getCompanies();
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

    /**
     * Accept intent from other fragment to pass data from
     *
     * @param visitor needs parcelable implementation
     * @return
     */
    public static Intent newIntent(Visitor visitor) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_VISITOR_MESSAGE, visitor);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == TARGET_FRAGMENT_REQUEST_CODE) {
            Visitor v = data.getExtras().getParcelable(EXTRA_VISITOR_MESSAGE);
            // @TODO: HANDLE AND SAVE VISITOR IN FRAGMENT TO SEND REQUEST
            arrayAdapter.addVisitor(v);
        }
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

    private void getCompanies() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "companies")
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
                        parseCompanies(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    private void parseCompanies(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        // prepare array list for rooms for adapter
        companies = new ArrayList<Company>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            // create new room
            Company company = new Company(obj.getInt("id"), obj.getString("name"));
            // add room to array list
            companies.add(company);
        }
    }

    public void createMeeting(int user_id, int room_id, String date, int duration) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", user_id);
                    // add seconds to date
                    jsonObject.put("date", date + ":00");
                    jsonObject.put("duration", duration);
                    jsonObject.put("room_id", room_id);
                    JSONArray nested_visitors = new JSONArray();
                    jsonObject.put("visitor", nested_visitors);

                    for (Visitor v : visitors) {
                        JSONObject nested_v = new JSONObject();
                        nested_v.put("name", v.getName());
                        nested_v.put("email", v.getEmail());
                        nested_v.put("tel", v.getTel());
                        nested_v.put("company_id", v.getCompany().getId());
                        nested_visitors.put(nested_v);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("JSON", jsonObject.toString());

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

                // prepare request
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "meetings/bundle")
                        .post(body)
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
                    Log.d("data", s);
                    // show toast
                    Toast.makeText(getContext(), "Meeting successfully created", Toast.LENGTH_SHORT).show();
                    // "go back" to meetings fragment
                    getFragmentManager().popBackStack();
//                    try {
//                        // pass to function to create List View elements and render view
//                        loadIntoListView(s);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        };

        asyncTask.execute();
    }
}
