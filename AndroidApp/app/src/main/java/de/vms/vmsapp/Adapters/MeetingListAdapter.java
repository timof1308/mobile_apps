package de.vms.vmsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.vms.vmsapp.Models.Meeting;
import de.vms.vmsapp.R;

public class MeetingListAdapter extends ArrayAdapter<Meeting> {
    private ArrayList<Meeting> meetings;

    /**
     * Constructer to override constructer of parent class
     *
     * @param context  Context
     * @param meetings ArrayList for meetings
     */
    public MeetingListAdapter(Context context, ArrayList<Meeting> meetings) {
        super(context, 0, meetings);
        this.meetings = meetings;
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

        // data population view lookup
        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        TextView roomNameTextView = (TextView) convertView.findViewById(R.id.roomNameTextView);
        TextView visitorsTextView = (TextView) convertView.findViewById(R.id.visitorsTextView);
        // populate data to text view
        DateFormat dateFormat = new SimpleDateFormat("yyyy-m-d hh:mm");
        String strDate = dateFormat.format(meeting.getDate());
        dateTextView.setText(strDate);
        roomNameTextView.setText(meeting.getRoom().getName());
        visitorsTextView.setText("" + meeting.getVisitors().size() + " vistor(s)");

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
}
