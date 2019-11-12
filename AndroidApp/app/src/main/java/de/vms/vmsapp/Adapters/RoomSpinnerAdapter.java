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

public class RoomSpinnerAdapter extends ArrayAdapter<Room> {
    private ArrayList<Room> rooms;

    public RoomSpinnerAdapter(Context context, ArrayList<Room> rooms) {
        super(context, android.R.layout.simple_list_item_1, rooms);
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Room room = getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.room_spinner_item, parent, false);
        }

        // data population view lookup
        TextView nameTextView = (TextView) convertView.findViewById(R.id.roomNameTextView);
        // populate data to text view
        // @TODO: FIX SETTEXT TO DISPLAY ROOMNAME NOT OBJECT-TO-STRING METHOD
        nameTextView.setText(room.getName());

        // return completed view to render on screen
        return convertView;
    }

    /**
     * Specify to display correct text in dropdown list
     *
     * @param position int
     * @param convertView View
     * @param parent ViewGroup
     * @return view
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public Room getItem(int position) {
        return this.rooms.get(position);
    }

    @Override
    public int getCount() {
        return this.rooms.size();
    }
}
