package de.vms.vmsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.vms.vmsapp.Models.Room;
import de.vms.vmsapp.R;

public class RoomListAdapter extends ArrayAdapter<Room> {
    // SOURCE: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView

    private ArrayList<Room> rooms;

    /**
     * Constructer to override constructer of parent class
     * @param context Context
     * @param rooms ArrayList for rooms
     */
    public RoomListAdapter(Context context, ArrayList<Room> rooms) {
        super(context, 0, rooms);
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Room room = getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.room_list_item, parent, false);
        }

        // data population view lookup
        TextView nameTextView = (TextView) convertView.findViewById(R.id.roomName);
        // populate data to text view
        nameTextView.setText(room.getName());

        // return completed view to render on screen
        return convertView;
    }

    /**
     * Get room on position
     *
     * @param position int that has been clicked
     * @return Room model
     */
    public Room getItem(int position) {
        return this.rooms.get(position);
    }
}
