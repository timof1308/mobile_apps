package de.vms.vmsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.vms.vmsapp.Models.Equipment;
import de.vms.vmsapp.R;

public class RoomEquipmentListAdapter extends ArrayAdapter<Equipment> {
    private ArrayList<Equipment> equipment;

    public RoomEquipmentListAdapter(Context context, ArrayList<Equipment> equipment) {
        super(context, 0, equipment);
        this.equipment = equipment;
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
        // set value
        roomEquipmentCheckBox.setChecked(false); // false = default
        roomEquipmentCheckBox.setText(e.getName());

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
}
