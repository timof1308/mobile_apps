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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.vms.vmsapp.MeetingDetailsFragment;
import de.vms.vmsapp.MeetingsFragment;
import de.vms.vmsapp.Models.Meeting;
import de.vms.vmsapp.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingListAdapter extends ArrayAdapter<Meeting> {
    private ArrayList<Meeting> meetings;
    private Button detailsButton;
    private Button deleteButton;
    private MeetingsFragment meetings_fragment;
    // api params
    private String URL;
    private String TOKEN;

    /**
     * Constructer to override constructer of parent class
     *
     * @param context  Context
     * @param meetings ArrayList for meetings
     */
    public MeetingListAdapter(Context context, ArrayList<Meeting> meetings, MeetingsFragment df) {
        super(context, 0, meetings);
        this.meetings = meetings;
        this.meetings_fragment = df;

        // get api url and token from shared pref
        SharedPreferences shared_pref = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Meeting meeting = getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meeting_list_item, parent, false);
        }

        detailsButton = (Button) convertView.findViewById(R.id.detailsButton);
        deleteButton = (Button) convertView.findViewById(R.id.deleteButton);

        // data population view lookup
        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        TextView roomNameTextView = (TextView) convertView.findViewById(R.id.roomNameTextView);
        TextView visitorsTextView = (TextView) convertView.findViewById(R.id.visitorsTextView);
        // populate data to text view
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMANY);
        String strDate = dateFormat.format(meeting.getDate());
        dateTextView.setText(strDate);
        roomNameTextView.setText(meeting.getRoom().getName());
        visitorsTextView.setText("" + meeting.getVisitors().size() + " vistor(s)");

        // click event listener for details button
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("meeting", meeting);

                MeetingDetailsFragment meeting_details_fragment = new MeetingDetailsFragment();
                meeting_details_fragment.setArguments(bundle);

                // Create new fragment and transaction
                FragmentTransaction transaction = meetings_fragment.getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
                transaction.replace(R.id.fragment_container, meeting_details_fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // click event listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // visitor is not checked in && not checked out -> delete
                // show confirmation prompt
                new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                        .setTitle(getContext().getString(R.string.meeting_cancel_title))
                        .setMessage(getContext().getString(R.string.meeting_cancel_text))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // send request to delete visitor
                                cancelMeeting(meeting.getId(), position);
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
        return this.meetings.size();
    }

    /**
     * Get room on position
     *
     * @param position int that has been clicked
     * @return Room model
     */
    public Meeting getItem(int position) {
        return this.meetings.get(position);
    }

    /**
     * add company to update list view
     *
     * @param meeting Meeting
     */
    public void addMeeting(Meeting meeting) {
        this.meetings.add(meeting);
        Toast.makeText(getContext(), "Meeting successfully created", Toast.LENGTH_SHORT).show();
        this.notifyDataSetChanged();
    }

    /**
     * Cancel meeting
     * @param meetingId int id of meeting
     * @param position int position of meetings array list
     */
    private void cancelMeeting(int meetingId, int position) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", TOKEN)
                        .url(URL + "meetings/" + meetingId)
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
                    removeMeetingFromList(position);
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
    public void removeMeetingFromList(int position) {
        this.meetings.remove(position);
        Toast.makeText(getContext(), "Meeting has been canceled", Toast.LENGTH_SHORT).show();
        this.notifyDataSetChanged();
    }
}
